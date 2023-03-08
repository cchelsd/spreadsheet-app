import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The graphical user interface for a spreadsheet application.
 * @author Chelsea Dacones
 * @author Makai Martines
 * @author Elias Peterson
 * @author Alexis Zakrzewski
 */
public class GUI extends JFrame {
    private final JTable myTable;
    private JTable myRowHeader;
    private Spreadsheet mySheet;
    private final JScrollPane myScrollPane;
    private JTextField myInputBar;
    private final JTableHeader myHeader;
    private final DefaultTableCellRenderer headerRenderer;

    private JMenuBar myMenuBar;

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
        headerRenderer = (DefaultTableCellRenderer) myTable.getTableHeader().getDefaultRenderer();
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
                    int rows = Integer.parseInt(rowInput.getText().trim());
                    int columns = Integer.parseInt(colInput.getText().trim());
                    if (rows > 0 && columns > 0) {
                        mySheet = new Spreadsheet(rows, columns);
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
        myRowHeader = new JTable(new DefaultTableModel(mySheet.getNumRows(), 1) {
            @Override
            public Object getValueAt(int row, int col) {
                return Integer.toString(row);
            }
        });
        myRowHeader.setPreferredScrollableViewportSize(new Dimension(30,0));
        myRowHeader.setGridColor(new Color(212, 212, 212));
        myRowHeader.setOpaque(true);
        myRowHeader.setBackground(new Color(248, 248, 248));
        myScrollPane.setRowHeaderView(myRowHeader);
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

    public void createMenu() {
        myMenuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenu edit = new JMenu("Edit");
        JMenuItem clear  = new JMenuItem("Clear");
        clear.addActionListener(e -> {
            for (int row = 0; row < myTable.getRowCount(); row++) {
                for (int col = 0; col < myTable.getColumnCount(); col++) {
                    CellToken cellToken = new CellToken(col, row);
                    mySheet.getCell(cellToken).setFormula("");
                }
            }
            updateAllCells();
        });

        JMenuItem newSpreadsheet = new JMenuItem("New");
        newSpreadsheet.addActionListener(e -> new GUI().start());

        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));
            int option = fileChooser.showSaveDialog(GUI.this);
            if (option == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getPath();
                try {
                    mySheet.saveToFile(filePath, myTable, myRowHeader);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        JMenuItem open = new JMenuItem("Open...");
        open.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));
            int option = fileChooser.showOpenDialog(GUI.this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file1 = fileChooser.getSelectedFile();
                try {
                    mySheet.readFromFile(file1.getAbsolutePath(), myTable, myRowHeader);
                    updateAllCells();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage() + "\nPlease create a new spreadsheet.");
                }
            }
        });
        file.add(newSpreadsheet);
        file.add(open);
        file.add(save);
        edit.add(clear);
        myMenuBar.add(file);
        myMenuBar.add(edit);
    }

    public void updateAllCells() {
        for(int x = 0; x < mySheet.getNumColumns(); x++) {
            for(int y = 0; y < mySheet.getNumRows(); y++) {
                CellToken cellToken = new CellToken(x, y);
                // Only print a value in this cell if it actually has a formula in it.
                if(mySheet.getCell(cellToken).getFormula().compareTo("") != 0) {
                    myTable.setValueAt(mySheet.getCell(cellToken).evaluate(mySheet), y, x);
                } else {
                    myTable.setValueAt("", y, x);
                }
            }
        }
    }

    /**
     * Sets up the components in this frame.
     */
    public void setUpComponents() {
        myTable.setGridColor(new Color(212, 212, 212));
        myTable.setShowGrid(true);
        myTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        myTable.setCellSelectionEnabled(true);
        myTable.setDefaultEditor(Object.class, null); // make cells uneditable
        headerRenderer.setHorizontalAlignment(JLabel.CENTER); // center text in column header
        myTable.getTableHeader().setDefaultRenderer(headerRenderer);

        // display the cell formula in the input bar upon selection of the cell
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
                SwingUtilities.invokeLater(() -> myInputBar.setCaretPosition(myInputBar.getText().length()));
            }
        });
        myHeader.setBackground(new Color(250, 250, 250));
        int height = myTable.getRowHeight() * myTable.getRowCount();
        int width = myTable.getColumnModel().getTotalColumnWidth();
        myScrollPane.setPreferredSize(new Dimension(width, height + 35));
        createInputBar();
        createRowHeader();
        createMenu();
        setJMenuBar(myMenuBar);
        add(myScrollPane);
    }

    /**
     * Performs all tasks necessary to display the UI.
     */
    public void start() {
        setUpComponents();
        pack();
        setResizable(true);
        setTitle("TCSS 342 - Spreadsheet");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
