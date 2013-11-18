package edu.mbl.jif.imaging.dataset.metadata;


import edu.mbl.jif.imaging.mmtiff.MDUtils;
import edu.mbl.jif.imaging.mmtiff.TaggedImage;
import ij.ImagePlus;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 * ImageAttributes Creates from Summary metadata ImagePlus BufferedImage
 *
 * new ImageAttributes([])
 *
 * @author GBH
 */
public class ImageAttributes {

   public int width = 0;
   public int height = 0;
   public String pixelType = "GRAY8"; // "GRAY8", "GRAY16", "RGB32", "RGB64"
   public int byteDepth = 8;  // case GRAY8 or COLOR_256: 8; GRAY16: 16; GRAY32: 32; COLOR_RGB: 24;
   public int bitDepth = 8;  //GRAY8 or COLOR_256: 8; GRAY16: 16; GRAY32: 32; case COLOR_RGB: 24; 
   public int bytesPerPixel = 1; // GRAY16: 2; GRAY32 or COLOR_RGB:  4; 	else 1;
   public int binning = 1;
   public int ijType = ImagePlus.GRAY8; // ImagePlus.GRAY8 | ImagePlus.GRAY16 | ImagePlus.GRAY32 | ImagePlus.COLOR_RGB
   public int pixelSize_um = 0;

   public ImageAttributes(int width, int height) {
      this.width = width;
      this.height = height;
   }

   public ImageAttributes(JSONObject sumMD) {
      try {
         width = MDUtils.getWidth(sumMD);
         height = MDUtils.getHeight(sumMD);
         bitDepth = MDUtils.getBitDepth(sumMD);
         ijType = MDUtils.getIJType(sumMD);
         if (ijType == ImagePlus.GRAY8) {
            pixelType = "GRAY8";
            byteDepth = 1;
            bytesPerPixel = 1;
         } else if (ijType == ImagePlus.GRAY16) {
            pixelType = "GRAY16";
            byteDepth = 2;
         } else if (ijType == ImagePlus.GRAY32) {
            pixelType = "GRAY32";
            byteDepth = 4;
         } else if (ijType == ImagePlus.COLOR_RGB) {
            pixelType = "RGB32";
            byteDepth = 4;
         }
         bytesPerPixel = byteDepth;
      } catch (Exception ex) {
         Logger.getLogger(ImageAttributes.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   public ImageAttributes(TaggedImage imp) {
      try {
         width = MDUtils.getWidth(imp.tags);
         height = MDUtils.getHeight(imp.tags);
         bitDepth = MDUtils.getBitDepth(imp.tags);
         ijType = MDUtils.getIJType(imp.tags);
         if (ijType == ImagePlus.GRAY8) {
            pixelType = "GRAY8";
            byteDepth = 1;
            bytesPerPixel = 1;
         } else if (ijType == ImagePlus.GRAY16) {
            pixelType = "GRAY16";
            byteDepth = 2;
         } else if (ijType == ImagePlus.GRAY32) {
            pixelType = "GRAY32";
            byteDepth = 4;
         } else if (ijType == ImagePlus.COLOR_RGB) {
            pixelType = "RGB32";
            byteDepth = 4;
         }
         bytesPerPixel = byteDepth;
      } catch (Exception ex) {
         Logger.getLogger(ImageAttributes.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

//	public ImageAttributes(ImagePlus imp) {
//		width = imp.getWidth();
//		height = imp.getHeight();
//		bitDepth = imp.getBitDepth();
//		bytesPerPixel = imp.getBytesPerPixel();
//		ijType = imp.getType();
//		if (ijType == ImagePlus.GRAY8) {
//			pixelType = "GRAY8";
//			byteDepth = 1;
//		} else if (ijType == ImagePlus.GRAY16) {
//			pixelType = "GRAY16";
//			byteDepth = 2;
//		} else if (ijType == ImagePlus.GRAY32) {
//			pixelType = "GRAY32";
//			byteDepth = 4;
//		} else if (ijType == ImagePlus.COLOR_RGB) {
//			pixelType = "RGB32";
//			byteDepth = 4;
//		}
//	}
   public ImageAttributes(BufferedImage imp) {
      width = imp.getWidth();
      height = imp.getHeight();
      int type = imp.getType();
      if (type == BufferedImage.TYPE_BYTE_GRAY) {
         pixelType = "GRAY8";
         byteDepth = 1;
         bitDepth = 8;
         bytesPerPixel = 1;
      } else if (type == BufferedImage.TYPE_USHORT_GRAY) {
         pixelType = "GRAY16";
         byteDepth = 2;
         bitDepth = 16;
         bytesPerPixel = 2;
      }
//		else if (ijType == ImagePlus.GRAY32) {
//			pixelType = "GRAY32";
//			byteDepth = 4;
//			bitDepth =;
//			bytesPerPixel =;
//		} else if (ijType == ImagePlus.COLOR_RGB) {
//			pixelType = "RGB32";
//			byteDepth = 4;
//			bitDepth =;
//			bytesPerPixel =;
//		}
   }
}