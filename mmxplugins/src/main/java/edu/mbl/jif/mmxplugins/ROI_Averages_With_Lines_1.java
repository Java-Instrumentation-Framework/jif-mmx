package edu.mbl.jif.mmxplugins;

import ij.*;
import ij.plugin.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import java.awt.*;
import ij.gui.*;
import ij.text.TextWindow;

/**
 * Calculates average ratio and azimuth values within ROI and draws azimuth lines;
 *
 * The ROI is divided into subROIs; the average and line is created for each subROI;
 *
 * The ROI width and height must be divisible by the * Created May 2012, copyright Rudolf
 * Oldenbourg, MBL;
 *
 * 6 June 2012: Version 1b
 *
 * see change history at end of file
 *
 */
public class ROI_Averages_With_Lines_1 implements PlugInFilter {

   ImagePlus imp1, imp2;
// azim line (al.) Variables from Preferences:
   String sampleStackTitle = Prefs.get("al.sampleStackTitle", "sampleStack"); //azimLine sample stack
   double pRCeiling = Prefs.get("al.pRCeiling", 3d);
   String fluorWeightChoice = Prefs.get("al.fluorWeightChoice", "No");  //use weight derived from fluorescence and ratio
   String ratioWeightChoice = Prefs.get("al.ratioWeightChoice", "No");  //use weight derived from fluorescence and ratio
   String statResultsChoice = Prefs.get("al.statResultsChoice", "Yes");  //create text window with statistics results
   int xNrOfROIs = (int) Prefs.get("al.xNrOfROIs", 1d);  //number of subROIs in horizontal direction
   int yNrOfROIs = (int) Prefs.get("al.yNrOfROIs", 1d);  //number of subROIs in vertical direction
   int xOffset = (int) Prefs.get("al.xOffset", 0d);  //xOffset for center pixel 
   int yOffset = (int) Prefs.get("al.yOffset", 0d);  //yOffset for center pixel
   int lineWidth = (int) Prefs.get("al.lineWidth", 2d);
   double lineLength = Prefs.get("al.lineLength", 1d);  //lineLength is factor to adjust line length 
   String ratioLengthChoice = Prefs.get("al.ratioLengthChoice", "No");  //line length proportional to ratio value
   String outlineChoice = Prefs.get("al. outlineChoice", "No");  //contrasting black and white azimuth line
   double lineLengthDraw;  //lineLengthDraw is drawn line length 
   static String[] colors = {"Red", "Green", "Blue", "Magenta", "Cyan", "Yellow", "Orange", "Black", "White"};
   static String color = "Red";
   String bitDepth;
   Overlay overlay = new Overlay();
   String textWindowString = "";

   public int setup(String arg, ImagePlus imp) {
      return DOES_8G + DOES_16 + STACK_REQUIRED + SUPPORTS_MASKING;
   }

   public void run(ImageProcessor ip) {
      if (showDialog()) {
         Draw_Overlay(imp1);
      }
   }

   public void Draw_Overlay(ImagePlus imp1) {
      if (imp1.getType() == 0) {				//getType returns 0 for 8-bit, 1 for 16-bit
         bitDepth = "8-bit";
         Prefs.set("ps.bitDepth", bitDepth);
      } else {
         bitDepth = "16-bit";
         Prefs.set("ps.bitDepth", bitDepth);
      }
      int sliceNum = imp1.getCurrentSlice();
      int width = imp1.getWidth();
      int height = imp1.getHeight();

      ImageStack stack1 = imp1.getStack();
      ImageProcessor ip = imp1.getProcessor();

      Roi roi = imp1.getRoi();
      if (roi != null && !roi.isArea()) {
         roi = null;
      }
      Rectangle r = roi != null ? roi.getBounds() : new Rectangle(0, 0, width, height);
      // check for subROIs to fit into ROI
      if ((r.width % xNrOfROIs) != 0) {
         error();
         return;
      }
      if ((r.height % yNrOfROIs) != 0) {
         error();
         return;
      }
      int widthSubROI = r.width / xNrOfROIs;
      int heightSubROI = r.height / yNrOfROIs;
      int smallSubROI;
      if (widthSubROI < heightSubROI) {
         smallSubROI = widthSubROI;
      } else {
         smallSubROI = heightSubROI;     //smallSubROI is the shorter interval
      }
      int dimension = width * height;
      byte[] pixB;
      short[] pixS;
      double[] pixD = new double[dimension];
      double[] ratioD = new double[dimension];
      double[] fluorD = new double[dimension];
      pRCeiling = pRCeiling - 1;
      double subROIAzimCos;
      double subROIAzimSin;
      double subROIAzim;
      byte subROIAzimB;
      short subROIAzimS;
      double orderParam;
      double yamartinoParam;
      double subROIAzim2;
      double subROIAzim2SD;
      double subROIRatio;
      double subROIRatioSD;
      byte subROIRatioB;
      short subROIRatioS;
      double subROIFluor;
      int count;
      double[][] fluorWeight = new double[heightSubROI][widthSubROI];
      double[][] ratioWeight = new double[heightSubROI][widthSubROI];

      // get the ratio values of 1st slice in the stack
      if (bitDepth == "8-bit") {
         pixB = (byte[]) stack1.getPixels(1);
         for (int j = 0; j < dimension; j++) {
            ratioD[j] = pRCeiling * (0xff & pixB[j]) / 255 + 1;
         }
      } else {
         pixS = (short[]) stack1.getPixels(1);
         for (int j = 0; j < dimension; j++) {
            ratioD[j] = pRCeiling * ((double) pixS[j]) / 255 + 1;
         }
      }

      // get the azimuth values of 2nd slice in the stack and convert to radian
      if (bitDepth == "8-bit") {
         pixB = (byte[]) stack1.getPixels(2);
         for (int j = 0; j < dimension; j++) {
            pixD[j] = Math.PI * (0xff & pixB[j]) / 180;
         }
      } else {
         pixS = (short[]) stack1.getPixels(2);
         for (int j = 0; j < dimension; j++) {
            pixD[j] = Math.PI * ((double) pixS[j]) / 1800;
         }
      }

      // get the fluorescence values of last slice in the stack
      int lastSlice = imp1.getNSlices();
      if (bitDepth == "8-bit") {
         pixB = (byte[]) stack1.getPixels(lastSlice);
         for (int j = 0; j < dimension; j++) {
            fluorD[j] = (0xff & pixB[j]);
         }
      } else {
         pixS = (short[]) stack1.getPixels(lastSlice);
         for (int j = 0; j < dimension; j++) {
            fluorD[j] = (double) pixS[j];
         }
      }

      int roiCount = 0;

      //subROI averages

      //fluorescence average
      for (int yCount = 0; yCount < yNrOfROIs; yCount++) {   // ghdfghds
         for (int xCount = 0; xCount < xNrOfROIs; xCount++) {
            roiCount++;
            count = 0;
            subROIFluor = 0;
            for (int y = 0; y < heightSubROI; y++) {
               for (int x = 0; x < widthSubROI; x++) {
                  count++;
                  subROIFluor += fluorD[(r.y + yCount * heightSubROI + y) * width + r.x + xCount
                          * widthSubROI + x];
               }
            }
            subROIFluor = subROIFluor / count;

//fluorescence weight
            for (int y = 0; y < heightSubROI; y++) {
               for (int x = 0; x < widthSubROI; x++) {
                  if (fluorWeightChoice == "No") {
                     fluorWeight[y][x] = 1;
                  } else {
                     fluorWeight[y][x] = fluorD[(r.y + yCount * heightSubROI + y) * width + r.x
                             + xCount * widthSubROI + x] / subROIFluor;
                  }
               }
            }

            //ratio average
            subROIRatio = 0;
            for (int y = 0; y < heightSubROI; y++) {
               for (int x = 0; x < widthSubROI; x++) {
                  subROIRatio += fluorWeight[y][x] * ratioD[(r.y + yCount * heightSubROI + y)
                          * width + r.x + xCount * widthSubROI + x];
               }
            }
            subROIRatio = subROIRatio / count;

            //ratio standard deviation
            subROIRatioSD = 0;
            for (int y = 0; y < heightSubROI; y++) {
               for (int x = 0; x < widthSubROI; x++) {
                  subROIRatioSD += Math.pow(fluorWeight[y][x] * (ratioD[(r.y + yCount * heightSubROI
                          + y) * width + r.x + xCount * widthSubROI + x] - subROIRatio), 2);
               }
            }
            subROIRatioSD = Math.sqrt(subROIRatioSD / count);

            //ratio weight
            for (int y = 0; y < heightSubROI; y++) {
               for (int x = 0; x < widthSubROI; x++) {
                  if (ratioWeightChoice == "No") {
                     ratioWeight[y][x] = 1;
                  } else {
                     ratioWeight[y][x] = ratioD[(r.y + yCount * heightSubROI + y) * width + r.x
                             + xCount * widthSubROI + x] / subROIRatio;
                  }
               }
            }

            //Cos and Sin of azimuth
            subROIAzimCos = 0;
            subROIAzimSin = 0;
            for (int y = 0; y < heightSubROI; y++) {
               for (int x = 0; x < widthSubROI; x++) {
                  subROIAzimCos += fluorWeight[y][x] * ratioWeight[y][x] * Math.cos(2 * pixD[(r.y
                          + yCount * heightSubROI + y) * width + r.x + xCount * widthSubROI + x]);
                  subROIAzimSin += fluorWeight[y][x] * ratioWeight[y][x] * Math.sin(2 * pixD[(r.y
                          + yCount * heightSubROI + y) * width + r.x + xCount * widthSubROI + x]);
               }
            }
            subROIAzimCos = subROIAzimCos / count;
            subROIAzimSin = subROIAzimSin / count;
            subROIAzim = (180 * Math.atan2(subROIAzimSin, subROIAzimCos) / (2 * Math.PI) + 180)
                    % 180;
            orderParam = Math.sqrt(Math.pow(subROIAzimCos, 2) + Math.pow(subROIAzimSin, 2));
            //				yamartinoParam = Math.asin(Math.sqrt(orderParam))*(1+(2/Math.sqrt(3)-1)*Math.pow(orderParam,3));
               
            //azimuth 2nd average, treating it like a variable on a straight line, instead of a circular variable
            subROIAzim2 = 0;
            for (int y = 0; y < heightSubROI; y++) {
               for (int x = 0; x < widthSubROI; x++) {
                  subROIAzim2 += fluorWeight[y][x] * ratioWeight[y][x] * pixD[(r.y + yCount
                          * heightSubROI + y) * width + r.x + xCount * widthSubROI + x];
               }
            }
            subROIAzim2 = subROIAzim2 / count;

            //azimuth standard deviation
            subROIAzim2SD = 0;
            for (int y = 0; y < heightSubROI; y++) {
               for (int x = 0; x < widthSubROI; x++) {
                  subROIAzim2SD += fluorWeight[y][x] * ratioWeight[y][x] * Math.pow(pixD[(r.y
                          + yCount * heightSubROI + y) * width + r.x + xCount * widthSubROI + x]
                          - subROIAzim2, 2);
               }
            }
            subROIAzim2SD = Math.sqrt(subROIAzim2SD / count);

            textWindowString = textWindowString + "ROI " + roiCount + "\t" + IJ.d2s(subROIRatio, 2)
                    + "\t" + IJ.d2s(subROIRatioSD, 2) + "\t"
                    + IJ.d2s(180 * subROIAzim2 / Math.PI, 1) + "\t" + IJ.d2s(180 * subROIAzim2SD
                    / Math.PI, 2) + "\t" + IJ.d2s(subROIAzim, 1) + "\t" + IJ.d2s(orderParam, 2)
                    + "\t" + IJ.d2s(subROIFluor, 0) + "\n";

            // enter average ratio and azimuth values in subROI
            if (bitDepth == "8-bit") {
               subROIAzimB = (byte) (((int) subROIAzim) & 0xff);
               subROIRatioB = (byte) (((int) (255 * (subROIRatio - 1) / pRCeiling)) & 0xff);
               imp1.setSlice(1);
               for (int y = r.y + yCount * heightSubROI; y < (r.y + (yCount + 1) * heightSubROI); y++) {
                  for (int x = r.x + xCount * widthSubROI; x < (r.x + (xCount + 1) * widthSubROI); x++) {
                     ip.set(x, y, subROIRatioB);
                  }
               }
               imp1.setSlice(2);
               for (int y = r.y + yCount * heightSubROI; y < (r.y + (yCount + 1) * heightSubROI); y++) {
                  for (int x = r.x + xCount * widthSubROI; x < (r.x + (xCount + 1) * widthSubROI); x++) {
                     ip.set(x, y, subROIAzimB);
                  }
               }
            } else {
               subROIAzimS = (short) (10 * subROIAzim);
               subROIRatioS = (short) (255 * (subROIRatio - 1) / pRCeiling);
               imp1.setSlice(1);
               for (int y = r.y + yCount * heightSubROI; y < (r.y + (yCount + 1) * heightSubROI); y++) {
                  for (int x = r.x + xCount * widthSubROI; x < (r.x + (xCount + 1) * widthSubROI); x++) {
                     ip.set(x, y, subROIRatioS);
                  }
               }
               imp1.setSlice(2);
               for (int y = r.y + yCount * heightSubROI; y < (r.y + (yCount + 1) * heightSubROI); y++) {
                  for (int x = r.x + xCount * widthSubROI; x < (r.x + (xCount + 1) * widthSubROI); x++) {
                     ip.set(x, y, subROIAzimS);
                  }
               }
            }
         }
      }

      if (statResultsChoice == "Yes") {
         String title = "Statistics Table";
         String headings = "Description\tRatio Mean\tRatio StdDev\tAzimuth Lin. Mean\tAzimuth StdDev\tAzimuth Circ. Mean\tOrder Parameter\tFluorescence Mean";
         TextWindow tw = new TextWindow(title, headings, textWindowString, 1100, 300);
      }

      if (ratioLengthChoice == "Yes") {
      // get the averaged ratio values of 1st slice in the stack, ratio value is fraction of maximum value (255 or 4095)
         if (bitDepth == "8-bit") {
            pixB = (byte[]) stack1.getPixels(1);
            for (int j = 0; j < dimension; j++) {
               ratioD[j] = (0xff & pixB[j]);
               ratioD[j] = ratioD[j] / 255;
            }
         } else {
            pixS = (short[]) stack1.getPixels(1);
            for (int j = 0; j < dimension; j++) {
               ratioD[j] = ((double) pixS[j]) / 4095;
            }
         }
      }

      // get the averaged azimuth values of 2nd slice in the stack and convert to radian 
      if (bitDepth == "8-bit") {
         pixB = (byte[]) stack1.getPixels(2);
         for (int j = 0; j < dimension; j++) {
            pixD[j] = Math.PI * (0xff & pixB[j]) / 180;
         }
      } else {
         pixS = (short[]) stack1.getPixels(2);
         for (int j = 0; j < dimension; j++) {
            pixD[j] = Math.PI * ((double) pixS[j]) / 1800;
         }
      }

      // Draw Overlay
      int widthSubROIHalf = (int) Math.ceil((double) widthSubROI / 2);
      int heightSubROIHalf = (int) Math.ceil((double) heightSubROI / 2);

      Roi azimLine;
      double azimLineXStart;
      double azimLineYStart;
      double azimLineXEnd;
      double azimLineYEnd;
      Roi centerCircle;
      float circleWidth = (float) (smallSubROI < 5 ? 1 : (lineWidth == 1 ? 1 : (lineWidth > 2
              ? (lineWidth - 1) : 1.01)));
      if (smallSubROI > 1) {
         if (outlineChoice == "Yes") {
            if (lineWidth > 1) {				//draw outlined central circles with stroke width > 1
               for (int i = 1; i <= xNrOfROIs; i++) {
                  for (int j = 1; j <= yNrOfROIs; j++) {
                     centerCircle = new OvalRoi(r.x + i * widthSubROI - widthSubROIHalf + xOffset
                             - 1, r.y + j * heightSubROI - heightSubROIHalf + yOffset - 1, 3, 3);
                     centerCircle.setStrokeColor(Color.white);
                     centerCircle.setStrokeWidth(2 * circleWidth);
                     overlay.add(centerCircle);
                  }
               }
            } else {							//draw outlined central circles with stroke width = 1
               for (int i = 1; i <= xNrOfROIs; i++) {
                  for (int j = 1; j <= yNrOfROIs; j++) {
                     centerCircle = new OvalRoi(r.x - widthSubROIHalf + xOffset + i * widthSubROI,
                             r.y - heightSubROIHalf + yOffset + j * heightSubROI, 1, 1);
                     centerCircle.setStrokeColor(Color.white);
                     centerCircle.setStrokeWidth(2 * circleWidth);
                     overlay.add(centerCircle);
                  }
               }
            }
         }
      }
      if (outlineChoice == "Yes") {
         for (int i = 1; i <= xNrOfROIs; i++) {		//draw outlined azimlines
            for (int j = 1; j <= yNrOfROIs; j++) {
               lineLengthDraw = smallSubROI * lineLength;
               if (ratioLengthChoice == "Yes") {
                  lineLengthDraw = lineLengthDraw * ratioD[r.x - widthSubROIHalf + xOffset + i
                          * widthSubROI + (r.y - heightSubROIHalf + yOffset + j * heightSubROI)
                          * width];
               }
               azimLineXStart = r.x - widthSubROIHalf + xOffset + i * widthSubROI + 0.5
                       - lineLengthDraw * Math.cos(pixD[r.x - widthSubROIHalf + xOffset + i
                       * widthSubROI + (r.y - heightSubROIHalf + yOffset + j * heightSubROI) * width])
                       / 2;
               azimLineYStart = r.y - heightSubROIHalf + yOffset + j * heightSubROI + 0.5
                       + lineLengthDraw * Math.sin(pixD[r.x - widthSubROIHalf + xOffset + i
                       * widthSubROI + (r.y - heightSubROIHalf + yOffset + j * heightSubROI) * width])
                       / 2;
               azimLineXEnd = r.x - widthSubROIHalf + xOffset + i * widthSubROI + 0.5
                       + lineLengthDraw * Math.cos(pixD[r.x - widthSubROIHalf + xOffset + i
                       * widthSubROI + (r.y - heightSubROIHalf + yOffset + j * heightSubROI) * width])
                       / 2;
               azimLineYEnd = r.y - heightSubROIHalf + yOffset + j * heightSubROI + 0.5
                       - lineLengthDraw * Math.sin(pixD[r.x - widthSubROIHalf + xOffset + i
                       * widthSubROI + (r.y - heightSubROIHalf + yOffset + j * heightSubROI) * width])
                       / 2;
               azimLine = new Line(azimLineXStart, azimLineYStart, azimLineXEnd, azimLineYEnd);
               azimLine.setStrokeColor(Color.white);
               azimLine.setStrokeWidth(2 * lineWidth);
               overlay.add(azimLine);
            }
         }
      }
      if (smallSubROI > 1) {
         if (lineWidth > 1) {				//draw central circle with stroke width > 1
            for (int i = 1; i <= xNrOfROIs; i++) {
               for (int j = 1; j <= yNrOfROIs; j++) {
                  centerCircle = new OvalRoi(r.x + i * widthSubROI - widthSubROIHalf + xOffset - 1,
                          r.y + j * heightSubROI - heightSubROIHalf + yOffset - 1, 3, 3);
                  centerCircle.setStrokeColor(getColor());
                  centerCircle.setStrokeWidth(circleWidth);
                  overlay.add(centerCircle);
               }
            }
         } else {							//draw central circle with stroke width = 1
            for (int i = 1; i <= xNrOfROIs; i++) {
               for (int j = 1; j <= yNrOfROIs; j++) {
                  centerCircle = new OvalRoi(r.x - widthSubROIHalf + xOffset + i * widthSubROI, r.y
                          - heightSubROIHalf + yOffset + j * heightSubROI, 1, 1);
                  centerCircle.setStrokeColor(getColor());
                  centerCircle.setStrokeWidth(circleWidth);
                  overlay.add(centerCircle);
               }
            }
         }
      }
      for (int i = 1; i <= xNrOfROIs; i++) {			//draw azimuth lines
         for (int j = 1; j <= yNrOfROIs; j++) {
            lineLengthDraw = smallSubROI * lineLength;
            if (ratioLengthChoice == "Yes") {
               lineLengthDraw = lineLengthDraw * ratioD[r.x - widthSubROIHalf + xOffset + i
                       * widthSubROI + (r.y - heightSubROIHalf + yOffset + j * heightSubROI) * width];
            }
            azimLineXStart = r.x - widthSubROIHalf + xOffset + i * widthSubROI + 0.5
                    - lineLengthDraw * Math.cos(pixD[r.x - widthSubROIHalf + xOffset + i
                    * widthSubROI + (r.y - heightSubROIHalf + yOffset + j * heightSubROI) * width])
                    / 2;
            azimLineYStart = r.y - heightSubROIHalf + yOffset + j * heightSubROI + 0.5
                    + lineLengthDraw * Math.sin(pixD[r.x - widthSubROIHalf + xOffset + i
                    * widthSubROI + (r.y - heightSubROIHalf + yOffset + j * heightSubROI) * width])
                    / 2;
            azimLineXEnd = r.x - widthSubROIHalf + xOffset + i * widthSubROI + 0.5 + lineLengthDraw
                    * Math.cos(pixD[r.x - widthSubROIHalf + xOffset + i * widthSubROI + (r.y
                    - heightSubROIHalf + yOffset + j * heightSubROI) * width]) / 2;
            azimLineYEnd = r.y - heightSubROIHalf + yOffset + j * heightSubROI + 0.5
                    - lineLengthDraw * Math.sin(pixD[r.x - widthSubROIHalf + xOffset + i
                    * widthSubROI + (r.y - heightSubROIHalf + yOffset + j * heightSubROI) * width])
                    / 2;
            azimLine = new Line(azimLineXStart, azimLineYStart, azimLineXEnd, azimLineYEnd);
            azimLine.setStrokeColor(getColor());
            azimLine.setStrokeWidth(lineWidth);
            overlay.add(azimLine);
         }
      }
      imp1.setOverlay(overlay);
      imp1.setSlice(sliceNum);

      Prefs.set("al.sampleStackTitle", sampleStackTitle);
      Prefs.set("al.pRCeiling", pRCeiling + 1);
      Prefs.set("al.fluorWeightChoice", fluorWeightChoice);
      Prefs.set("al.ratioWeightChoice", ratioWeightChoice);
      Prefs.set("al.statResultsChoice", statResultsChoice);
      Prefs.set("al.xNrOfROIs", xNrOfROIs);
      Prefs.set("al.yNrOfROIs", yNrOfROIs);
      Prefs.set("al.xOffset", xOffset);
      Prefs.set("al.yOffset", yOffset);
      Prefs.set("al.lineWidth", lineWidth);
      Prefs.set("al.lineLength", lineLength);
      Prefs.set("al.ratioLengthChoice", ratioLengthChoice);
      Prefs.set("al. outlineChoice", outlineChoice);
      Prefs.savePreferences();
   }

   public boolean showDialog() {

      String azimChoice = "no azim stack";
      int[] wList = WindowManager.getIDList();
      if (wList == null) {
         IJ.noImage();
         return false;
      }

      String[] sampleTitles = new String[wList.length];
      for (int i = 0; i < wList.length; i++) {
         ImagePlus imp = WindowManager.getImage(wList[i]);
         sampleTitles[i] = imp != null ? imp.getTitle() : "";
      }
      String sampleChoice = sampleTitles[0];
      for (int i = 1; i < (wList.length); i++) {
         if (sampleTitles[i] == sampleStackTitle) {
            sampleChoice = sampleStackTitle;
         }
      }

      String[] yesNoQL = new String[2];
      yesNoQL[0] = "Yes";
      yesNoQL[1] = "No";

      GenericDialog gd = new GenericDialog("ROI Averages with Lines");
      gd.addChoice("sample stack title:", sampleTitles, sampleChoice);
      gd.addNumericField("ratio ceiling: ", pRCeiling, 1, 8, "");
      gd.addChoice("apply fluor. weight: ", yesNoQL, fluorWeightChoice);
      gd.addChoice("apply ratio weight: ", yesNoQL, ratioWeightChoice);
      gd.addChoice("display stat. results: ", yesNoQL, statResultsChoice);
      gd.addNumericField("horiz. nr. of subROIs: ", xNrOfROIs, 0, 8, "");
      gd.addNumericField("vert. nr. of subROIs: ", yNrOfROIs, 0, 8, "");
      gd.addNumericField("azim. line width: ", lineWidth, 0, 8, "pixel");
      gd.addNumericField("azim. line length: ", lineLength, 2, 8, "interval");
      gd.addChoice("line length prop. ratio: ", yesNoQL, ratioLengthChoice);
      gd.addChoice("color: ", colors, color);
      gd.addNumericField("hor. offset: ", xOffset, 0, 8, "pixel");
      gd.addNumericField("vert. offset: ", yOffset, 0, 8, "pixel");
      gd.addChoice("draw outline:", yesNoQL, outlineChoice);

      gd.showDialog();
      if (gd.wasCanceled()) {
         return false;
      }

      int index1 = gd.getNextChoiceIndex();
      pRCeiling = gd.getNextNumber();
      fluorWeightChoice = gd.getNextChoice();
      ratioWeightChoice = gd.getNextChoice();
      statResultsChoice = gd.getNextChoice();
      xNrOfROIs = (int) gd.getNextNumber();
      yNrOfROIs = (int) gd.getNextNumber();
      lineWidth = (int) gd.getNextNumber();
      lineLength = gd.getNextNumber();
      ratioLengthChoice = gd.getNextChoice();
      color = gd.getNextChoice();
      xOffset = (int) gd.getNextNumber();
      yOffset = (int) gd.getNextNumber();
      outlineChoice = gd.getNextChoice();

      imp1 = WindowManager.getImage(wList[index1]);
      sampleStackTitle = sampleTitles[index1];

      return true;
   }

   Color getColor() {
      Color c = Color.cyan;
      if (color.equals(colors[0])) {
         c = Color.red;
      } else if (color.equals(colors[1])) {
         c = Color.green;
      } else if (color.equals(colors[2])) {
         c = Color.blue;
      } else if (color.equals(colors[3])) {
         c = Color.magenta;
      } else if (color.equals(colors[4])) {
         c = Color.cyan;
      } else if (color.equals(colors[5])) {
         c = Color.yellow;
      } else if (color.equals(colors[6])) {
         c = Color.orange;
      } else if (color.equals(colors[7])) {
         c = Color.black;
      } else if (color.equals(colors[8])) {
         c = Color.white;
      }
      return c;
   }

   void error() {
      IJ.showMessage("Average ROIs with lines"
              + "\nROI width and height have to be divisible by horizontal and vertical number of subROIs");
   }
}
/**
 * Change history
 *
 * Version 1, 7 May 2012 Based on Azim_LinesV2c
 *
 * 11 May 2012 Added text window with statistics results for each ROI
 *
 * 12 May 2012 corrected bug in calculation of average ratio and its standard deviation
 *
 * 14 May 2012 fixed a bug in writing pRCeiling value into IJ_Prefs file
 *
 * 27 May 2012 fixed a bug when fluorescence and ratio weights were not applied changed generic
 * dialogue field names
 *
 * Version 1b 6 June 2012 In the formula for the std. dev., I moved the weight factors so they are
 * not squared when calculating the sum of the squared residuals. Redefined order parameter, so 0
 * corresponds to random azimuth values and 1 corresponds to fully ordered or equal azimuth values.
 * Removed Yamartino parameter from calculation and display in statistics table.
 *
 */
