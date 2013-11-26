package edu.mbl.cdp.ps.orient;

import ij.ImagePlus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author GBH
 */
public class PolScope {

   private static final String POLALGO = "PolScope_Algorithm";

   private static final String BIREF = "Birefringence";
   private static final String RETCEIL = "~ Retardance Ceiling (nm)";
   //
   private static final String FLUOR = "Fluorescence";
   private static final String DIAT = "Diattenuation";
   private static final String PROCCEIL = "~Process Ceiling";

   public enum Type {

      birefringence, diattenuation, fluorescence
   }

   public static JSONObject getSummaryMetadataFromImageInfo(ImagePlus imp) throws JSONException {
      JSONObject sumMD;
      try {
         String infoProperty = (String) imp.getProperty("Info");
         sumMD = new JSONObject(infoProperty);
         return sumMD;
      } catch (Exception ex) {
           throw new JSONException("unable to getSummaryMetadataFromImageInfo");
      }
      
   }

   public static PolScope.Type getType(JSONObject sumMD) throws JSONException {
      PolScope.Type psType;
      try {
         String algo = getPolScopeAlgorithm(sumMD);
         if (algo == null) {
            throw new JSONException("PolScope algorithm not found");
         }
         return getType(algo);
      } catch (JSONException ex) {
         throw new JSONException("PolScope algorithm not found");
      }
   }

   public static PolScope.Type getType(String algo) {
      PolScope.Type psType;
      if (algo.contains(BIREF)) {
         psType = PolScope.Type.birefringence;
      } else if (algo.contains(FLUOR)) {
         psType = PolScope.Type.fluorescence;
      } else if (algo.contains(DIAT)) {
         psType = PolScope.Type.diattenuation;
      } else {
         psType = null;
      }
      return psType;

   }

   public static String getPolScopeAlgorithm(JSONObject map) throws JSONException {
      if (map.has(POLALGO)) {
         return map.getString(POLALGO);
      } else {
         throw new JSONException("PolScope algorithm not found");
      }
   }

   public static float getRetardanceCeiling(JSONObject map) throws JSONException {
      if (map.has(RETCEIL)) {
         try {
            return Float.valueOf(String.valueOf(map.getDouble(RETCEIL)));
         } catch (JSONException ex) {
            throw ex;
         }
      } else {
         throw new JSONException("RetardanceCeiling not found");
      }
   }

   public static float getProcessCeiling(JSONObject map) throws JSONException {
      if (map.has(PROCCEIL)) {
         try {
            return Float.valueOf(String.valueOf(map.getDouble(PROCCEIL)));
         } catch (JSONException ex) {
            throw ex;
         }
      } else {
         throw new JSONException("ProcessCeiling not found");
      }
   }

}
