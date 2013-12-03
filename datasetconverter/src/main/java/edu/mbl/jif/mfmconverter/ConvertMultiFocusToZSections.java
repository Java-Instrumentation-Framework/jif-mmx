/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mbl.jif.mfmconverter;

import edu.mbl.jif.job.JobMonitorPanel;
import edu.mbl.jif.gui.DialogBox;
import edu.mbl.jif.imaging.dataset.MMgrDatasetAccessor;
import edu.mbl.jif.imaging.dataset.MMgrDatasetGenerator;
import edu.mbl.jif.imaging.dataset.metadata.DimensionalExtents;
import edu.mbl.jif.imaging.dataset.metadata.FrameEvent;
import edu.mbl.jif.imaging.dataset.metadata.FrameMetadata;
import edu.mbl.jif.imaging.dataset.metadata.SumMetadata;
import edu.mbl.jif.imaging.mmtiff.ImageUtils;
import edu.mbl.jif.imaging.mmtiff.MDUtils;
import edu.mbl.jif.imaging.mmtiff.TaggedImage;
import foxtrot.Job;
import ij.ImageStack;
import ij.process.ImageProcessor;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author GBH
 */
public class ConvertMultiFocusToZSections extends Job {

   MMgrDatasetGenerator dsg;
   //
   private boolean multiPage = false;
   private boolean seperateMDFile = true;
   private boolean seperateFilesForPositions = true;
   //
   private JobMonitorPanel jobMonitor;
   private String inputPath;
   private String inputPrefix;
   private String outputDir;
   private String outputPrefix;
   private int numSubImgsHorizontal;
   private int numSubImgsVertical;
   private Rectangle[] subImageRectangles;

   public ConvertMultiFocusToZSections(JobMonitorPanel jobMonitor, 
           String inputPath, String inputPrefix, 
           String outputDir, String outputPrefix, 
           int numSubImgsHorizontal, int numSubImgsVertical, Rectangle[] subImageRectangles) {
      this.jobMonitor = jobMonitor;
      this.inputPath = inputPath;
      this.inputPrefix = inputPrefix;
      this.outputDir = outputDir;
      this.outputPrefix = outputPrefix;
      this.numSubImgsHorizontal = numSubImgsHorizontal;
      this.numSubImgsVertical = numSubImgsVertical;
      this.subImageRectangles = subImageRectangles;
   }

   public void setmultiPage(boolean isMultiPage) {
      this.multiPage = isMultiPage;
   }

   public void setSeperateMDFile(boolean seperateMDFile) {
      this.seperateMDFile = seperateMDFile;
   }

   public void setSeperateFilesForPositions(boolean seperateFilesForPositions) {
      this.seperateFilesForPositions = seperateFilesForPositions;
   }

   
   @Override
   public Object run() {
      StringBuffer buffer = new StringBuffer();
      int numSubImgs = numSubImgsVertical * numSubImgsHorizontal;
      if (numSubImgs != subImageRectangles.length) {
         DialogBox.boxError("Error", "Input error: numSubImgs != subImageRect.length");
         return false;
      }
      int subW = subImageRectangles[0].width;
      int subH = subImageRectangles[0].height;
      boolean unequalSizes = false;
      for (int i = 0;
              i < subImageRectangles.length;
              i++) {
         Rectangle rectangle = subImageRectangles[i];
         if (rectangle.width != subW || rectangle.height != subH) {
            unequalSizes = true;
         }
      }
      if (unequalSizes) {
         DialogBox.boxError("Error", "Input error: sizes of sub-images not all equal.");
         return false;
      }
      int imgW = 0;
      int imgH = 0;
      MMgrDatasetAccessor mda;


      try {
         mda = new MMgrDatasetAccessor(inputPath, inputPrefix, false, false);
      } catch (Exception ex) {
         DialogBox.boxError("Error", "Cannot open input dataset: " + inputPath + "/" + inputPrefix);
         return false;
      }
      JSONObject sumMDIn = mda.getImageCache().getSummaryMetadata();
      //

      System.out.println("Expanding Z-Sections...");
      System.out.println("From: " + inputPath + "/" + inputPrefix);
      System.out.println("To  : " + outputDir + "/" + outputPrefix);
      System.out.println("Number of sub-images (horiz X vert): " + numSubImgsHorizontal + " X " + numSubImgsVertical);
      System.out.println("Sub-image coordinates:");

      //printRects(subImageRect);

      System.out.println("Image    : " + imgW + " x " + imgH);
      System.out.println("Sub-image: " + subW + " x " + subH);
      System.out.println("Processing...");
      // 
      //
      boolean copyContext = true;
      JSONObject sumMD_Out = null;
      // Generate appropriate metadata...
      try {
         sumMD_Out = SumMetadata.newCopyOfSummaryMetadata(sumMDIn, outputDir, outputPrefix, false,
                 "Converted from MFM 3x3 to 9 z-sections");
         SumMetadata.applyDimensionalExtents(sumMD_Out, numSubImgs, 0, 0);  //, don't change t,p
         SumMetadata.changeImageSize(sumMD_Out, subW, subH);
      } catch (JSONException ex) {
         DialogBox.boxError("Error", "JSONError: " + ex.getMessage());
         return false;
      }

      // Creation of the dataset generator will create a file/directory with a unique name for the outputPrefix
      try {
         dsg = new MMgrDatasetGenerator(sumMD_Out,
                 multiPage, seperateMDFile, seperateFilesForPositions);
      } catch (Exception ex) {
         DialogBox.boxError("Error", "Cannot open output dataset for: " + outputDir + "/" + outputPrefix);
         return false;
      }
      // Get dimensional extends and channel names...
      DimensionalExtents dsdIn = SumMetadata.getDimensionalExtents(sumMDIn);
      String[] channelNames;

      try {
         channelNames = SumMetadata.getChannelNames(sumMDIn);
      } catch (JSONException ex) {
         DialogBox.boxError("Error", "JSONError: " + ex.getMessage());
         return false;
      }
      int totalToDo = dsdIn.numPositions * dsdIn.numFrames;
      int done = 0;
      for (int pos = 0; pos < dsdIn.numPositions; pos++) {
         for (int frame = 0; frame < dsdIn.numFrames; frame++) {
            // at a given timepoint...
            //for (int slice = 0; slice < dsdIn.numSlices; slice++) { // Only 1 - becomes nXm
            // get the image for all the channels
            System.out.println("\nPos: " + pos + ", Frame: " + frame);
            done++;
            // Prepare the progress bar string
            buffer.setLength(0);
            buffer.append("Step ").append(done).append(" of ").append(totalToDo);
            // Update the progress bar
            if (jobMonitor.isTaskInterrupted()) {
               buffer.append(" - Interrupted !");
               jobMonitor.update(done, totalToDo, buffer.toString());
               break;
            } else {
               jobMonitor.update(done, totalToDo, buffer.toString());
            }
            TaggedImage[] imgNxNChannel = new TaggedImage[dsdIn.numChannels];
            for (int chan = 0; chan < dsdIn.numChannels; chan++) {
               imgNxNChannel[chan] = mda.getImageCache().getImage(chan, 0, frame, pos);
               System.out.print(chan + " ");
            }
            generateSlices(imgNxNChannel, channelNames, subImageRectangles, frame, pos);
         }
         //}
      }
      System.out.println("Completed.");
      dsg.stop();
      System.out.println("exit");
      // either done or cancelled...
      if (jobMonitor.isTaskInterrupted()) {
         // cleanup
         System.out.println("Process was interupted... time to clean-up");
         return false;
      } else {
         return true;
      }

   }

   private void generateSlices(TaggedImage[] imgNxNChannel,
           String[] channelNames,
           Rectangle[] subImageRects,
           int frame, int pos) {
      int numSlices = subImageRects.length;
      int numChannels = channelNames.length;
      for (int slice = 0; slice < numSlices; slice++) {
         Rectangle rect = subImageRects[slice];
         //TaggedImage[] channelsForSlice = extractChannelsForSlice(rect, imgNxNChannel, numChannels);
         ImageStack stackOfChannelsForSlice = extractChannelsForSlice(rect, imgNxNChannel, numChannels);
         // Save the channels for this slice.
         writeToDataset(stackOfChannelsForSlice, slice, frame, pos, imgNxNChannel, numChannels);

      }
   }

   private ImageStack extractChannelsForSlice(Rectangle rect, TaggedImage[] imgNxNChannel,
           int numChannels) {
      //TaggedImage[] subImages = new TaggedImage[numChannels];
      ImageStack stack = new ImageStack(rect.width, rect.height);
      for (int chan = 0; chan < numChannels; chan++) {
         ImageProcessor ip = ImageUtils.makeProcessor(imgNxNChannel[chan]);
         ip.setRoi(rect.x, rect.y, rect.width, rect.height);
         ImageProcessor ipSub = ip.crop();
         stack.addSlice("" + chan, ipSub);
      }
      return stack;
   }

   private void writeToDataset(ImageStack stackOfChannelsForSlice, int slice, int frame, int pos,
           TaggedImage[] imgNxNChannel, int numChannels) {
      for (int chan = 0; chan < numChannels; chan++) {
         JSONObject frameMD_NxN = imgNxNChannel[chan].tags;
         String channelName;
         try {
            channelName = MDUtils.getChannelName(frameMD_NxN);
            // create Frame metadata from a copy from frameMD_NxN
            FrameEvent evt = new FrameEvent();
            evt.frame = frame;
            evt.frameIndex = frame;
            evt.channelIndex = chan;
            evt.channel = channelName;
            // evt.channel = name;
            evt.slice = slice;
            evt.sliceIndex = slice;
            evt.positionIndex = pos;
            JSONObject frameMD = FrameMetadata.copyAndApplyEvent(frameMD_NxN, evt, multiPage);

            ImageProcessor ip = stackOfChannelsForSlice.getProcessor(chan + 1);
            try {
               // new UUID
               // else?
               MDUtils.setWidth(frameMD, ip.getWidth());
               MDUtils.setHeight(frameMD, ip.getHeight());


            } catch (JSONException ex) {
               Logger.getLogger(ConvertMultiFocusToZSections.class
                       .getName()).log(Level.SEVERE, null, ex);
            }
//            try {
//               System.out.println("Channel: " + MDUtils.getChannelIndex(frameMD));
//               System.out.println("Frame: " + MDUtils.getFrameIndex(frameMD));
//               System.out.println("Slice: " + MDUtils.getSliceIndex(frameMD));
//            } catch (JSONException ex) {
//               Logger.getLogger(ConvertMultiFocusToZSections.class.getName()).log(Level.SEVERE, null, ex);
//            }
            dsg.putImage(ip.getPixels(), frameMD);
         } catch (JSONException ex) {
            DialogBox.boxError(outputDir, frame);
            Logger.getLogger(ConvertMultiFocusToZSections.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
   }

   public static Rectangle[] initRects(int imgW, int imgH,
           int numSubImgsHorizontal, int numSubImgsVertical) {
      int w = imgW / numSubImgsHorizontal;
      int h = imgH / numSubImgsVertical;
      int numSubImgs = numSubImgsVertical * numSubImgsHorizontal;
      Rectangle[] subImageRect = new Rectangle[numSubImgs];
      int n = 0;
      for (int i = 0; i < numSubImgsHorizontal; i++) {
         for (int j = 0; j < numSubImgsVertical; j++) {
            Rectangle rectangle = subImageRect[n];
            int x = (imgW / numSubImgsHorizontal) * j;
            int y = (imgH / numSubImgsVertical) * i;
            int w_;
            int h_;
            if (x + w > imgW) {
               w_ = w - 1;
            } else {
               w_ = w;
            }
            if (y + h > imgH) {
               h_ = h - 1;
            } else {
               h_ = h;
            }
            subImageRect[n] = new Rectangle(x, y, w_, h_);
            n++;
         }
      }
      return subImageRect;
   }

   private static void printRects(Rectangle[] subImageRect) {
      for (int i = 0; i < subImageRect.length; i++) {
         System.out.print("" + subImageRect[i].toString());
         System.out.print(" : " + (subImageRect[i].x + subImageRect[i].width) + ", "
                 + (subImageRect[i].y + subImageRect[i].height) + "\n");
      }
   }


//   public static void main(String[] args) {
//      String dir = "C:/MicroManagerData/Test/dataXMT15";
//      String prefixIn = "SMS_2012_1206_1749_1";
//      String outDir = "C:/MicroManagerData/Test/dataXMT15";
//      String outPrefix = "converted";
//      // for sub-imaging
////      int imgW = 1392;
////      int imgH = 1040;
//      int imgW = 1200;
//      int imgH = 900;
//      int numSubImgsHorizontal = 3;
//      int numSubImgsVertical = 3;
//      int subW = imgW / numSubImgsHorizontal;
//      int subH = imgH / numSubImgsVertical;
//      
//      Rectangle[] subImageRect = initRects(imgW, imgH, numSubImgsHorizontal, numSubImgsVertical);
//      printRects(subImageRect);
//      new ConvertMultiFocusToZSections().convert(dir, prefixIn, outDir, outPrefix,
//              numSubImgsHorizontal, numSubImgsVertical, subImageRect, subW, subH);
//
//   }
   public static void main(String[] args) {
      String dir = "G:/data";
      String prefixIn = "SMS_2013_0823_0017_1";
      String outDir = "G:/data";
      String outPrefix = prefixIn + "_Z";
      // for sub-imaging
//      int imgW = 1392;
//      int imgH = 1040;
      int x0 = 30;
      int y0 = 80;
      int imgW = 870;
      int imgH = 870;
      int numSubImgsHorizontal = 3;
      int numSubImgsVertical = 3;
      int subW = imgW / numSubImgsHorizontal;
      int subH = imgH / numSubImgsVertical;

      Rectangle[] subImageRect = initRects(imgW, imgH, numSubImgsHorizontal, numSubImgsVertical);

//      new ConvertMultiFocusToZSections().convert(dir, prefixIn, outDir, outPrefix,
//              numSubImgsHorizontal, numSubImgsVertical, subImageRect);
      // 960 * 9 produces 8640
   }
}
