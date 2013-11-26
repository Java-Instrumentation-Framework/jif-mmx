
package edu.mbl.cdp.ps.orient;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.ImageCanvas;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.measure.Measurements;
import ij.plugin.filter.Analyzer;
import ij.plugin.frame.Recorder;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics; 
import ij.util.Tools;
import java.awt.Rectangle;

/**
 *
 * @author GBH
 */
public class OrientationAnalyzer {
   
   private ImagePlus imp1;
   private boolean appendResults;
   private boolean onePerSlice;
   private boolean measureAll;

   public OrientationAnalyzer(ImagePlus imp1) {
      this.imp1 = imp1;
   }
   
   //
   // ROI Measurements =================================================================
   //
   private void measureFromRois() {
      if (RoiManager.getInstance() != null) { // get Rois from RoiManager
         Roi[] rois = RoiManager.getInstance().getSelectedRoisAsArray();
         doMeasurements(rois);
      } else { // Use the Roi in the ImagePlus
         Roi roi = imp1.getRoi();
         if (roi != null) {
            doMeasurements(new Roi[]{roi});
         }
      }
   }

   private void measureFromRoi(Roi roi) {
      if(roi.getClass().isAssignableFrom(ShapeRoi.class)) {
         System.out.println("Roi is a shape.");
      }
      //roi.getMask()
              
      

   }

   private void doMeasurements(Roi[] rois) {
      // get Roi(s)
      for (Roi roi : rois) {
         System.out.println(roi);
         ImageProcessor ip = imp1.getProcessor();
         ip.setRoi(roi);

         // create AveragedArea
         // measure the roi area
         ImageStatistics stats = ImageStatistics.getStatistics(ip, Measurements.AREA
                 + Measurements.CENTROID, null);

         // OrientationStatistics.
         // 
         // find the Centroid point for the indicator
         // create the indicator
      }

   }

   // modeled on RoiManager.measure(mode)
   
   boolean measureWithRoiManager(int mode) {
      if (imp1 == null) {
         return false;
      }
      RoiManager rm = RoiManager.getInstance();
      if (rm == null) {
         return false;
      }
      Roi[] rois = rm.getSelectedRoisAsArray();
      if (rois.length == 0) {
         rois = rm.getRoisAsArray();
      }
      if (rois.length == 0) {
         return false;
      }
      //
      boolean allSliceOne = true;
      for (int i = 0; i < rois.length; i++) {
         String label = rois[i].getName();  // (String) listModel.getElementAt(indexes[i]);
         if (getSliceNumber(rois[i], label) > 1) {
            allSliceOne = false;
         }
      }
		//int measurements = Analyzer.getMeasurements();
      //if (imp1.getStackSize()>1)
      //  Analyzer.setMeasurements(measurements|Measurements.SLICE);
      int currentSlice = imp1.getCurrentSlice();
      for (int i = 0; i < rois.length; i++) {
         if (restore(rm, imp1, rois[i], !allSliceOne, false)) {
            //IJ.run("Measure");
            this.measureFromRoi(rois[i]);
            System.out.println("Measure: " + rois[i].getName() + " - " + rois[i].getBounds());
         } else {
            break;
         }
      }
      imp1.setSlice(currentSlice);
		//Analyzer.setMeasurements(measurements);
//		if (indexes.length>1)
//			IJ.run("Select None");
      //if (record()) Recorder.record("roiManager", "Measure");
      return true;
   }

   /* This method performs measurements for several ROI's in a stack
		and arranges the results with one line per slice.  By constast, the 
		measure() method produces several lines per slice.	The results 
		from multiMeasure() may be easier to import into a spreadsheet 
		program for plotting or additional analysis. Based on the multi() 
		method in Bob Dougherty's Multi_Measure plugin
		(http://www.optinav.com/Multi-Measure.htm).
	*/
   private ij.measure.ResultsTable mmResults;
   
//	boolean multiMeasure(String cmd) {
//		ImagePlus imp = getImage();
//		if (imp==null) return false;
//		int[] indexes = getSelectedIndexes();
//		if (indexes.length==0)
//			indexes = getAllIndexes();
//		if (indexes.length==0) return false;
//		int measurements = Analyzer.getMeasurements();
//
//		int nSlices = imp.getStackSize();
//		if (cmd!=null)
//			appendResults = cmd.contains("append")?true:false;
//		if (IJ.isMacro()) {
//			if (nSlices>1) measureAll = true;
//			onePerSlice = true;
//		} else {
//			GenericDialog gd = new GenericDialog("Multi Measure");
//			if (nSlices>1)
//				gd.addCheckbox("Measure all "+nSlices+" slices", measureAll);
//			gd.addCheckbox("One Row Per Slice", onePerSlice);
//			gd.addCheckbox("Append results", appendResults);
//			int columns = getColumnCount(imp, measurements)*indexes.length;
//			String str = nSlices==1?"this option":"both options";
//			gd.setInsets(10, 25, 0);
//			gd.addMessage(
//				"Enabling "+str+" will result\n"+
//				"in a table with "+columns+" columns."
//			);
//			gd.showDialog();
//			if (gd.wasCanceled()) return false;
//			if (nSlices>1)
//				measureAll = gd.getNextBoolean();
//			onePerSlice = gd.getNextBoolean();
//			appendResults = gd.getNextBoolean();
//		}
//		if (!measureAll) nSlices = 1;
//		int currentSlice = imp.getCurrentSlice();
//		
//		if (!onePerSlice) {
//			int measurements2 = nSlices>1?measurements|Measurements.SLICE:measurements;
//			ij.measure.ResultsTable rt = new ij.measure.ResultsTable();
//			Analyzer analyzer = new Analyzer(imp, measurements2, rt);
//			for (int slice=1; slice<=nSlices; slice++) {
//				if (nSlices>1) imp.setSliceWithoutUpdate(slice);
//				for (int i=0; i<indexes.length; i++) {
//					if (restoreWithoutUpdate(indexes[i]))
//						analyzer.measure();
//					else
//						break;
//				}
//			}
//			rt.show("Results");
//			if (nSlices>1) imp.setSlice(currentSlice);
//			return true;
//		}
//
//		Analyzer aSys = new Analyzer(imp); // System Analyzer
//		ij.measure.ResultsTable rtSys = Analyzer.getResultsTable();
//		ij.measure.ResultsTable rtMulti = new ij.measure.ResultsTable();
//		if (appendResults && mmResults!=null)
//			rtMulti = mmResults;
//		rtSys.reset();
//		//Analyzer aMulti = new Analyzer(imp, measurements, rtMulti); //Private Analyzer
//
//		for (int slice=1; slice<=nSlices; slice++) {
//			int sliceUse = slice;
//			if (nSlices==1) sliceUse = currentSlice;
//			imp.setSliceWithoutUpdate(sliceUse);
//			rtMulti.incrementCounter();
//			if ((Analyzer.getMeasurements()&Measurements.LABELS)!=0)
//				rtMulti.addLabel("Label", imp.getTitle());
//			int roiIndex = 0;
//			for (int i=0; i<indexes.length; i++) {
//				if (restore(imp1, index, false, true)){
//                    //restoreWithoutUpdate(indexes[i])) {
//					roiIndex++;
//					aSys.measure();
//					for (int j=0; j<=rtSys.getLastColumn(); j++){
//						float[] col = rtSys.getColumn(j);
//						String head = rtSys.getColumnHeading(j);
//						String suffix = ""+roiIndex;
//						Roi roi = imp.getRoi();
//						if (roi!=null) {
//							String name = roi.getName();
//							if (name!=null && name.length()>0 && (name.length()<9||!Character.isDigit(name.charAt(0))))
//								suffix = "("+name+")";
//						}
//						if (head!=null && col!=null && !head.equals("Slice"))
//							rtMulti.addValue(head+suffix, rtSys.getValue(j,rtSys.getCounter()-1));
//					}
//				} else
//					break;
//			}
//		}
//		mmResults = (ij.measure.ResultsTable)rtMulti.clone();
//		rtMulti.show("Results");
//
//		imp.setSlice(currentSlice);
//		if (indexes.length>1)
//			IJ.run("Select None");
//		return true;
//	}
   /**
    * Returns the slice number associated with the specified name, or -1 if the name does not
    * include a slice number.
    */
   public int getSliceNumber(String label) {
      // Copied from RoiManager
      int slice = -1;
      if (label.length() >= 14 && label.charAt(4) == '-' && label.charAt(9) == '-') {
         slice = (int) Tools.parseDouble(label.substring(0, 4), -1);
      } else if (label.length() >= 17 && label.charAt(5) == '-' && label.charAt(11) == '-') {
         slice = (int) Tools.parseDouble(label.substring(0, 5), -1);
      } else if (label.length() >= 20 && label.charAt(6) == '-' && label.charAt(13) == '-') {
         slice = (int) Tools.parseDouble(label.substring(0, 6), -1);
      }
      return slice;
   }

   /**
    * Returns the slice number associated with the specified ROI or name, or -1 if the ROI or name
    * does not include a slice number.
    */
   int getSliceNumber(Roi roi, String label) {
      // Copied from RoiManager
      int slice = roi != null ? roi.getPosition() : -1;
      if (slice == 0) {
         slice = -1;
      }
      if (slice == -1) {
         slice = getSliceNumber(label);
      }
      return slice;
   }

   boolean restore(RoiManager rm, ImagePlus imp, Roi roi, boolean setSlice, boolean noUpdateMode) {
      // Copied from RoiManager
      // setSlice = true if Rois have been defined on more than one slice...
      if (setSlice) {
         int c = roi.getCPosition();
         int z = roi.getZPosition();
         int t = roi.getTPosition();
         boolean hyperstack = imp.isHyperStack();
         //IJ.log("restore: "+hyperstack+" "+c+" "+z+" "+t);
         if (hyperstack && (c > 0 || z > 0 || t > 0)) {
            imp.setPosition(c, z, t);
         } else {
            int n = getSliceNumber(roi, roi.getName());
            if (n >= 1 && n <= imp.getStackSize()) {
               if (hyperstack) {
                  if (imp.getNSlices() > 1 && n < imp.getNSlices()) {
                     imp.setPosition(imp.getC(), n, imp.getT());
                  } else if (imp.getNFrames() > 1 && n < imp.getNFrames()) {
                     imp.setPosition(imp.getC(), imp.getZ(), n);
                  } else {
                     imp.setPosition(n);
                  }
               } else {
                  imp.setSlice(n);
               }
            }
         }
      }
      //??
//		if (rm.showAllCheckbox.getState() && !restoreCentered && !noUpdateMode) {
//			roi.setImage(null);
//			imp.setRoi(roi);
//			return true;
//		}
      Roi roi2 = (Roi) roi.clone();
      Rectangle r = roi2.getBounds();
      int width = imp.getWidth(), height = imp.getHeight();
      boolean restoreCentered = false;
      if (restoreCentered) {
         ImageCanvas ic = imp.getCanvas();
         if (ic != null) {
            Rectangle r1 = ic.getSrcRect();
            Rectangle r2 = roi2.getBounds();
            roi2.setLocation(r1.x + r1.width / 2 - r2.width / 2, r1.y + r1.height / 2 - r2.height
                    / 2);
         }
      }
      if (r.x >= width || r.y >= height || (r.x + r.width) < 0 || (r.y + r.height) < 0) {
         roi2.setLocation((width - r.width) / 2, (height - r.height) / 2);
      }
      if (noUpdateMode) {
         imp.setRoi(roi2, false);
         noUpdateMode = false;
      } else {
         imp.setRoi(roi2, true); 
      }
      return true;
   }

//   boolean restoreWithoutUpdate(int index) {
//      return restore(imp1, index, false, true);
//   }
   //====================================================================================
   //

   /*
   From Wayne...
   It is easy to add custom measurements to the Results table. This example macro opens the Blobs sample image, creates a selection, measures the area, mean, centroid and perimeter, and then calculates the perimeter mean and adds it to the same row of the Results table as "PMean". This macro has a keyboard shortcut so you can run it by typing "1".

  macro "Measure Boundary Mean [1]" {
     saveSettings;
     run("Blobs (25K)");
     setAutoThreshold("Default");
     doWand(114, 82);
     run("Interpolate", "interval=1 smooth");
     resetThreshold();
     run("To Selection");
     run("Set Measurements...", "area mean centroid perimeter");
     run("Measure");
     getSelectionCoordinates(x, y);
     n = x.length;
     sum = 0;
     for (i=0; i<n; i++)
        sum += getPixel(x[i], y[i]);
     mean = sum/n;
     setResult("PMean", nResults-1, mean);
     restoreSettings;
  }
   */
}
