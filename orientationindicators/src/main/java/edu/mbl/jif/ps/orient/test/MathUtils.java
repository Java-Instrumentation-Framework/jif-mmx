package edu.mbl.jif.ps.orient.test;

import ij.ImagePlus;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author GBH
 */
public class MathUtils {

   private static boolean showArrays = true;

   public static void setShowArrays(boolean showArrays_) {
      showArrays = showArrays_;
   }

   public static void dump(String label, double[][] array) {
      System.out.println(label + "\n" + Arrays.deepToString(array));
   }

   // DisplayArrayFrame
   // 
   // display vector
   public static void displayListOfArraysAsTableFrame(String title, List<Object[]> listOfArrays) {
      displayListOfArraysAsTableFrame(title, null, listOfArrays);
   }

   public static void displayListOfArraysAsTableFrame(String title, String[] colHeaders, List<Object[]> listOfArrays) {
      if (!showArrays) {
         return;
      }
      // TODO check for correct sizes of inputs...

      int numRows = listOfArrays.size();
      int numCols = listOfArrays.get(0).length + 1;
      Object rows[][] = new Object[numRows][numCols];
      int row = 0;
      for (Object[] array : listOfArrays) {
         rows[row][0] = row;
         for (int j = 1; j < numCols; j++) {
            rows[row][j] = array[j - 1];
         }
         row++;
      }
      Object[] columns;
      if (colHeaders == null) {
         columns = new Object[numCols];
         columns[0] = "";

         for (int i = 1; i < numCols; i++) {
            columns[i] = "" + i;
         }
      } else {
         columns = new Object[colHeaders.length];
         for (int i = 0; i < colHeaders.length; i++) {
            columns[i] = colHeaders[i];
         }
      }
      displayTable(title, rows, columns);
   }

   public static void displayArrayAsTableFrame(String title, float[][] array) {
      if (!showArrays) {
         return;
      }
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
      displayTable(title, rows, columns);
   }

   private static void displayTable(String title, Object[][] rows, Object[] columns) {
      JFrame f = new JFrame(title + " [" + (columns.length - 1) + " x " + rows.length + "]");
      f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      Container content = f.getContentPane();
      JTable table = new JTable(rows, columns);
      TableCellRenderer renderer = table.getDefaultRenderer(Object.class);
      ((DefaultTableCellRenderer) renderer).setHorizontalAlignment(SwingConstants.RIGHT);
      table.setDefaultRenderer(Object.class, new CustomTableCellRenderer(renderer));
      //CustomTableCellRenderer mr = new CustomTableCellRenderer(renderer);
      //table.getColumnModel().getColumn(0).setCellRenderer(renderer);
//      table.getColumnModel().getColumn(1).setCellRenderer(mr);
//      table.getColumnModel().getColumn(2).setCellRenderer(mr);
      JScrollPane scrollPane = new JScrollPane(table);
      content.add(scrollPane, BorderLayout.CENTER);
      f.setSize(20 + columns.length * 100, 40 + rows.length * 20);
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

   //
   //
   //=============================================================
   // View array as an image in a ZoomWindow
   public static void displayArrayAsImage(String title, byte[] array, int w, int h) {
      displayImage(title, createImage(w, h, array));
   }

   public static void displayArrayAsImage(String title, short[] array, int w, int h) {
      displayImage(title, createImage(w, h, array));
   }

   public static void displayArrayAsImage(String title, short[] array, int w, int h, int depth) {
      displayImage(title, createImage(w, h, depth, array));
   }

   public static void displayArrayAsImage(String title, float[] array, int w, int h) {
      displayImage(title, createImage(w, h, array));
   }

   public static void displayArrayAsImage(String title, double[] array, int w, int h) {
      displayImage(title, createImage(w, h, toFloatArray(array)));
   }

   static float[] toFloatArray(double[] arr) {
      if (arr == null) {
         return null;
      }
      int n = arr.length;
      float[] ret = new float[n];
      for (int i = 0; i < n; i++) {
         ret[i] = (float) arr[i];
      }
      return ret;
   }

   private static void displayImage(final String title, final BufferedImage image) {
      if (!SwingUtilities.isEventDispatchThread()) {
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               ZoomWindow zoomwin = new ZoomWindow(title, 1f);
               zoomwin.setImage(image);
               zoomwin.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
               zoomwin.setVisible(true);

            }
         });
      }
   }

   // create from short[]
   public static BufferedImage createImage(int imageWidth, int imageHeight, short[] data) {
      return createImage(imageWidth, imageHeight, 16, data);
   }

   public static BufferedImage createImage(int imageWidth, int imageHeight, int imageDepth,
           short[] data) {
      ComponentColorModel ccm =
              new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY),
              new int[]{imageDepth}, false, false, Transparency.OPAQUE, DataBuffer.TYPE_USHORT);
      ComponentSampleModel csm =
              new ComponentSampleModel(DataBuffer.TYPE_USHORT, imageWidth, imageHeight, 1, imageWidth,
              new int[]{0});
      DataBuffer dataBuf = new DataBufferUShort((short[]) data, imageWidth);
      WritableRaster wr = Raster.createWritableRaster(csm, dataBuf, new Point(0, 0));
      Hashtable ht = new Hashtable();
      ht.put("owner", "edu.mbl.jif");
      return new BufferedImage(ccm, wr, true, ht);
   }

   // create from byte[]
   public static BufferedImage createImage(int imageWidth, int imageHeight, byte[] data) {
      int imageDepth = 8;
      ComponentColorModel ccm =
              new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY),
              new int[]{imageDepth}, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
      ComponentSampleModel csm =
              new ComponentSampleModel(DataBuffer.TYPE_BYTE, imageWidth, imageHeight, 1, imageWidth,
              new int[]{0});
      DataBuffer dataBuf = new DataBufferByte((byte[]) data, imageWidth);
      WritableRaster wr = Raster.createWritableRaster(csm, dataBuf, new Point(0, 0));
      Hashtable ht = new Hashtable();
      ht.put("owner", "PSj");
      return new BufferedImage(ccm, wr, true, ht);
   }

   // Float ==============================================================================\
   public static ij.ImagePlus createImagePlus(int width, int height, float[] pixels) {
      ij.ImagePlus ip = new ImagePlus("Float", createImage(width, height, pixels));
      return ip;
   }
   public static ij.ImagePlus createPosNegImagePlus(int width, int height, float[] pixels) {
      ij.ImagePlus ip = new ImagePlus("Float", createPosNegImage(width, height, pixels));
      return ip;
   }

   // Image makers...
   public static BufferedImage createImage(int width, int height, float[] pixels) {
      BufferedImage myimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      for (int x = 0; x < width; x++) {
         for (int y = 0; y < height; y++) {
            int value = (int) ((1f - pixels[y * width + x]) * 255f);
            myimage.setRGB(x, y, (value << 16) | (value << 8) | value);
         }
      }
      return myimage;
   }

   public static BufferedImage createPosNegImage(int width, int height, float[] pixels) {
      BufferedImage myimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      int count = 0;
      for (int x = 0; x < width; x++) {
         for (int y = 0; y < height; y++) {
            float h = 0;
            float s = 1;
            float b = 0;
            
            float v = pixels[y * width + x];
            //  System.out.println(v);
            //int value = (int) ((1f - pixels[y * width + x]) * 255f);
            if(v<0)
               h = .666f;
            else h = 0f;
            v = Math.abs(v);
            int c = Color.HSBtoRGB(h, s, v);
            //int v = (value << 16) | (value << 8) | value;
            //myimage.setRGB(x, y, (value << 16) | (value << 8) | value);
            myimage.setRGB(x, y, c);
            count++;
         }
      }
      return myimage;
   }

   static BufferedImage BImageFrom2DArray(float data[][]) {
      int width = data.length;
      int height = data[0].length;
      BufferedImage myimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      for (int x = 0; x < width; x++) {
         for (int y = 0; y < height; y++) {
            int value = (int) ((1f - data[x][y]) * 255f);
            myimage.setRGB(x, y, (value << 16) | (value << 8) | value);
         }
      }
      return myimage;
   }

   public static void testDisplayImageFloat() {
      int wid = 50;
      int ht = 50;
      float max = 1.0f;
      int len = wid * ht;
      double scale = max / (float) len;
      float[] data = new float[wid * ht];
      for (int i = 0; i < len; i++) {
         data[i] = max/2 - (float) ((float) i * (float) scale);
      }
      // displayArrayAsTableFrame("dump", data);
      displayArrayAsImage("Float data", data, wid, ht);
      
      ij.ImagePlus ip = new ImagePlus("Float", createPosNegImage(wid, ht, data));
      ip.show();
   }

   public static void main(String[] args) {
      testDisplayImageFloat();
   }
}
