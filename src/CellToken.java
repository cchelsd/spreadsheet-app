/**
 * CellToken stores a token representing a cell in a formula.
 * @author Chelsea Dacones
 * @author Makai Martines
 * @author Elias Peterson
 * @author Alexis Zakrzewski
 */
public class CellToken {

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
}
