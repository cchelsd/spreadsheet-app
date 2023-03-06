import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Cell represents an individual Cell in the spreadsheet. It contains a formula and
 * can be evaluated to return its result.
 * @author Chelsea Dacones
 * @author Makai Martines
 * @author Elias Peterson
 * @author Alexis Zakrzewski
 */
public class Cell {
    /**
     * The formula inside this cell. Printed when the cell is selected in the GUI.
     */
    private String myFormula;

    /**
     * The ExpressionTree of this cell, representing its formula for evaluation.
     */
    private final ExpressionTree myExpressionTree;

    /**
     * Constructs a new Cell object and initializes its values.
     */
    public Cell() {
        myExpressionTree = new ExpressionTree();
        myFormula = "";
    }

    /**
     * Evaluates this Cell's ExpressionTree and returns the result.
     * @param theSpreadsheet The Spreadsheet this Cell is in.
     * @return The result of this Cell's formula.
     */
    public int evaluate (final Spreadsheet theSpreadsheet) {
        return myExpressionTree.evaluate(myExpressionTree.getRoot(), theSpreadsheet);
    }

    /**
     * @return The formula of this cell as a String.
     */
    public String getFormula() {
        return myFormula;
    }

    /**
     * Sets the String representation of this Cell's formula.
     * @param theFormula The String to store.
     */
    public void setFormula(final String theFormula) {
        myFormula = theFormula;
    }

    /**
     * Build this cell's ExpressionTree with a provided Stack of Tokens.
     * @param theStack The Stack of Tokens to process.
     */
    public void buildExpressionTree (final Stack<Token> theStack) {
        myExpressionTree.buildExpressionTree(theStack);
        //myExpressionTree.printTree();
        System.out.println();
    }

    /**
     * Finds all cells that this cell depends on and returns them as a list of CellTokens.
     * @return A List of CellTokens that this Cell depends on in its formula.
     */
    public List<CellToken> getDependencies() {
        List<CellToken> dependencies = new ArrayList<>();
        myExpressionTree.findDependencies(myExpressionTree.getRoot(), dependencies);
        return dependencies;
    }

}