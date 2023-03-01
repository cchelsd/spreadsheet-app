/**
 * LiteralToken represents a literal integer value in a formula.
 * @author Chelsea Dacones
 * @author Makai Martines
 * @author Elias Peterson
 * @author Alexis Zakrzewski
 */
public class LiteralToken extends Token {
    private int myValue;

    public LiteralToken(final int theValue) {
        myValue = theValue;
    }

    public int getValue() { return myValue; }

    public String toString() {
        return Integer.toString(myValue);
    }
}
