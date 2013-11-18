package edu.mbl.jif.imaging.dataset.metadata;

import edu.mbl.jif.imaging.dataset.MMgrDatasetGenerator;
import edu.mbl.jif.imaging.mmtiff.MDUtils;
import edu.mbl.jif.imaging.mmtiff.ReportingUtils;
import java.awt.Color;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * SummaryMetadata object for Micro-Manager datasets
 *
 * @author GBH
 */
public class SummaryMetadata {

   String rootDirectory = null;
   String prefix = "prefix";
   //
   String source = "undefined";
   String comment = "";
   // Channels
   String[] channelNames = null;
   int numChannels = 0;
   // Dimensions
   int numSlices = 0;
   int numFrames = 0;
   int numPositions = 0;
   //		
   // Image Type/Properties
//	int binning = 1;
//   ImageAttributes imgAtribs = new ImageAttributes();
//	int height;
//	int width;
//	int pixelSize_um;  // ? units
//	int bitDepth;
//	// from core.GetBitDepth() Returns binnings factor.  Used to calculate current pixelsize
//	// "Not appropriately named."
//	int bytesPerPixel;
//	// from core.GetImageBytesPerPixel()  Returns the bit depth (dynamic range) of the pixel.
//	//  E.g. 12bit range in 16bit short.
//	String pixelType; // GRAY8 | GRAY16 | RBG32 | RBG64
//	int byteDepth;    //   1 | 2 | 4 | 8
//	int ijType = -1;// ImagePlus.GRAY8 | ImagePlus.GRAY16 | ImagePlus.GRAY32 | ImagePlus.COLOR_RGB
   //-----------

   //String startTime = "";
   //
   public void setPrefix(String prefix, String rootDirectory) {
      this.prefix = prefix;
      this.rootDirectory = rootDirectory;
   }

   public static String getDirectory(JSONObject map) throws JSONException {
      if (map.has("Directory")) {
         return map.getString("Directory");
      } else {
         return null;
      }
   }

   public static void setDirectory(JSONObject map, String dir) throws JSONException {
      map.put("Directory", dir);
   }

   public static String getPrefix(JSONObject map) throws JSONException {
      if (map.has("Prefix")) {
         return map.getString("Prefix");
      } else {
         return null;
      }
   }

   public static void setPrefix(JSONObject map, String prefix) throws JSONException {
      map.put("Prefix", prefix);
   }

   public void setDimensionExtents(
           int numFrames,
           int numChannels,
           int numSlices,
           int numPositions) {
      this.numFrames = numFrames;
      this.numChannels = numChannels;
      this.numSlices = numSlices;
      this.numPositions = numPositions;
   }

   public void setChannelNames(String[] chNames) {
      this.channelNames = chNames;
   }

   public static String[] getChannelNames(JSONObject sumMD) throws JSONException {
      JSONArray chNames = MDUtils.getJSONArrayMember(sumMD, "ChNames");
      String[] names = new String[chNames.length()];
      for (int j = 0; j < chNames.length(); j++) {
         names[j] = chNames.getString(j);
      }
      return names;
   }

   
//   public void setImageProps(ImageAttributes attribs) {
//      this.imgAtribs.height = attribs.height;
//      this.imgAtribs.width = attribs.width;
//      this.imgAtribs.binning = attribs.binning;
//      this.imgAtribs.bitDepth = attribs.bitDepth;
//      this.imgAtribs.bytesPerPixel = attribs.bytesPerPixel;
//      this.imgAtribs.pixelType = attribs.pixelType;
//      this.imgAtribs.byteDepth = attribs.byteDepth;
//      this.imgAtribs.ijType = attribs.ijType;
//      this.imgAtribs.pixelSize_um = attribs.pixelSize_um;
//   }

//   public void setImageProps(
//           int height,
//           int width,
//           int binning,
//           int pixelSizeUm,
//           int bitDepth,
//           int bytesPerPixel,
//           String pixelType,
//           int byteDepth,
//           int ijType) {
//      this.imgAtribs.height = height;
//      this.imgAtribs.width = width;
//      this.imgAtribs.binning = binning;
//      this.imgAtribs.pixelSize_um = pixelSizeUm;
//      this.imgAtribs.bitDepth = bitDepth;
//      this.imgAtribs.bytesPerPixel = bytesPerPixel;
//      this.imgAtribs.pixelType = pixelType;
//      this.imgAtribs.byteDepth = byteDepth;
//      this.imgAtribs.ijType = ijType;
//   }

   // Add other fields to the summary metadata...
   public static void addToSummaryMetadata(Map map, JSONObject sumMd) {
      Iterator entries = map.entrySet().iterator();
      while (entries.hasNext()) {
         Map.Entry entry = (Map.Entry) entries.next();
         try {
            sumMd.put((String) entry.getKey(), entry.getValue());
         } catch (JSONException ex) {
            // error
         }
      }
   }

   //
//   public static SummaryMetadata createSummaryMetadata(MMDatasetDefinition dsd) {
//      SummaryMetadata sumMD = new SummaryMetadata();
//      sumMD.setPrefix(dsd.prefix, dsd.directory);
//      sumMD.setImageProps(dsd.attribs);
//      sumMD.setChannelNames(dsd.channelNames);
//      int numChannels = dsd.channelNames.length;
//      sumMD.setDimensionExtents(dsd.numFrames, numChannels, dsd.numSlices, dsd.numPositions);
//      return sumMD;
//   }
//
//   //
//   public static SummaryMetadata createSummaryMetadata(JSONObject summaryMetadataIn, MMDatasetDefinition dsd) {
//      SummaryMetadata sumMD = new SummaryMetadata();
//      sumMD.setPrefix(dsd.prefix, dsd.directory);
//      sumMD.setImageProps(dsd.attribs);
//      sumMD.setChannelNames(dsd.channelNames);
//      int numChannels = dsd.channelNames.length;
//      sumMD.setDimensionExtents(dsd.numFrames, numChannels, dsd.numSlices, dsd.numPositions);
//      return sumMD;
//   }

//   public JSONObject createJsonSummaryMetadata() {
//      return createJsonSummaryMetadata(null);
//   }
//
//   public JSONObject createJsonSummaryMetadata(JSONObject summaryMetadataIn) {
//      JSONObject summaryMetadata = null;
//      if (summaryMetadataIn == null) {
//         summaryMetadata = new JSONObject();
//      } else {
//         summaryMetadata = MDUtils.copy(summaryMetadataIn);
//      }
//      try {
//         // Context...
//
//         String compName = null;
//         try {
//            compName = InetAddress.getLocalHost().getHostName();
//         } catch (UnknownHostException e) {
//            ReportingUtils.showError(e);
//         }
//         if (compName != null) {
//            summaryMetadata.put("ComputerName", compName);
//         }
//         //
//         summaryMetadata.put("UserName", System.getProperty("user.name"));
//         // 
//         summaryMetadata.put("Source", source); // clj
//         //
//         summaryMetadata.put("MetadataVersion", 10);
//         summaryMetadata.put("MicroManagerVersion", "1.4" /*MMStudioMainFrame.getInstance().getVersion()*/);
//         //
//         summaryMetadata.put("Directory", rootDirectory);// clj
//         summaryMetadata.put("Prefix", prefix); // clj
//         //
//         summaryMetadata.put("Comment", comment);
//         //
//         summaryMetadata.put("Date", new SimpleDateFormat("yyyy-MM-dd").
//                 format(Calendar.getInstance().getTime()));
//         summaryMetadata.put("Time", Calendar.getInstance().getTime());
//         summaryMetadata.put("UUID", UUID.randomUUID());
//         //
//         // Image
//         summaryMetadata.put("Width", imgAtribs.width);
//         summaryMetadata.put("Height", imgAtribs.height);
//         summaryMetadata.put("Depth", imgAtribs.bytesPerPixel);
//         summaryMetadata.put("BitDepth", imgAtribs.bitDepth);
//         summaryMetadata.put("IJType", imgAtribs.ijType);
//         summaryMetadata.put("PixelAspect", 1.0);
//         summaryMetadata.put("PixelSize_um", imgAtribs.pixelSize_um);
//         summaryMetadata.put("PixelType", imgAtribs.pixelType);
//         summaryMetadata.put("Binning", imgAtribs.binning);
//         //summaryMetadata.put("NumComponents", 1);  //??
//         //
//         // Channels
//         summaryMetadata.put("Channels", numChannels);
//         setChannelsAndDisplay(summaryMetadata);
//         /*	Display settings for Channels	*/
////			summaryMetadata.put("ChContrastMin", "[...]");
////			summaryMetadata.put("ChContrastMax", "[...]");
////			summaryMetadata.put("ChColors", "[...]");
//         //
//         // Dimensions
//         summaryMetadata.put("Slices", numSlices); // z-sections
//         summaryMetadata.put("Frames", numFrames); // timePoints
//         summaryMetadata.put("Positions", numPositions); // x-y
//         summaryMetadata.put("TimeFirst", isTimeFirst(summaryMetadata));
//         summaryMetadata.put("SlicesFirst", !isTimeFirst(summaryMetadata));
//         //
//         // Unused...
//         //summaryMetadata.put("ROI", cameraRoi); // clj
//         //summaryMetadata.put("StartTime", startTime); // ??
//         //summaryMetadata.put("GridColumn", 0);
//         //summaryMetadata.put("GridRow", 0);
//         //summaryMetadata.put("z-step_um", zStep_um); // clj
//         //summaryMetadata.put("Interval_ms", interval_ms); // clj
//         //summaryMetadata.put("CustomIntervals_ms", customIntervals_ms); // clj
//         //summaryMetadata.put("KeepShutterOpenChannels", keepShutterOpenChannels); // clj
//         //summaryMetadata.put("KeepShutterOpenSlices", keepShutterOpenSlices); // clj
//      } catch (JSONException ex) {
//         ReportingUtils.showError(ex);
//      }
//      return summaryMetadata;
//   }
//
//   public JSONObject copyJsonSummaryMetadata(JSONObject summaryMetadataIn) {
//      JSONObject summaryMetadata = null;
//      if (summaryMetadataIn == null) {
//         summaryMetadata = new JSONObject();
//      } else {
//         summaryMetadata = MDUtils.copy(summaryMetadataIn);
//      }
//      try {
//         // Context...
//         String compName = null;
//         try {
//            compName = InetAddress.getLocalHost().getHostName();
//         } catch (UnknownHostException e) {
//            ReportingUtils.showError(e);
//         }
//         if (compName != null) {
//            summaryMetadata.put("ComputerName", compName);
//         }
//         summaryMetadata.put("UserName", System.getProperty("user.name"));
//         // 
//         summaryMetadata.put("Source", source); // clj
//         //
//         summaryMetadata.put("MetadataVersion", 10);
//         summaryMetadata.put("MicroManagerVersion", "1.4" /*MMStudioMainFrame.getInstance().getVersion()*/);
//         //
//         summaryMetadata.put("Directory", rootDirectory);// clj
//         summaryMetadata.put("Prefix", prefix); // clj
//         //
//         summaryMetadata.put("Comment", comment);
//         //
//         summaryMetadata.put("Date", new SimpleDateFormat("yyyy-MM-dd").
//                 format(Calendar.getInstance().getTime()));
//         summaryMetadata.put("Time", Calendar.getInstance().getTime());
//         summaryMetadata.put("UUID", UUID.randomUUID());
//         //
//         // Image
//         summaryMetadata.put("Width", imgAtribs.width);
//         summaryMetadata.put("Height", imgAtribs.height);
//         summaryMetadata.put("Depth", imgAtribs.bytesPerPixel);
//         summaryMetadata.put("BitDepth", imgAtribs.bitDepth);
//         summaryMetadata.put("IJType", imgAtribs.ijType);
//         summaryMetadata.put("PixelAspect", 1.0);
//         summaryMetadata.put("PixelSize_um", imgAtribs.pixelSize_um);
//         summaryMetadata.put("PixelType", imgAtribs.pixelType);
//         summaryMetadata.put("Binning", imgAtribs.binning);
//         //summaryMetadata.put("NumComponents", 1);  //??
//         //
//         // Channels
//         summaryMetadata.put("Channels", numChannels);
//         setChannelsAndDisplay(summaryMetadata);
//         /*	Display settings for Channels	*/
////			summaryMetadata.put("ChContrastMin", "[...]");
////			summaryMetadata.put("ChContrastMax", "[...]");
////			summaryMetadata.put("ChColors", "[...]");
//         //
//         // Dimensions
//         summaryMetadata.put("Slices", numSlices); // z-sections
//         summaryMetadata.put("Frames", numFrames); // timePoints
//         summaryMetadata.put("Positions", numPositions); // x-y
//         summaryMetadata.put("TimeFirst", isTimeFirst(summaryMetadata));
//         summaryMetadata.put("SlicesFirst", !isTimeFirst(summaryMetadata));
//         //
//         // Unused...
//         //summaryMetadata.put("ROI", cameraRoi); // clj
//         //summaryMetadata.put("StartTime", startTime); // ??
//         //summaryMetadata.put("GridColumn", 0);
//         //summaryMetadata.put("GridRow", 0);
//         //summaryMetadata.put("z-step_um", zStep_um); // clj
//         //summaryMetadata.put("Interval_ms", interval_ms); // clj
//         //summaryMetadata.put("CustomIntervals_ms", customIntervals_ms); // clj
//         //summaryMetadata.put("KeepShutterOpenChannels", keepShutterOpenChannels); // clj
//         //summaryMetadata.put("KeepShutterOpenSlices", keepShutterOpenSlices); // clj
//      } catch (JSONException ex) {
//         ReportingUtils.showError(ex);
//      }
//      return summaryMetadata;
//   }
//
//   void setChannelsAndDisplay(JSONObject md) {
//      numChannels = channelNames.length;
//      JSONArray channelMaxes = new JSONArray();
//      JSONArray channelMins = new JSONArray();
//      JSONArray channelColors = new JSONArray();
//      JSONArray channelNames = new JSONArray();
//      for (int i = 0; i < numChannels; i++) {
//         try {
//            channelColors.put(i, Color.white.getRGB());
//            channelNames.put(i, this.channelNames[i]);
//            channelMaxes.put(Math.pow(2, md.getInt("BitDepth")) - 1);
//            channelMins.put(0);
//         } catch (JSONException ex) {
//            Logger.getLogger(MMgrDatasetGenerator.class.getName()).log(Level.SEVERE, null, ex);
//         }
//      }
//      try {
//         // Display settings
//         md.put("ChColors", channelColors);
//         md.put("ChContrastMax", channelMaxes);
//         md.put("ChContrastMin", channelMins);
//         // Channels
//         md.put("ChNames", channelNames);
//         md.put("Channels", channelNames.length());
//      } catch (Exception e) {
//         ReportingUtils.logError(e);
//      }
//   }
//
//   public JSONObject changeDimensionalExtents(JSONObject summaryMetadataIn, int numSlices, int numFrames, int numPositions) {
//      JSONObject summaryMetadata = null;
//      summaryMetadata = MDUtils.copy(summaryMetadataIn);
//      try {
//         summaryMetadata.put("Slices", numSlices); // z-sections
//         summaryMetadata.put("Frames", numFrames); // timePoints
//         summaryMetadata.put("Positions", numPositions); // x-y
//      } catch (JSONException ex) {
//         Logger.getLogger(SummaryMetadata.class.getName()).log(Level.SEVERE, null, ex);
//      }
//      return summaryMetadata;
//   }
//
//   public static DimensionalExtents getDimensionalExtents(JSONObject summaryMD) {
//      DimensionalExtents dimExts = null;
//      try {
//         dimExts = new DimensionalExtents(
//                 MDUtils.getNumChannels(summaryMD),
//                 MDUtils.getNumSlices(summaryMD),
//                 MDUtils.getNumFrames(summaryMD),
//                 isTimeFirst(summaryMD),
//                 MDUtils.getNumPositions(summaryMD));
//      } catch (JSONException ex) {
//         Logger.getLogger(SummaryMetadata.class.getName()).log(Level.SEVERE, null, ex);
//         return null;
//      }
//      return dimExts;
//   }
//
//   public static boolean isTimeFirst(JSONObject map) {
//      if (map.has("TimeFirst")) {
//         try {
//            return map.getBoolean("TimeFirst");
//         } catch (JSONException ex) {
//            return false;
//         }
//      } else {
//         return false;
//      }
//   }
   // When derived dataset...
   // TODO This is no good  !!!!! but not used .
//	JSONObject createDerivedSummaryMetadata(JSONObject inSumMD) {
//		try {
//			
//			JSONObject meta = MDUtils.copy(inSumMD);
//			//
//			/*	New UUID	*/ meta.put("UUID", "bc2215c6-f449-4b6c-a00e-071011c1e23b");
//			/*	Context	*/
//			meta.put("UserName", "CDP-HPPAV");
//			meta.put("ComputerName", "CDP-HPPAV-PC");
//			/*	Processor	*/
//			meta.put("Source", "Micro-Manager");
//			/*	Keep	*/
//			meta.put("MetadataVersion", 10);
//			/*	Keep	*/
//			meta.put("MicroManagerVersion", "1.4.7  20111110");
//			/*	Time	*/
//			meta.put("Time", "2012-04-20 13,46,03 -0400");
//			/*	Date	*/
//			meta.put("Date", "2012-04-20");
//			/*	New Dir	*/
//			meta.put("Directory", "C,\\MicroManagerData\\Project 1\\2012_04_17");
//			/*	New Prefix	*/
//			meta.put("Prefix", "SM_2012_0420_1345");
//
//			/*	ImageType	*/
//			meta.put("Depth", 1);
//			meta.put("PixelType", "GRAY8");
//			meta.put("Width", 800);
//			meta.put("Height", 600);
//			meta.put("IJType", 0);
//			meta.put("BitDepth", 8);
//
//			/*	New Channels	*/
//			meta.put("ChNames", "[...]");
//			meta.put("Channels", 7);
//
//			/*	Keep	*/
//			meta.put("PixelSize_um", 0);
//			meta.put("PixelAspect", 1);
//
//			/*	Dimensions	*/
//			meta.put("Frames", 0);
//			meta.put("Slices", 0);
//			meta.put("PositionIndex", 0);
//			meta.put("Positions", 0);
//
//			/*	Display	*/
//			meta.put("ChContrastMin", "[...]");
//			meta.put("ChContrastMax", "[...]");
//			meta.put("ChColors", "[...]");
//			/*	Comment	*/
//			meta.put("Comment", "");
//			//
//			
//			
//			//
//			return meta;
//		} catch (JSONException ex) {
//			Logger.getLogger(SummaryMetadata.class.getName()).log(Level.SEVERE, null, ex);
//			return null;
//		}
//	}
//	int getIJType(int byteDepth_) {
//		int ijType = -1;
//		if (byteDepth_ == 1) {
//			ijType = ImagePlus.GRAY8;
//		} else if (byteDepth_ == 2) {
//			ijType = ImagePlus.GRAY16;
//		} else if (byteDepth_ == 8) {
//			ijType = 64;
//		} else if (byteDepth_ == 4 && core.getNumberOfComponents() == 1) {
//			ijType = ImagePlus.GRAY32;
//		} else if (byteDepth_ == 4 && core.getNumberOfComponents() == 4) {
//			ijType = ImagePlus.COLOR_RGB;
//		}
//		return ijType;
//	}
   /*
    public static String get_(JSONObject map) throws JSONException {
    if (map.has("_")) {
    return map.getString("_");
    } else {
    return null;
    }
    }
	
    public static void set_(JSONObject map, _) throws JSONException {
    map.put("_", _);
    }
    */
}
/* Metadata creation code from AcqEngine.clj
 (defn make-summary-metadata [settings]
 (let [depth (core getBytesPerPixel)
 channels (:channels settings)
 num-camera-channels (core getNumberOfCameraChannels)
 simple-channels (if-not (empty? channels) channels [{:name "Default" :color java.awt.Color/WHITE}])
 super-channels (all-super-channels simple-channels (get-camera-channel-names))
 ch-names (vec (map :name super-channels))]
 (JSONObject. {
 "BitDepth" (core getImageBitDepth)
 "Channels" (count super-channels)
 "ChNames" (JSONArray. ch-names)
 "ChColors" (JSONArray. (channel-colors simple-channels super-channels ch-names))
 "ChContrastMax" (JSONArray. (repeat (count super-channels) Integer/MIN_VALUE))
 "ChContrastMin" (JSONArray. (repeat (count super-channels) Integer/MAX_VALUE))
 "Comment" (:comment settings)
 "ComputerName" (.. InetAddress getLocalHost getHostName)
 "Depth" (core getBytesPerPixel)
 "Directory" (if (:save settings) (settings :root) "")
 "Frames" (count (:frames settings))
 "GridColumn" 0
 "GridRow" 0
 "Height" (core getImageHeight)
 "Interval_ms" (:interval-ms settings)
 "CustomIntervals_ms" (JSONArray. (or (:custom-intervals-ms settings) []))
 "IJType" (get-IJ-type depth)
 "KeepShutterOpenChannels" (:keep-shutter-open-channels settings)
 "KeepShutterOpenSlices" (:keep-shutter-open-slices settings)
 "MicroManagerVersion" (.getVersion gui)
 "MetadataVersion" 10
 "PixelAspect" 1.0
 "PixelSize_um" (core getPixelSizeUm)
 "PixelType" (get-pixel-type)
 "Positions" (count (:positions settings))
 "Prefix" (if (:save settings) (:prefix settings) "")
 "ROI" (JSONArray. (get-camera-roi))
 "Slices" (count (:slices settings))
 "SlicesFirst" (:slices-first settings)
 "Source" "Micro-Manager"
 "TimeFirst" (:time-first settings)
 "UserName" (System/getProperty "user.name")
 "UUID" (UUID/randomUUID)
 "Width" (core getImageWidth)
 "z-step_um" (get-z-step-um (:slices settings))
 })))
 */


/*
	 * Example Result in .txt file:
	 {
"Summary": {
  "Slices": 2,
  "Interval_ms": 1000,
  "UUID": "806454e6-7bec-4d34-aee0-48c0ab98b28e",
  "UserName": "GBH",
  "Depth": 2,
  "PixelType": "GRAY16",
  "z-step_um": -1,
  "MetadataVersion": 10,
  "SlicesFirst": false,
  "ChContrastMin": [ 0, 0 ],
  "Width": 512,
  "PixelAspect": 1,
  "MicroManagerVersion": "1.4.13",
  "ROI": [ 0, 0, 512, 512],
  "ChNames": ["Cy5", "DAPI"],
  "IJType": 1,
  "GridRow": 0,
  "Height": 512,
  "GridColumn": 0,
  "Prefix": "PREFIX1",
  "PixelSize_um": 1,
  "Frames": 3,
  "BitDepth": 16,
  "KeepShutterOpenChannels": false,
  "Source": "Micro-Manager",
  "Channels": 2,
  "ComputerName": "GBH-VAIO",
  "CustomIntervals_ms": [],
  "KeepShutterOpenSlices": false,
  "ChColors": [ -1, -1 ],
  "TimeFirst": false,
  "ChContrastMax": [ 65536, 65536],
  "Positions": 1,
  "Directory": "C:\\MicroManagerData\\project\\MultiTiffPosXX"
},
"FrameKey-0-0-0": {
  "Objective-Name": "DObjective",
  "Path-Label": "State-0",
  "Core-Focus": "Z",
  "Channel": "Cy5",
  "Core-Initialize": "1",
  "Z-Name": "DStage",
  "FrameIndex": 0,
  "Excitation-ClosedPosition": "0",
  "Emission-State": "0",
  "Shutter-Description": "Demo shutter driver",
  "Dichroic-State": "0",
  "Camera": "",
  "Core-ImageProcessor": "",
  "Camera-TransposeXY": "0",
  "Camera-Name": "DCam",
  "SlicePosition": 1,
  "DHub-DivideOneByMe": "1",
  "Exposure-ms": 10,
  "Path-Name": "DLightPath",
  "Objective-Trigger": "-",
  "Core-AutoShutter": "1",
  "Dichroic-Description": "Demo filter wheel driver",
  "NextFrame": 0,
  "Emission-Description": "Demo filter wheel driver",
  "Core-XYStage": "XY",
  "Camera-CameraID": "V1.0",
  "AxisPositions": null,
  "Objective-Label": "Nikon 10X S Fluor",
  "Camera-CCDTemperature RO": "0.0000",
  "Time": "2012-12-04 14:16:45 -0500",
  "Path-Description": "Demo light-path driver",
  "Dichroic-Name": "DWheel",
  "Excitation-Label": "Chroma-HQ570",
  "Core-AutoFocus": "Autofocus",
  "Objective-State": "1",
  "Camera-CameraName": "DemoCamera-MultiMode",
  "Autofocus-Name": "DAutoFocus",
  "Camera-OnCameraCCDXSize": "512",
  "Camera-ScanMode": "1",
  "Shutter-Name": "DShutter",
  "Dichroic-ClosedPosition": "0",
  "Camera-TransposeCorrection": "0",
  "BitDepth": 16,
  "Slice": 0,
  "Dichroic-Label": "400DCLP",
  "Excitation-State": "0",
  "ChannelIndex": 0,
  "Path-HubID": "DHub",
  "Core-Shutter": "Shutter",
  "UUID": "9093e2a0-c6a8-4e04-96b5-094970f105b8",
  "Camera-TestProperty1": "0.0000",
  "Camera-TestProperty2": "0.0000",
  "Core-Camera": "Camera",
  "Core-TimeoutMs": "5000",
  "Core-SLM": "",
  "Camera-TestProperty5": "0.0000",
  "Camera-TestProperty6": "0.0000",
  "Camera-TestProperty3": "0.0000",
  "Camera-TestProperty4": "0.0000",
  "ElapsedTime-ms": 53,
  "Frame": 0,
  "Core-ChannelGroup": "Channel",
  "PositionIndex": 0,
  "Width": 512,
  "WaitInterval": 0,
  "Camera-Description": "Demo Camera Device Adapter",
  "PositionName": null,
  "Camera-CCDTemperature": "0.0000",
  "Emission-ClosedPosition": "0",
  "Camera-Offset": "0",
  "XY-Name": "DXYStage",
  "XY-Description": "Demo XY stage driver",
  "PixelSizeUm": 1,
  "Height": 512,
  "Shutter-State": "0",
  "Emission-HubID": "DHub",
  "Camera-HubID": "DHub",
  "Dichroic-HubID": "DHub",
  "Source": "Camera",
  "Emission-Name": "DWheel",
  "Camera-TriggerDevice": "",
  "Autofocus-Description": "Demo auto-focus adapter",
  "Camera-SaturatePixels": "0",
  "Camera-TransposeMirrorX": "0",
  "Camera-TransposeMirrorY": "0",
  "XY-TransposeMirrorY": "0",
  "Excitation-Description": "Demo filter wheel driver",
  "XY-TransposeMirrorX": "0",
  "Camera-OnCameraCCDYSize": "512",
  "XY-HubID": "DHub",
  "Binning": "1",
  "Z-HubID": "DHub",
  "DHub-SimulatedErrorRate": "0.0000",
  "Excitation-Name": "DWheel",
  "Objective-HubID": "DHub",
  "Camera-FractionOfPixelsToDropOrSaturate": "0.0020",
  "PixelType": "GRAY16",
  "Shutter-HubID": "DHub",
  "Objective-Description": "Demo objective turret driver",
  "Excitation-HubID": "DHub",
  "Z-Description": "Demo stage driver",
  "Camera-Gain": "0",
  "YPositionUm": -0,
  "Path-State": "0",
  "Camera-PixelType": "16bit",
  "Emission-Label": "Chroma-HQ700",
  "XPositionUm": -0,
  "Z-Position": "0.0000",
  "Camera-DropPixels": "0",
  "Camera-BitDepth": "16",
  "Camera-ReadoutTime": "0.0000",
  "Autofocus-HubID": "DHub",
  "FileName": "PREFIX1_MMImages.ome.tif",
  "Core-Galvo": "Undefined",
  "CameraChannelIndex": 0,
  "SliceIndex": 0,
  "ZPositionUm": 1,
  "Camera-Binning": "1",
  "Camera-Exposure": "10.00"
	}...
	 */