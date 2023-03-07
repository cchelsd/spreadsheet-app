import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;

public class GUI extends JFrame {
    private final JTable myTable;
    private JTable myRowHeader;
    private Spreadsheet mySheet;
    private final JScrollPane myScrollPane;
    private JTextField myInputBar;
    private final JTableHeader myHeader;

    /**
     * A constructor for the GUI that creates a new spreadsheet and initializes
     * GUI components.
     */
    public GUI() {
        createSheet();
        TableModel myModel = new DefaultTableModel(mySheet.getNumRows(), mySheet.getNumColumns());
        myTable = new JTable(myModel);
        myScrollPane = new JScrollPane(myTable);
        myHeader = myTable.getTableHeader();
    }

    /**
     * Runs the program.
     * @param args
     */
    public static void main(String[] args) {
        new GUI().start();
    }

    /**
     * Prompts the user to input the desired number of rows and columns for their spreadsheet.
     * Then creates the table for the spreadsheet.
     */
    public void createSheet() {
        boolean validInput = false;
        while (!validInput) {
            JTextField rowInput = new JTextField();
            JTextField colInput = new JTextField();
            int input = JOptionPane.showConfirmDialog(this, new Object[]{"Number of Rows:", rowInput,
                    "Number of Columns:", colInput}, "Enter spreadsheet size", JOptionPane.OK_CANCEL_OPTION);
            if (input == JOptionPane.OK_OPTION) {
                try {
                    int numRows = Integer.parseInt(rowInput.getText().trim());
                    int numCols = Integer.parseInt(colInput.getText().trim());
                    if (numRows > 0 && numCols > 0) {
                        mySheet = new Spreadsheet(numRows, numCols);
                        validInput = true;
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid input. Please enter positive integers.");
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid input. Please enter positive integers.");
                }
            } else {
                System.exit(0);
            }
        }
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
                return Integer.toString(rowIndex);
            }
        });
        JViewport rowHeaderViewport = new JViewport();
        rowHeaderViewport.setView(myRowHeader);
        myRowHeader.setPreferredScrollableViewportSize(new Dimension(30,0));
        myRowHeader.setGridColor(new Color(212, 212, 212));
        myRowHeader.setOpaque(true);
        myRowHeader.setBackground(new Color(248, 248, 248));
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
            try {
                mySheet.changeCellFormulaAndRecalculate(cellToken, formula);
            } catch (ArrayIndexOutOfBoundsException a) {
                JOptionPane.showMessageDialog(this, "Please select a cell.");
            } catch (IllegalArgumentException illegalArgumentException) {
                JOptionPane.showMessageDialog(this, "There are one or more circular\n" +
                        "references where a formula\n" +
                        "refers to its own cell either\n" +
                        "directly or indirectly.", "Cycle Detected", JOptionPane.ERROR_MESSAGE);
            }
            updateAllCells();
        });
        add(myInputBar, BorderLayout.NORTH);
    }

    public void updateAllCells() {
        CellToken cellToken = new CellToken();
        for(int x = 0; x < mySheet.getNumColumns(); x++) {
            for(int y = 0; y < mySheet.getNumRows(); y++) {
                cellToken.setRow(y);
                cellToken.setColumn(x);
                // Only print a value in this cell if it actually has a formula in it.
                if(mySheet.getCell(cellToken).getFormula().compareTo("") != 0) {
                    myTable.setValueAt(mySheet.getCell(cellToken).evaluate(mySheet), y, x);
                }
            }
        }
    }

    public void setUpComponents() {
        myTable.setGridColor(new Color(212, 212, 212));
        myTable.setShowGrid(true);
        myTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        myTable.setCellSelectionEnabled(true);
        myTable.getModel().addTableModelListener(new SpreadsheetListener());
        myTable.setDefaultEditor(Object.class, null);
        myTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                myRowHeader.clearSelection();
                myInputBar.requestFocus(); // sets focus to text field
                int row = myTable.getSelectedRow();
                int col = myTable.getSelectedColumn();
                CellToken curr = new CellToken(col, row);
                Cell cell = mySheet.getCell(curr);
                myInputBar.setText(cell.getFormula());
                //sets cursor position after setting text to prevent highlighting of text in input bar
//                myInputBar.selectAll();
//                SwingUtilities.invokeLater(() -> myInputBar.setCaretPosition(myInputBar.getText().length()));
            }
        });
        myHeader.setBackground(new Color(250, 250, 250));
        createInputBar();
        int height = myTable.getRowHeight() * myTable.getRowCount();
        int width = myTable.getColumnModel().getTotalColumnWidth();
        myScrollPane.setPreferredSize(new Dimension(width, height + 35));
        createRowHeader();
        add(myScrollPane);
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
