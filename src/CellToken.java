/**
 * CellToken stores a token representing a cell in a formula.
 * @author Chelsea Dacones
 * @author Makai Martines
 * @author Elias Peterson
 * @author Alexis Zakrzewski
 */
public class CellToken extends Token {

    private int myColumn;
    private int myRow;

    /**
     * Creates a new CellToken
     */
    public CellToken() {

    }

    public int getColumn() {
        return myColumn;
    }
    public void setColumn(final int theColumn) { myColumn = theColumn; }

    public int getRow() {
        return myRow;
    }
    public void setRow(final int theRow) { myRow = theRow; }

    @Override
    public String toString() {
        char ch;
        String returnString = "";
        int col;
        int largest = 26;  // minimum col number with number_of_digits digits
        int number_of_digits = 2;

        col = myColumn;

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
        returnString += myRow;

        return returnString;
    }

    @Override
    public boolean equals(Object theOther) {
        if(theOther instanceof CellToken) {
            CellToken otherToken = (CellToken)theOther;
            return otherToken.getColumn() == myColumn && otherToken.getRow() == myRow;
        }
        else return false;
    }
}
