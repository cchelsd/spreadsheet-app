import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;

public class GUI extends JFrame {
    private final JTable myTable;
    private final TableModel model;
    private final Spreadsheet mySheet;
    private final JScrollPane myScrollPane;

    public static void main(String[] args) {
        GUI gui = new GUI();
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setTitle("Spreadsheet");
        gui.setLocationRelativeTo(null);
        gui.setVisible(true);
    }
    public GUI() {
        mySheet = new Spreadsheet(4);
        model = new DefaultTableModel(mySheet.getNumRows(), mySheet.getNumColumns());
        myTable = new JTable(model);
        myTable.setGridColor(Color.BLACK);
        myTable.setShowGrid(true);
        myScrollPane = new JScrollPane(myTable);
        add(myScrollPane);
        pack();
    }
}
