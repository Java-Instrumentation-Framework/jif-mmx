package edu.mbl.jif.ps.orient;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author GBH
 */
public class ResultsTable {
   
   public static void dump(String label, double[][] array) {
      System.out.println(label + "\n" + Arrays.deepToString(array));
   }
   
   public static void displayArrayAsTableFrame(String title, Object[][] array) {
      int numCols = array[0].length + 1;
      int numRows = array.length;
      Object rows[][] = new Object[numRows][numCols];
      for (int i = 0; i < numRows; i++) {
         rows[i][0] = i;
         for (int j = 1; j < numCols; j++) {
            rows[i][j] = array[i][j - 1];
         }
      }
      Object columns[] = new Object[numCols];
      columns[0] = "";
      for (int i = 1; i < numCols; i++) {
         columns[i] = "" + i;
      }
      JFrame f = new JFrame(title + " [" +(numCols-1) + " x " + numRows +"]");
      f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      Container content = f.getContentPane();
      JTable table = new JTable(rows, columns);
      TableCellRenderer renderer = table.getDefaultRenderer(Object.class);
      table.setDefaultRenderer(Object.class, new CustomTableCellRenderer(renderer));
      //CustomTableCellRenderer mr = new CustomTableCellRenderer(renderer);
      //table.getColumnModel().getColumn(0).setCellRenderer(renderer);
//      table.getColumnModel().getColumn(1).setCellRenderer(mr);
//      table.getColumnModel().getColumn(2).setCellRenderer(mr);
      JScrollPane scrollPane = new JScrollPane(table);
      content.add(scrollPane, BorderLayout.CENTER);
      f.setSize(20 + numCols*50, 40+numRows*20);
      f.setVisible(true);
   }



   static public class CustomTableCellRenderer implements TableCellRenderer {

      private TableCellRenderer delegate;

      public CustomTableCellRenderer(TableCellRenderer defaultRenderer) {
         this.delegate = defaultRenderer;
      }
      //Color[] color = {Color.BLUE, Color.WHITE, Color.RED};

      @Override
      public Component getTableCellRendererComponent(JTable table, Object value,
              boolean isSelected, boolean hasFocus, int row, int column) {
         Component c = delegate.getTableCellRendererComponent(table, value, isSelected,
                 hasFocus, row, column);
         if (column < 1) {
            c.setBackground(UIManager.getColor("TableHeader.background"));
         } else {
            c.setBackground(table.getBackground());
         }
         return c;
      }
   }
   
   public static void main(String[] args) {
      int n = 20;
      int m = 40;
      Integer[][] array = new Integer[n][m];
      for (int i = 0; i < n; i++) {
         for (int j = 0; j < m; j++) {
            array[i][j] = i*j;
         }
         
      }
      displayArrayAsTableFrame("Test", array);
              
      
   }
}
