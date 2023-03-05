import java.util.List;
import java.util.Stack;

/**
 * ExpressionTree holds a collection of ExpressionTreeNodes that
 * collectively represent the formula inside a Cell.
 * @author Chelsea Dacones
 * @author Makai Martines
 * @author Elias Peterson
 * @author Alexis Zakrzewski
 */
public class ExpressionTree {
    /**
     * The root node of this tree.
     */
    private ExpressionTreeNode root;

    public void buildExpressionTree(Stack<Token> theTokens) {
        root = getExpressionTree(theTokens);
        if (!theTokens.isEmpty()) {
            System.out.println("Error in BuildExpressionTree.");
        }
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

    /**
     * Prints out all the nodes inside this Expression Tree.
     */
    public void printTree() {
        printTreeNode(root);
    }

    /**
     * Debug method to recursively print out this tree.
     * @param theNode The node to print.
     */
    private void printTreeNode(final ExpressionTreeNode theNode) {
        // If this node is null, exit now.
        if(theNode == null) return;

        // Prints in infix notation.
        printTreeNode(theNode.getLeft());
        theNode.printToken();
        printTreeNode(theNode.getRight());
    }

    /**
     * Recursively finds all cells that this expression tree depends on, and returns them in a List.
     * @param theNode The node to check.
     * @param theList The list to insert values into.
     */
    public void findDependencies(final ExpressionTreeNode theNode, final List<CellToken> theList) {
        // If this node is null, just exit now.
        if(theNode == null) return;
        // If this node is a CellToken, then add it to the list.
        if(theNode.getToken() instanceof CellToken) theList.add((CellToken)theNode.getToken());
        // Recursively follow left and right children.
        findDependencies(theNode.getLeft(), theList);
        findDependencies(theNode.getRight(), theList);
    }

    /**
     * Evaluates this tree based on the Spreadsheet.
     * @param theNode The node to evaluate.
     * @param theSpreadsheet The spreadsheet to evaluate with.
     * @return An integer representing the final value of this tree's expression.
     */
    public int evaluate(final ExpressionTreeNode theNode, final Spreadsheet theSpreadsheet) {
        // If this node is null, just exit early.
        if(theNode == null) return 0;

        Token nodeToken = theNode.getToken();

        // If this is an operator token, then process it's left and right subtrees before processing itself.
        if (nodeToken instanceof OperatorToken operatorToken) {
            int leftValue = evaluate(theNode.getLeft(), theSpreadsheet);
            int rightValue = evaluate(theNode.getRight(), theSpreadsheet);
            switch (operatorToken.getToken()) {
                case OperatorToken.Plus -> {
                    return leftValue + rightValue;
                }
                case OperatorToken.Minus -> {
                    return leftValue - rightValue;
                }
                case OperatorToken.Mult -> {
                    return leftValue * rightValue;
                }
                case OperatorToken.Div -> {
                    return leftValue / rightValue;
                }
                case OperatorToken.Pow -> {
                    return (int)Math.pow(leftValue, rightValue);
                }
                case OperatorToken.LeftParen -> {
                    return rightValue;
                }
            }
        }
        // If the token is a literal, just return the literal's value
        else if (nodeToken instanceof LiteralToken literalToken) {
            return literalToken.getValue();
        }
        // If the token is a cellToken, return the value in that cell.
        else if (nodeToken instanceof CellToken cellToken) {
            return theSpreadsheet.evaluateCell(cellToken);
        }

        // We should never get here. Return 0.
        return 0;
    }

    /**
     * @return The root node of this expression tree.
     */
    public ExpressionTreeNode getRoot() { return root; }

    /**
     * ExpressionTreeNode is an internal class for ExpressionTree that represents
     * individual nodes in the tree.
     */
    private static class ExpressionTreeNode {
        private final Token token;

        private final ExpressionTreeNode left;
        private final ExpressionTreeNode right;

        public ExpressionTreeNode(final Token theToken, final ExpressionTreeNode theLeft, final ExpressionTreeNode theRight) {
            token = theToken;
            left = theLeft;
            right = theRight;
        }

        public void printToken() {
            System.out.print(token.toString());
        }

        public Token getToken() { return token; }

        public ExpressionTreeNode getLeft() { return left; }

        public ExpressionTreeNode getRight() { return right; }
    }
}
