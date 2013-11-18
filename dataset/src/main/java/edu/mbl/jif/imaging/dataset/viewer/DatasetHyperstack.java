/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mbl.jif.imaging.dataset.viewer;

import edu.mbl.jif.imaging.dataset.MMgrDatasetAccessor;
import edu.mbl.jif.imaging.mmtiff.AcquisitionVirtualStack;
import edu.mbl.jif.imaging.mmtiff.ImageCache;
import edu.mbl.jif.imaging.mmtiff.MDUtils;
import edu.mbl.jif.imaging.mmtiff.ReportingUtils;
import ij.ImageJ;
import ij.ImagePlus;
import ij.io.FileInfo;
import java.awt.Color;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.File;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author GBH
 *
 * Is accesses with a plugin that is added to the Open/Import menu of ImageJ (in mmxplugins)
 *
 * TODO: Add the approp. summary metadata to the image info...
 *
 */
public class DatasetHyperstack {

   private AcquisitionVirtualStack virtualStack_;
   private ImageCache imageCache_ = null;
   private int numComponents_;
   final static Color[] rgb = {Color.red, Color.green, Color.blue};
   final static String[] rgbNames = {"Red", "Blue", "Green"};
   private String dir;
   private String prefix;
   private JSONObject firstImageMetadata;
   private JSONObject summaryMetadata;

   // dir and prefix are directories... it is assumed that there is a file with the name <prefix>.tif
   public DatasetHyperstack(String dir, String prefix) {
      this.dir = dir;
      this.prefix = prefix;
   }

   public ImagePlus createImagePlus() {
      try {
         MMgrDatasetAccessor datasetIn = new MMgrDatasetAccessor(dir, prefix, true, false);
         ImagePlus imp = createVirtualAcqStack(datasetIn.getImageCache());
         return imp;
      } catch (Exception e) {
         e.printStackTrace();
      }
      return null;
   }

   private ImagePlus createVirtualAcqStack(ImageCache imageCache) {
      imageCache_ = imageCache;
      firstImageMetadata = imageCache_.getImageTags(0, 0, 0, 0);
      summaryMetadata = imageCache_.getSummaryMetadata();
      int numSlices = 1;
      int numFrames = 1;
      int numChannels = 1;
      int numGrayChannels;
      int numPositions = 1;
      int bitDepth = 8;
      int width = 0;
      int height = 0;
      int numComponents = 1;
      try {
         if (firstImageMetadata != null) {
            width = MDUtils.getWidth(firstImageMetadata);
            height = MDUtils.getHeight(firstImageMetadata);
         } else {
            width = MDUtils.getWidth(summaryMetadata);
            height = MDUtils.getHeight(summaryMetadata);
         }
         numSlices = Math.max(summaryMetadata.getInt("Slices"), 1);
         numFrames = Math.max(summaryMetadata.getInt("Frames"), 1);
         bitDepth = MDUtils.getBitDepth(summaryMetadata);
         int imageChannelIndex;
         try {
            imageChannelIndex = MDUtils.getChannelIndex(firstImageMetadata);
         } catch (Exception e) {
            imageChannelIndex = -1;
         }
         numChannels = Math.max(1 + imageChannelIndex,
                 Math.max(summaryMetadata.getInt("Channels"), 1));
         numPositions = Math.max(summaryMetadata.getInt("Positions"), 1);
         numComponents = Math.max(MDUtils.getNumberOfComponents(summaryMetadata), 1);
      } catch (Exception e) {
         ReportingUtils.showError(e);
      }
      numComponents_ = numComponents;
      numGrayChannels = numComponents_ * numChannels;

      if (imageCache_.getDisplayAndComments() == null || imageCache_.getDisplayAndComments().isNull("Channels")) {
         imageCache_.setDisplayAndComments(getDisplaySettingsFromSummary(summaryMetadata));
      }

      int type = 0;
      try {
         if (firstImageMetadata != null) {
            type = MDUtils.getSingleChannelType(firstImageMetadata);
         } else {
            type = MDUtils.getSingleChannelType(summaryMetadata);
         }
      } catch (Exception ex) {
         ReportingUtils.showError(ex, "Unable to determine acquisition type.");
      }
      ColorModel cm = null; // createGrayscaleColorModel(false);
      virtualStack_ = new AcquisitionVirtualStack(width, height, type, cm,
              imageCache_, numChannels, numSlices, numFrames);
      virtualStack_.setBitDepth(bitDepth);
      // TODO add summary metadata...
      if (summaryMetadata.has("PositionIndex")) {
         try {
            virtualStack_.setPositionIndex(MDUtils.getPositionIndex(summaryMetadata));
         } catch (Exception ex) {
            ReportingUtils.logError(ex);
         }
      }
      ImagePlus mmImp = createMMImagePlus(virtualStack_);
      mmImp.setOpenAsHyperStack(true);
      mmImp.setDimensions(numChannels, numSlices, numFrames);
      return mmImp;
   }

   final public ImagePlus createMMImagePlus(AcquisitionVirtualStack virtualStack) {
      // TODO Calibration? 
      String diskLocation = imageCache_.getDiskLocation();
      File f = new File(diskLocation);
      String prefix = f.getName();
      ImagePlus img = new ImagePlus(prefix, virtualStack);
      FileInfo fi = new FileInfo();
      fi.width = virtualStack.getWidth();
      fi.height = virtualStack.getHeight();
      fi.fileName = null;
		fi.directory = diskLocation;
      fi.url = null;
      img.setFileInfo(fi);
      return img;
   }

//   private String getIJDescriptionString() {
//      StringBuffer sb = new StringBuffer();
//      sb.append("ImageJ=" + ImageJ.VERSION + "\n");
//      if (numChannels_ > 1) {
//         sb.append("channels=" + numChannels_ + "\n");
//      }
//      if (numSlices_ > 1) {
//         sb.append("slices=" + numSlices_ + "\n");
//      }
//      if (numFrames_ > 1) {
//         sb.append("frames=" + numFrames_ + "\n");
//      }
//      if (numFrames_ > 1 || numSlices_ > 1 || numChannels_ > 1) {
//         sb.append("hyperstack=true\n");
//      }
//      if (numChannels_ > 1 && numSlices_ > 1 && masterMPTiffStorage_.slicesFirst()) {
//         sb.append("order=zct\n");
//      }
//      //cm so calibration unit is consistent with units used in Tiff tags
//      sb.append("unit=um\n");
//      if (numSlices_ > 1) {
//         sb.append("spacing=" + zStepUm_ + "\n");
//      }
//      //write single channel contrast settings or display mode if multi channel
//      try {             
//         JSONObject channel0setting = masterMPTiffStorage_.getDisplayAndComments().getJSONArray("Channels").getJSONObject(0);
//         if (numChannels_ == 1) {
//            double min = channel0setting.getInt("Min");
//            double max = channel0setting.getInt("Max");
//            sb.append("min=" + min + "\n");
//            sb.append("max=" + max + "\n");
//         } else {
//            int displayMode = channel0setting.getInt("DisplayMode");
//            //COMPOSITE=1, COLOR=2, GRAYSCALE=3
//            if (displayMode == 1) {
//               sb.append("mode=composite\n");
//            } else if (displayMode == 2) {
//               sb.append("mode=color\n");
//            } else if (displayMode==3) {
//               sb.append("mode=gray\n");
//            }    
//         }
//      } catch (JSONException ex) {}
//   }
   public static ColorModel createGrayscaleColorModel(boolean invert) {
      byte[] rLUT = new byte[256];
      byte[] gLUT = new byte[256];
      byte[] bLUT = new byte[256];
      if (invert) {
         for (int i = 0; i < 256; i++) {
            rLUT[255 - i] = (byte) i;
            gLUT[255 - i] = (byte) i;
            bLUT[255 - i] = (byte) i;
         }
      } else {
         for (int i = 0; i < 256; i++) {
            rLUT[i] = (byte) i;
            gLUT[i] = (byte) i;
            bLUT[i] = (byte) i;
         }
      }
      return (new IndexColorModel(8, 256, rLUT, gLUT, bLUT));
   }

   public static JSONObject getDisplaySettingsFromSummary(JSONObject summaryMetadata) {
      try {
         JSONObject displaySettings = new JSONObject();

         JSONArray chColors = MDUtils.getJSONArrayMember(summaryMetadata, "ChColors");
         JSONArray chNames = MDUtils.getJSONArrayMember(summaryMetadata, "ChNames");
         JSONArray chMaxes, chMins;
         if (summaryMetadata.has("ChContrastMin")) {
            chMins = MDUtils.getJSONArrayMember(summaryMetadata, "ChContrastMin");
         } else {
            chMins = new JSONArray();
            for (int i = 0; i < chNames.length(); i++) {
               chMins.put(0);
            }
         }
         if (summaryMetadata.has("ChContrastMax")) {
            chMaxes = MDUtils.getJSONArrayMember(summaryMetadata, "ChContrastMax");
         } else {
            int max = 65536;
            if (summaryMetadata.has("BitDepth")) {
               max = (int) (Math.pow(2, summaryMetadata.getInt("BitDepth")) - 1);
            }
            chMaxes = new JSONArray();
            for (int i = 0; i < chNames.length(); i++) {
               chMaxes.put(max);
            }
         }

         int numComponents = MDUtils.getNumberOfComponents(summaryMetadata);

         JSONArray channels = new JSONArray();
         if (numComponents > 1) //RGB
         {
            int rgbChannelBitDepth;
            try {
               rgbChannelBitDepth = MDUtils.getBitDepth(summaryMetadata);
            } catch (Exception e) {
               rgbChannelBitDepth = summaryMetadata.getString("PixelType").endsWith("32") ? 8 : 16;
            }
            for (int k = 0; k < 3; k++) {
               JSONObject channelObject = new JSONObject();
               channelObject.put("Color", rgb[k].getRGB());
               channelObject.put("Name", rgbNames[k]);
               channelObject.put("Gamma", 1.0);
               channelObject.put("Min", 0);
               channelObject.put("Max", Math.pow(2, rgbChannelBitDepth) - 1);
               channels.put(channelObject);
            }
         } else {
            for (int k = 0; k < chNames.length(); ++k) {
               String name = (String) chNames.get(k);
               int color = 0;
               if (k < chColors.length()) {
                  color = chColors.getInt(k);
               }
               int min = 0;
               if (k < chMins.length()) {
                  min = chMins.getInt(k);
               }
               int max = chMaxes.getInt(0);
               if (k < chMaxes.length()) {
                  max = chMaxes.getInt(k);
               }
               JSONObject channelObject = new JSONObject();
               channelObject.put("Color", color);
               channelObject.put("Name", name);
               channelObject.put("Gamma", 1.0);
               channelObject.put("Min", min);
               channelObject.put("Max", max);
               channels.put(channelObject);
            }
         }

         displaySettings.put("Channels", channels);

         JSONObject comments = new JSONObject();
         String summary = "";
         try {
            summary = summaryMetadata.getString("Comment");
         } catch (JSONException ex) {
            summaryMetadata.put("Comment", "");
         }
         comments.put("Summary", summary);
         displaySettings.put("Comments", comments);
         return displaySettings;
      } catch (Exception e) {
         ReportingUtils.showError("Summary metadata not found or corrupt.  Is this a Micro-Manager dataset?");
         return null;
      }
   }

   public static void main(String[] args) {
      ImageJ imagej = new ImageJ();
      //String dir = "G:/data/Output/";
      //String prefixIn = "SMS_2013_0823_0017_1_Z_10";
      String dir = "C:/MicroManagerData/Test/dataXMT15";
      String prefixIn = "SMS_2012_1206_1749_1";
      new DatasetHyperstack(dir, prefixIn).createImagePlus().show();
   }
}
