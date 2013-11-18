package edu.mbl.jif.datasetconvert;

import edu.mbl.jif.imaging.dataset.MMgrDatasetAccessor;
import edu.mbl.jif.imaging.dataset.MMgrDatasetGenerator;
import edu.mbl.jif.imaging.dataset.metadata.DimensionalExtents;
import edu.mbl.jif.imaging.dataset.metadata.SumMetadata;
import edu.mbl.jif.imaging.dataset.tests.DatasetGenTester;
import edu.mbl.jif.imaging.mmtiff.TaggedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 * Convert from MM Dataset from single-page tiff dataset format to multipage tiff format.
 *
 * @author GBH
 */
public class ConvertFromSingleToMultipageTiff {

   public void convert(String inputPath, String inputPrefix,
           String outputDir, String outputPrefix) {
      try {
         MMgrDatasetAccessor datasetIn = new MMgrDatasetAccessor(inputPath, inputPrefix, true, false);
         JSONObject sumMDj = datasetIn.getImageCache().getSummaryMetadata();

         JSONObject sumMDOut = SumMetadata.newCopyOfSummaryMetadata(sumMDj, outputDir, outputPrefix, true, "Extraction");
      boolean isMultiPage = true;
      boolean seperateMDFile = false;
      boolean seperateFilesForPositions = false;
         MMgrDatasetGenerator datasetOut = new MMgrDatasetGenerator(sumMDj, 
                 isMultiPage, seperateMDFile, seperateFilesForPositions);
         DimensionalExtents dsdIn = SumMetadata.getDimensionalExtents(sumMDj);      //
         //
         // copy all the images...
         for (int pos = 0; pos < dsdIn.numPositions; pos++) {
            for (int frame = 0; frame < dsdIn.numFrames; frame++) {
               for (int slice = 0; slice < dsdIn.numSlices; slice++) {
                  for (int chan = 0; chan < dsdIn.numChannels; chan++) {
                     TaggedImage img = datasetIn.getImageCache().getImage(chan, slice, frame, pos);
                     // TODO Modify FileName...
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
         Logger.getLogger(ConvertFromSingleToMultipageTiff.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   public static void main(String[] args) {
      String dir = "C:/MicroManagerData/Test/dataXMT15";
      String prefixIn = "SMS_2012_1206_1749_1";
      String outDir = "C:/MicroManagerData/Test/dataXMT15";
      String outPrefix = "converted";
      new ConvertFromSingleToMultipageTiff().convert(dir, prefixIn, outDir, outPrefix);
      //new ConvertFromSingleToMultipageTiff().test();

   }
}
