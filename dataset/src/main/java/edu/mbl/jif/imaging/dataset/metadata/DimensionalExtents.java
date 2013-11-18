package edu.mbl.jif.imaging.dataset.metadata;

import edu.mbl.jif.imaging.mmtiff.MDUtils;
import org.json.JSONObject;

/**
 * Container for dimensional extents of a dataset
 *
 * @author GBH
 */
public class DimensionalExtents {

   // **** One-Based !!!
   public int numChannels = 1;
   public int numSlices = 1;
   public int numFrames = 1;
   public int numPositions = 1;
   //
   public int startChannel = 1;
   public int startSlice = 1;
   public int startFrame = 1;
   public int startPosition = 1;
   //
   public int endChannel = numChannels;
   public int endSlice = numSlices;
   public int endFrame = numFrames;
   public int endPosition = numPositions;
   //
   public boolean timeFirst;

   public DimensionalExtents(int numChannels, int numSlices, int numFrames, boolean timeFirst) {
      this(numChannels, numSlices, numFrames, timeFirst, 1);
   }

   public DimensionalExtents(
           int numChannels,
           int numSlices,
           int numFrames,
           boolean timeFirst,
           int numPositions) {

      this.numChannels = numChannels;
      this.numSlices = numSlices;
      this.numFrames = numFrames;
      this.timeFirst = timeFirst;
      this.numPositions = numPositions;
      //
      endChannel = numChannels;
      endSlice = numSlices;
      endFrame = numFrames;
      endPosition = numPositions;
   }

   public static void showDims(JSONObject summaryMetadata_) {
      try {
         int numChannels = MDUtils.getNumChannels(summaryMetadata_);
         int numFrames = MDUtils.getNumFrames(summaryMetadata_);
         int numSlices = MDUtils.getNumSlices(summaryMetadata_);
         int numPositions = MDUtils.getNumPositions(summaryMetadata_);
         //
         System.out.println(
                 "Frames: " + numFrames + ", "
                 + "Slices: " + numSlices + ", "
                 + "Channels: " + numChannels + ", "
                 + "Positions: " + numPositions);
//			TaggedImage image;
//			int channelIndex = 0, sliceIndex = 0, frameIndex = 0, positionIndex = 0;
//			image = acq.getImageCache().getImage(channelIndex, sliceIndex,
//					frameIndex, positionIndex);
      } catch (Exception ex) {
      }
   }
}
