package edu.mbl.jif.imaging.dataset.metadata;

import edu.mbl.jif.imaging.dataset.MMgrDatasetGenerator;
import edu.mbl.jif.imaging.dataset.linked.DerivedFrom;
import edu.mbl.jif.imaging.mmtiff.MDUtils;
import java.util.Calendar;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Metadata associated with a Frame (timepoint) Uses a FrameEvent to pass the values.
 *
 * @author GBH
 */

/*
 * "FileName" tag is added by writer...
 */
public class FrameMetadata {

   // TODO Add FrameKey and FileName...
   private static boolean includeSummary = false;
   String Source = null;
   String FileName = null;
   String FrameKey = null;
   String Comment = null;
   // Image Type
   ImageAttributes imgAttribs = null;
   // Summary
   JSONObject Summary = null;

   public void setSource(String Source) {
      this.Source = Source;
   }

   public void setFileName(String FileName) {
      this.FileName = FileName;
   }

   public void setComment(String Comment) {
      this.Comment = Comment;
   }

   public void setImgAttribs(ImageAttributes imgAttribs) {
      this.imgAttribs = imgAttribs;
   }

   public void setSummary(JSONObject Summary) {
      this.Summary = Summary;
   }

   public void generateFileName() {
      this.FileName = "";
   }

   //
   public static JSONObject generateFrameMetadata(JSONObject sumMD, FrameEvent event) {
      return generateFrameMetadata(sumMD, event, null);
   }

   public static JSONObject generateFrameMetadata(
           JSONObject sumMD,
           FrameEvent event,
           DerivedFrom deriv) {
      JSONObject meta = new JSONObject();
      try {
         meta.put("Source", sumMD.get("Source")); // or replace
         meta.put("FileName", sumMD.get("Source"));
         //meta.put("Comment", sumMD.get("Source"));
         //
         meta.put("Time", Calendar.getInstance().getTime());
         meta.put("UUID", UUID.randomUUID());
         // TODO Add...
         // Image Type
         meta.put("Width", sumMD.get("Width"));
         meta.put("Height", sumMD.get("Height"));
         meta.put("Binning", sumMD.get("Binning"));
         meta.put("BitDepth", sumMD.get("BitDepth"));
         meta.put("PixelSizeUm", sumMD.get("PixelSize_um"));
         meta.put("PixelType", sumMD.get("PixelType"));
         //
         // Dimensional indices
         meta.put("Channel", event.channel);
         meta.put("ChannelIndex", event.channelIndex);//
         //
         meta.put("Slice", event.sliceIndex);
         meta.put("SliceIndex", event.sliceIndex);
         meta.put("SlicePosition", event.slice);
         meta.put("Frame", event.frameIndex);
         meta.put("FrameIndex", event.frameIndex);
         // add TimeFirst & SliceFirst
         meta.put("PositionIndex", event.positionIndex);
         if (MDUtils.getNumPositions(sumMD) > 1) {
            meta.put("PositionName", "Pos" + event.positionIndex);
         }
         // Summary... do not include if Multipage OME-Tiff
         if (includeSummary) {
            meta.put("Summary", sumMD);
         }
         //
         meta.put("Exposure-ms", event.exposure); //??
         meta.put("CameraChannelIndex", event.cameraChannelIndex); // ??
         //meta.put("NextFrame", event.next-frame-index );
         //meta.put("WaitInterval", event.wait - time - ms);
         //meta.put("XPositionUm" x);
         //meta.put("YPositionUm" y);
         //meta.put("ZPositionUm", state [:last-stage-positions, state.default-z-drive)]);
         //
         if (deriv != null) {
            deriv.addToMetadata(meta);
         }
      } catch (JSONException ex) {
         Logger.getLogger(MMgrDatasetGenerator.class.getName()).log(Level.SEVERE, null, ex);
      }
      return meta;
   }

   public JSONObject generateFrameMetadata(
           FrameEvent event,
           DerivedFrom deriv) {
      JSONObject meta = new JSONObject();
      try {
         meta.put("Source", Source);
         meta.put("FileName", FileName);
         meta.put("Comment", Comment);
         //
         meta.put("Time", Calendar.getInstance().getTime());
         meta.put("UUID", UUID.randomUUID());
         if (includeSummary) {
            // Summary... do not include if Multipage OME-Tiff
            meta.put("Summary", this.Summary);
         }
         // Image Type
         meta.put("Width", imgAttribs.width);
         meta.put("Height", imgAttribs.height);
         meta.put("Binning", imgAttribs.binning);
         meta.put("BitDepth", imgAttribs.bitDepth);
         meta.put("PixelSizeUm", imgAttribs.pixelSize_um);
         meta.put("PixelType", imgAttribs.pixelType);
         // From FrameEvent...
         // Dimensional indices
         meta.put("Channel", event.channel);
         meta.put("ChannelIndex", event.channelIndex);//
         //
         meta.put("Slice", event.sliceIndex);
         meta.put("SliceIndex", event.sliceIndex);
         meta.put("SlicePosition", event.slice);
         meta.put("Frame", event.frameIndex);
         meta.put("FrameIndex", event.frameIndex);
         meta.put("PositionIndex", event.positionIndex);
         //if (MDUtils.getNumPositions(sumMD) > 1) {
         if (event.positionIndex > 1) {
            meta.put("PositionName", "Pos" + event.positionIndex);
         }

         meta.put("Exposure-ms", event.exposure); //??
         meta.put("CameraChannelIndex", event.cameraChannelIndex); // ??
         //meta.put("NextFrame", event.next-frame-index );
         //meta.put("WaitInterval", event.wait - time - ms);
         //meta.put("XPositionUm" x);
         //meta.put("YPositionUm" y);
         //meta.put("ZPositionUm", state [:last-stage-positions, state.default-z-drive)]);
         //
         if (deriv != null) {
            deriv.addToMetadata(meta);
         }
      } catch (JSONException ex) {
         Logger.getLogger(MMgrDatasetGenerator.class.getName()).log(Level.SEVERE, null, ex);
      }
      return meta;
   }

   // TODO: Must it be a copy?
   public static JSONObject copyAndApplyEvent(JSONObject frameMD, FrameEvent event, boolean isMultipage) {
      JSONObject meta = MDUtils.copy(frameMD);
      try {
         applyEvent(meta, event, isMultipage);
      } catch (JSONException ex) {
         ex.printStackTrace();
      }
      return meta;
   }

   // ??
   public void changeFileName(String newFileName) {
   }

//   public static void applyEvent(JSONObject meta, JSONObject sumMD)
//           throws JSONException {
//   }
   public static void applyEvent(JSONObject meta, FrameEvent event, boolean isMultipage)
           throws JSONException {
      meta.put("Channel", event.channel);
      meta.put("ChannelIndex", event.channelIndex);
      meta.put("Slice", event.sliceIndex);
      meta.put("SliceIndex", event.sliceIndex);
      meta.put("SlicePosition", event.slice);
      meta.put("Frame", event.frameIndex);
      meta.put("FrameIndex", event.frameIndex);
      // add TimeFirst & SliceFirst
      meta.put("PositionIndex", event.positionIndex);
      // TODO generate FileName
      if (event.positionIndex > 1) {
         if (event.positionName != null) {
            meta.put("PositionName", event.positionName);
         } else {
            meta.put("PositionName", "Pos" + event.positionIndex);
         }
      }

      String frameKey = "FrameKey-" + event.frameIndex + "-" + event.channelIndex + "-" + event.sliceIndex;
      meta.put("FrameKey", frameKey);
      // If single-page tiffs...
      if (!isMultipage) {
         String fileName = String.format("img_%09d_%s_%03d.tif",
                 event.frameIndex, event.channelIndex, event.sliceIndex);
         meta.put("FileName", fileName);
      } else {
      }
   }

   public static void applyChannel(JSONObject meta, int channelIndex, String channelName)
           throws JSONException {
      meta.put("Channel", channelName);
      meta.put("ChannelIndex", channelIndex);
   }

   public static void applyImageSize(JSONObject meta, int w, int h)
           throws JSONException {
      meta.put("Width", w);
      meta.put("Height", h);
   }

   public static void applyDerived(JSONObject meta, DerivedFrom deriv) {
   }
   //   public static JSONObject newCopyOf(
//           JSONObject frameMD,
//           JSONObject sumMD,
//           String fileName, 
//           boolean replaceContext) throws JSONException {
//      
//       
//           FrameEvent event,
//           
//              
//           }
//      JSONObject meta = new JSONObject();
//      try {
//         meta.put("Source", sumMD.get("Source")); // or replace
//         meta.put("FileName", sumMD.get("Source"));
//         //meta.put("Comment", sumMD.get("Source"));
//         //
//         meta.put("Time", Calendar.getInstance().getTime());
//         meta.put("UUID", UUID.randomUUID());
//         // TODO Add...
//         // Image Type
//         meta.put("Width", sumMD.get("Width"));
//         meta.put("Height", sumMD.get("Height"));
//         meta.put("Binning", sumMD.get("Binning"));
//         meta.put("BitDepth", sumMD.get("BitDepth"));
//         meta.put("PixelSizeUm", sumMD.get("PixelSize_um"));
//         meta.put("PixelType", sumMD.get("PixelType"));
//         //
//         // Dimensional indices
//         meta.put("Channel", event.channel);
//         meta.put("ChannelIndex", event.channelIndex);//
//         //
//         meta.put("Slice", event.sliceIndex);
//         meta.put("SliceIndex", event.sliceIndex);
//         meta.put("SlicePosition", event.slice);
//         meta.put("Frame", event.frameIndex);
//         meta.put("FrameIndex", event.frameIndex);
//         // add TimeFirst & SliceFirst
//         meta.put("PositionIndex", event.positionIndex);
//         if (MDUtils.getNumPositions(sumMD) > 1) {
//            meta.put("PositionName", "Pos" + event.positionIndex);
//         }
//         // Summary... do not include if Multipage OME-Tiff
//         if (includeSummary) {
//            meta.put("Summary", sumMD);
//         }
//         //
//         meta.put("Exposure-ms", event.exposure); //??
//         meta.put("CameraChannelIndex", event.cameraChannelIndex); // ??
//         //meta.put("NextFrame", event.next-frame-index );
//         //meta.put("WaitInterval", event.wait - time - ms);
//         //meta.put("XPositionUm" x);
//         //meta.put("YPositionUm" y);
//         //meta.put("ZPositionUm", state [:last-stage-positions, state.default-z-drive)]);
//         //
//         if (deriv != null) {
//            deriv.addToMetadata(meta);
//         }
//      } catch (JSONException ex) {
//         Logger.getLogger(MMgrDatasetGenerator.class.getName()).log(Level.SEVERE, null, ex);
//      }
//      return meta;
//   }
   /*===============================================================
    Frame (Sorted, missing quotes on propname)

    UUID:"dd2cf5d2-eaac-4ac0-b90e-4796030c73e8",
    FileName:"Untitled_1_MMStack.ome.tif",

    Time:"2013-08-24 21:07:17 -0400",
    WaitInterval:null,

    (Dimensions:)
    Channel:"Cy5",
    ChannelIndex:0,
    Slice:1,
    SliceIndex:1,
    Frame:1,
    FrameIndex:1,
    Position:"Default",
    PositionIndex:0,
    PositionName:null,

    (Image:)
    Width:512,
    Height:512,
    BitDepth:16,
    PixelType:"GRAY16",
    Binning:"1",
    ROI:"0-0-512-512",
    PixelSizeUm:1,

    Source:"Camera",

    Camera:"",
    Camera-Binning:"1",
    Camera-BitDepth:"16",
    Camera-CameraID:"V1.0",
    Camera-CameraName:"DemoCamera-MultiMode",
    Camera-CCDTemperature RO:"0.0000",
    Camera-CCDTemperature:"0.0000",
    CameraChannelIndex:0,
    Camera-Description:"Demo Camera Device Adapter",
    Camera-DropPixels:"0",
    Camera-Exposure:"10.00"
    Camera-FastImage:"0",
    Camera-FractionOfPixelsToDropOrSaturate:"0.0020",
    Camera-Gain:"0",
    Camera-HubID:"DHub",
    Camera-Name:"DCam",
    Camera-Offset:"0",
    Camera-OnCameraCCDXSize:"512",
    Camera-OnCameraCCDYSize:"512",
    Camera-PixelType:"16bit",
    Camera-ReadoutTime:"0.0000",
    Camera-SaturatePixels:"0",
    Camera-ScanMode:"1",
    Camera-TestProperty1:"0.0000",
    Camera-TestProperty2:"0.0000",
    Camera-TestProperty3:"0.0000",
    Camera-TestProperty4:"0.0000",
    Camera-TestProperty5:"0.0000",
    Camera-TestProperty6:"0.0000",
    Camera-TransposeCorrection:"0",
    Camera-TransposeMirrorX:"0",
    Camera-TransposeMirrorY:"0",
    Camera-TransposeXY:"0",
    Camera-TriggerDevice:"",
    Camera-UseExposureSequences:"No",

    Core-AutoFocus:"Autofocus",
    Core-AutoShutter:"1",
    Core-Camera:"Camera",
    Core-ChannelGroup:"Channel",
    Core-Focus:"Z",
    Core-Galvo:"Undefined",
    Core-ImageProcessor:"",
    Core-Initialize:"1",
    Core-Shutter:"Shutter",
    Core-SLM:"",
    Core-TimeoutMs:"5000",
    Core-XYStage:"XY",

    DHub-DivideOneByMe:"1",
    DHub-SimulatedErrorRate:"0.0000",
    Dichroic-ClosedPosition:"0",
    Dichroic-Description:"Demo filter wheel driver",
    Dichroic-HubID:"DHub",
    Dichroic-Label:"400DCLP",
    Dichroic-Name:"DWheel",
    Dichroic-State:"0",
    DLightPath,
    ElapsedTime-ms:2972,
    Emission-ClosedPosition:"0",
    Emission-Description:"Demo filter wheel driver",
    Emission-HubID:"DHub",
    Emission-Label:"Chroma-HQ700",
    Emission-Name:"DWheel",
    Emission-State:"0",
    Excitation-ClosedPosition:"0",
    Excitation-Description:"Demo filter wheel driver",
    Excitation-HubID:"DHub",
    Excitation-Label:"Chroma-HQ570",
    Excitation-Name:"DWheel",
    Excitation-State:"0",
    Exposure-ms:10,

    SlicePosition:4,
    NextFrame:1,
    Objective-Description:"Demo objective turret driver",
    Objective-HubID:"DHub",
    Objective-Label:"Nikon 10X S Fluor",
    Objective-Name:"DObjective",
    Objective-State:"1",
    Objective-Trigger:"-",

    Path-Description:"Demo light-path driver",
    Path-HubID:"DHub",
    Path-Label:"State-0",
    Path-Name:
    Path-State:"0",

    Shutter-Description:"Demo shutter driver",
    Shutter-HubID:"DHub",
    Shutter-Name:"DShutter",
    Shutter-State:"0",

    XPositionUm:-0,
    XY-Description:"Demo XY stage driver",
    XY-HubID:"DHub",
    XY-Name:"DXYStage",
    XY-TransposeMirrorX:"0",
    XY-TransposeMirrorY:"0",
    YPositionUm:-0,

    Z-Description:"Demo stage driver
    Z-HubID:"DHub",
    Z-Name:"DStage",
    Z-Position:"0.0000",
    ZPositionUm:4,
    Z-UseSequences:"No",

    Autofocus-Description:"Demo auto-focus adapter",
    Autofocus-HubID:"DHub",
    Autofocus-Name:"DAutoFocus",
    AxisPositions:null
    */
//	class State {
//
//		public int initWidth;   // (core getImageWidth)
//		public int initHeight;  // (core getImageHeight)
//		//summaryMetadata (make-summary-metadata nil)
//		public int pixelType;   // (get-pixel-type)
//		public int bitDepth;    // (core getImageBitDepth)
//		public int binning;     // (core getProperty (core getCameraDevice) "Binning")})
	/*
    (defn create-basic-state []
    {:init-width (core getImageWidth)
    :init-height (core getImageHeight)
    :summary-metadata (make-summary-metadata nil)
    :pixel-type (get-pixel-type)
    :bit-depth (core getImageBitDepth)
    :binning (core getProperty (core getCameraDevice) "Binning")})
    */
   //}
	/* From Acq.clj
    * ;; image metadata

    (defn generate-metadata [event state]
    (merge
    (map-config (core getSystemStateCache))
    (:metadata event)
    (let [[x y] (let [xy-stage (state :default-xy-stage)]
    (when-not (empty? xy-stage)
    (get-in state [:last-stage-positions xy-stage])))]
    {
    "AxisPositions" (when-let [axes (get-in event [:position :axes])]
    (JSONObject. axes))
    "Binning" (state :binning)
    "BitDepth" (state :bit-depth)
    "Channel" (get-in event [:channel :name])
    "ChannelIndex" (:channel-index event)
    "CameraChannelIndex" (:camera-channel-index event)
    "Exposure-ms" (:exposure event)
    "Frame" (:frame-index event)
    "FrameIndex" (:frame-index event)
    "Height" (state :init-height)
    "NextFrame" (:next-frame-index event)
    "PixelSizeUm" (state :pixel-size-um)
    "PixelType" (state :pixel-type)
    "PositionIndex" (:position-index event)
    "PositionName" (when-lets [pos (:position event)
    msp (get-msp pos)]
    (.getLabel msp))
    "Slice" (:slice-index event)
    "SliceIndex" (:slice-index event)
    "SlicePosition" (:slice event)
    "Summary" (state :summary-metadata)
    "Source" (state :source)
    "Time" (get-current-time-str)
    "UUID" (UUID/randomUUID)
    "WaitInterval" (:wait-time-ms event)
    "Width"  (state :init-width)
    "XPositionUm" x
    "YPositionUm" y
    "ZPositionUm" (get-in state [:last-stage-positions (state :default-z-drive)])
    })))

    * 
    */
   /* 
    * Frame Metadata Example Result in .txt file:
    * 
    "FrameKey-0-0-0": {
    "Core-Focus": "",
    "Core-Shutter": "",
    "QCamera-TriggerType": "Freerun",
    "Channel": "Retardance - Computed Image",
    "Core-Initialize": "1",
    "UUID": "394f712e-0210-4ab5-8d86-98f091365bc2",
    "Core-Camera": "QCamera",
    "Core-TimeoutMs": "5000",
    "Core-SLM": "",
    "FrameIndex": 0,
    "QCamera-OffsetMax": "2047",
    "ElapsedTime-ms": 503,
    "QCamera-CameraName": "Retiga 2000R",
    "VariLC-String send to VariLC": "v?",
    "VariLC-Port": "COM3",
    "Frame": 0,
    "QCamera-GainMin": "0.4510",
    "Core-ChannelGroup": "VariLC_PolAcq",
    "PositionIndex": 0,
    "Width": 800,
    "QCamera-ExposureMin": "0.0100",
    "WaitInterval": 0,
    "COM3-StopBits": "1",
    "PositionName": null,
    "PixelSizeUm": 0,
    "VariLC-Mode, 1=Brief, 0=Standard": " 0",
    "Height": 600,
    "QCamera-OffsetMin": "-2048",
    "COM3-Verbose": "1",
    "VariLC-Retardance LC-A": "0.2482",
    "Core-ImageProcessor": "",
    "VariLC-Retardance LC-B": "0.5388",
	 
    "Summary": {
    "Slices": 0,
    "Interval_ms": 0,
    "UUID": "bc2215c6-f449-4b6c-a00e-071011c1e23b",
    "UserName": "CDP-HPPAV",
    "Depth": 1,
    "PixelType": "GRAY8",
    "Time": "2012-04-20 13:46:03 -0400",
    "Date": "2012-04-20",
    "z-step_um": 0,
    "MetadataVersion": 10,
    "PositionIndex": 0,
    "SlicesFirst": true,
    "ChContrastMin": [
    2147483647,
    2147483647,
    2147483647,
    2147483647,
    2147483647,
    2147483647,
    2147483647
    ],
    "Width": 800,
    "PixelAspect": 1,
    "MicroManagerVersion": "1.4.7  20111110",
    "ROI": [
    0,
    0,
    800,
    600
    ],
    "ChNames": [
    "Retardance - Computed Image",
    "Slow Axis Orientation - Computed Image",
    "VariLC - State0 - Acquired Image",
    "VariLC - State1 - Acquired Image",
    "VariLC - State2 - Acquired Image",
    "VariLC - State3 - Acquired Image",
    "VariLC - State4 - Acquired Image"
    ],
    "IJType": 0,
    "GridRow": 0,
    "Comment": "",
    "Height": 600,
    "GridColumn": 0,
    "Prefix": "SM_2012_0420_1345",
    "PixelSize_um": 0,
    "Frames": 0,
    "BitDepth": 8,
    "KeepShutterOpenChannels": false,
    "Source": "Micro-Manager",
    "Channels": 7,
    "ComputerName": "CDP-HPPAV-PC",
    "CustomIntervals_ms": [],
    "KeepShutterOpenSlices": false,
    "ChColors": [
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1
    ],
    "TimeFirst": false,
    "ChContrastMax": [
    -2147483648,
    -2147483648,
    -2147483648,
    -2147483648,
    -2147483648,
    -2147483648,
    -2147483648
    ],
    "Positions": 0,
    "Directory": "C:\\MicroManagerData\\Project 1\\2012_04_17"
    },
	 
    "QCamera-Exposure": "0.00",
    "Source": "QCamera",
    "SlicePosition": null,
    "QCamera-CameraID": "Serial number 32-0131A-118",
    "VariLC-Wavelength": "546.0000",
    "Exposure-ms": 0,
    "QCamera-TransposeCorrection": "0",
    "QCamera-Offset": "0",
    "COM3-Name": "COM3",
    "Core-AutoShutter": "1",
    "VariLC-Name": "VariLC",
    "VariLC-Pal. elem. 2, enter 0 to define, 1 to activate": "  546  0.2473  0.5712",
    "COM3-DataBits": "8",
    "QCamera-TransposeXY": "0",
    "Binning": "2",
    "NextFrame": 0,
    "Core-XYStage": "",
    "AxisPositions": null,
    "COM3-DelayBetweenCharsMs": "0.0000",
    "PixelType": "GRAY8",
    "Time": "2012-04-20 13:46:03 -0400",
    "QCamera-GainMax": "21.5000",
    "QCamera-Binning": "2",
    "VariLC-Number of Active LCs": "2",
    "VariLC-Pal. elem. 3, enter 0 to define, 1 to activate": "  546  0.2473  0.5071",
    "COM3-Description": "Serial port driver (boost:asio)",
    "QCamera-TransposeMirrorY": "0",
    "VariLC-Pal. elem. 1, enter 0 to define, 1 to activate": "  546  0.2773  0.5379",
    "Core-AutoFocus": "",
    "YPositionUm": null,
    "COM3-Parity": "None",
    "VariLC-Total Number of LCs": "2",
    "VariLC-Total Number of Palette Elements": "5",
    "VariLC-Pal. elem. 4, enter 0 to define, 1 to activate": "  546  0.2139  0.5379",
    "XPositionUm": null,
    "QCamera-Description": "QCam 2.0.11",
    "QCamera-ReadoutTime": "20.0 MHz",
    "VariLC-Version Number": "V 10.70     2 400.0 700.0     80035",
    "QCamera-TransposeMirrorX": "0",
    "BitDepth": 8,
    "Slice": 0,
    "COM3-AnswerTimeout": "500.0000",
    "QCamera-PixelType": "8bit",
    "FileName": "img_000000000_Retardance - Computed Image_000.tif",
    "VariLC-String from VariLC": "V 10.70     2 400.0 700.0     80035",
    "Core-Galvo": "Undefined",
    "COM3-BaudRate": "9600",
    "QCamera-ExposureMax": "1073741.8750",
    "VariLC-Pal. elem. 0, enter 0 to define, 1 to activate": "  546  0.2473  0.5379",
    "QCamera-Gain": "1.0000",
    "QCamera-Name": "QCamera",
    "CameraChannelIndex": 0,
    "ChannelIndex": 0,
    "SliceIndex": 0,
    "ZPositionUm": null,
    "COM3-Handshaking": "Off"
    }
    */
}
