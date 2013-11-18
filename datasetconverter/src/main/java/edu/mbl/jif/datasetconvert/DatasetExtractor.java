/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mbl.jif.datasetconvert;

import edu.mbl.jif.imaging.dataset.MMgrDatasetAccessor;
import edu.mbl.jif.imaging.dataset.MMgrDatasetGenerator;
import edu.mbl.jif.utils.PrintWhatever;
import edu.mbl.jif.imaging.dataset.metadata.DimensionalExtents;
import edu.mbl.jif.imaging.dataset.metadata.SumMetadata;
import edu.mbl.jif.imaging.dataset.tests.DatasetGenTester;
import edu.mbl.jif.imaging.mmtiff.TaggedImage;
import java.awt.Rectangle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author GBH
 */
public class DatasetExtractor {

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

   public DatasetExtractor(
           String inputPath, String inputPrefix, String outputDir, String outputPrefix,
           int[] channelIndices, int[] sliceIndices, int[] frameIndices, int[] positionIndices,
           boolean isMultiPage, boolean seperateMDFile, boolean seperateFilesForPositions) {
      this.inputPath = inputPath;
      this.inputPrefix = inputPrefix;
      this.outputDir = outputDir;
      this.outputPrefix = outputPrefix;
      this.channelIndices = channelIndices;
      this.sliceIndices = sliceIndices;
      this.frameIndices = frameIndices;
      this.positionIndices = positionIndices;
      this.isMultiPage = isMultiPage;
      this.seperateMDFile = seperateMDFile;
      this.seperateFilesForPositions = seperateFilesForPositions;
   }

   public void extractFrom() {

      try {
         MMgrDatasetAccessor datasetIn = new MMgrDatasetAccessor(inputPath, inputPrefix, false, false);
         JSONObject sumMD_In = datasetIn.getImageCache().getSummaryMetadata();
         // create new multi-page dataset...
         JSONObject sumMDOut = SumMetadata.newCopyOfSummaryMetadata(sumMD_In,
                 outputDir, outputPrefix, true, "Extraction");
         // change dimensions
         SumMetadata.applyDimensionalExtents(sumMDOut,
                 sliceIndices.length, frameIndices.length, positionIndices.length);
         // change channelIndices
         String[] channelNamesIn = SumMetadata.getChannelNames(sumMD_In);
         String[] channelNamesNew = new String[channelIndices.length];
         for (int i = 0; i < channelIndices.length; i++) {
            channelNamesNew[i] = channelNamesIn[channelIndices[i]];
         }
         SumMetadata.setChannelsAndDisplay(sumMDOut, channelNamesNew);
         // 
         MMgrDatasetGenerator datasetOut = new MMgrDatasetGenerator(sumMDOut,
                 isMultiPage, seperateMDFile, seperateFilesForPositions);

         // *********** 
         for (int pos = 0; pos < positionIndices.length; pos++) {
            for (int frame = 0; frame < frameIndices.length; frame++) {
               for (int slice = 0; slice < sliceIndices.length; slice++) {
                  for (int chan = 0; chan < channelIndices.length; chan++) {

                     TaggedImage img = datasetIn.getImageCache().getImage(
                             channelIndices[chan], sliceIndices[slice],
                             frameIndices[frame], positionIndices[pos]);

                     PrintWhatever.out(channelIndices[chan], sliceIndices[slice],
                             frameIndices[frame], positionIndices[pos]);

                     JSONObject frameMD = img.tags;

                     // Change metadata to new dimensionalIndicies: (chan,slice,frame,pos)
                     // TODO...
                     
                     PrintWhatever.out(chan, slice, frame, pos);
                     //System.out.println("");
                     // set channelName
                     try {
                        datasetOut.putImage(img);
                     } catch (Exception ex) {
                        Logger.getLogger(DatasetGenTester.class.getName()).log(Level.SEVERE, null, ex);
                        ex.printStackTrace();
                     }

                  }
               }
            }
         }
         datasetOut.stop();
      } catch (Exception ex) {
         Logger.getLogger(DatasetExtractor.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   public static void main(String[] args) {
//      String inDir = "C:/MicroManagerData/Test/dataXMT15";
//      String inPrefix = "SMS_2012_1206_1749_1";
//      String outDir = "C:/MicroManagerData/Test/dataXMT15";
//      String outPrefix = "extracted";
      // "SMS_2012_1206_1749_1" dims is (7, 1, 3, 1)

      String inDir = "G:/data";
      String inPrefix = "SMS_2013_0823_0017_1";
      String outDir = "G:/data/Output";
      String outPrefix = "extracted";


      int[] channels = new int[]{0, 1, 2, 3, 4, 5, 6};
      int[] slices = new int[]{0};
      int[] frames = new int[]{0, 2};
      int[] positions = new int[]{0};

      DatasetExtractor extractor = new DatasetExtractor(inDir, inPrefix, outDir, outPrefix,
              channels, slices, frames, positions,
              false, true, true);
      extractor.extractFrom();
   }
}
