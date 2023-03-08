/**
 * LiteralToken represents a literal integer value in a formula.
 * @author Chelsea Dacones
 * @author Makai Martines
 * @author Elias Peterson
 * @author Alexis Zakrzewski
 */
public class LiteralToken extends Token {
    private final int myValue;

    /**
     * Creates a Literal token using the input integer.
     * @param theValue the integer we want to create a literal with.
     */
    public LiteralToken(final int theValue) {
        myValue = theValue;
    }

    /**
     * Gets the value in the literal token.
     * @return the value stored in the token.
     */
    public int getValue() { return myValue; }

    /**
     * Converts the integer to a string.
     * @return The value stored in the literal token as a string.
     */
    public String toString() {
        return Integer.toString(myValue);
    }
}
