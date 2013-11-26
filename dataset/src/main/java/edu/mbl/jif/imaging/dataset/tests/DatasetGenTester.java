
/*
 * Export to MM Dataset for PolFlour stacks.
 */
package edu.mbl.jif.imaging.dataset.tests;

import edu.mbl.jif.imaging.dataset.MMgrDatasetGenerator;
import edu.mbl.jif.imaging.dataset.metadata.DimensionalExtents;
import edu.mbl.jif.imaging.dataset.metadata.MMDatasetDefinition;
import edu.mbl.jif.imaging.dataset.metadata.ImageAttributes;
import edu.mbl.jif.imaging.dataset.metadata.SumMetadata;
import edu.mbl.jif.imaging.dataset.util.DatasetUtils;
//import edu.mbl.jif.imaging.tiff.MultipageTiffFile;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 * *
 * @author GBH
 */
/*
 * TODO - Handle multiple positions
 */
public class DatasetGenTester {

   public static void main(String[] args) {
      new DatasetGenTester();
   }

   public DatasetGenTester() {

      runTest4D();
      // genPSDataset();

   }

   public void genPSDataset() {

      // generate a synthetic PolScope dataset...

      String outputDir = "C:/MicroManagerData/TestDatasetGen";
      String outputprefix = "pssynthBG";
      //
      String[] channelNames = {"PS0", "PS1", "PS2", "PS3", "PS4"};
      int numSlices = 1;
      int numFrames = 1;
      int numPositions = 1;
      boolean timeFirst = false;
      //
      String comment = "synthetic PolScope";
      String source = "edu.mbl.jif.imaging.dataset.DatasetGenTester";
      boolean isMultiPage = true;
      boolean seperateMDFile = false;
      boolean seperateFilesForPositions = false;
      //

      //loadPSImagesFile
      String inFile = "C:/MicroManagerData/PS-Synth/Synthetic2.tif";
      ArrayList<BufferedImage> images = null;
      //ArrayList<BufferedImage> images = MultipageTiffFile.loadImageArrayList(inFile);
      ImageAttributes imgAtribs = new ImageAttributes(images.get(0));
      //
      JSONObject mdOut = SumMetadata.newSummaryMetadata(outputDir, outputprefix, imgAtribs,
              channelNames, numSlices, numFrames, numPositions, source, comment);

      MMgrDatasetGenerator dsg = null;
      try {
         dsg = new MMgrDatasetGenerator(mdOut,
                 isMultiPage, seperateMDFile, seperateFilesForPositions);
      } catch (Exception ex) {
         Logger.getLogger(DatasetGenTester.class.getName()).log(Level.SEVERE, null, ex);
         return;
      }
      DimensionalExtents dsd = SumMetadata.getDimensionalExtents(mdOut);
      for (int pos = 0; pos < dsd.numPositions; pos++) {
         for (int frame = 0; frame < dsd.numFrames; frame++) {
            for (int slice = 0; slice < dsd.numSlices; slice++) {
               for (int chan = 0; chan < dsd.numChannels; chan++) {
                  BufferedImage image = TestImageSeriesGenerator.labelImage(images.get(chan), chan, slice, frame, pos);
                  //BufferedImage image = images.get(chan);
                  try {
                     dsg.putImageToChannel(chan, channelNames[chan],
                             slice, frame, pos, DatasetUtils.createPix(image));
                  } catch (Exception ex) {
                     Logger.getLogger(DatasetGenTester.class.getName()).log(Level.SEVERE, null, ex);
                  }
               }
            }
         }
      }
      dsg.stop();
   }

   public void runTest4D() {
      String outputDir = "C:/MicroManagerData/TestDatasetGen";
      String outputprefix = "dataset4d";
      //
      int numSlices = 10;
      int numFrames = 50;
      int numPositions = 1;
      boolean isMultiPage = true;
      boolean seperateMDFile = false;
      boolean seperateFilesForPositions = false;
      //
      //String[] channelNames = {"Channel1", "Channel2", "Channel3"};
      String[] channelNames = {"Mag", "Ort", "PS0", "PS1", "PS2", "PS3", "PS4"};
      String comment = "Test 4D";
      String source = "edu.mbl.jif.imaging.dataset.DatasetGenTester";
      ImageAttributes imgAtribs = new ImageAttributes(256, 256);
//      dsd.attribs = ia;
//      dsd.channelNames = channelNames;
//      dsd.directory = outputDir;
//      dsd.prefix = outputprefix;
//      dsd.numFrames = numFrames;
//      dsd.numSlices = numSlices;
//      dsd.numPositions = numPositions;
//      dsd.summaryMetadataMap = null;
//      dsd.isMultipageTiff = isMultiPage;
//      dsd.seperateMDFile = true;
//      dsd.seperateFilesForPositions = false;
//      dsd.displayMin = 255;
//      dsd.displayMax = 255;
//      dsd.displayHistMax = 255;
      JSONObject mdOut = SumMetadata.newSummaryMetadata(outputDir, outputprefix, imgAtribs,
              channelNames, numSlices, numFrames, numPositions, source, comment);

      MMgrDatasetGenerator dsg = null;
      try {
         dsg = new MMgrDatasetGenerator(mdOut,
                 isMultiPage, seperateMDFile, seperateFilesForPositions);
      } catch (Exception ex) {
         Logger.getLogger(DatasetGenTester.class.getName()).log(Level.SEVERE, null, ex);
         return;
      }
      
      DimensionalExtents dsd = SumMetadata.getDimensionalExtents(mdOut);

      for (int pos = 0; pos < dsd.numPositions; pos++) {
         for (int frame = 0; frame < dsd.numFrames; frame++) {
            for (int slice = 0; slice < dsd.numSlices; slice++) {
               for (int chan = 0; chan < dsd.numChannels; chan++) {
                  BufferedImage image = TestImageSeriesGenerator.generateImage(
                          imgAtribs.width, imgAtribs.height, imgAtribs.bitDepth,
                          frame, dsd.numFrames, slice, dsd.numSlices, chan, dsd.numChannels,
                          pos, dsd.numPositions);
                  try {
                     dsg.putImageToChannel(chan, channelNames[chan], slice, frame, pos, 
                             DatasetUtils.createPix(image));
                  } catch (Exception ex) {
                     Logger.getLogger(DatasetGenTester.class.getName()).log(Level.SEVERE, null, ex);
                  }
               }
            }
         }
      }
      dsg.stop();
   }

   public void runTestFast(MMDatasetDefinition dsd) {
//
//      MMgrDatasetGenerator dsg = new MMgrDatasetGenerator(dsd);
//      BufferedImage image =
//              image = new BufferedImage(dsd.attribs.width, dsd.attribs.height,
//              BufferedImage.TYPE_BYTE_GRAY);
//      for (int pos = 0; pos < dsd.numPositions; pos++) {
//         for (int frame = 0; frame < dsd.numFrames; frame++) {
//            for (int slice = 0; slice < dsd.numSlices; slice++) {
//               for (int chan = 0; chan < dsd.channelNames.length; chan++) {
//                  try {
//                     dsg.putImageToChannel(chan, dsd.channelNames[chan],
//                             slice, frame, pos, createPix(image));
//                  } catch (UnsupportedImageModelException2 ex) {
//                     Logger.getLogger(DatasetGenTester.class.getName()).log(Level.SEVERE, null, ex);
//                  }
//               }
//            }
//         }
//      }
//      dsg.stop();
   }

 
   public void template() {
      String outputDir = "C:/MicroManagerData/TestDatasetGen";
      String outputprefix = "dataset4d";
      int numSlices = 10;
      int numFrames = 50;
      int numPositions = 1;
      boolean isMultiPage = true;
      boolean seperateMDFile = false;
      boolean seperateFilesForPositions = false;
      String[] channelNames = {"Mag", "Ort", "PS0", "PS1", "PS2", "PS3", "PS4"};
      String comment = "Test 4D";
      String source = "edu.mbl.jif.imaging.dataset.DatasetGenTester";
      ImageAttributes imgAtribs = new ImageAttributes(256, 256);
      JSONObject mdOut = SumMetadata.newSummaryMetadata(outputDir, outputprefix, imgAtribs,
              channelNames, numSlices, numFrames, numPositions, source, comment);
      MMgrDatasetGenerator dsg = null;
      try {
         dsg = new MMgrDatasetGenerator(mdOut,
                 isMultiPage, seperateMDFile, seperateFilesForPositions);
      } catch (Exception ex) {
         Logger.getLogger(DatasetGenTester.class.getName()).log(Level.SEVERE, null, ex);
         return;
      }
      //......
//                  try {
//                     dsg.putImageToChannel(chan, channelNames[chan], slice, frame, pos, 
//                             DatasetUtils.createPix(image));
//                  } catch (Exception ex) {
//                     Logger.getLogger(DatasetGenTester.class.getName()).log(Level.SEVERE, null, ex);
//                  }
      //......                     
         
      
      dsg.stop();
   }
   

   
}
