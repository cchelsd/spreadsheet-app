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
    public static final char Pow = '^';
    public static final char LeftParen = '(';
    public static final char RightParen = ')';

    public char myToken;

    /**
     * Creates a operator token using the input character.
     * @param theToken a character representing an operator.
     */
    public OperatorToken(char theToken) {
        myToken = theToken;
    }

    /**
     * gets the operator token.
     * @return the node that we need to get.
     */
    public char getToken() {
        return myToken;
    }

    /**
     * Converts the character to a string.
     * @return The character stored in the operator token as a string.
     */
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
                || theChar == OperatorToken.Pow
                || theChar == OperatorToken.LeftParen
                || theChar == OperatorToken.RightParen;
    }

    /**
     * Given an operator, return its priority.
     * <br>
     * priorities: <br>
     *   +, - : 0 <br>
     *   *, / : 1 <br>
     *   ^    : 2 <br>
     *   (    : 3
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
            case Minus:
                return 0;
            case Mult:
            case Div:
                return 1;
            case Pow:
                return 2;
            case LeftParen:
                return 3;
            default:
                // This case should NEVER happen
                System.out.println("Error in operatorPriority.");
                System.exit(0);
                return 0; // Only here to prevent compile error.

        }
    }

    /**
     * Return the priority of this OperatorToken.
     * <br>
     * priorities: <br>
     *   +, - : 0 <br>
     *   *, / : 1 <br>
     *   ^    : 2 <br>
     *   (    : 3 <br>
     * @return  the priority of operatorToken
     */
    int priority () {
        switch (this.myToken) {
            case Plus:
            case Minus:
                return 0;
            case Mult:
            case Div:
                return 1;
            case Pow:
                return 2;
            case LeftParen:
                return 3;
            default:
                // This case should NEVER happen
                System.out.println("Error in priority.");
                System.out.println(this.myToken);
                System.exit(0);
                return 0; // Only here to prevent compile error.
        }
    }
}
