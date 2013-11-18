package edu.mbl.jif.imaging.mmtiff;
/*
 * Micro-Manager datasets in single and multiple-page tiff files,
 * with or without OMETiff file metadata
 * 
 * TODO Extend to N-dimensions... 
 */

/*
 * OMETiff file metadata, Aug 2013:

Summary:

{
"Directory":"C:\\MicroManagerData\\Test\\dataXMT15"
"Prefix":"SMS_2012_1206_1749",

"UUID":"f726d54d-50dc-41fa-9bcc-d645d485d825",
"Time":"2012-12-06 17:50:44 -0500",
"Date":"2012-12-06",

"UserName":"GBH",
"ComputerName":"GBH-VAIO",
"MicroManagerVersion":"1.4.13",
"MetadataVersion":"10",
"Source":"Micro-Manager",

"PolScope_Plugin_Version":"1",
"PolScope_Algorithm":"Birefringence",
"PolScope_Info":"LC-PolScope, Calc_Proc, version 3a, RO 04 Jan 2012",
"~ Swing (fraction)":"0.03",
"~ Wavelength (nm)":"546",
"~ Mirror":"Yes",
"~ Background":"BG_2012_1206_1701_1",

"ROI":"[0,0,1392,1040]",

"Width":"1392",
"Height":"1040",
"PixelAspect":"1",
"IJType":"0",
"BitDepth":"8",
"Depth":"1",
"PixelType":"GRAY8",

"Slices":"1",
"Frames":"3",
"Positions":"1",

"TimeFirst":"true",
"SlicesFirst":"false",

"PositionIndex":"0",
"GridRow":"0",
"GridColumn":"0",

"Interval_ms":"1000",
"CustomIntervals_ms":"[]",
"z-step_um":"0",
"PixelSize_um":"0",

"KeepShutterOpenChannels":"false",
"KeepShutterOpenSlices":"false",

"Channels":"7",
"ChNames":"[\"Retardance - Computed Image\",
\"Slow Axis Orientation - Computed Image\",
\"VariLC - State0 - Acquired Image\",
\"VariLC - State1 - Acquired Image\",
\"VariLC - State2 - Acquired Image\",
\"VariLC - State3 - Acquired Image\",
\"VariLC - State4 - Acquired Image\"]",
"ChColors":"[-1,-1,-1,-1,-1,-1,-1]",
"ChContrastMin":"[0,0,0,0,0]",
"ChContrastMax":"[65536,65536,65536,65536,65536]",

}

Frame
{
"Objective-Name":"DObjective",
"Path-Label":"State-0",
"Core-Focus":"Z",
"Channel":"Cy5",
"Core-Initialize":"1",
"Z-Name":"DStage",
"FrameIndex":1,
"Excitation-ClosedPosition":"0",
"Emission-State":"0",
"ROI":"0-0-512-512",
"Shutter-Description":"Demo shutter driver",
"Dichroic-State":"0",
"Camera":"",
"Camera-UseExposureSequences":"No",
"Core-ImageProcessor":"",
"Camera-TransposeXY":"0",
"Camera-Name":"DCam",
"SlicePosition":4,
"DHub-DivideOneByMe":"1",
"Exposure-ms":10,
"Path-Name":
"DLightPath",
"Objective-Trigger":"-",
"Core-AutoShutter":"1",
"Dichroic-Description":"Demo filter wheel driver",
"NextFrame":1,
"Emission-Description":"Demo filter wheel driver",
"Core-XYStage":"XY",
"Camera-CameraID":"V1.0",
"AxisPositions":null,
"Objective-Label":"Nikon 10X S Fluor",
"Camera-CCDTemperature RO":"0.0000",
"Time":"2013-08-24 21:07:17 -0400",
"Path-Description":"Demo light-path driver",
"Dichroic-Name":"DWheel",
"Excitation-Label":"Chroma-HQ570",
"Core-AutoFocus":"Autofocus",
"Objective-State":"1",
"Camera-CameraName":"DemoCamera-MultiMode",
"Autofocus-Name":"DAutoFocus",
"Camera-OnCameraCCDXSize":"512",
"Camera-ScanMode":"1",
"Shutter-Name":"DShutter",
"Dichroic-ClosedPosition":"0",
"Camera-TransposeCorrection":"0",
"BitDepth":16,
"Slice":1,
"Dichroic-Label":"400DCLP",
"Excitation-State":"0",
"ChannelIndex":0,
"Path-HubID":"DHub",
+"Core-Shutter":"Shutter",
"UUID":"dd2cf5d2-eaac-4ac0-b90e-4796030c73e8",
"Camera-TestProperty1":"0.0000",
"Camera-TestProperty2":"0.0000",
"Core-Camera":"Camera",
"Core-TimeoutMs":"5000",
"Core-SLM":"",
"Camera-TestProperty5":"0.0000",
"Camera-TestProperty6":"0.0000",
"Camera-TestProperty3":"0.0000",
"Camera-TestProperty4":"0.0000",
"Camera-FastImage":"0",
"ElapsedTime-ms":2972,
"Frame":1,
"Core-ChannelGroup":"Channel",
"PositionIndex":0,
"Width":512,
"WaitInterval":null,
"Camera-Description":"Demo Camera Device Adapter",
"PositionName":null,
"Camera-CCDTemperature":"0.0000",
"Emission-ClosedPosition":"0",
"Camera-Offset":"0",
"XY-Name":"DXYStage",
"XY-Description":"Demo XY stage driver",
"PixelSizeUm":1,
"Height":512,
"Shutter-State":"0",
"Emission-HubID":"DHub",
"Camera-HubID":"DHub",
"Dichroic-HubID":"DHub",
"Source":"Camera",
"Emission-Name":"DWheel",
"Camera-TriggerDevice":"",
"Autofocus-Description":"Demo auto-focus adapter",
"Camera-SaturatePixels":"0",
"Camera-TransposeMirrorX":"0",
"Camera-TransposeMirrorY":"0",
"XY-TransposeMirrorY":"0",
"Excitation-Description":"Demo filter wheel driver",
"XY-TransposeMirrorX":"0",
"Camera-OnCameraCCDYSize":"512",
"XY-HubID":"DHub",
"Binning":"1",
"Z-HubID":"DHub",
"DHub-SimulatedErrorRate":"0.0000",
"Excitation-Name":"DWheel",
"Objective-HubID":"DHub",
"Camera-FractionOfPixelsToDropOrSaturate":"0.0020",
"PixelType":"GRAY16",
"Shutter-HubID":"DHub",
"Objective-Description":"Demo objective turret driver",
"Excitation-HubID":"DHub",
"Z-Description":"Demo stage driver",
"Camera-Gain":"0",
"YPositionUm":-0,
"Path-State":"0",
"Camera-PixelType":"16bit",
"Emission-Label":"Chroma-HQ700",
"XPositionUm":-0,
"Z-Position":"0.0000",
"Camera-DropPixels":"0",
"Camera-BitDepth":"16",
"Camera-ReadoutTime":"0.0000",
"Autofocus-HubID":"DHub",
"FileName":"Untitled_1_MMStack.ome.tif",
"Core-Galvo":"Undefined",
"Position":"Default",
"Z-UseSequences":"No",
"CameraChannelIndex":0,
"SliceIndex":1,
"ZPositionUm":4,
"Camera-Binning":"1",
"Camera-Exposure":"10.00"
}

 
 
 OME Metadata:

<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<OME xmlns="http://www.openmicroscopy.org/Schemas/OME/2012-06" 
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2012-06 http://www.openmicroscopy.org/Schemas/OME/2012-06/ome.xsd">

<Instrument ID="Microscope">
	<Detector ID="Camera" Manufacturer="DCam" Model="DemoCamera-MultiMode" Offset="0.0" SerialNumber="V1.0"/>
</Instrument>

<Image ID="Image:0" Name="Untitled_1_MMStack">
<AcquisitionDate>2013-08-24T21:07:15</AcquisitionDate>
<Description>Comments</Description>
<InstrumentRef ID="Microscope"/>
<StageLabel Name="Pos0" X="-0.0" Y="-0.0"/>

<Pixels DimensionOrder="XYCZT" ID="Pixels:0" PhysicalSizeX="1.0" PhysicalSizeY="1.0" SizeC="2" SizeT="4" SizeX="512" SizeY="512" SizeZ="6" Type="uint16">
<Channel Color="-1" ID="Channel:0:0" Name="Cy5" SamplesPerPixel="1"><LightPath/></Channel>
<Channel Color="-1" ID="Channel:0:1" Name="DAPI" SamplesPerPixel="1"><LightPath/></Channel>
<BinData xmlns="http://www.openmicroscopy.org/Schemas/BinaryFile/2012-06" BigEndian="false"/>
<TiffData FirstC="0" FirstT="0" FirstZ="0" IFD="0" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="1" FirstT="0" FirstZ="0" IFD="1" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="0" FirstT="0" FirstZ="1" IFD="2" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="1" FirstT="0" FirstZ="1" IFD="3" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="0" FirstT="0" FirstZ="2" IFD="4" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="1" FirstT="0" FirstZ="2" IFD="5" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="0" FirstT="0" FirstZ="3" IFD="6" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="1" FirstT="0" FirstZ="3" IFD="7" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="0" FirstT="0" FirstZ="4" IFD="8" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="1" FirstT="0" FirstZ="4" IFD="9" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="0" FirstT="0" FirstZ="5" IFD="10" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="1" FirstT="0" FirstZ="5" IFD="11" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="0" FirstT="1" FirstZ="0" IFD="12" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="1" FirstT="1" FirstZ="0" IFD="13" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="0" FirstT="1" FirstZ="1" IFD="14" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="1" FirstT="1" FirstZ="1" IFD="15" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="0" FirstT="1" FirstZ="2" IFD="16" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="1" FirstT="1" FirstZ="2" IFD="17" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="0" FirstT="1" FirstZ="3" IFD="18" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="1" FirstT="1" FirstZ="3" IFD="19" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="0" FirstT="1" FirstZ="4" IFD="20" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="1" FirstT="1" FirstZ="4" IFD="21" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="0" FirstT="1" FirstZ="5" IFD="22" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="1" FirstT="1" FirstZ="5" IFD="23" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="0" FirstT="2" FirstZ="0" IFD="24" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="1" FirstT="2" FirstZ="0" IFD="25" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="0" FirstT="2" FirstZ="1" IFD="26" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="1" FirstT="2" FirstZ="1" IFD="27" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="0" FirstT="2" FirstZ="2" IFD="28" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="1" FirstT="2" FirstZ="2" IFD="29" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="0" FirstT="2" FirstZ="3" IFD="30" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="1" FirstT="2" FirstZ="3" IFD="31" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="0" FirstT="2" FirstZ="4" IFD="32" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="1" FirstT="2" FirstZ="4" IFD="33" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="0" FirstT="2" FirstZ="5" IFD="34" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="1" FirstT="2" FirstZ="5" IFD="35" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="0" FirstT="3" FirstZ="0" IFD="36" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="1" FirstT="3" FirstZ="0" IFD="37" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="0" FirstT="3" FirstZ="1" IFD="38" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="1" FirstT="3" FirstZ="1" IFD="39" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="0" FirstT="3" FirstZ="2" IFD="40" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="1" FirstT="3" FirstZ="2" IFD="41" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="0" FirstT="3" FirstZ="3" IFD="42" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="1" FirstT="3" FirstZ="3" IFD="43" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="0" FirstT="3" FirstZ="4" IFD="44" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="1" FirstT="3" FirstZ="4" IFD="45" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="0" FirstT="3" FirstZ="5" IFD="46" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<TiffData FirstC="1" FirstT="3" FirstZ="5" IFD="47" PlaneCount="1"><UUID FileName="Untitled_1_MMStack.ome.tif"/></TiffData>
<Plane DeltaT="0.564" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="5.0" TheC="0" TheT="0" TheZ="0"/>
<Plane DeltaT="0.802" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="5.0" TheC="1" TheT="0" TheZ="0"/>
<Plane DeltaT="1.033" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="4.0" TheC="0" TheT="0" TheZ="1"/>
<Plane DeltaT="1.18" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="4.0" TheC="1" TheT="0" TheZ="1"/>
<Plane DeltaT="1.409" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="3.0" TheC="0" TheT="0" TheZ="2"/>
<Plane DeltaT="1.595" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="3.0" TheC="1" TheT="0" TheZ="2"/>
<Plane DeltaT="1.767" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="2.0" TheC="0" TheT="0" TheZ="3"/>
<Plane DeltaT="1.934" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="2.0" TheC="1" TheT="0" TheZ="3"/>
<Plane DeltaT="2.117" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="1.0" TheC="0" TheT="0" TheZ="4"/>
<Plane DeltaT="2.269" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="1.0" TheC="1" TheT="0" TheZ="4"/>
<Plane DeltaT="2.404" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="0.0" TheC="0" TheT="0" TheZ="5"/>
<Plane DeltaT="2.535" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="0.0" TheC="1" TheT="0" TheZ="5"/>
<Plane DeltaT="2.69" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="5.0" TheC="0" TheT="1" TheZ="0"/>
<Plane DeltaT="2.847" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="5.0" TheC="1" TheT="1" TheZ="0"/>
<Plane DeltaT="2.972" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="4.0" TheC="0" TheT="1" TheZ="1"/>
<Plane DeltaT="3.072" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="4.0" TheC="1" TheT="1" TheZ="1"/>
<Plane DeltaT="3.191" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="3.0" TheC="0" TheT="1" TheZ="2"/>
<Plane DeltaT="3.291" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="3.0" TheC="1" TheT="1" TheZ="2"/>
<Plane DeltaT="3.404" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="2.0" TheC="0" TheT="1" TheZ="3"/>
<Plane DeltaT="3.506" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="2.0" TheC="1" TheT="1" TheZ="3"/>
<Plane DeltaT="3.625" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="1.0" TheC="0" TheT="1" TheZ="4"/>
<Plane DeltaT="3.737" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="1.0" TheC="1" TheT="1" TheZ="4"/>
<Plane DeltaT="3.866" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="0.0" TheC="0" TheT="1" TheZ="5"/>
<Plane DeltaT="3.979" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="0.0" TheC="1" TheT="1" TheZ="5"/>
<Plane DeltaT="4.118" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="5.0" TheC="0" TheT="2" TheZ="0"/>
<Plane DeltaT="4.228" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="5.0" TheC="1" TheT="2" TheZ="0"/>
<Plane DeltaT="4.361" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="4.0" TheC="0" TheT="2" TheZ="1"/>
<Plane DeltaT="4.484" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="4.0" TheC="1" TheT="2" TheZ="1"/>
<Plane DeltaT="4.611" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="3.0" TheC="0" TheT="2" TheZ="2"/>
<Plane DeltaT="4.7  " ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="3.0" TheC="1" TheT="2" TheZ="2"/>
<Plane DeltaT="4.814" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="2.0" TheC="0" TheT="2" TheZ="3"/>
<Plane DeltaT="4.926" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="2.0" TheC="1" TheT="2" TheZ="3"/>
<Plane DeltaT="5.054" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="1.0" TheC="0" TheT="2" TheZ="4"/>
<Plane DeltaT="5.154" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="1.0" TheC="1" TheT="2" TheZ="4"/>
<Plane DeltaT="5.254" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="0.0" TheC="0" TheT="2" TheZ="5"/>
<Plane DeltaT="5.346" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="0.0" TheC="1" TheT="2" TheZ="5"/>
<Plane DeltaT="5.458" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="5.0" TheC="0" TheT="3" TheZ="0"/>
<Plane DeltaT="5.558" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="5.0" TheC="1" TheT="3" TheZ="0"/>
<Plane DeltaT="5.672" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="4.0" TheC="0" TheT="3" TheZ="1"/>
<Plane DeltaT="5.767" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="4.0" TheC="1" TheT="3" TheZ="1"/>
<Plane DeltaT="5.871" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="3.0" TheC="0" TheT="3" TheZ="2"/>
<Plane DeltaT="5.962" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="3.0" TheC="1" TheT="3" TheZ="2"/>
<Plane DeltaT="6.058" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="2.0" TheC="0" TheT="3" TheZ="3"/>
<Plane DeltaT="6.153" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="2.0" TheC="1" TheT="3" TheZ="3"/>
<Plane DeltaT="6.254" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="1.0" TheC="0" TheT="3" TheZ="4"/>
<Plane DeltaT="6.347" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="1.0" TheC="1" TheT="3" TheZ="4"/>
<Plane DeltaT="6.45" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="0.0" TheC="0" TheT="3" TheZ="5"/>
<Plane DeltaT="6.544" ExposureTime="0.01" PositionX="-0.0" PositionY="-0.0" PositionZ="0.0" TheC="1" TheT="3" TheZ="5"/>
</Pixels>
</Image>
</OME>

(ImageJ Metadata:)
ImageJ=1.47f
channels=2
slices=6
frames=4
hyperstack=true
unit=um
spacing=-1.0
mode=composite

(ChannelObject:)
[ {"Name":"Cy5","DisplayMode":1,"HistogramMax":-1,"Max":65535,"Gamma":1,"Min":0,"Color":-1},
  {"Name":"DAPI","DisplayMode":1,"HistogramMax":-1,"Max":65535,"Gamma":1,"Min":0,"Color":-1} ]

(Comments, At the very end...:)
{"Summary":"Comments"}
 
 * 
 * 
 * 
 */