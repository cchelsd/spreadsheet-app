/**
 * CellToken stores a token representing a cell in a formula.
 * @author Chelsea Dacones
 * @author Makai Martines
 * @author Elias Peterson
 * @author Alexis Zakrzewski
 */
public class CellToken extends Token {

    /** The column this token refers to. */
    private int myColumn;
    /** The row this token refers to.m*/
    private int myRow;

    /**
     * Creates a new CellToken
     */
    public CellToken() {
    }

    /**
     * Creates a new CellToken with the row and column set
     */
    public CellToken(int theColumn, int theRow) {
        myColumn = theColumn;
        myRow = theRow;
    }

    /**
     * Gets the column the cell refers to.
     * @return An integer representing the column.
     */
    public int getColumn() {
        return myColumn;
    }

    /**
     * Sets the column the cell refers to.
     * @param theColumn the column number the cell refers to.
     */
    public void setColumn(final int theColumn) { myColumn = theColumn; }

    /**
     * Gets the row the cell refers to.
     * @return An integer representing the column.
     */
    public int getRow() {
        return myRow;
    }

    /**
     * Sets the row the cell refers to.
     * @param theRow the row number the cell refers to.
     */
    public void setRow(final int theRow) { myRow = theRow; }

    @Override
    public String toString() {
        char ch;
        StringBuilder returnString = new StringBuilder();
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
            returnString.append(ch);
            col = col % largest;
            largest = largest  / 26;
            number_of_digits--;
        }

        // handle last digit
        ch = (char)(col + 'A');
        returnString.append(ch);

        // append the row as an integer
        returnString.append(myRow);

        return returnString.toString();
    }

    @Override
    public boolean equals(Object theOther) {
        if(theOther instanceof CellToken) {
            CellToken otherToken = (CellToken) theOther;
            return otherToken.getColumn() == myColumn && otherToken.getRow() == myRow;
        }
        else return false;
    }
}
