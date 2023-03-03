import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GUI extends JFrame {
    private final JTable myTable;
    private JTable myRowHeader;
    private final TableModel myModel;
    private final Spreadsheet mySheet;
    private final JScrollPane myScrollPane;
    private JTextField myInputBar;
    private final JTableHeader myHeader;
    private Object[][] data;
    private int activeRow, activeCol;
    public GUI() {
        mySheet = new Spreadsheet(20);
        data = new Object[mySheet.getNumRows()][mySheet.getNumColumns()];
        myModel = new DefaultTableModel(mySheet.getNumRows(), mySheet.getNumColumns());
        myTable = new JTable(myModel);
        myScrollPane = new JScrollPane(myTable);
        myHeader = myTable.getTableHeader();
    }

    public static void main(String[] args) {
        new GUI().start();
    }

    public void createRowHeader() {
        myRowHeader = new JTable(new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return myTable.getRowCount();
            }
            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return Integer.toString(rowIndex + 1);
            }
        });
        JViewport rowHeaderViewport = new JViewport();
        rowHeaderViewport.setView(myRowHeader);
        myRowHeader.setPreferredScrollableViewportSize(new Dimension(30,0));
        myScrollPane.setRowHeader(rowHeaderViewport);
    }
    public void start() {
        setUpComponents();
        pack();
        setResizable(true);
        setTitle("TCSS 342 - Spreadsheet");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void createInputBar() {
        myInputBar = new JTextField();
        myInputBar.addActionListener(e -> {
            String formula = myInputBar.getText();
            int row = myTable.getSelectedRow();
            int col = myTable.getSelectedColumn();
            CellToken cellToken = new CellToken();
            cellToken.setRow(row);
            cellToken.setColumn(col);
            mySheet.changeCellFormulaAndRecalculate(cellToken, formula);
            updateAllCells();
            //myTable.setValueAt(formula, row, col);
        });
        add(myInputBar, BorderLayout.NORTH);
    }

    public void updateAllCells() {
        for(int x = 0; x < mySheet.getNumColumns(); x++) {
            for(int y = 0; y < mySheet.getNumRows(); y++) {
                CellToken cellToken = new CellToken();
                cellToken.setRow(y);
                cellToken.setColumn(x);
                myTable.setValueAt(mySheet.getCell(cellToken).evaluate(mySheet), y, x);
            }
        }
    }

    public void setBarFormula() {
        myTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = myTable.getSelectedRow();
                int col = myTable.getSelectedColumn();

            }
            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    public void setUpComponents() {
        myTable.setGridColor(Color.BLACK);
        myTable.setShowGrid(true);
        myTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        myTable.setCellSelectionEnabled(true);
        myTable.getModel().addTableModelListener(new SpreadsheetListener());
        myHeader.setBackground(Color.LIGHT_GRAY);
        createRowHeader();
        add(myScrollPane);
        createInputBar();
    }

    public class SpreadsheetListener implements TableModelListener {
        public SpreadsheetListener() {
            myTable.getModel().addTableModelListener(this);
        }
        @Override
        public void tableChanged(TableModelEvent e) {
        }
    }
}
