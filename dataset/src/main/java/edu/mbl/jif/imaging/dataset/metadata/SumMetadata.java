/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mbl.jif.imaging.dataset.metadata;

import edu.mbl.jif.imaging.dataset.MMgrDatasetAccessor;
import edu.mbl.jif.imaging.dataset.MMgrDatasetGenerator;
import edu.mbl.jif.imaging.mmtiff.MDUtils;
import edu.mbl.jif.imaging.mmtiff.ReportingUtils;
import java.awt.Color;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * Creates and modifies SummaryMetadata
 *
 * @author GBH
 */
public class SumMetadata {

   public static JSONObject newCopyOfSummaryMetadata(JSONObject summaryMetadataIn,
           String directory, String prefix, boolean replaceContext, String comment)
           throws JSONException {

      JSONObject summaryMetadata = MDUtils.copy(summaryMetadataIn);
      summaryMetadata.put("UUID", UUID.randomUUID());
      summaryMetadata.put("Directory", directory);// clj
      summaryMetadata.put("Prefix", prefix); // clj

      if (replaceContext) {
         summaryMetadata.put("Date", new SimpleDateFormat("yyyy-MM-dd").
                 format(Calendar.getInstance().getTime()));
         summaryMetadata.put("Time", Calendar.getInstance().getTime());
         String compName = null;
         try {
            compName = InetAddress.getLocalHost().getHostName();
         } catch (UnknownHostException e) {
            ReportingUtils.showError(e);
         }
         if (compName != null) {
            summaryMetadata.put("ComputerName", compName);
         }
         summaryMetadata.put("UserName", System.getProperty("user.name"));
      }
      //
      if (comment != null) {
         String c = "";
         try {
            c = summaryMetadata.getString("Comment");
            c = c + " ... ";
         } catch (JSONException jSONException) {
         }
         summaryMetadata.put("Comment", c + comment);
      }
      return summaryMetadata;
   }

   public static void addChannel(JSONObject summaryMetadata, String channelName, int chanIndex) {
      ChannelMetadata.addChannel(summaryMetadata, channelName, chanIndex);
   }

   public static void removeChannel(JSONObject summaryMetadata, int chanIndex) {
      ChannelMetadata.removeChannel(summaryMetadata, chanIndex);
   }

   public static void applyDimensionalExtents(JSONObject summaryMetadata,
           int numSlices, int numFrames, int numPositions)
           throws JSONException {
      if (numSlices > 0) {
         summaryMetadata.put("Slices", numSlices); // z-sections
      }
      if (numFrames > 0) {
         summaryMetadata.put("Frames", numFrames); // timePoints
      }
      if (numPositions > 0) {
         summaryMetadata.put("Positions", numPositions); // x-y
      }
   }

   public static void applyImageAttributes(JSONObject summaryMetadata, ImageAttributes imgAtribs)
           throws JSONException {
      summaryMetadata.put("Width", imgAtribs.width);
      summaryMetadata.put("Height", imgAtribs.height);
      summaryMetadata.put("Depth", imgAtribs.bytesPerPixel);
      summaryMetadata.put("BitDepth", imgAtribs.bitDepth);
      summaryMetadata.put("IJType", imgAtribs.ijType);
      summaryMetadata.put("PixelAspect", 1.0);
      summaryMetadata.put("PixelSize_um", imgAtribs.pixelSize_um);
      summaryMetadata.put("PixelType", imgAtribs.pixelType);
      summaryMetadata.put("Binning", imgAtribs.binning);
   }

   public static void changeImageSize(JSONObject summaryMetadata, int w, int h)
           throws JSONException {
      summaryMetadata.put("Width", w);
      summaryMetadata.put("Height", h);

   }

   public static JSONObject newSummaryMetadata(
           String directory, String prefix,
           ImageAttributes imgAtribs,
           String[] channelNames, int numSlices, int numFrames, int numPositions,
           String source, String comment) {

      JSONObject summaryMetadata = new JSONObject();

      try {
         // Context...
         String compName = null;
         try {
            compName = InetAddress.getLocalHost().getHostName();
         } catch (UnknownHostException e) {
            ReportingUtils.showError(e);
         }
         if (compName != null) {
            summaryMetadata.put("ComputerName", compName);
         }
         summaryMetadata.put("UserName", System.getProperty("user.name"));
         // 
         summaryMetadata.put("Source", source); // clj
         //
         summaryMetadata.put("MetadataVersion", 10);
         summaryMetadata.put("MicroManagerVersion", "1.4" /*MMStudioMainFrame.getInstance().getVersion()*/);
         //
         summaryMetadata.put("Directory", directory);// clj
         summaryMetadata.put("Prefix", prefix); // clj
         //
         summaryMetadata.put("Comment", comment);
         //
         summaryMetadata.put("Date", new SimpleDateFormat("yyyy-MM-dd").
                 format(Calendar.getInstance().getTime()));
         summaryMetadata.put("Time", Calendar.getInstance().getTime());
         summaryMetadata.put("UUID", UUID.randomUUID());
         //
         // Image
         summaryMetadata.put("Width", imgAtribs.width);
         summaryMetadata.put("Height", imgAtribs.height);
         summaryMetadata.put("Depth", imgAtribs.bytesPerPixel);
         summaryMetadata.put("BitDepth", imgAtribs.bitDepth);
         summaryMetadata.put("IJType", imgAtribs.ijType);
         summaryMetadata.put("PixelAspect", 1.0);
         summaryMetadata.put("PixelSize_um", imgAtribs.pixelSize_um);
         summaryMetadata.put("PixelType", imgAtribs.pixelType);
         summaryMetadata.put("Binning", imgAtribs.binning);
         //summaryMetadata.put("NumComponents", 1);  //??
         // Channels
         setChannelsAndDisplay(summaryMetadata, channelNames);
         // Dimensions
         summaryMetadata.put("Slices", numSlices); // z-sections
         summaryMetadata.put("Frames", numFrames); // timePoints
         summaryMetadata.put("Positions", numPositions); // x-y
         summaryMetadata.put("TimeFirst", isTimeFirst(summaryMetadata));
         summaryMetadata.put("SlicesFirst", !isTimeFirst(summaryMetadata));
         //
         // Unused...
         //summaryMetadata.put("ROI", cameraRoi); // clj
         //summaryMetadata.put("StartTime", startTime); // ??
         //summaryMetadata.put("GridColumn", 0);
         //summaryMetadata.put("GridRow", 0);
         //summaryMetadata.put("z-step_um", zStep_um); // clj
         //summaryMetadata.put("Interval_ms", interval_ms); // clj
         //summaryMetadata.put("CustomIntervals_ms", customIntervals_ms); // clj
         //summaryMetadata.put("KeepShutterOpenChannels", keepShutterOpenChannels); // clj
         //summaryMetadata.put("KeepShutterOpenSlices", keepShutterOpenSlices); // clj
      } catch (JSONException ex) {
         ReportingUtils.showError(ex);
      }
      return summaryMetadata;
   }

   public static void setChannelsAndDisplay(JSONObject md, String[] channelNames_) {
      int numChannels = channelNames_.length;
      JSONArray channelMaxes = new JSONArray();
      JSONArray channelMins = new JSONArray();
      JSONArray channelColors = new JSONArray();
      JSONArray channelNames = new JSONArray();
      for (int i = 0; i < numChannels; i++) {
         try {
            channelColors.put(i, Color.white.getRGB());
            channelNames.put(i, channelNames_[i]);
            channelMaxes.put(Math.pow(2, md.getInt("BitDepth")) - 1);
            channelMins.put(0);
         } catch (JSONException ex) {
            Logger.getLogger(MMgrDatasetGenerator.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
      try {
         // Display settings
         md.put("ChColors", channelColors);
         md.put("ChContrastMax", channelMaxes);
         md.put("ChContrastMin", channelMins);
         // Channels
         md.put("ChNames", channelNames);
         md.put("Channels", channelNames.length());
      } catch (Exception e) {
         ReportingUtils.logError(e);
      }
   }

   public static DimensionalExtents getDimensionalExtents(JSONObject summaryMD) {
      DimensionalExtents dimExts = null;
      try {
         dimExts = new DimensionalExtents(
                 MDUtils.getNumChannels(summaryMD),
                 MDUtils.getNumSlices(summaryMD),
                 MDUtils.getNumFrames(summaryMD),
                 isTimeFirst(summaryMD),
                 MDUtils.getNumPositions(summaryMD));
      } catch (JSONException ex) {
         Logger.getLogger(SummaryMetadata.class.getName()).log(Level.SEVERE, null, ex);
         return null;
      }
      return dimExts;
   }

   public static boolean isTimeFirst(JSONObject map) {
      if (map.has("TimeFirst")) {
         try {
            return map.getBoolean("TimeFirst");
         } catch (JSONException ex) {
            return false;
         }
      } else {
         return false;
      }
   }

   public static String[] getChannelNames(JSONObject sumMD)
           throws JSONException {
      JSONArray chNames = MDUtils.getJSONArrayMember(sumMD, "ChNames");
      String[] names = new String[chNames.length()];
      for (int j = 0; j < chNames.length(); j++) {
         names[j] = chNames.getString(j);
      }
      return names;
   }

   public static String toOrderedString(JSONObject sumMD) {
      StringBuilder sb = new StringBuilder();
      sb.append("Summary Metadata:\n");
      item(sb,sumMD, "Directory");
      item(sb,sumMD, "Prefix");
      item(sb,sumMD, "UserName");
      item(sb,sumMD, "Source");
      item(sb,sumMD, "MetadataVersion");
      item(sb,sumMD, "MicroManagerVersion");
      item(sb,sumMD, "Comment");
      item(sb,sumMD, "Date");
      item(sb,sumMD, "Time");
      item(sb,sumMD, "UUID");
      sb.append("Image Attributes:\n");
      item(sb,sumMD, "Width");
      item(sb,sumMD, "Height");
      item(sb,sumMD, "Depth");
      item(sb,sumMD, "BitDepth");
      item(sb,sumMD, "IJType");
      item(sb,sumMD, "PixelAspect");
      item(sb,sumMD, "PixelSize_um");
      item(sb,sumMD, "PixelType");
      item(sb,sumMD, "Binning");
      item(sb,sumMD, "NumComponents");

      sb.append("Dimensional extents:\n");
      item(sb,sumMD, "Slices"); // z-sections
      item(sb,sumMD, "Frames"); // timePoints
      item(sb,sumMD, "Positions"); // x-y
      item(sb,sumMD, "TimeFirst");
      item(sb,sumMD, "SlicesFirst");
      // add channelNames...
      sb.append("end.\n");
      
      return sb.toString();
   }

   private static void item(StringBuilder sb, JSONObject sumMD, String key) {
      try {
          String value = sumMD.get(key).toString();
         sb.append(key + ": " + value + "\n");
      } catch (JSONException ex) {
         sb.append(key + " (not found)\n");
      }
      
   }
   public static void main(String[] args) {
      String dir = "C:\\MicroManagerData\\Test\\dataXMT15";
      String prefix = "SM_2012_1206_1606_1";
      try {
         MMgrDatasetAccessor datasetIn = new MMgrDatasetAccessor(dir, prefix, true, false);
         String out = SumMetadata.toOrderedString(datasetIn.getImageCache().getSummaryMetadata());
         System.out.println(out);
      } catch (Exception ex) {
      ex.printStackTrace();
      }
   }
}
