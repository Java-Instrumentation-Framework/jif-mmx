/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mbl.jif.datasetconvert;

import edu.mbl.jif.imaging.dataset.MMgrDatasetAccessor;
import edu.mbl.jif.imaging.dataset.MMgrDatasetGenerator;
import edu.mbl.jif.utils.PrintWhatever;
import edu.mbl.jif.imaging.dataset.metadata.DimensionalExtents;
import edu.mbl.jif.imaging.dataset.metadata.FrameMetadata;
import edu.mbl.jif.imaging.dataset.metadata.SumMetadata;
import edu.mbl.jif.imaging.dataset.tests.DatasetGenTester;
import edu.mbl.jif.imaging.mmtiff.ImageUtils;
import edu.mbl.jif.imaging.mmtiff.MDUtils;
import edu.mbl.jif.imaging.mmtiff.TaggedImage;
import edu.mbl.jif.datasetconvert.job.JobMonitorPanel;
import foxtrot.Job;
import ij.process.ImageProcessor;
import java.awt.Rectangle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author GBH
 */
public class DatasetCropper extends Job {

   private JobMonitorPanel jobMonitor;
   String inputPath;
   String inputPrefix;
   String outputDir;
   String outputPrefix;
   int[] channelIndices;
   int[] sliceIndices;
   int[] frameIndices;
   int[] positionIndices;
   boolean isMultiPage = true;
   boolean seperateMDFile = false;
   boolean seperateFilesForPositions = false;
   Rectangle roi;

   public DatasetCropper(
           JobMonitorPanel jobMonitor,
           String inputPath, String inputPrefix, String outputDir, String outputPrefix,
           Rectangle roi,
           boolean isMultiPage, boolean seperateMDFile, boolean seperateFilesForPositions) {
      this.inputPath = inputPath;
      this.inputPrefix = inputPrefix;
      this.outputDir = outputDir;
      this.outputPrefix = outputPrefix;
      this.roi = roi;
      this.isMultiPage = isMultiPage;
      this.seperateMDFile = seperateMDFile;
      this.seperateFilesForPositions = seperateFilesForPositions;
      this.jobMonitor = jobMonitor;
   }

   @Override
   public Object run() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public boolean crop() {

      try {
         MMgrDatasetAccessor datasetIn = new MMgrDatasetAccessor(inputPath, inputPrefix, false, false);
         JSONObject sumMD_In = datasetIn.getImageCache().getSummaryMetadata();
         // create new multi-page dataset...
         JSONObject sumMDOut = SumMetadata.newCopyOfSummaryMetadata(sumMD_In,
                 outputDir, outputPrefix, true, "Cropper");
         
         // if cropping...
         int origW = MDUtils.getWidth(sumMD_In);
         int origH = MDUtils.getHeight(sumMD_In);
         int subW = roi.width;
         int subH = roi.height;
         SumMetadata.changeImageSize(sumMDOut, subW, subH);
         
         
         MMgrDatasetGenerator datasetOut = new MMgrDatasetGenerator(sumMDOut,
                 isMultiPage, seperateMDFile, seperateFilesForPositions);
         //
         DimensionalExtents dsdIn = SumMetadata.getDimensionalExtents(sumMD_In);
         int max = dsdIn.numPositions * dsdIn.numFrames;
         int done = 0;
         StringBuilder buffer = new StringBuilder();
         for (int pos = 0; pos < dsdIn.numPositions; pos++) {
            for (int frame = 0; frame < dsdIn.numFrames; frame++) {
               for (int slice = 0; slice < dsdIn.numSlices; slice++) {
                  for (int chan = 0; chan < dsdIn.numChannels; chan++) {
                     TaggedImage img = datasetIn.getImageCache().getImage(chan, slice, frame, pos);
                     buffer.setLength(0);
                     buffer.append("Step ").append(done).append(" of ").append(max);
                     if (jobMonitor.isTaskInterrupted()) {
                        buffer.append(" - Interrupted !");
                        jobMonitor.update(done, max, buffer.toString());
                        break;
                     } else {
                        jobMonitor.update(done, max, buffer.toString());
                     }
                     // change size
                     try {
                        PrintWhatever.out(chan, slice, frame, pos);
                        TaggedImage croppedImage = cropImage(img, origW, origH, roi);
                        datasetOut.putImage(croppedImage);
                     } catch (Exception ex) {
                        Logger.getLogger(DatasetGenTester.class.getName()).log(Level.SEVERE, null, ex);
                        ex.printStackTrace();
                     }
                  }
               }
            }
         }
         datasetOut.stop();
         // either done or cancelled...
         if (jobMonitor.isTaskInterrupted()) {
            // cleanup
            System.out.println("Process was interupted... time to clean-up");
            return false;
         } else {
            return true;
         }
      } catch (Exception ex) {
         Logger.getLogger(DatasetCropper.class.getName()).log(Level.SEVERE, null, ex);
      }
      return false;
   }

   private TaggedImage cropImage(TaggedImage img, int origW, int origH, Rectangle roi)
           throws JSONException {
      ImageProcessor ip = ImageUtils.makeProcessor(img);
      ip.setRoi(roi.x, roi.y, roi.width, roi.height);
      ImageProcessor ipSub = ip.crop();
      Object subPix = ipSub.getPixels();
      FrameMetadata.applyImageSize(img.tags, roi.width, roi.height);
      return new TaggedImage(subPix, img.tags);

   }

   public static void main(String[] args) {
//      String inDir = "C:/MicroManagerData/Test/dataXMT15";
//      String inPrefix = "SMS_2012_1206_1749_1";
//      String outDir = "C:/MicroManagerData/Test/dataXMT15";
//      String outPrefix = "cropped";
//      // "SMS_2012_1206_1749_1" dims is (7, 1, 3, 1)
//      DatasetCropper cropper = new DatasetCropper(inDir, inPrefix, outDir, outPrefix,
//              new Rectangle(100, 100, 50, 50),
//              false, true, true);
//      cropper.crop();
   }
}
