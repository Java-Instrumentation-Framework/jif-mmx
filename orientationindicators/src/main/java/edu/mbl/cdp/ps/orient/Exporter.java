package edu.mbl.cdp.ps.orient;

import com.infomata.data.CSVFormat;
import com.infomata.data.DataFile;
import com.infomata.data.DataFileFactory;
import com.infomata.data.DataRow;
import edu.mbl.jif.gui.file.FileChooserPanel;
import edu.mbl.jif.imaging.tiff.MultipageTiffFile;
import edu.mbl.jif.utils.StaticSwingUtils;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * TODO - export as a new MM dataset with what(?) metadata...
 *
 * @author GBH
 */
public class Exporter {

   private ImagePlus imgPlus;
   private Rectangle roi = null;
   private IndicatorCanvas cnvs;
   private String outFileName;
   private String dataFileName;
   private DataFile datafile;
   //
   private int xOut;
   private int yOut;
   private int wOut;
   private int hOut;

   private boolean all;
   private boolean data;
   //private String filename;
   private Orientation_Indicators oi;
   //
   JCheckBox chkData;
   FileChooserPanel fileChoozData;

   // Export the grid data (AveragedAreas) as a .csv file.
   // For a single image or for a dataset.
   //
   // Get scaling and ROI from current view of the hyperstack.
   // If ROI is select use that as the cropping rectangle, 
   // else use the bounding box of the current window/canvas
   // Options to export only one channel
// Output file - default to current dir, same filename as dataset
   public Exporter(Orientation_Indicators oi) {
      this.oi = oi;
      this.imgPlus = oi.imp1;
      Roi ijRoi = imgPlus.getRoi();
      if (ijRoi != null) {
         roi = ijRoi.getBounds();
      }
      this.cnvs = oi.getCanvas();
   }

   public void promptForExport() {
      // TODO Export dialog...
      this.showDialog(imgPlus.getWindow());
   }

   private void export() {

      Runnable runnable = new Runnable() {
         public void run() {
            cnvs.setForExport(true);
            cnvs.repaint();
            exportImagesWithIndicators();
            //BufferedImage image = Exporter.canvasToImage(canvas);
            //ImagePlus imgp = new ImagePlus("Canvas", image);
            //imgp.show();
            cnvs.setForExport(false);
            cnvs.repaint();
         }
      };
      runnable.run();

   }

//   public static void dispatchToEDT(Runnable runnable) {
//      if (!SwingUtilities.isEventDispatchThread()) {
//         SwingUtilities.invokeLater(runnable);
//      } else {
//         runnable.run();
//      }
//   }
   private void exportImagesWithIndicators() {
      final int currentChannel = imgPlus.getChannel();
      if (roi != null) {
         wOut = (int) (roi.width * cnvs.getMagnification()) - 1;
         hOut = (int) (roi.height * cnvs.getMagnification()) - 1;
         //Rectangle srcRect = cnvs.getSrcRect();
         xOut = cnvs.screenX(roi.x) + 1;
         yOut = cnvs.screenY(roi.y) + 1;
//         xOut = roi.x - srcRect.x;
//         yOut = roi.y - srcRect.y;
      } else {
         wOut = cnvs.getWidth();
         hOut = cnvs.getHeight();
      }

      if (data) { // Export data

         try {
            datafile = DataFileFactory.createWriter("8859_2", false);
            datafile.setDataFormat(new CSVFormat());
            datafile.open(new File(dataFileName));
            List<String> headers = Arrays.asList(
                    "c", "z", "t",
                    "x", "y", "anisotropy", "orientation", "orientVar", "intensity");
            datafile.setHeaderList(headers);
            DataRow row = datafile.next();
            for (String head : headers) {
               row.add(head);
            }

         } catch (IOException ex) {
         }
         // TODO create data output file
      }
      // get a BufferedImage for setting up the metadata ImageAttributes
//      BufferedImage tempImg = canvasToImage(cnvs);
//      if (roi != null) {
//         tempImg = tempImg.getSubimage(xOut, yOut, wOut, hOut);
//      }
//      ImageAttributes ia = new ImageAttributes(tempImg);
      // TODO *!*!*!*!   I cannot set the fucking filename!
      File f = new File(outFileName);
      if (!f.getParentFile().exists()) { // create dir if it does not exist
         f.getParentFile().mkdir();
      }
      final MultipageTiffFile tiff = new MultipageTiffFile(outFileName);
      if (tiff == null) {
         IJ.error("Couldn't open file");
         return;
      }

      if (all) {
         StaticSwingUtils.dispatchToEDTWait(new Runnable() {
            public void run() {
               // For each channelSet:
               int[] d = imgPlus.getDimensions();
               int nChannels = d[2];
               int nSlices = d[3];
               int nFrames = d[4];
               for (int frame = 0; frame < nFrames; frame++) {
                  for (int slice = 0; slice < nSlices; slice++) {
                     int offset = currentChannel * slice + nSlices * frame;
                     //System.out.println("edt?:" + SwingUtilities.isEventDispatchThread());
                     System.out.println("" + currentChannel + "," + slice + "," + frame);
                     imgPlus.setPositionWithoutUpdate(currentChannel, slice + 1, frame + 1);
                     System.out.println("" + imgPlus.getChannel() + "," + imgPlus.getSlice() + ","
                             + imgPlus.getFrame() + "\n");
                     int channel = 1; // (imp1).getC();
                     int index = imgPlus.getStackIndex(channel, slice + 1, frame + 1) - 1;
                     oi.updateIndicators(imgPlus, index);
                     imgPlus.updateAndRepaintWindow();
                     BufferedImage outImg = canvasToImage(cnvs);
                     if (roi != null) {
                        outImg = outImg.getSubimage(xOut, yOut, wOut, hOut);
                     }
                     // write image to Tiff file
                     tiff.appendImage(outImg);
                     if (data) {
                        exportData(currentChannel, slice + 1, frame + 1);
                     }
                  }
               }
            }

         });
      } else { // only current slice
         BufferedImage outImg = canvasToImage(cnvs);
         if (roi != null) {
            outImg = outImg.getSubimage(xOut, yOut, wOut, hOut);
         }
         tiff.appendImage(outImg);
         if (data) {
            exportData(imgPlus.getChannel(), imgPlus.getSlice(), imgPlus.getFrame());
         }
      }
      tiff.close();
      if (data) {
         try {
            datafile.close();   // close data output file
         } catch (IOException ex) {
         }
      }
   }

   public BufferedImage canvasToImage(Canvas cnvs) {
      int w = cnvs.getWidth();
      int h = cnvs.getHeight();
      int type = BufferedImage.TYPE_INT_RGB;
      BufferedImage image = new BufferedImage(w, h, type);
      Graphics2D g2 = image.createGraphics();
      cnvs.paint(g2);
      g2.dispose();
      return image;
   }

   private void exportData(int currentChannel, int slice, int frame) {
      try {
         Vector<AveragedArea> roiAreas = getAverageAreasInsideRoi();
         System.out.println("\nAreas within Roi (" + roiAreas.size() + "):");
         for (AveragedArea averagedArea : roiAreas) {
            //System.out.println(averagedArea.toString());
            DataRow row = datafile.next();
            row.add(currentChannel);
            row.add(slice);
            row.add(frame);
            row.add(averagedArea.x);
            row.add(averagedArea.y);
            row.add(averagedArea.anisotropy);
            row.add(averagedArea.orientation);
            row.add(averagedArea.orientationVariance);
            row.add(averagedArea.intensity);
         }
      } catch (IOException ex) {
      }

   }

   public void showDialog(Component parent) {
      JOptionPane optionPane = new JOptionPane();
      // Add options
      JCheckBox chkAll = new JCheckBox("Export all planes");
      //
      String filename = this.getDefaultFilename(imgPlus, false);
      FileChooserPanel fileChoozImg
              = new FileChooserPanel("Output file: ", filename, "tif", FileChooserPanel.Type.SELECT);

      chkData = new JCheckBox("Export data also (.csv)");
      chkData.addItemListener(new CheckBoxListener());
      String filenameData = this.getDefaultFilename(imgPlus, true);
      fileChoozData
              = new FileChooserPanel("Data file: ", filenameData, "csv",
                      FileChooserPanel.Type.SELECT);
      Object[] msg = {"Select a value:", fileChoozImg, chkAll, chkData, fileChoozData};
      optionPane.setMessage(msg);
      optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
      optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
      JDialog dialog = optionPane.createDialog(parent, "Export");
      dialog.show();

      Object value = optionPane.getValue();
      if ((value == null) || !(value instanceof Integer)) {
         return;
      } else {
         int i = ((Integer) value).intValue();
         if (i == JOptionPane.OK_OPTION) {
            //System.out.println("OKAY - value is: " + optionPane.getInputValue());
            this.outFileName = forceForwardSlashes(fileChoozImg.getFilePath());
            this.dataFileName = forceForwardSlashes(fileChoozData.getFilePath());
            this.all = chkAll.isSelected();
            this.data = chkData.isSelected();

            System.out.println("filePath = " + outFileName);
            System.out.println("filePathData = " + dataFileName);
            System.out.println("All? " + chkAll.isSelected());
            System.out.println("Data? " + chkData.isSelected());

         } else if (i == JOptionPane.CLOSED_OPTION) {
            return;
         } else if (i == JOptionPane.CANCEL_OPTION) {
            return;
         }
         export();
      }
   }

   private class CheckBoxListener implements ItemListener {

      public void itemStateChanged(ItemEvent e) {
         if (e.getSource() == chkData) {
            fileChoozData.setEnabled(chkData.isSelected());
         }
      }
   }

   public Vector<AveragedArea> getAverageAreasInsideRoi() {
      Rectangle roiRect = getRoiRectangle(this.imgPlus);
      if (roiRect == null) {
         return null;
      }
      Vector<AveragedArea> roiAreas = new Vector<AveragedArea>();
      Vector<AveragedArea> allAreas = oi.getAveragedAreas();
      for (AveragedArea a : allAreas) {
         if (insideRoiX(a.x, roiRect) && insideRoiY(a.y, roiRect)) {
            roiAreas.add(a);
         }
      }
      return roiAreas;
   }

   public Rectangle getRoiRectangle(ImagePlus imp1) {
      Roi roi = imp1.getRoi();
      if (roi != null && !roi.isArea()) {
         roi = null;
      }
      Rectangle roiRect;
      if (roi != null) {
         roiRect = roi.getBounds();
      } else {
         // what if window/canvas larger than image?
         // WHOLE IMAGE new Rectangle(0, 0, imp1.getWidth(), imp1.getHeight());
         // Get visible rect
         roiRect = imp1.getCanvas().getSrcRect();
      }
      return roiRect;
   }

   public boolean insideRoiX(float x, Rectangle roiRect) {
      return x >= roiRect.x && x <= roiRect.x + roiRect.width;
   }

   public boolean insideRoiY(float y, Rectangle roiRect) {
      return y >= roiRect.y && y <= roiRect.y + roiRect.height;
   }

   private String getDefaultFilename(ImagePlus imp, boolean isData) {
      String outFilename = "";
      String dir = imp.getOriginalFileInfo().directory;
      if (!isData) {
         String file = "orientation";
         outFilename = makeOutputFilename(dir, file, "tif");
      } else {
         String file = "data";
         outFilename = makeOutputFilename(dir, file, "csv");
      }
      if (outFilename == null) {
         return null;
      } else {
         return outFilename;
      }
   }

   private static final String SUB_DIR = "orientation";

   public String makeOutputFilename(String dir, String baseFilename, String ext) {
      String fullPath = dir + "/" + SUB_DIR + "/" + baseFilename + "." + ext;
      // assure unique
      int n = 1;
      while (new File(fullPath).exists()) {
         fullPath = dir + "/" + SUB_DIR + "/" + baseFilename + n + "." + ext;
         n++;
      }
      return fullPath;
   }

   public static String forceForwardSlashes(String dirPath) {
      String foreslash = "/";
      String regex = "\\\\";
      final String dirPathFixed = dirPath.replaceAll(regex, foreslash);
      return dirPathFixed;
   }

}
