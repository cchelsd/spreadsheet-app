public class ExpressionTreeNode {
    private Token token;

    private ExpressionTreeNode left;
    private ExpressionTreeNode right;

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