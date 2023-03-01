/**
 * OperatorToken stores an operator in a formula, along with methods for calculating its priority.
 * @author Chelsea Dacones
 * @author Makai Martines
 * @author Elias Peterson
 * @author Alexis Zakrzewski
 */
public class OperatorToken extends Token {
    public static final char Plus = '+';
    public static final char Minus = '-';
    public static final char Mult = '*';
    public static final char Div = '/';
    public static final char LeftParen = '(';
    public static final char RightParen = ')';

    public char myToken;

    public OperatorToken(char theToken) {
        myToken = theToken;
    }

    public char getToken() {
        return myToken;
    }

    public String toString() {
        return Character.toString(myToken);
    }

    /**
     * Returns whether the provided character is a valid operator token
     * @param theChar The Character to check.
     * @return True if the character is an operator token, false if otherwise.
     */
    public static boolean isOperator(final Character theChar) {
        return theChar == OperatorToken.Plus
                || theChar == OperatorToken.Minus
                || theChar == OperatorToken.Mult
                || theChar == OperatorToken.Div
                || theChar == OperatorToken.LeftParen
                || theChar == OperatorToken.RightParen;
    }

    /**
     * Given an operator, return its priority.
     *
     * priorities:
     *   +, - : 0
     *   *, / : 1
     *   (    : 2
     *
     * @param ch  a char
     * @return  the priority of the operator
     */
    static int operatorPriority (char ch) {
        if (!isOperator(ch)) {
            // This case should NEVER happen
            System.out.println("Error in operatorPriority.");
            System.exit(0);
        }
        switch (ch) {
            case Plus:
                return 0;
            case Minus:
                return 0;
            case Mult:
                return 1;
            case Div:
                return 1;
            case LeftParen:
                return 2;

            default:
                // This case should NEVER happen
                System.out.println("Error in operatorPriority.");
                System.exit(0);
                return 0; // Only here to prevent compile error.
        }
    }

    /*
     * Return the priority of this OperatorToken.
     *
     * priorities:
     *   +, - : 0
     *   *, / : 1
     *   (    : 2
     *
     * @return  the priority of operatorToken
     */
    int priority () {
        switch (this.myToken) {
            case Plus:
                return 0;
            case Minus:
                return 0;
            case Mult:
                return 1;
            case Div:
                return 1;
            case LeftParen:
                return 2;

            default:
                // This case should NEVER happen
                System.out.println("Error in priority.");
                System.exit(0);
                return 0; // Only here to prevent compile error.
        }
    }


}
