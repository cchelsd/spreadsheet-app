import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spreadsheet is a class containing a spreadsheet of cells with equations.
 * @author Chelsea Dacones
 * @author Makai Martines
 * @author Elias Peterson
 * @author Alexis Zakrzewski
 */
public class Spreadsheet {
    /**
     * The Cells of this spreadsheet.
     */
    private final Cell[][] cells;

    /**
     * Constructs a spreadsheet object of a specified size, where sheetSize is the
     * number of rows and columns in the sheet.
     * @param rows The amount of rows in the spreadsheet.
     * @param cols The amount of columns in the spreadsheet
     */
    public Spreadsheet(final int rows, final int cols) {
        cells = new Cell[rows][cols];
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                cells[x][y] = new Cell();
            }
        }
    }

    /**
     * @return The number of rows in the spreadsheet.
     */
    public int getNumRows() {
        return cells.length;
    }

    /**
     * @return The number of columns in the spreadsheet.
     */
    public int getNumColumns() {
        return cells[0].length;
    }

    /**
     * Changes the given cellToken's formula to the String provided, then recalculates the spreadsheet.
     * @param cellToken The CellToken to change.
     * @param theFormula The formula for this Cell to have.
     */
    public void changeCellFormulaAndRecalculate(final CellToken cellToken, final String theFormula) throws IllegalArgumentException {
        Stack<Token> expTreeTokenStack = getFormula(theFormula);
        // We save a copy of the previous formula just in-case the new one causes an error.
        String previousFormula = getCell(cellToken).getFormula();
        // Update our cell with the new expression tree stack we were given.
        cells[cellToken.getRow()][cellToken.getColumn()].buildExpressionTree(expTreeTokenStack);
        getCell(cellToken).setFormula(theFormula);

        Queue<CellToken> processQueue = new LinkedList<>(); // Stores which cells to process, and in what order.
        ConcurrentHashMap<CellToken, List<CellToken>> cellDependencies = new ConcurrentHashMap<>(); // All dependencies of a given cell.

        for(int y = 0; y < getNumRows(); y++) {
            for(int x = 0; x < getNumColumns(); x++) {
                CellToken cToken = new CellToken();
                cToken.setRow(y);
                cToken.setColumn(x);
                cellDependencies.put(cToken, getCell(cToken).getDependencies());
            }
        }

        // Go through the cell dependencies, topologically sorting them into the process queue.
        while(!cellDependencies.isEmpty()) {
            boolean removedValue = false;
            for (CellToken t : cellDependencies.keySet()) {
                // If this cell has an empty list of dependencies, remove it from the hashtable.
                if (cellDependencies.get(t).isEmpty()) {
                    removedValue = true;
                    cellDependencies.remove(t);
                    processQueue.add(t);
                    for (CellToken u : cellDependencies.keySet()) {
                        // If this cell has a dependent, it's removed. if not, then it won't.
                        cellDependencies.get(u).remove(t);
                    }
                }
            }
            // If we didn't remove anything during a loop, a cycle has been found.
            if (!removedValue) {
                System.out.println("Cycle found");
                // Since we know the graph originally worked before we changed a cell,
                // we revert the change and then break.
                Stack<Token> prevExpTreeTokenStack = getFormula(previousFormula);
                // Update our cell with the original expression tree.
                cells[cellToken.getRow()][cellToken.getColumn()].buildExpressionTree(prevExpTreeTokenStack);
                if (!previousFormula.isEmpty()) {
                    getCell(cellToken).setFormula(previousFormula);
                } else {
                    getCell(cellToken).setFormula("0");
                }
                throw new IllegalArgumentException();
            }
        }

        // Now iterate through the queue of cells.
        while(!processQueue.isEmpty()) {
            CellToken cToken = processQueue.remove();
            getCell(cToken).evaluate(this);
        }
    }

    /**
     * Evaluates the formula in this cell and returns the result.
     * @param theCellToken The CellToken of the cell we want to evaluate.
     * @return The result of this cell's formula.
     */
    public int evaluateCell(final CellToken theCellToken) {
        return cells[theCellToken.getRow()][theCellToken.getColumn()].evaluate(this);
    }

    /**
     * Gets the cell at the specified row and column.
     * @param theRow The row of the cell.
     * @param theColumn The column of the cell.
     * @return The Cell of this spreadsheet.
     */
    public Cell getCell(final int theRow, final int theColumn) {
        return cells[theRow][theColumn];
    }

    /**
     * Gets the cell at the specified CellToken.
     * @param theToken The CellToken referring to a given cell.
     * @return The Cell from this spreadsheet.
     */
    public Cell getCell(final CellToken theToken) { return getCell(theToken.getRow(), theToken.getColumn()); }

    /**
     * getCellToken
     * <br>
     * Assuming that the next chars in a String (at the given startIndex)
     * is a cell reference, set cellToken's column and row to the
     * cell's column and row.
     * If the cell reference is invalid, the row and column of the return CellToken
     * are both set to BadCell (which should be a final int that equals -1).
     * Also, return the index of the position in the string after processing
     * the cell reference.
     * (Possible improvement: instead of returning a CellToken with row and
     * column equal to BadCell, throw an exception that indicates a parsing error.)
     * <br>
     * A cell reference is defined to be a sequence of CAPITAL letters,
     * followed by a sequence of digits (0-9).  The letters refer to
     * columns as follows: A = 0, B = 1, C = 2, ..., Z = 25, AA = 26,
     * AB = 27, ..., AZ = 51, BA = 52, ..., ZA = 676, ..., ZZ = 701,
     * AAA = 702.  The digits represent the row number.
     * <br>
     * @param inputString  the input string
     * @param startIndex  the index of the first char to process
     * @param cellToken  a cellToken (essentially a return value)
     * @return  index corresponding to the position in the string just after the cell reference
     */
    int getCellToken (String inputString, int startIndex, CellToken cellToken) {
        char ch;
        int column;
        int row;
        int index = startIndex;

        // handle a bad startIndex
        if ((startIndex < 0) || (startIndex >= inputString.length() )) {
            cellToken.setColumn(-1);
            cellToken.setRow(-1);
            return index;
        }

        // get rid of leading whitespace characters
        while (index < inputString.length() ) {
            ch = inputString.charAt(index);
            if (!Character.isWhitespace(ch)) {
                break;
            }
            index++;
        }
        if (index == inputString.length()) {
            // reached the end of the string before finding a capital letter
            cellToken.setColumn(-1);
            cellToken.setRow(-1);
            return index;
        }

        // ASSERT: index now points to the first non-whitespace character

        ch = inputString.charAt(index);
        // process CAPITAL alphabetic characters to calculate the column
        if (!Character.isUpperCase(ch)) {
            cellToken.setColumn(-1);
            cellToken.setRow(-1);
            return index;
        } else {
            column = ch - 'A';
            index++;
        }

        while (index < inputString.length() ) {
            ch = inputString.charAt(index);
            if (Character.isUpperCase(ch)) {
                column = ((column + 1) * 26) + (ch - 'A');
                index++;
            } else {
                break;
            }
        }
        if (index == inputString.length() ) {
            // reached the end of the string before fully parsing the cell reference
            cellToken.setColumn(-1);
            cellToken.setRow(-1);
            return index;
        }

        // ASSERT: We have processed leading whitespace and the
        // capital letters of the cell reference

        // read numeric characters to calculate the row
        if (Character.isDigit(ch)) {
            row = ch - '0';
            index++;
        } else {
            cellToken.setColumn(-1);
            cellToken.setRow(-1);
            return index;
        }

        while (index < inputString.length() ) {
            ch = inputString.charAt(index);
            if (Character.isDigit(ch)) {
                row = (row * 10) + (ch - '0');
                index++;
            } else {
                break;
            }
        }

        // successfully parsed a cell reference
        cellToken.setColumn(column);
        cellToken.setRow(row);
        return index;
    }

    /**
     * getFormula
     * <br>
     * Given a string that represents a formula that is an infix
     * expression, return a stack of Tokens so that the expression,
     * when read from the bottom of the stack to the top of the stack,
     * is a postfix expression.
     * <br>
     * A formula is defined as a sequence of tokens that represents
     * a legal infix expression.
     * <br>
     * A token can consist of a numeric literal, a cell reference, or an
     * operator (+, -, *, /).
     * <br>
     * Multiplication (*) and division (/) have higher precedence than
     * addition (+) and subtraction (-).  Among operations within the same
     * level of precedence, grouping is from left to right.
     * <br>
     * This algorithm follows the algorithm described in Weiss, pages 105-108.
     */
    Stack<Token> getFormula(String formula) {
        Stack<Token> returnStack = new Stack<>();  // stack of Tokens (representing a postfix expression)
        boolean error = false;
        char ch = ' ';

        int literalValue;

        CellToken cellToken;

        int index = 0;  // index into formula
        Stack<Token> operatorStack = new Stack<>();  // stack of operators

        while (index < formula.length() ) {
            // get rid of leading whitespace characters
            while (index < formula.length() ) {
                ch = formula.charAt(index);
                if (!Character.isWhitespace(ch)) {
                    break;
                }
                index++;
            }

            if (index == formula.length() ) {
                error = true;
                break;
            }

            // ASSERT: ch now contains the first character of the next token.
            if (OperatorToken.isOperator(ch)) {
                // We found an operator token
                switch (ch) {
                    case OperatorToken.Plus:
                    case OperatorToken.Minus:
                    case OperatorToken.Mult:
                    case OperatorToken.Div:
                    case OperatorToken.Pow:
                    case OperatorToken.LeftParen:
                        // push operatorTokens onto the output stack until
                        // we reach an operator on the operator stack that has
                        // lower priority than the current one.
                        OperatorToken stackOperator;
                        while (!operatorStack.isEmpty()) {
                            stackOperator = (OperatorToken) operatorStack.peek();
                            if ((stackOperator.priority() >= OperatorToken.operatorPriority(ch)) &&
                                    (stackOperator.getToken() != OperatorToken.LeftParen) ) {

                                // output the operator to the return stack
                                operatorStack.pop();
                                returnStack.push(stackOperator);
                            } else {
                                break;
                            }
                        }

                        // push the operator on the operator stack
                        operatorStack.push(new OperatorToken(ch));
                        index++;
                        break;
                    case OperatorToken.RightParen:
                        if(!operatorStack.isEmpty()) {
                            stackOperator = (OperatorToken) operatorStack.pop();
                            // This code does not handle operatorStack underflow.
                            while (stackOperator.getToken() != OperatorToken.LeftParen) {
                                // pop operators off the stack until a LeftParen appears and
                                // place the operators on the output stack
                                returnStack.push(stackOperator);
                                if (!operatorStack.isEmpty())
                                    stackOperator = (OperatorToken) operatorStack.pop();
                                else break;
                            }
                        }

                        index++;
                        break;
                    default:
                        // This case should NEVER happen
                        System.out.println("Error in getFormula.");
                        System.exit(0);
                        break;
                }

            } else if (Character.isDigit(ch)) {
                // We found a literal token
                literalValue = ch - '0';
                index++;
                while (index < formula.length()) {
                    ch = formula.charAt(index);
                    if (Character.isDigit(ch)) {
                        literalValue = (literalValue * 10) + (ch - '0');
                        index++;
                    } else {
                        break;
                    }
                }
                // place the literal on the output stack
                returnStack.push(new LiteralToken(literalValue));

            } else if (Character.isUpperCase(ch)) {
                // We found a cell reference token
                //CellToken cellToken = new CellToken();
                cellToken = new CellToken();
                index = getCellToken(formula, index, cellToken);
                if (cellToken.getRow() == -1) {
                    error = true;
                    break;
                } else {
                    // place the cell reference on the output stack
                    returnStack.push(cellToken);
                }

            } else {
                error = true;
                break;
            }
        }

        // pop all remaining operators off the operator stack
        while (!operatorStack.isEmpty()) {
            returnStack.push(operatorStack.pop());
        }

        if (error) {
            // a parse error; return the empty stack.
            // Go through the entire return stack and pop every item.
            // Maybe there's a better way to do this?
            while(!returnStack.empty())
                returnStack.pop();
        }

        return returnStack;
    }

    /**
     *  Given a CellToken, print it out as it appears on the
     *  spreadsheet (e.g., "A3")
     *  @param cellToken  a CellToken
     *  @return  the cellToken's coordinates
     */
    String printCellToken (CellToken cellToken) {
        char ch;
        StringBuilder returnString = new StringBuilder();
        int col;
        int largest = 26;  // minimum col number with number_of_digits digits
        int number_of_digits = 2;

        col = cellToken.getColumn();

        // compute the biggest power of 26 that is less than or equal to col
        // We don't check for overflow of largest here.
        while (largest <= col) {
            largest = largest * 26;
            number_of_digits++;
        }
        largest = largest / 26;
        number_of_digits--;

        // append the column label, one character at a time
        while (number_of_digits > 1) {
            ch = (char)(((col / largest) - 1) + 'A');
            returnString.append(ch);
            col = col % largest;
            largest = largest  / 26;
            number_of_digits--;
        }

        // handle last digit
        ch = (char)(col + 'A');
        returnString.append(ch);

        // append the row as an integer
        returnString.append(cellToken.getRow());

        return returnString.toString();
    }

    /**
     * Prints out the values inside this spreadsheet.
     */
    public void printValues() {
        for(int y = 0; y < getNumRows(); y++) {
            for(int x = 0; x < getNumColumns(); x++) {
                CellToken cellToken = new CellToken();
                cellToken.setRow(y);
                cellToken.setColumn(x);
                System.out.print(printCellToken(cellToken));
                System.out.print(": ");
                System.out.print(evaluateCell(cellToken));
                System.out.print(" | ");
            }
            System.out.println();
        }
    }

    /**
     * Prints out the formula inside the given CellToken
     * @param cellToken The CellToken to print the formula from.
     */
    public void printCellFormula(CellToken cellToken) {
        System.out.println(getCell(cellToken).getFormula());
    }

    /**
     * Prints out ALL formulas inside this spreadsheet.
     */
    public void printAllFormulas() {
        CellToken cellToken = new CellToken();
        for(int y = 0; y < getNumRows(); y++) {
            for(int x = 0; x < getNumColumns(); x++) {
                cellToken.setRow(y);
                cellToken.setColumn(x);
                System.out.print(printCellToken(cellToken));
                System.out.print(": ");
                System.out.print(getCell(cellToken).getFormula());
                System.out.print(" | ");
            }
            System.out.println();
        }
    }

    /**
     * Saves the current spreadsheet as a file.
     * @param theFilePath
     * @param mainTable
     * @param headerTable
     * @throws IOException
     */
    public void saveToFile(String theFilePath, JTable mainTable, JTable headerTable) throws IOException {
        FileWriter writer = new FileWriter(theFilePath);

        writer.write("\t");
        JTableHeader header = mainTable.getTableHeader();
        for (int i = 0; i < header.getColumnModel().getColumnCount(); i++) {
            writer.write(header.getColumnModel().getColumn(i).getHeaderValue().toString() + "\t");
        }
        writer.write("\n");

        for (int i = 0; i < cells.length; i++) {
            writer.write(headerTable.getValueAt(i, 0).toString() + "\t");
            for (int j = 0; j < cells[i].length; j++) {
                Cell cell = cells[i][j];
                String formula = cell.getFormula();
                if (formula != null) {
                    writer.write(formula);
                }
                writer.write("\t");
            }
            writer.write("\n");
        }
        writer.close();
    }

    /**
     * Opens a file chooser window that defaults to the current directory.
     * @param theFilePath
     * @param mainTable
     * @param headerTable
     * @throws IOException
     */
    public void readFromFile(String theFilePath, JTable mainTable, JTable headerTable) throws IOException {
        FileReader reader = new FileReader(theFilePath);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;

        // read the header row
        line = bufferedReader.readLine();
        String[] headerColumns = line.split("\t");

        int row = 0;
        while ((line = bufferedReader.readLine()) != null) {
            String[] columns = line.split("\t");
            if (headerColumns.length - 1 > mainTable.getColumnCount() || headerColumns.length - 1 < mainTable.getColumnCount()
                    || columns.length - 1 > mainTable.getColumnCount() || row >= mainTable.getRowCount()) {
                bufferedReader.close();
                throw new IllegalArgumentException("File differs in size from the current table.");
            }
            headerTable.setValueAt(columns[0], row, 0);
            for (int i = 1; i < columns.length; i++) {
                mainTable.setValueAt(columns[i], row, i-1);
                CellToken curr = new CellToken(i-1, row);
                changeCellFormulaAndRecalculate(curr, columns[i]);
            }
            row++;
        }
        bufferedReader.close();

    }
}
