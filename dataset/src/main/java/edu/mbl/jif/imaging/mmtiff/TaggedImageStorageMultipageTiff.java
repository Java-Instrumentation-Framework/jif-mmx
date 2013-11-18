///////////////////////////////////////////////////////////////////////////////
//FILE:          TaggedImageStorageMultipageTiff.java
//PROJECT:       Micro-Manager
//SUBSYSTEM:     mmstudio
//-----------------------------------------------------------------------------
//
// AUTHOR:       Henry Pinkard, henry.pinkard@gmail.com, 2012
//
// COPYRIGHT:    University of California, San Francisco, 2012
//
// LICENSE:      This file is distributed under the BSD license.
//               License text is included with the source distribution.
//
//               This file is distributed in the hope that it will be useful,
//               but WITHOUT ANY WARRANTY; without even the implied warranty
//               of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//
//               IN NO EVENT SHALL THE COPYRIGHT OWNER OR
//               CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
//               INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES.
//
package edu.mbl.jif.imaging.mmtiff;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import org.json.JSONException;
import org.json.JSONObject;

public final class TaggedImageStorageMultipageTiff implements TaggedImageStorage {

   private boolean newDataSet_;
   private int lastFrameOpenedDataSet_ = -1;
   private Thread shutdownHook_;
   private CachedImages cached_;
   private boolean finished_ = false;
   //
   public int numChannels_, numSlices_;
   public boolean expectedImageOrder_ = true;
   public boolean splitByXYPosition_ = true;
   public int numPositions_;
   public int lastFrame_ = 0;
   //
   public String directory_;
   final public boolean omeTiff_;
   public String omeXML_ = null;
   //used for estimating total length of ome xml
   public int totalNumImagePlanes_ = 0;
   public OMEMetadata omeMetadata_;
   //
   public JSONObject summaryMetadata_;
   final public boolean seperateMetadataFile_;
   //
   private JSONObject displayAndComments_;
   // Filesets...
   //map of position indices to objects associated with each
   private HashMap<Integer, FileSet> fileSets_;
   //Map of image labels to file 
   private TreeMap<String, MultipageTiffReader> tiffReadersByLabel_;

   /*
    * Constructor that doesnt make reference to MMStudioMainFrame so it can be used 
    * independently of MM GUI
    */
   public TaggedImageStorageMultipageTiff(String dir, Boolean newDataSet, JSONObject summaryMetadata) throws IOException {
      this(dir, newDataSet, summaryMetadata,
              true, //getMetadataFileWithMultipageTiff(),
              true // getSeparateFilesForPositionsMPTiff(),
              );
   }

   public TaggedImageStorageMultipageTiff(String dir, boolean newDataSet, JSONObject summaryMetadata,
           boolean seperateMDFile, boolean seperateFilesForPositions) throws IOException {
      omeTiff_ = true;
      seperateMetadataFile_ = seperateMDFile;
      splitByXYPosition_ = seperateFilesForPositions;
      newDataSet_ = newDataSet;
      directory_ = dir;
      tiffReadersByLabel_ = new TreeMap<String, MultipageTiffReader>(new ImageLabelComparator());
      cached_ = new CachedImages();
      setSummaryMetadata(summaryMetadata);

      // TODO: throw error if no existing dataset
      if (!newDataSet_) {
         openExistingDataSet();
      }

      //add shutdown hook --> thread to be run when JVM shuts down
      shutdownHook_ = new Thread() {
         @Override
         public void run() {
            finished();
            writeDisplaySettings();
         }
      };
      Runtime.getRuntime().addShutdownHook(this.shutdownHook_);
   }

   private void openExistingDataSet() throws IOException {
      //Need to throw error if file not found
      MultipageTiffReader reader = null;
      File dir = new File(directory_);
      for (File f : dir.listFiles()) {
         if (f.getName().endsWith(".tif") || f.getName().endsWith(".TIF")) {
            reader = new MultipageTiffReader(f);
            Set<String> labels = reader.getIndexKeys();
            for (String label : labels) {
               tiffReadersByLabel_.put(label, reader);
               int frameIndex = Integer.parseInt(label.split("_")[2]);
               lastFrameOpenedDataSet_ = Math.max(frameIndex, lastFrameOpenedDataSet_);
            }
         }
      }
      try {
         setSummaryMetadata(reader.getSummaryMetadata());
         numPositions_ = MDUtils.getNumPositions(summaryMetadata_);
         displayAndComments_ = reader.getDisplayAndComments();
      } catch (JSONException ex) {
         ReportingUtils.logError(ex);
      }
   }

   boolean slicesFirst() {
      return ((ImageLabelComparator) tiffReadersByLabel_.comparator()).getSlicesFirst();
   }

   boolean timeFirst() {
      return ((ImageLabelComparator) tiffReadersByLabel_.comparator()).getTimeFirst();
   }

   @Override
   public TaggedImage getImage(int channelIndex, int sliceIndex, int frameIndex, int positionIndex) {
      String label = MDUtils.generateLabel(channelIndex, sliceIndex, frameIndex, positionIndex);
      TaggedImage img = cached_.get(label);
      if (img != null) {
         return img;
      }
      if (!tiffReadersByLabel_.containsKey(label)) {
         return null;
      }
      return tiffReadersByLabel_.get(label).readImage(label);
   }

   @Override
   public JSONObject getImageTags(int channelIndex, int sliceIndex, int frameIndex, int positionIndex) {
      String label = MDUtils.generateLabel(channelIndex, sliceIndex, frameIndex, positionIndex);
      TaggedImage img = cached_.get(label);
      if (img != null) {
         return img.tags;
      }
      if (!tiffReadersByLabel_.containsKey(label)) {
         return null;
      }
      return tiffReadersByLabel_.get(label).readImage(label).tags;
   }

   @Override
   public void putImage(TaggedImage taggedImage) throws MMException {
      if (!newDataSet_) {
         throw new MMException("This ImageFileManager is read-only.");
      }
      int fileSetIndex = 0;
      if (splitByXYPosition_) {
         try {
            fileSetIndex = MDUtils.getPositionIndex(taggedImage.tags);
         } catch (JSONException ex) {
            ReportingUtils.logError(ex);
         }
      }
      String label = MDUtils.getLabel(taggedImage.tags);
      if (fileSets_ == null) {
         try {
            fileSets_ = new HashMap<Integer, FileSet>();
            createDirectory(directory_);
         } catch (Exception ex) {
            ReportingUtils.logError(ex);
         }
      }

      if (omeTiff_) {
         if (omeMetadata_ == null) {
            omeMetadata_ = new OMEMetadata(this);
         }
      }

      if (fileSets_.get(fileSetIndex) == null) {
         fileSets_.put(fileSetIndex, new FileSet(taggedImage.tags, this));
      }
      FileSet set = fileSets_.get(fileSetIndex);
      try {
         set.writeImage(taggedImage);
      } catch (IOException ex) {
         ReportingUtils.showError("problem writing image to file");
      }
      tiffReadersByLabel_.put(label, set.getCurrentReader());

      int frame;
      try {
         frame = MDUtils.getFrameIndex(taggedImage.tags);
      } catch (JSONException ex) {
         frame = 0;
      }
      lastFrameOpenedDataSet_ = Math.max(frame, lastFrameOpenedDataSet_);
      cached_.add(taggedImage, label);
   }

   @Override
   public Set<String> imageKeys() {
      return tiffReadersByLabel_.keySet();
   }

   /**
    * Call this function when no more images are expected Finishes writing the metadata file and
    * closes it. After calling this function, the imagestorage is read-only
    */
   @Override
   public synchronized void finished() {
      if (finished_) {
         return;
      }
      newDataSet_ = false;
      try {
         if (fileSets_ != null) {
            for (FileSet p : fileSets_.values()) {
               p.finishAbortedAcqIfNeeded();
            }
            for (FileSet p : fileSets_.values()) {
               p.finished();
            }
         }
      } catch (IOException ex) {
         ReportingUtils.logError(ex);
      }
      finished_ = true;
   }

   @Override
   public boolean isFinished() {
      return !newDataSet_;
   }

   /**
    * Disposes of the tagged images in the imagestorage
    */
   @Override
   public void close() {
      shutdownHook_.run();
      Runtime.getRuntime().removeShutdownHook(shutdownHook_);
      for (MultipageTiffReader r : new HashSet<MultipageTiffReader>(tiffReadersByLabel_.values())) {
         try {
            r.close();
         } catch (IOException ex) {
            ReportingUtils.logError(ex);
         }
      }
   }

   // <editor-fold defaultstate="collapsed" desc=" Metadata ">
   //
   private void processSummaryMD() {
      // TODO      displayAndComments_ = VirtualAcquisitionDisplay.getDisplaySettingsFromSummary(summaryMetadata_);
      try {
         numPositions_ = MDUtils.getNumPositions(summaryMetadata_);
         if (numPositions_ <= 0) {
            numPositions_ = 1;
         }
      } catch (JSONException ex) {
         ReportingUtils.logError(ex);
         numPositions_ = 1;
      }
      try {
         //Estimate of max number of image planes
         numChannels_ = MDUtils.getNumChannels(summaryMetadata_);
         numSlices_ = MDUtils.getNumSlices(summaryMetadata_);
         totalNumImagePlanes_ = numChannels_ * MDUtils.getNumFrames(summaryMetadata_)
                 * numPositions_ * numSlices_;
      } catch (Exception ex) {
         ReportingUtils.logError("Error estimating total number of image planes");
         totalNumImagePlanes_ = 1;
      }
   }

   @Override
   public void setSummaryMetadata(JSONObject md) {
      summaryMetadata_ = md;
      if (summaryMetadata_ != null) {
         try {
            boolean slicesFirst = summaryMetadata_.getBoolean("SlicesFirst");
            boolean timeFirst = summaryMetadata_.getBoolean("TimeFirst");
            TreeMap<String, MultipageTiffReader> oldImageMap = tiffReadersByLabel_;
            tiffReadersByLabel_ = new TreeMap<String, MultipageTiffReader>(new ImageLabelComparator(slicesFirst, timeFirst));
            tiffReadersByLabel_.putAll(oldImageMap);
         } catch (JSONException ex) {
            ReportingUtils.logError("Couldn't find SlicesFirst or TimeFirst in summary metadata");
         }
         if (summaryMetadata_ != null && summaryMetadata_.length() > 0) {
            processSummaryMD();
         }
      }
   }

   @Override
   public JSONObject getSummaryMetadata() {
      return summaryMetadata_;
   }

   @Override
   public JSONObject getDisplayAndComments() {
      return displayAndComments_;
   }

   @Override
   public void setDisplayAndComments(JSONObject settings) {
      displayAndComments_ = settings;
   }

   @Override
   public void writeDisplaySettings() {
      for (MultipageTiffReader r : new HashSet<MultipageTiffReader>(tiffReadersByLabel_.values())) {
         try {
            r.rewriteDisplaySettings(displayAndComments_.getJSONArray("Channels"));
            r.rewriteComments(displayAndComments_.getJSONObject("Comments"));
         } catch (JSONException ex) {
            ReportingUtils.logError("Error writing display settings");
         } catch (IOException ex) {
            ReportingUtils.logError(ex);
         }
      }
   }
   // </editor-fold>

   @Override
   public String getDiskLocation() {
      return directory_;
   }

   public static File createDirectory(String dirPath) throws Exception {
      File dir = new File(dirPath);
      if (!dir.exists()) {
         if (!dir.mkdirs()) {
            throw new Exception("Unable to create directory.");
         }
      }
      return dir;
   }

   @Override
   public int lastAcquiredFrame() {
      if (newDataSet_) {
         return lastFrame_;
      } else {
         return lastFrameOpenedDataSet_;
      }
   }

   public long getDataSetSize() {
      File dir = new File(directory_);
      LinkedList<File> list = new LinkedList<File>();
      for (File f : dir.listFiles()) {
         if (f.isDirectory()) {
            for (File fi : f.listFiles()) {
               list.add(f);
            }
         } else {
            list.add(f);
         }
      }
      long size = 0;
      for (File f : list) {
         size += f.length();
      }
      return size;
   }

   public boolean hasExpectedImageOrder() {
      return expectedImageOrder_;
   }

   // CachedImages ------------------------------
   private class CachedImages {

      private static final int NUM_TO_CACHE = 10;
      private LinkedList<TaggedImage> images;
      private LinkedList<String> labels;

      public CachedImages() {
         images = new LinkedList<TaggedImage>();
         labels = new LinkedList<String>();
      }

      public void add(TaggedImage img, String label) {
         images.addFirst(img);
         labels.addFirst(label);
         while (images.size() > NUM_TO_CACHE) {
            images.removeLast();
            labels.removeLast();
         }
      }

      public TaggedImage get(String label) {
         int i = labels.indexOf(label);
         return i == -1 ? null : images.get(i);
      }
   }
}