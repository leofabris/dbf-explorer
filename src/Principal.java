import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.Arrays;

public class Principal {
    private JTable table1;
    private JPanel panel1;

    private String fileDir;

    public Principal(String fileDir) {
        this.fileDir = fileDir;
        initComponent();
    }

    private void initComponent() {
        String[] columnNames;
        Object[][] tableData;

        DBFReader reader = null;
        try {
            reader = new DBFReader(new FileInputStream(fileDir));
            int cols = reader.getFieldCount();
            System.out.println("Cols: " + cols);
            columnNames = new String[cols];
            for (int i = 0; i < cols; i++) {
                columnNames[i] = i + " -> " + reader.getField(i).getName();
            }

            Object[] rowCount;
            int rows = 0;
            while ((rowCount = reader.nextRecord()) != null) {
                rows++;
            }
            System.out.println("Rows: " + rows);

            tableData = new String[rows][cols];

            Object[] rowObjects;

            DBFUtils.close(reader);

            reader = new DBFReader(new FileInputStream(fileDir));

            int cont = 0;
            while ((rowObjects = reader.nextRecord()) != null) {
                for (int i = 0; i < rowObjects.length; i++) {
                    if (rowObjects[i] instanceof BigDecimal) {
                        tableData[cont][i] = ((BigDecimal) rowObjects[i]).toString();
                    } else {
                        tableData[cont][i] = rowObjects[i];
                    }
                }
                cont++;
            }
            DefaultTableModel model = new DefaultTableModel(tableData, columnNames);
            table1.setModel(model);

        } catch (DBFException e) {
            throw new DBFException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            DBFUtils.close(reader);
        }
    }

    public static void main(String[] args) {

        if(args.length == 0) {
            System.out.println("Java DBFExplorer\n" +
                    "Usage:\n" +
                    "java -jar DBFExplorer.jar [path to dbf file]");
            System.exit(0);
        }

        JFrame frame = new JFrame();
        frame.setContentPane(new Principal(args[0]).panel1);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setVisible(true);
    }

}
