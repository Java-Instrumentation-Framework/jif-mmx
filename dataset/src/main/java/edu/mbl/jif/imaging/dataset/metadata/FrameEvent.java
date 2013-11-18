package edu.mbl.jif.imaging.dataset.metadata;

/**
 *
 * @author GBH
 */
public class FrameEvent {

	public int channelIndex = 0;
	public String channel = "";
	//
	public int slice = 0;
	public int frame = 0;
	public int frameIndex = 0;
	public int sliceIndex = 0;
	public int positionIndex = 0;
	public String position = "Default";
	public String positionName = null;
	//
	public int cameraChannelIndex = 0;
	public int exposure = 0; 
	//public boolean relativeZ = true;
	//public int waitTimeMs = 0;  //??
}
