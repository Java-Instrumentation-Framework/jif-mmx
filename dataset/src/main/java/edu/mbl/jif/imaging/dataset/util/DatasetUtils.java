package edu.mbl.jif.imaging.dataset.util;

import edu.mbl.jif.imaging.mmtiff.AcquisitionVirtualStack;
import ij.ImagePlus;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.io.File;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author GBH
 */
public class DatasetUtils {

   public static JSONObject getSummaryMetadata(ImagePlus image)
           throws JSONException {
      if (isMMDataset(image)) {
         JSONObject sumMD = ((AcquisitionVirtualStack) image.getStack()).getCache().getSummaryMetadata();
         return sumMD;
      } else {
         throw new JSONException("unable to getSummaryMetadata");
      }

   }

   public static JSONObject getFrameMetadata(ImagePlus image, int c, int z, int t, int p)
           throws JSONException {
      if (isMMDataset(image)) {
         JSONObject frameMD
                 = ((AcquisitionVirtualStack) image.getStack()).getCache().getImage(c, z, t, p).tags;
         return frameMD;
      } else {
         throw new JSONException("unable to getFrameMetadata");
      }

   }

   public static boolean isMMDataset(ImagePlus image) {
      return image.getStack() instanceof AcquisitionVirtualStack;
   }

   public static String[] parsePath(String path) {

      File filePath = new File(path);
      // if this points at a file, the directory containing it is the 'prefix'
      File fullPath;
      if (filePath.isFile()) {
         fullPath = filePath.getParentFile();
      } else {
         fullPath = filePath;
      }
      String rootDir = fullPath.getAbsolutePath();
      String prefix = fullPath.getName();
      String dir = rootDir.substring(0, rootDir.length() - (prefix.length() + 1));
      dir = replaceBackslash(dir);
      return new String[]{dir, prefix};
   }

   public static void test(String path) {
      String[] paths = DatasetUtils.parsePath(path);

      System.out.println("path: " + path);
      System.out.println("dir: " + paths[0]);
      System.out.println("prefix: " + paths[1]);
   }

   public static String replaceBackslash(final String aPath) {
      return aPath.replace('\\', '/');
   }
   
   
     public static Object createPix(BufferedImage img)
           throws UnsupportedImageModelException2 {
      int w = img.getWidth();
      int h = img.getHeight();
      DataBuffer buffer = img.getData().getDataBuffer();
      if (buffer.getOffset() != 0) {
         throw new UnsupportedImageModelException2("Expecting BufferData with no offset.");
      }
      switch (buffer.getDataType()) {
         case DataBuffer.TYPE_BYTE:
            return ((DataBufferByte) buffer).getData();
         case DataBuffer.TYPE_USHORT:
            return ((DataBufferUShort) buffer).getData();
         case DataBuffer.TYPE_SHORT:
            short[] pixels = ((DataBufferShort) buffer).getData();
            for (int i = 0; i < pixels.length; ++i) {
               pixels[i] = (short) (pixels[i] + 32768);
            }
            return pixels;
         case DataBuffer.TYPE_INT:
            return ((DataBufferInt) buffer).getData();
         case DataBuffer.TYPE_FLOAT: {
            DataBufferFloat dbFloat = (DataBufferFloat) buffer;
            return dbFloat.getData();
         }
         case DataBuffer.TYPE_DOUBLE:
            return ((DataBufferDouble) buffer).getData();
         case DataBuffer.TYPE_UNDEFINED:
            // ENH: Should this be reported as data problem?
            throw new UnsupportedImageModelException2("Pixel type is undefined.");
         default:
            throw new UnsupportedImageModelException2("Unrecognized DataBuffer data type");
      }
   }

   private static class UnsupportedImageModelException2 extends Exception {

      public UnsupportedImageModelException2(String msg) {
      }
   }
   

   public static void main(String[] args) {
      test("C:/MicroManagerData/Test/dataXMT15/BG_2012_1206_1701_1");
      test("C:/MicroManagerData/Test/dataXMT15/BG_2012_1206_1701_1/img_000000000_Slow Axis Orientation - Computed Image_000.tif");
   }
   /* TODO -
    * What if a .ome.tif file is not in the original (prefix) directory?
    This would be a problem if the dataset has multiple positions in multiple files.


    Multiposition ome.tif Naming: 
    e.g. "C:\MicroManagerData\Test\dataXMT16\dataXMT16_MMImages_PosN.ome.tif" 


    dir + "/" + prefix + "/" + prefix + "_MMImages.ome.tif"

    dir + "/" + prefix + "/" + prefix + "_MMImages" + "_Pos" + positionIndex + ".ome.tif"
    */
}
