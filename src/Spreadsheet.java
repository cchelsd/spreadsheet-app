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
    private Cell[][] cells;

    public Spreadsheet(final int sheetSize) {
        cells = new Cell[sheetSize][sheetSize];
        for (int x = 0; x < sheetSize; x++) {
            for (int y = 0; y < sheetSize; y++) {
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
     * Changes the given cellToken's Stack to the Stack provided, then recalculates its formula.
     * @param cellToken The CellToken to change.
     * @param expTreeTokenStack The new formula this CellToken should have.
     */
    public void changeCellFormulaAndRecalculate(final CellToken cellToken, final Stack<Token> expTreeTokenStack) {
        cells[cellToken.getRow()][cellToken.getColumn()].buildExpressionTree(expTreeTokenStack);

        Stack<CellToken> processStack = new Stack<>(); // Stores which cells to process, and in what order.
        ConcurrentHashMap<CellToken, List<CellToken>> cellDependencies = new ConcurrentHashMap<>(); // All dependencies of a given cell.

        for(int y = 0; y < getNumRows(); y++) {
            for(int x = 0; x < getNumColumns(); x++) {
                CellToken cToken = new CellToken();
                cToken.setRow(y);
                cToken.setColumn(x);
                cellDependencies.put(cToken, getCell(cToken).getDependencies());
            }
        }

        while(!cellDependencies.isEmpty()) {
            boolean removedValue = false;
            for (CellToken t : cellDependencies.keySet()) {
                // If this cell has an empty list of dependencies, remove it from the hashtable.
                if (cellDependencies.get(t).isEmpty()) {
                    removedValue = true;
                    cellDependencies.remove(t);
                    processStack.push(t);
                    for (CellToken u : cellDependencies.keySet()) {
                        // If this cell has a dependent, it's removed. if not, then it won't.
                        cellDependencies.get(u).remove(t);
                    }
                }
            }
            if (!removedValue) {
                System.out.println("Cycle found");
                System.exit(-1);
            }

        }

        // Now iterate through the stack of cells.
        while(!processStack.isEmpty()) {
            CellToken cToken = processStack.pop();
            getCell(cToken).evaluate(this);
        }
    }


    public int evaluateCell(final CellToken theCellToken) {
        return cells[theCellToken.getRow()][theCellToken.getColumn()].evaluate(this);
    }

    public Cell getCell(final int theRow, final int theColumn) {
        return cells[theRow][theColumn];
    }

    public Cell getCell(final CellToken theToken) { return getCell(theToken.getRow(), theToken.getColumn()); }

    /**
     * getCellToken
     *
     * Assuming that the next chars in a String (at the given startIndex)
     * is a cell reference, set cellToken's column and row to the
     * cell's column and row.
     * If the cell reference is invalid, the row and column of the return CellToken
     * are both set to BadCell (which should be a final int that equals -1).
     * Also, return the index of the position in the string after processing
     * the cell reference.
     * (Possible improvement: instead of returning a CellToken with row and
     * column equal to BadCell, throw an exception that indicates a parsing error.)
     *
     * A cell reference is defined to be a sequence of CAPITAL letters,
     * followed by a sequence of digits (0-9).  The letters refer to
     * columns as follows: A = 0, B = 1, C = 2, ..., Z = 25, AA = 26,
     * AB = 27, ..., AZ = 51, BA = 52, ..., ZA = 676, ..., ZZ = 701,
     * AAA = 702.  The digits represent the row number.
     *
     * @param inputString  the input string
     * @param startIndex  the index of the first char to process
     * @param cellToken  a cellToken (essentially a return value)
     * @return  index corresponding to the position in the string just after the cell reference
     */
    int getCellToken (String inputString, int startIndex, CellToken cellToken) {
        char ch;
        int column = 0;
        int row = 0;
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
     *
     * Given a string that represents a formula that is an infix
     * expression, return a stack of Tokens so that the expression,
     * when read from the bottom of the stack to the top of the stack,
     * is a postfix expression.
     *
     * A formula is defined as a sequence of tokens that represents
     * a legal infix expression.
     *
     * A token can consist of a numeric literal, a cell reference, or an
     * operator (+, -, *, /).
     *
     * Multiplication (*) and division (/) have higher precedence than
     * addition (+) and subtraction (-).  Among operations within the same
     * level of precedence, grouping is from left to right.
     *
     * This algorithm follows the algorithm described in Weiss, pages 105-108.
     */
    Stack getFormula(String formula) {
        Stack returnStack = new Stack<Token>();  // stack of Tokens (representing a postfix expression)
        boolean error = false;
        char ch = ' ';

        int literalValue = 0;

        CellToken cellToken = new CellToken();
        int column = 0;
        int row = 0;

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
                        break;
                    case OperatorToken.RightParen:
                        stackOperator = (OperatorToken) operatorStack.pop();
                        // This code does not handle operatorStack underflow.
                        while (stackOperator.getToken() != OperatorToken.LeftParen) {
                            // pop operators off the stack until a LeftParen appears and
                            // place the operators on the output stack
                            returnStack.push(stackOperator);
                            stackOperator = (OperatorToken) operatorStack.pop();
                        }

                        index++;
                        break;
                    default:
                        // This case should NEVER happen
                        System.out.println("Error in getFormula.");
                        System.exit(0);
                        break;
                }
                // push the operator on the operator stack
                operatorStack.push(new OperatorToken(ch));

                index++;

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
        String returnString = "";
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
            returnString += ch;
            col = col % largest;
            largest = largest  / 26;
            number_of_digits--;
        }

        // handle last digit
        ch = (char)(col + 'A');
        returnString += ch;

        // append the row as an integer
        returnString += cellToken.getRow();

        return returnString;
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
        cells[cellToken.getRow()][cellToken.getColumn()].printExpressionTree();
    }

    /**
     * Prints out ALL formulas inside this spreadsheet.
     */
    public void printAllFormulas() {
    }
}
