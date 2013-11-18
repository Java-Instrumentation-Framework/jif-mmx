package edu.mbl.jif.imaging.dataset.metadata;

import edu.mbl.jif.imaging.mmtiff.MDUtils;
import edu.mbl.jif.imaging.mmtiff.ReportingUtils;
import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Adds or removes a channel from the appropriate metadata arrays.
 * 
 * @author GBH
 */
public class ChannelMetadata {


   static JSONArray chMaxes = new JSONArray();
   static JSONArray chMins = new JSONArray();
   static JSONArray chColors = new JSONArray();
   static JSONArray chNames = new JSONArray();
   
      // Add a channel...
   // Start with:  numChannels = n, chNames = ["chanA", "chanB", ...]
   // Add channel "chanC" at index 2
   // End with:   numChannels = n + 1, chNames = ["chanA", "chanC", "chanB", ...]
   // Display settings: 
   public static JSONObject addChannel(JSONObject sumMD, String channelName, int index) {
      JSONObject newSumMD = MDUtils.copy(sumMD);
      getChannelArrays(newSumMD);
      // add the channel with default color, min and max.
      try {
         int max = (int) (Math.pow(2, sumMD.getInt("BitDepth")) - 1); //default Max
         insertChannel(channelName, index, max);
      } catch (JSONException ex) {
         Logger.getLogger(ChannelMetadata.class.getName()).log(Level.SEVERE, null, ex);
      }
      putChannelArrays(newSumMD);
      return newSumMD;
   }

   private static void insertChannel(String channelName, int index, int max) {
      JSONArray chMaxes_ = new JSONArray();
      JSONArray chMins_ = new JSONArray();
      JSONArray chColors_ = new JSONArray();
      JSONArray chNames_ = new JSONArray();
      int numChannels = chNames.length();
      boolean pastIndex = false;
      for (int i = 0; i < numChannels + 1; i++) {
         if (i == index) { // add the new channel
            try {
               chColors_.put(0, Color.white.getRGB());
               chNames_.put(0, channelName);
               chMins_.put(0);
               chMaxes_.put(max);
            } catch (JSONException ex) {
               ReportingUtils.logError(ex);
            }
            pastIndex = true;
         } else { // copy the old channel
            int j = i;
            if (pastIndex) {
               j++;
            }
            try {
               chColors_.put(j, chColors.get(j));
               chNames_.put(j, chNames.get(j));
               chMins_.put(j, chMins.get(j));
               chMaxes_.put(j, chMaxes.get(j));
            } catch (JSONException ex) {
               ReportingUtils.logError(ex);
            }
         }
      }
      chMaxes = chMaxes_;
      chMins = chMins_;
      chColors = chColors_;
      chNames = chNames_;
   }

   // Remove a channel...
   // Start with:  numChannels = n, chNames = ["chanA", "chanB", "chanC", ...]
   // Remove channel 2,
   // End with:   numChannels = n - 1, chNames = ["chanA", "chanC", ...]
   // Display settings: 
   public static JSONObject removeChannel(JSONObject sumMD, int channelIndex) {
      JSONObject newSumMD = MDUtils.copy(sumMD);
      getChannelArrays(newSumMD);
      chNames.remove(channelIndex);
      chColors.remove(channelIndex);
      chMins.remove(channelIndex);
      chMaxes.remove(channelIndex);
      putChannelArrays(newSumMD);
      return newSumMD;
   }

   private  static void getChannelArrays(JSONObject summaryMetadata) {
      try {
         chColors = MDUtils.getJSONArrayMember(summaryMetadata, "ChColors");
         chNames = MDUtils.getJSONArrayMember(summaryMetadata, "ChNames");
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
      } catch (JSONException ex) {
         Logger.getLogger(ChannelMetadata.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   private static void putChannelArrays(JSONObject sumMD) {
      try {
         sumMD.put("ChColors", MDUtils.copy(chColors));
         sumMD.put("ChNames", MDUtils.copy(chNames));
         sumMD.put("ChContrastMax", MDUtils.copy(chMaxes));
         sumMD.put("ChContrastMin", MDUtils.copy(chMins));
      } catch (Exception e) {
         ReportingUtils.logError(e);
      }
   }


}
