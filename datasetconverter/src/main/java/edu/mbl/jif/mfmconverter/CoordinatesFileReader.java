 /*
 * TextDataFile.java
 *
 * Created on July 10, 2006, 2:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.mbl.jif.mfmconverter;

import java.io.File;
import java.io.IOException;

import com.infomata.data.DataFile;
import com.infomata.data.DataFileFactory;
import com.infomata.data.DataRow;
import com.infomata.data.SimpleDelimiterFormat;
import edu.mbl.jif.gui.error.ErrorDialog;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Depends on datafile.jar
 *
 * @author GBH
 */
public class CoordinatesFileReader {

   /**
    * Creates a new instance of TextDataFile
    */
   private CoordinatesFileReader() {
   }

   /*
    Creating a reader for CSV file using
    ISO-8859-1
    */
   public static void main(String[] args)
           throws IOException {
      printRects(readPointsFromFile("testPoints3x3.coord"));
   }

   public static Rectangle[] readPointsFromFile(String fileStr) {
      File file = new File(fileStr);
      // open the file
      DataFile read = DataFileFactory.createReader("8859_1");
      //read.setDataFormat(new CSVFormat());
      read.setDataFormat(new SimpleDelimiterFormat(",", null));
      // first line is column header
      //read.containsHeader(true);

      List<Point> points = new ArrayList<Point>();
      int width = 0;
      int height = 0;
      try {
         read.open(file);
         for (DataRow row = read.next(); row != null; row = read.next()) {
            //System.out.println(row.toString());
            String text = row.getString(0);
            if (text.equalsIgnoreCase("widthHeight")) {
               width = row.getInt(1);
               height = row.getInt(2);
            } else {
               int x = row.getInt(0);
               int y = row.getInt(1);
               points.add(new Point(x, y));
            }
         }
      } catch (Exception ex) {
         ErrorDialog.showError("Error in readPointsFromFile("+fileStr+")", ex);
         return null;
      } finally {
         try {
            read.close();
         } catch (IOException ex) {
            ex.printStackTrace();
         }
      }
      int numSubImages = points.size();
      // create rectangles for subimages...
      Rectangle[] subImageRect = new Rectangle[numSubImages];
      for (int i = 0; i < numSubImages; i++) {
         Point point = points.get(i);
         int x0 = point.x;
         int y0 = point.y;
         subImageRect[i] = new Rectangle(x0, y0, width, height);
      }
      return subImageRect;
   }

   private static void printRects(Rectangle[] subImageRect) {
      for (int i = 0; i < subImageRect.length; i++) {
         System.out.println("" + subImageRect[i].toString());
      }
   }
}
