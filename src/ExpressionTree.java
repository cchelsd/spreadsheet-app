public class ExpressionTree {
    private ExpressionTreeNode root;

    /**
     * Prints out all the nodes inside this Expression Tree.
     */
    public void printTree() {
        printTreeNode(root);
    }

    private void printTreeNode(final ExpressionTreeNode theNode) {
        if(theNode == null) return;

        printTreeNode(theNode.getLeft());
        theNode.printToken();
        printTreeNode(theNode.getRight());
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
        if (nodeToken instanceof OperatorToken) {
            int leftValue = evaluate(theNode.getLeft(), theSpreadsheet);
            int rightValue = evaluate(theNode.getRight(), theSpreadsheet);
            switch(((OperatorToken)nodeToken).getToken()) {
                case OperatorToken.Plus:
                    return leftValue + rightValue;
                case OperatorToken.Minus:
                    return leftValue - rightValue;
                case OperatorToken.Mult:
                    return leftValue * rightValue;
                case OperatorToken.Div:
                    return leftValue / rightValue;
            }
        }
        if(nodeToken instanceof LiteralToken) {
            return ((LiteralToken)theNode.getToken()).getValue();
        }
        if(nodeToken instanceof CellToken) {
            CellToken cellToken = (CellToken)nodeToken;
            return theSpreadsheet.evaluateCell(cellToken);
        }

        return 0;
    }

    public ExpressionTreeNode getRoot() { return root; }

    public void setRoot(final ExpressionTreeNode theRoot) { root = theRoot; }
}
