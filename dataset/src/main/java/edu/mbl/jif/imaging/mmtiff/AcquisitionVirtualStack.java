/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mbl.jif.imaging.mmtiff;

//import ij.ImagePlus;
import ij.process.ImageProcessor;
import java.awt.image.ColorModel;
import org.json.JSONObject;

/**
 * This stack class provides the ImagePlus with images from the MMImageCache.
 *
 */
public class AcquisitionVirtualStack extends ij.VirtualStack {

   final private ImageCache imageCache_;
   //final private ImagePlus imgPlus;
   final protected int width_, height_, type_;
   int nChannels;
   int nZslices;
   int nTimePoints;
   private int nSlices_;
   private int positionIndex_ = 0;

   public AcquisitionVirtualStack(int width, int height, int type,   
           ColorModel cm, ImageCache imageCache, int nChannels, int nZslices, int nTimePoints
           
           //,ImagePlus imgPlus
           ) {
      super(width, height, cm, "");
      width_ = width;
      height_ = height;
      imageCache_ = imageCache;
      nSlices_ = nChannels * nZslices * nTimePoints;
      this.nChannels = nChannels;
      this.nZslices = nZslices;
      this.nTimePoints = nTimePoints;
      //this.imgPlus = imgPlus;
      type_ = type;
   }

   public void setPositionIndex(int pos) {
      positionIndex_ = pos;
   }

   public int getPositionIndex() {
      return positionIndex_;
   }

//   public VirtualAcquisitionDisplay getVirtualAcquisitionDisplay() {
//      return acq_;
//   }
   public void setSize(int size) {
      nSlices_ = size;
   }

   public TaggedImageStorage getCache() {
      return imageCache_;
   }

   public TaggedImage getTaggedImage(int flatIndex) {
      int[] pos;
      // If we don't have the ImagePlus yet, then we need to assume
      // we are on the very first image.
//      if (imgPlus == null) {
//         return getTaggedImage(0, 0, 0);
//      } else {
         pos = convertIndexToPosition(flatIndex);
//      }
      int chanIndex = grayToRGBChannel(pos[0] - 1);
      int frame = pos[2] - 1;
      int slice = pos[1] - 1;

      return getTaggedImage(chanIndex, slice, frame);
   }

   public int grayToRGBChannel(int grayIndex) {
      try {
         if (imageCache_ != null) {
            if (imageCache_.getSummaryMetadata() != null) {
               if (MDUtils.getNumberOfComponents(imageCache_.getSummaryMetadata()) == 3) {
                  return grayIndex / 3;
               }
            }
         }
         return grayIndex;
      } catch (Exception ex) {
         ReportingUtils.logError(ex);
         return 0;
      }
   }

   public TaggedImage getTaggedImage(int chanIndex, int slice, int frame) {
      int nSlices = this.nSlices_;
//      if (imgPlus == null) {
//         nSlices = 1;
//      } else {
//         nSlices = imgPlus.getNSlices();
//      }
      
      try {
         TaggedImage img;
         img = imageCache_.getImage(chanIndex, slice, frame, positionIndex_);
         int backIndex = slice - 1, forwardIndex = slice + 1;
         int frameSearchIndex = frame;
         //If some but not all channels have z stacks, find the closest slice for the given
         //channel that has an image.  Also if time point missing, go back until image is found
         while (img == null) {
            img = imageCache_.getImage(chanIndex, slice, frameSearchIndex, positionIndex_);
            if (img != null) {
               break;
            }

            if (backIndex >= 0) {
               img = imageCache_.getImage(chanIndex, backIndex, frameSearchIndex, positionIndex_);
               if (img != null) {
                  break;
               }
               backIndex--;
            }
            if (forwardIndex < nSlices) {
               img = imageCache_.getImage(chanIndex, forwardIndex, frameSearchIndex, positionIndex_);
               if (img != null) {
                  break;
               }
               forwardIndex++;
            }

            if (backIndex < 0 && forwardIndex >= nSlices) {
               frameSearchIndex--;
               backIndex = slice - 1;
               forwardIndex = slice + 1;
               if (frameSearchIndex < 0) {
                  break;
               }
            }
         }

         return img;
      } catch (Exception e) {
         ReportingUtils.logError(e);
         return null;
      }
   }

   @Override
   public Object getPixels(int flatIndex) {
      Object pixels = null;
      try {
         TaggedImage image = getTaggedImage(flatIndex);
         if (image == null) {
            pixels = ImageUtils.makeProcessor(type_, width_, height_).getPixels();
         } else if (MDUtils.isGRAY(image)) {
            pixels = image.pix;
         } else if (MDUtils.isRGB32(image)) {
            pixels = ImageUtils.singleChannelFromRGB32((byte[]) image.pix, (flatIndex - 1) % 3);
         } else if (MDUtils.isRGB64(image)) {
            pixels = ImageUtils.singleChannelFromRGB64((short[]) image.pix, (flatIndex - 1) % 3);
         }
      } catch (Exception ex) {
         ReportingUtils.logError(ex);
      }

      return pixels;
   }

   @Override
   public ImageProcessor getProcessor(int flatIndex) {
      return ImageUtils.makeProcessor(type_, width_, height_, getPixels(flatIndex));
   }

   @Override
   public int getSize() {
      // returns the stack size of VirtualAcquisitionDisplay unless this size is -1
      // which occurs in constructor while hyperImage_ is still null. In this case
      // returns the number of slices speciefiec in AcquisitionVirtualStack constructor
      int size = nSlices_;
      //int size = imgPlus.getStackSize();
      if (size == -1) {
         return nSlices_;
      }
      return size;
   }

   @Override
   public String getSliceLabel(int n) {
      TaggedImage img = getTaggedImage(n);
      if (img == null) {
         return "";
      }
      JSONObject md = img.tags;
      try {
         return md.get("Acquisition-PixelSizeUm") + " um/px";
         //return MDUtils.getChannelName(md) + ", " + md.get("Acquisition-ZPositionUm") + " um(z), " + md.get("Acquisition-TimeMs") + " s";
      } catch (Exception ex) {
         return "";
      }
   }
   
   	/** Converts the stack index 'n' (one-based) into a hyperstack position (channel, slice, frame). */
	public int[] convertIndexToPosition(int n) {
//		if (n<1 || n>getStackSize())
//			throw new IllegalArgumentException("n out of range: "+n);
		int[] position = new int[3];
		int[] dim = new int[]{0,0,this.nChannels, this.nZslices, this.nTimePoints};
		position[0] = ((n-1)%dim[2])+1;
		position[1] = (((n-1)/dim[2])%dim[3])+1;
		position[2] = (((n-1)/(dim[2]*dim[3]))%dim[4])+1;
		return position;
	}
}
