import java.util.Stack;

public class Cell {
    private String myFormula;
    private int myValue;
    // the expression tree below represents the formula.
    private final ExpressionTree myExpressionTree;

    public Cell() {
        myExpressionTree = new ExpressionTree();
        myFormula = "";
        myValue = 0;
    }

    public int evaluate (final Spreadsheet theSpreadsheet) {
        return myExpressionTree.evaluate(myExpressionTree.getRoot(), theSpreadsheet);
    }

    // Build an expression tree from a stack of ExpressionTreeTokens
    public void buildExpressionTree (Stack<Token> s) {
        myExpressionTree.setRoot(getExpressionTree(s));
        if (!s.isEmpty()) {
            System.out.println("Error in BuildExpressionTree.");
        }

        // // These are for testing if the expression tree's print function works correctly. Uncomment to test.
        //myExpressionTree.printTree();
        //System.out.println();

    }

    /**
     * Returns an expression tree from a given stack of Tokens.
     * Runs recursively to create the entire tree.
     * @param s The Stack of Tokens to build from
     * @return An ExpressionTreeNode containing subtrees of the rest of the stack.
     */
    private ExpressionTreeNode getExpressionTree(Stack<Token> s) {
        ExpressionTreeNode returnTree = null;
        Token token;

        if (s.isEmpty())
            return null;

        token = s.pop();  // need to handle stack underflow
        if ((token instanceof LiteralToken) ||
                (token instanceof CellToken) ) {

            // Literals and Cells are leaves in the expression tree
            returnTree = new ExpressionTreeNode(token, null, null);

        } else if (token instanceof OperatorToken) {
            // Continue finding tokens that will form the
            // right subtree and left subtree.
            ExpressionTreeNode rightSubtree = getExpressionTree (s);
            ExpressionTreeNode leftSubtree  = getExpressionTree (s);
            returnTree =
                    new ExpressionTreeNode(token, leftSubtree, rightSubtree);
        }

        return returnTree;
    }

    public void printExpressionTree() {
        myExpressionTree.printTree();
    }

}