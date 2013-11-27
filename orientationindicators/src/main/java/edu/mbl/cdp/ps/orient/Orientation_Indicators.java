package edu.mbl.cdp.ps.orient;

import edu.mbl.cdp.ps.orient.OrientationIndicators.Type;
import edu.mbl.jif.imaging.dataset.util.DatasetUtils;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Prefs;
import ij.gui.GenericDialog;
import ij.gui.ImageWindow;
import ij.gui.ScrollbarWithLabel;
import ij.gui.StackWindow;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.net.URL;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * Orientation_Indicators for OpenPolScope data visualization.
 * 
 * Displays orientation indicators on a grid.
 * The grid size can change with the magnification.

 Birefringence: Anisotropy(or Magnitude) & Orientation
 FlourPol or Diattenuation: Intensity, Anisotropy, Orientation
 

 TODOs: 

 [ ] On rerun, channel slider moved to 0, but image displayed is last channel
 [ ] Three repaints on initial run
 if orient = 0 or 90, no indicator... when not scaling...
 [ ] Threshold is fraction of range, not nm.
 // If ratio values of 1st slice in the stack, ratio value is fraction of maximum value (255 or 4095)
 // calc anisotropy from ratio...

 Nov 1
 [x] Should no longer need jai tiff installed - 
 added dependency in jif.imaging.tiff for jai_imageio... test this.

 [x] length %


 [ ] Grid size is not the same as the original after zooming in then out
 ! At 25% (after zooming out) grid not happening at Border 

 [X] not recomputing on change timepoint...

 [?]  add input for ceiling if it is not found in the metadata

 [ ] What if applied to an MM VirtualAcqDisplay?

 [ ] ++ Only draw in visible area of canvas
 
 + Change line width with scale
 
 + Need PolStack metadata... from dataset
 retCeiling is in summary metadata
 
 + add non-linear variation to: 
 lengthProportionalToAnisotropy
 + non-linear scaleWithMag .. ??
 
 + On export, save the x0, y0, w, h of the canvas in relation to the full image to the metadata
  
 [x] Operates on IJ Hyperstack - if there is metadata... 
 
 [x] Export the scaled image with indicators as RGB
 * + Keep the zoom box on the exported images
 
 Check imageInfo, "ChNames" for "Retardance"
 If RetardanceCeiling is not in summary metadata, get from FrameMetadata
 
 Measurement on ROI ...
 Requires copying all of ij/text components, damn it.
 
 Generalize
 setDataSource - ImagePlus/hyperstack or dataset on disk
 if ImagePlus, replace ImageWindow with IndicatorCanvas
 
 
 CircularStatistics:
 [ ] What is the std for R, Theta?
 [ ] What is the average intensity?

/////////////////////////
 [ ] Export to hyperstack... 
[ ] Not seeing Ceiling from old data set 
[x] add .5 to x,y when cell/grid size = [1x1]  
[x] Doesn't draw 0 or 90 degree lines... add 0.0001 to orient for indicator
 
 */
public class Orientation_Indicators {

   public static final String LiveWindowName = "Snap/Live Window";
   private final String PluginName_ = "Orientation_Indicators";
   private static final String PluginName_Manual_ = "Orientation_Indicators.pdf";
   public static final String IconLocation = "icons/azimMap.gif";
   public static final URL IconURL = Orientation_Indicators.class.getResource(IconLocation);
   public static final Image ImageIcon = Toolkit.getDefaultToolkit().createImage(IconURL);
   //
   // 
//      public static String azimPrefs = "xinterval=10 yinterval=10 indicatorwidth=1 indicatorlength=1 "
//           + "lengthpropratio=No color=Red xoffset=0 yoffset=0 draw_outindicator=No use_listener=Yes";

   //private ImagePlus imagePlus;
   private int width;
   private int height;
   private IndicatorCanvas canvas;
   private float circleWidth;
   //
   // Controls...
   Checkbox indicatorsCheckBox;
   //String EnableIndicators = "Yes";
   int lastIndex = -1; //?

   ImagePlus imp1;
   private ImageWindow frame;
   //boolean isRunningOnIjStack = false;
   // float arrays
   float[] anisotropy;
   float[] intensity;
   float[] orient;
   // Results
   private PolScope.Type psType;
   private Vector<AveragedArea> areas;
   private Vector<Indicator> indicators;

   // Scaling
   private float lastMagnification = 0;
   private float scaledLength;
   private int scaledCellSize;
   private int lastCellSize = 0;

   private boolean promptForCeiling = false;
   private float displayCeiling;

//<editor-fold defaultstate="collapsed" desc="Variables from Prefs">
   //
   private static final String key = "oi.";
   private static final String key_sampleStackTitle = "sampleStackTitle";
   private static final String key_azimStackTitle = "azimStackTitle";
   private static final String key_interval = "interval";
   private static final String key_scaleWithMag = "scaleWithMag";
   private static final String key_indicatorWidth = "indicatorWidth";
   private static final String key_indicatorLength = "indicatorLength";
   private static final String key_ratioLengthChoice = "ratioLengthChoice";
   private static final String key_thresholdMax = "thresholdMax";
   private static final String key_thresholdMin = "thresholdMin";
   private static final String key_drawChoice = "drawChoice";
   private static final String key_centerMarkers = "centerMarkers";
   private static final String key_color = "color";
   private static final String key_indicatorOpacity = "indicatorOpacity";
   private static final String key_mapAnisotropyToAlpha = "mapAnisotropyToAlpha";
   private static final String key_displayCeiling = "displayCeiling";

   //// Variables from Preferences:
   String sampleStackTitle; //azimLine sample stack
   String azimStackTitle;//azim stack
   OrientationIndicators.Type type = OrientationIndicators.Type.LINE;
   //OrientationIndicators.Type type = OrientationIndicators.Type.ELLIPSE;
   // Display grid interval...
   static final int INTERVAL_DEFAULT = 20;
   int interval;
   // Change display grid scale with magnification level
   boolean scaleWithMag;
   float indicatorWidth;
   // Factor to adjust indicator length
   float indicatorLength;
   // Indicator length proportional to anisotropy
   boolean ratioLengthChoice;
   //boolean lengthProportionalToAnisotropy = true;
   float thresholdMax;
   float thresholdMin;
   boolean dropshadow;
   boolean centerMarkers;
   String colorStr;
   float opacity;
   boolean mapAnisotropyToAlpha;  // Alpha mapped to anisotropy (or intensity)
   private boolean rerun = false;

   public void loadFromPrefs() {
      sampleStackTitle = Prefs.get(key + key_sampleStackTitle, "sampleStack"); //azimLine sample stack
      azimStackTitle = Prefs.get(key + key_azimStackTitle, "azimStack"); //azim stack               
      // TODO OrientationIndicators.Type type = OrientationIndicators.Type.LINE;               
      interval = (int) Prefs.get(key + key_interval, INTERVAL_DEFAULT);
      scaleWithMag = (boolean) Prefs.get(key + key_scaleWithMag, true);
      indicatorWidth = (float) Prefs.get(key + key_indicatorWidth, 2d);
      indicatorLength = (float) Prefs.get(key + key_indicatorLength, 16);
      ratioLengthChoice = Prefs.get(key + key_ratioLengthChoice, false);
      thresholdMax = (float) Prefs.get(key + key_thresholdMax, 1f);
      thresholdMin = (float) Prefs.get(key + key_thresholdMin, 0f);
      dropshadow = Prefs.get(key + key_drawChoice, false);
      centerMarkers = Prefs.get(key + key_centerMarkers, false);
      colorStr = Prefs.get(key + key_color, "Red");
      opacity = (float) Prefs.get(key + key_indicatorOpacity, 1f);
      mapAnisotropyToAlpha = (boolean) Prefs.get(key + key_mapAnisotropyToAlpha, false);
      displayCeiling = (float) Prefs.get(key + key_displayCeiling, 1d);
   }

   private void saveToPref() {
      Prefs.set(key + key_sampleStackTitle, sampleStackTitle);
      Prefs.set(key + key_azimStackTitle, azimStackTitle);
      Prefs.set(key + key_interval, interval);
      Prefs.set(key + key_scaleWithMag, scaleWithMag);
      Prefs.set(key + key_indicatorWidth, indicatorWidth);
      Prefs.set(key + key_indicatorLength, indicatorLength);
      Prefs.set(key + key_ratioLengthChoice, ratioLengthChoice);
      Prefs.set(key + key_thresholdMax, thresholdMax);
      Prefs.set(key + key_thresholdMin, thresholdMin);
      Prefs.set(key + key_drawChoice, dropshadow);
      Prefs.set(key + key_centerMarkers, centerMarkers);
      Prefs.set(key + key_color, colorStr);
      Prefs.set(key + key_indicatorOpacity, opacity);
      Prefs.set(key + key_mapAnisotropyToAlpha, mapAnisotropyToAlpha);
      Prefs.set(key + key_displayCeiling, displayCeiling);
      Prefs.savePreferences();
   }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Color & Alpha">
   // Color
   static Color DefaultColor = Color.white;

   Color color = colorHSBA(0f, 1f, 1f, 1f);
   boolean mapOrientationToHue = (boolean) Prefs.get("oi.mapOrientationToHue", false); // Hue mapped to orientation
   static String[] colors = {"Red", "Green", "Blue", "Magenta", "Cyan", "Yellow", "Orange", "Black", "White"};

   //
   public static Color colorHSBA(float h, float s, float b, float alpha) {
      int aint = (int) (alpha * 254);
      int rgb = Color.HSBtoRGB(h, s, b);
      return new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, aint);
   }

   private static float hueFromColor(Color color) {
      float[] hsb = new float[3];
      Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
      return hsb[0];
   }

   public static Color getColor(String color) {
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
//</editor-fold>

   // ImageJ Plugin... 
//   @Override  // Plugin
//   public int setup(String arg, ImagePlus imp) {
//      // TODO - test type... stack or hyperstack?
//      // Determine PolScope type: Birefringence, FluorPol, Diattenuation
//      //  
//      this.imp1 = imp;
//      return DOES_8G + DOES_16 + SUPPORTS_MASKING + STACK_REQUIRED;
//   }
//
//   @Override // Plugin
//   public void run(ImageProcessor ip) {
//      run(imp1);
//   }
   public void run(ImagePlus imagePlus) {
      this.imp1 = imagePlus;
      loadFromPrefs();
      if (checkIfPolScopeType(imagePlus)) {
         run();
      } else {
         // error("Stack is not an OpenPolScope type")
         IJ.error("Orientation Indicators", "Image/Stack is not a PolStack type");
      }
   }

   public void run() {
      if (showDialog()) {
         if (!rerun) {
            replaceCanvas();
            assignDimensionalControls();
            addListeners();
         }
         //canvas.setIndicatorsColor(color);
         canvas.setEnabled(true);
         runViaListener();
         System.out.println("EDT? " + SwingUtilities.isEventDispatchThread());
         canvas.repaint();
         rerun = false;
//         indicatorsCheckBox.setState(true);
         //enable();
      }
   }

//<editor-fold defaultstate="collapsed" desc="Type and Metadata">
   public boolean checkIfPolScopeType(ImagePlus imagePlus) {
      try {
         JSONObject sumMD = getSummaryMetadata(imagePlus);
         psType = PolScope.getType(sumMD);
         try {
            displayCeiling = getDisplayCeiling(sumMD, psType);
            return true;
         } catch (JSONException ex) {
            // may be an old dataset, get from frame metadata
            try {
               JSONObject frameMD = DatasetUtils.getFrameMetadata(imagePlus, 0, 0, 0, 0);
               displayCeiling = getDisplayCeiling(frameMD, psType);
               return true;
            } catch (JSONException jSONException) {
               // is of known type but cannot find display ceiling, so
               promptForCeiling = true;
               return true;
            }
         }
      } catch (JSONException ex) {
         return false;
      }
   }

   private JSONObject getSummaryMetadata(ImagePlus imp)
           throws JSONException {
      JSONObject sumMD;
      if (DatasetUtils.isMMDataset(imp)) {
         sumMD = DatasetUtils.getSummaryMetadata(imp);
      } else { // is IJ Hyperstack...
         sumMD = PolScope.getSummaryMetadataFromImageInfo(imp);
      }
      return sumMD;
   }
   private String displayCeilingPrompt = "Ceiling: ";
   private String displayCeilingUnits = "(nm)";

   public float getDisplayCeiling(JSONObject meta, PolScope.Type psType)
           throws JSONException {
      float displayCeiling = 0;
      if (psType == PolScope.Type.birefringence) {
         displayCeiling = PolScope.getRetardanceCeiling(meta);
         displayCeilingPrompt = "Retardance Ceiling: ";
         displayCeilingUnits = "(nm)";
      } else if (psType == PolScope.Type.diattenuation || psType == PolScope.Type.fluorescence) {
         displayCeiling = PolScope.getProcessCeiling(meta);
         displayCeilingPrompt = "Ceiling: ";
         displayCeilingUnits = "(0 - 1)";
      }
      return displayCeiling;
   }
//</editor-fold>

   public boolean showDialog() {
//      String azimChoice = "no azim stack";
//      int[] wList = WindowManager.getIDList();
//      if (wList == null) {
//         IJ.noImage();
//         return false;
//      }
//      String[] sampleTitles = new String[wList.length];
//      for (int i = 0; i < wList.length; i++) {
//         ImagePlus imp = WindowManager.getImage(wList[i]);
//         String str = imp != null ? imp.getTitle() : "";
//         if (str.contains("(")) {
//            str = WindowManager.getImage(wList[i]).getWindow().getTitle();
//            int i0 = str.indexOf('(');
//            if (i0 > 1) {
//               str = str.substring(0, i0 - 1);
//            }
//         }
//         sampleTitles[i] = str;
//      }
//      String sampleChoice = sampleTitles[0];
//      for (int i = 1; i < (wList.length); i++) {
//         if (sampleTitles[i] == null ? sampleStackTitle == null : sampleTitles[i].equals(
//                 sampleStackTitle)) {
//            sampleChoice = sampleStackTitle;
//         }
//      }
//      if (sampleChoice.startsWith(LiveWindowName) && sampleTitles.length > 1) {
//         sampleChoice = sampleTitles[1];
//      }
//      String[] azimTitles = new String[wList.length + 1];
//      azimTitles[0] = "no azim stack";
//      for (int i = 1; i < wList.length + 1; i++) {
//         azimTitles[i] = sampleTitles[i - 1];
//      }
//      for (int i = 1; i < (wList.length + 1); i++) {
//         if (azimTitles[i] == null ? azimStackTitle == null : azimTitles[i].equals(azimStackTitle)) {
//            azimChoice = azimStackTitle;
//         }
//      }
      //String[] yesNoQL = new String[]{"Yes", "No"};
      String indicatorType = "Line";
      //float width = 1;
      //
      GenericDialog gd = new GenericDialog("Orientation Indicators");
      //gd.addChoice("sample_stack_title:", sampleTitles, sampleChoice);
      //gd.addChoice("azim_stack_title:", azimTitles, azimChoice);
      if (promptForCeiling) {
         gd.addNumericField(displayCeilingPrompt, displayCeiling, 0, 8, displayCeilingUnits);
      }
      gd.addNumericField("Interval (at 100%): ", interval, 0, 4, "pixels");
      gd.addCheckbox("Scale interval with mag.", scaleWithMag);
      //gd.addChoice("Indicator: ", new String[]{"Line", "Ellipse", "Fan"}, indicatorType);
      gd.addNumericField("Length: ", indicatorLength * 100, 0, 3, "% of interval");
      gd.addNumericField("Width: ", indicatorWidth, 1, 4, "pixels");
      gd.addCheckbox("Length proporational", ratioLengthChoice);
      gd.addNumericField("Threshold min: ", thresholdMin, 2, 4, "(0... 1)");
      gd.addNumericField("Threshold max: ", thresholdMax, 2, 4, "(0... 1)");
      gd.addChoice("Color: ", colors, colorStr);
      gd.addNumericField("Opacity: ", opacity, 2, 4, "(0... 1)");
      //gd.addCheckbox("Dropshadow", dropshadow);
      //gd.addCheckbox("Show_Center", centerMarkers);
      gd.showDialog();
      if (gd.wasCanceled()) {
         return false;
      }
      //int index1 = gd.getNextChoiceIndex();
      //int index2 = gd.getNextChoiceIndex();
      if (promptForCeiling) {
         displayCeiling = (float) gd.getNextNumber();
      }
      interval = (int) gd.getNextNumber();
      scaleWithMag = gd.getNextBoolean();
      if (scaleWithMag && interval < 2) {
         interval = 2;
      }
      //indicatorType = gd.getNextChoice();
      indicatorLength = (float) gd.getNextNumber();
      indicatorLength = indicatorLength / 100;
      if (indicatorLength > 3) {
         indicatorLength = 3;
      }
      if (indicatorLength < 0.25) {
         indicatorLength = 0.25f;
      }
      indicatorWidth = (float) gd.getNextNumber();
      ratioLengthChoice = gd.getNextBoolean();
      thresholdMin = (float) gd.getNextNumber();
      thresholdMax = (float) gd.getNextNumber();
      colorStr = gd.getNextChoice();
      opacity = (float) gd.getNextNumber();
      if (opacity > 1) {
         opacity = 1;
      }
      //dropshadow = gd.getNextBoolean();
      //centerMarkers = gd.getNextBoolean();
      //EnableIndicators = "Yes";//gd.getNextChoice();              
      if (thresholdMin < 0) {
         thresholdMin = 0;
      }
      if (thresholdMax > 1) {
         thresholdMax = 1;
      }
//      imp1 = WindowManager.getImage(wList[index1]);
//      sampleStackTitle = sampleTitles[index1];
//      azimStackTitle = azimTitles[index2];
//      if ("no azim stack".equals(azimStackTitle)) {
//         //only to assign a valid ImagePlus to imp2 which is not used when NoBg
//         imp2 = WindowManager.getImage(wList[index1]);
//      } else {
//         imp2 = WindowManager.getImage(wList[index2 - 1]);
//      }
      //if (EnableIndicators.equals("Yes")) {
      Color myColor = getColor(colorStr);
      color = colorHSBA(hueFromColor(myColor), 1f, 1f, opacity);
      //
      saveToPref();
      //}
      return true;
   }

   public IndicatorCanvas getCanvas() {
      return canvas;
   }

   // 
   public void updateIndicators(final ImagePlus imp1, int index) {
      this.imp1 = imp1;
      width = this.imp1.getWidth();
      height = this.imp1.getHeight();
      createfloatArrays(imp1, index, psType);
      //MathUtils.displayArrayAsImage("Orient", orient, width, height);
      Color myColor = getColor(colorStr);
      color = colorHSBA(hueFromColor(myColor), 1f, 1f, opacity);
      canvas.setIndicatorsColor(color);
      boolean regenerate = setScaling((float) canvas.getMagnification()) || (lastIndex != index)
              || rerun;
      System.out.println("regenerate = " + regenerate);
      if (regenerate) {
         generateAreasAndIndicators();
      }
   }

   // Called from IndicatorCanvas on change of magnification
   void rescaleIndicators(float magnification) {
      // if cellSize is 1
      boolean regenerate = setScaling((float) canvas.getMagnification());
      System.out.println("regenerate = " + regenerate);
      if (regenerate) {
         if (orient != null && anisotropy != null) {
            generateAreasAndIndicators();
         }
      }
      lastMagnification = magnification;
   }

   public boolean setScaling(float magnification) {
      if (scaleWithMag) {
         scaledCellSize = (int) (interval / magnification);
         scaledLength = (float) (scaledCellSize * indicatorLength);
         //scaledLength = (float)interval / (float)magnification * (float)indicatorLength;
      } else {
         scaledCellSize = interval;
         scaledLength = (float) indicatorLength * scaledCellSize;
      }
      float scaledStrokeWidth = (float) (indicatorWidth / magnification);
      canvas.setStrokeWidth(scaledStrokeWidth);
      if (scaledLength < 1) {
         scaledLength = 1f;
      }
      if (scaledCellSize < 1) {
         scaledCellSize = 1;
      }
      if (cellSizeLabel != null) {
         this.cellSizeLabel.setText("[" + scaledCellSize + "x" + scaledCellSize + "]");
      }
      System.out.println("Cell, length: " + scaledCellSize + ", " + scaledLength);
      if (scaledCellSize == lastCellSize && !rerun && scaleWithMag) {
         return false;
      }
      lastCellSize = scaledCellSize;
      return true;
   }

   public void createfloatArrays(final ImagePlus imp1, int index, PolScope.Type psType) {
      ImageStack stack1 = this.imp1.getStack();
      // Convert pixel values to measurement values
      int length = width * height;
      ImageToFloatConverter converter = new ImageToFloatConverter();
      anisotropy = converter.getAnisotropyArray(stack1, length, index);
      orient = converter.getOrientationArray(stack1, length, index, 2);
      if (psType == PolScope.Type.diattenuation || psType == PolScope.Type.fluorescence) {
         intensity = converter.getIntensityArray(stack1, length, index);
      } else {
         intensity = null;
      }
   }

   public void generateAreasAndIndicators() {
      System.out.println("Generating...");
      //System.out.println("scaledCellSizer = " + scaledCellSize);
      cellSizeLabel.setText("[" + scaledCellSize + "x" + scaledCellSize + "]");
      areas = generateAreas(imp1, anisotropy, orient, intensity, scaledCellSize, type);
      indicators = generateIndicators(imp1, areas,
              scaledCellSize, type, scaledLength, ratioLengthChoice);
      canvas.setIndicators(indicators);
      //canvas.repaint();
   }

   public Vector<AveragedArea> getAveragedAreas() {
      return areas;
   }

   private Vector<AveragedArea> generateAreas(ImagePlus imp,
           float[] anisotropy, float[] orient, float[] intensity,
           int cellSize, Type type) {
      areas = new Vector<AveragedArea>();
      CircularStatistics stat = new CircularStatistics();

      int w = imp.getWidth();
      int h = imp.getHeight();

      int nX = w / cellSize;
      int nY = h / cellSize;
      //int nX = w / cellSize - 1;
      //int nY = h / cellSize - 1;

      //int nX = (int) Math.ceil((float) roiRect.width / interval + 0.5);
      //int nY = (int) Math.ceil((float) roiRect.height / interval + 0.5);
      //System.out.println("nX, nY: " + nX +", " + nY);
      //      xIHalf = (int) Math.ceil((float) interval / 2);
      //      yIHalf = (int) Math.ceil((float) interval / 2);
      int numCells = nX * nY;
      float[] orientCell = new float[cellSize * cellSize];
      float[] anisoptropyCell = new float[cellSize * cellSize];
      float[] intensityCell = new float[cellSize * cellSize];
//      for (int i = 1; i < iMax; i++) {
//         for (int j = 1; j < jMax; j++) {
//            drawGlyphs(i, j, graphics);
//         }
//      }
      // What if interval ==1 ???

      // for each cell
      for (int n = 0; n < nX; n++) {
         for (int m = 0; m < nY; m++) {
//            int x0 = roiRect.x + (n * cellSize);
//            int y0 = roiRect.y + (m * cellSize);
            int x0 = (n * cellSize);
            int y0 = (m * cellSize);
//            float centerX = x0 + cellSize / 2f;
//            float centerY = y0 + cellSize / 2f;
            //System.out.println("  " + x0 + ", " + y0 + ", " +centerX + ", " +centerY );
            AveragedArea aa;
            if (cellSize > 1) {
               float centerX = x0 + cellSize / 2f;
               float centerY = y0 + cellSize / 2f;
               // within each cell, calculate averages...
               int count = 0;
               for (int i = 0; i < cellSize; i++) {
                  for (int j = 0; j < cellSize; j++) {
                     int offset = (y0 + j) * w + (x0 + i);
                     //int offset = j * w + i;
                     if (offset >= orient.length) {
                        break;
                     }
                     orientCell[count] = (float) orient[offset];
                     anisoptropyCell[count] = (float) anisotropy[offset];
                     if (intensity == null) {
                        intensityCell[count] = 1.0f;
                     } else {
                        intensityCell[count] = (float) intensity[offset];
                     }
                     count++;
                  }
               }
               float[] cellAverages = stat.process(orientCell, anisoptropyCell, intensityCell);
               //process returns: [meanR, meanTheta, std, intensity] ??? which std
               //AveragedArea(x, y,  intensity, anisotropy, orientation, orientationVariance)
               aa = new AveragedArea(centerX, centerY,
                       cellAverages[3], cellAverages[0], cellAverages[1], cellAverages[2]);
            } else {
               int offset = m * w + n;
               float i;
               if (intensity == null) {
                  i = 1.0f;
               } else {
                  i = (float) intensity[offset];
               }
               aa = new AveragedArea(x0, y0, i,
                       (float) anisotropy[offset], (float) orient[offset], 0);
            }
            areas.add(aa);
         }
      }

      return areas;
   }

      private Vector<AveragedArea> generateAreasFromRois(ImagePlus imp,
           float[] anisotropy, float[] orient, float[] intensity,
           int cellSize, Type type) {
      areas = new Vector<AveragedArea>();
      CircularStatistics stat = new CircularStatistics();

      int w = imp.getWidth();
      int h = imp.getHeight();

      int nX = w / cellSize;
      int nY = h / cellSize;
      //int nX = w / cellSize - 1;
      //int nY = h / cellSize - 1;

      //int nX = (int) Math.ceil((float) roiRect.width / interval + 0.5);
      //int nY = (int) Math.ceil((float) roiRect.height / interval + 0.5);
      //System.out.println("nX, nY: " + nX +", " + nY);
      //      xIHalf = (int) Math.ceil((float) interval / 2);
      //      yIHalf = (int) Math.ceil((float) interval / 2);
      int numCells = nX * nY;
      float[] orientCell = new float[cellSize * cellSize];
      float[] anisoptropyCell = new float[cellSize * cellSize];
      float[] intensityCell = new float[cellSize * cellSize];
//      for (int i = 1; i < iMax; i++) {
//         for (int j = 1; j < jMax; j++) {
//            drawGlyphs(i, j, graphics);
//         }
//      }
      // What if interval ==1 ???

      // for each cell
      for (int n = 0; n < nX; n++) {
         for (int m = 0; m < nY; m++) {
//            int x0 = roiRect.x + (n * cellSize);
//            int y0 = roiRect.y + (m * cellSize);
            int x0 = (n * cellSize);
            int y0 = (m * cellSize);
//            float centerX = x0 + cellSize / 2f;
//            float centerY = y0 + cellSize / 2f;
            //System.out.println("  " + x0 + ", " + y0 + ", " +centerX + ", " +centerY );
            AveragedArea aa;
            if (cellSize > 1) {
               float centerX = x0 + cellSize / 2f;
               float centerY = y0 + cellSize / 2f;
               // within each cell, calculate averages...
               int count = 0;
               for (int i = 0; i < cellSize; i++) {
                  for (int j = 0; j < cellSize; j++) {
                     int offset = (y0 + j) * w + (x0 + i);
                     //int offset = j * w + i;
                     if (offset >= orient.length) {
                        break;
                     }
                     orientCell[count] = (float) orient[offset];
                     anisoptropyCell[count] = (float) anisotropy[offset];
                     if (intensity == null) {
                        intensityCell[count] = 1.0f;
                     } else {
                        intensityCell[count] = (float) intensity[offset];
                     }
                     count++;
                  }
               }
               float[] cellAverages = stat.process(orientCell, anisoptropyCell, intensityCell);
               //process returns: [meanR, meanTheta, std, intensity] ??? which std
               //AveragedArea(x, y,  intensity, anisotropy, orientation, orientationVariance)
               aa = new AveragedArea(centerX, centerY,
                       cellAverages[3], cellAverages[0], cellAverages[1], cellAverages[2]);
            } else {
               int offset = m * w + n;
               float i;
               if (intensity == null) {
                  i = 1.0f;
               } else {
                  i = (float) intensity[offset];
               }
               aa = new AveragedArea(x0, y0, i,
                       (float) anisotropy[offset], (float) orient[offset], 0);
            }
            areas.add(aa);
         }
      }

      return areas;
   }
   private Vector<Indicator> generateIndicators(ImagePlus imp,
           Vector<AveragedArea> areas,
           int cellSize, Type type, float length, boolean lengthProportionalToAnisotropy) {

      Vector<Indicator> _indicators = new Vector<Indicator>();
      OrientationIndicators og = new OrientationIndicators();
      for (AveragedArea aa : areas) {
         if (aa.anisotropy >= thresholdMin && aa.anisotropy <= thresholdMax) {
            float adjustedLength;
            if (lengthProportionalToAnisotropy) {
               adjustedLength = aa.anisotropy * (float) length;
            } else {
               adjustedLength = (float) length;
            }
            float x = aa.x;
            float y = aa.y;
            if(cellSize == 1) {
               x = x + 0.5f;
               y = y + 0.5f;
            }
            // stroke, if indicator
            if (type == Type.LINE) {
               _indicators.add(new Indicator(og.createLineAt(x, y,
                       aa.orientation, adjustedLength, aa.orientationVariance),
                       color, null));
            }
            if (type == Type.ELLIPSE) {
               if (cellSize > 1) {
                  _indicators.add(new Indicator(og.createEllipseAt(x, y,
                          aa.orientation, adjustedLength, aa.orientationVariance),
                          color, null));
               } else {
                  _indicators.add(new Indicator(og.createLineAt(x, y,
                          aa.orientation, adjustedLength, aa.orientationVariance),
                          color, null));
               }
            }
            if (type == Type.FAN) {
               if (cellSize > 1) {
                  _indicators.add(new Indicator(og.createFanAt(x, y,
                          aa.orientation, adjustedLength, aa.orientationVariance),
                          color, null));
               } else {
                  _indicators.add(new Indicator(og.createLineAt(x, y,
                          aa.orientation, adjustedLength, aa.orientationVariance),
                          color, null));
               }
            }
         }
//         if (aa.orientation == 0 && aa.orientation == 90) {
//            System.out.println("anis: " + aa.anisotropy + " :  " + aa.orientation + " - [" + aa.x
//                    + "," + aa.y + "]");
//         }
      }
      return _indicators;
   }

   // ==========================
   Label cursorValueLabel;
   Label cellSizeLabel;
   Label ceilingLabel;
   Checkbox ch;

   public void replaceCanvas() {
      // make this the ImageCanvas of the ImagePlus by replacing the window.
      canvas = new IndicatorCanvas(imp1, null, 1, 1, 1);
      canvas.setIndicatorMaker(this);
//      canvas.setIndicatorsColor(color);
      if (imp1.getStackSize() == 1) {
         imp1.setWindow(new ImageWindow(imp1, canvas));
      } else {
         imp1.setWindow(new StackWindow(imp1, canvas));
      }
      // Add control sub-panel
      ImageWindow win = imp1.getWindow();
      Panel p = createSubPanel();
      win.add(p);
      win.pack();
   }

   public Panel createSubPanel() {
      Panel p = new Panel();
      p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
      // add readout for anisotropy/orientation values by pixel
      cursorValueLabel = new Label("(x,y)  AA.A, OO");
      cursorValueLabel.setPreferredSize(new Dimension(190, 24));
      p.add(cursorValueLabel);
      p.add(Box.createHorizontalStrut(5));
      ceilingLabel = new Label("" + this.displayCeiling);
      ceilingLabel.setPreferredSize(new Dimension(30, 24));
      p.add(ceilingLabel);
      p.add(Box.createHorizontalStrut(5));
      cellSizeLabel = new Label("[" + scaledCellSize + "x" + scaledCellSize + "]");
      cellSizeLabel.setPreferredSize(new Dimension(50, 24));
      p.add(cellSizeLabel);
      p.add(Box.createHorizontalGlue());
      // CheckBox for enable/disable indicators
      final JCheckBox indicatorsCheck = new JCheckBox("Indicators");
      // set indicators to null 
      indicatorsCheck.setSelected(true);
      indicatorsCheck.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent e) {
            if (indicatorsCheck.isSelected()) {
               enable();
            } else {
               disable();
            }
         }
      });
      p.add(indicatorsCheck);
      p.add(Box.createHorizontalStrut(5));
      final JCheckBox hideImageCheck = new JCheckBox("HideImage");
      hideImageCheck.setSelected(false);
      hideImageCheck.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent e) {
            if (canvas != null) {
               canvas.setHideImage(hideImageCheck.isSelected());
               canvas.repaint();
            }
         }
      });
      p.add(hideImageCheck);
      p.add(Box.createHorizontalStrut(5));
      Button testButton = new Button("test");
      testButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            //measureFromRois();
            measureRois();
         }

         private void measureRois() {
            OrientationAnalyzer oa = new OrientationAnalyzer(imp1);
         }

      });
      p.add(testButton);
      p.add(Box.createHorizontalStrut(5));
      // ColorMapping
//      p.add(new Button("Create Color-mapped stack"));
//      p.add(Box.createHorizontalStrut(5));

      // ReRun
      Button reRunButton = new Button("reRun");
      reRunButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            lastCellSize = 0;
            rerun = true;
            run();
         }
      });
      p.add(reRunButton);
      p.add(Box.createHorizontalStrut(5));
      // Export
      Button exportButton = new Button("Export");
      exportButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            Exporter exp = new Exporter(Orientation_Indicators.this);
            exp.promptForExport();
         }
      });
      p.add(exportButton);
      return p;
   }

   private void showValues() {
      System.out.println(this.toString());
//      System.out.println("" + interval + "\n" + //indicatorType  + "\n" +
//              indicatorLength + "\n" + indicatorWidth + "\n" + ratioLengthChoice + "\n"
//              + thresholdMin + "\n" + thresholdMax + "\n" + colorStr + "\n" + opacity + "\n"
//              + dropshadow + "\n" + centerMarkers + "\n");
   }



   void cursorMoved(int ox, int oy) {
      // get the aniso & orient values at this pixel
      int offset = oy * width + ox;

      if (anisotropy != null && orient != null) {
         if (offset > anisotropy.length) {
            return;
         }
         float a = anisotropy[offset] * displayCeiling;
         float o = (float) (orient[offset] * 180 / Math.PI);
         String s = String.format("(%4d, %4d)   %4.1f nm  %4.0f deg", ox, oy, a, o);
         cursorValueLabel.setText(s);
      }
   }

   void error() {
      IJ.showMessage("Draws Orientation indicators into overlay."
              + "\nThis plugin requires one or two stacks (two if azimuth is separate) that have"
              + "\nthe same width, height, data type, and at least two slices");
   }

   public void enable() {
      //imp1 = imp;
      //imp2 = imp;
      //UseListener = "Yes";
      //EnableIndicators = "Yes";
      canvas.setEnabled(true);
      addListeners();
      runViaListener();
      canvas.repaint();
   }

   public void disable() {
      //UseListener = "No";
      //EnableIndicators = "No";
      canvas.setEnabled(false);
      canvas.repaint();
      removeListeners();
   }
   //=================================================================================   
//<editor-fold defaultstate="collapsed" desc="Dimensional Controller Listening">
   //String UseListener = "Yes";
   private ScrollbarWithLabel cSelectorChannel_ = new ScrollbarWithLabel();
   private ScrollbarWithLabel tSelectorTime_ = new ScrollbarWithLabel();
   private ScrollbarWithLabel zSelectorZSlice_ = new ScrollbarWithLabel();
   private ScrollbarWithLabel pSelectorPos = new ScrollbarWithLabel();

   public void assignDimensionalControls() {
      // gets the currently selected dataset upon which it will draw the indicators
      frame = imp1.getWindow();
      Component[] comps = frame.getComponents();
      try {
         cSelectorChannel_ = (ScrollbarWithLabel) comps[1];
         if (comps.length > 2) {
            tSelectorTime_ = (ScrollbarWithLabel) comps[2];
         }
         if (comps.length > 3) {
            zSelectorZSlice_ = (ScrollbarWithLabel) comps[3];
         }
      } catch (Exception ex) {
      }
      try {
         pSelectorPos = (ScrollbarWithLabel) comps[4];
      } catch (Exception ex) {
         System.out.println(
                 "Warning: Orientation indicators could not attach to Position dim. It might not be present.");
      }
//      if (imp1.getRoi() == null && imp1.getCanvas().getMagnification() > 1) {
//         Rectangle rec = new Rectangle(imp1.getCanvas().getSrcRect());
//         imp1.setRoi(rec);
//      }
//      if ("Yes".equals(UseListener) && "Yes".equals(EnableIndicators)) {
//         //loadParams();
//         addListeners();
//      } else {
//         //UseListener = "No";
//         EnableIndicators = "No";
//         removeListeners();
//      }
   }
   AdjustmentListener adjustmentListener = new AdjustmentListener() {
      @Override
      public void adjustmentValueChanged(AdjustmentEvent evt) {
         //if ("Yes".equals(UseListener) && "Yes".equals(EnableIndicators)) {
         runViaListener();
         canvas.repaint();
         //}
      }
   };
   AdjustmentListener adjustmentListenerPos = new AdjustmentListener() {
      @Override
      public void adjustmentValueChanged(AdjustmentEvent evt) {
         //if ("Yes".equals(UseListener) && "Yes".equals(EnableIndicators)) {
         runViaListenerPos();
         canvas.repaint();
         //}
      }
   };

   public void addListeners() {
      // cSelectorChannel_.addAdjustmentListener(adjustmentListener);
      tSelectorTime_.addAdjustmentListener(adjustmentListener);
      zSelectorZSlice_.addAdjustmentListener(adjustmentListener);
      pSelectorPos.addAdjustmentListener(adjustmentListenerPos);
   }

   public void removeListeners() {
      // cSelectorChannel_.removeAdjustmentListener(adjustmentListener);
      tSelectorTime_.removeAdjustmentListener(adjustmentListener);
      zSelectorZSlice_.removeAdjustmentListener(adjustmentListener);
      pSelectorPos.removeAdjustmentListener(adjustmentListenerPos);
   }

   public void runViaListener() {
      if (imp1.isVisible()) {
         int slices = imp1.getZ();
         int frames = imp1.getT();
         int channels = 1; // (imp1).getC();
         int index = imp1.getStackIndex(channels, slices, frames) - 1;
         if (lastIndex != index || rerun) {
            System.out.println("index: " + index);
            //imp1 = IJ.getImage();
            updateIndicators(imp1, index);

         }
         lastIndex = index;
      }
   }

   public void runViaListenerPos() {
      if (imp1.isVisible()) {
         lastIndex = -1;
         int slices = imp1.getZ();
         int frames = imp1.getT();
         int channels = 1; // (imp1).getC();
         int index = imp1.getStackIndex(channels, slices, frames) - 1;
         if (lastIndex != index || rerun) {
            System.out.println("TP: " + index);
            //imp1 = IJ.getImage();
            updateIndicators(imp1, index);

         }
         lastIndex = index;
      }
   }

   //</editor-fold>
   @Override
   public String toString() {
      return "Orientation_Indicators{" + "PluginName_=" + PluginName_ + ", width=" + width
              + ", height=" + height + ", canvas=" + canvas + ", circleWidth=" + circleWidth
              + ", indicatorsCheckBox=" + indicatorsCheckBox + ", lastIndex=" + lastIndex
              + ", psType=" + psType
              + ", scaledCellSize=" + scaledCellSize + ", lastCellSize=" + lastCellSize
              + ", retardanceCeiling=" + displayCeiling + ", sampleStackTitle=" + sampleStackTitle
              + ", azimStackTitle=" + azimStackTitle + ", type=" + type + ", interval=" + interval
              + ", scaleWithMag=" + scaleWithMag + ", indicatorWidth=" + indicatorWidth
              + ", indicatorLength=" + indicatorLength + ", ratioLengthChoice=" + ratioLengthChoice
              + ", thresholdMax=" + thresholdMax + ", thresholdMin=" + thresholdMin
              + ", dropshadow=" + dropshadow + ", centerMarkers=" + centerMarkers + ", colorStr="
              + colorStr + ", opacity=" + opacity + ", mapAnisotropyToAlpha=" + mapAnisotropyToAlpha
              + ", color=" + color + '}';
   }

}
/**
 * Change history
 *
 * Version 3, 29 June 2012 added Listener for timepoint in a stack to recompute AzimLines
 *
 * Version 2, 31 March 2010 added central dot and contrasting outindicator option (only black and
 * White)
 *
 * Version 2b, 9 June 2010 Added the option of a second stack that holds the azimuth data; if single
 * stack, azimuth is expected in 2nd slice.
 *
 * Version 2c, 14 July 2010 added the option of making the length of the azimuth indicator
 * proportional to the ratio value works only with a PolScope stack, not with separate azimuth stack
 *
 * 3 Oct 2011 added units to the parameters listed in the query window
 *
 * Aug 2013, GBH: removed tons of redundant calculations. Aug 2013, GBH: Complete rework... using
 * IndicatorCanvas and Shapes as indicators
 *
 *
 * Then...
 */
/*
 * Copyright © 2009 – 2013, Marine Biological Laboratory
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, 
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution.

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS “AS IS�? AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those of 
 * the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of any organization.
 * 
 * @author Rudolf Oldenbourg
 * @author Amitabh Verma (averma@mbl.edu)
 * @author Grant B. Harris
 * Marine Biological Laboratory, Woods Hole, Mass.
 * 
 * Draws orientation indicators in non-distructive overlay based on orientation data
 * stored in 2nd slice or in separate stack; 
 * 
 *
 * see change history at end of file 
 */
