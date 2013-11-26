package edu.mbl.cdp.ps.orient;

import ij.ImageStack;

/**
 *
 * @author GBH
 */
public class ImageToFloatConverter {
   //
   // Image to float[] converters...

   public static final int STR_KEY_8Bit = 8;
   public static final int STR_KEY_12Bit = 12;
   public static final int STR_KEY_14Bit = 14;
   public static final int STR_KEY_16Bit = 16;
   // max for bit depth
   public static final int INT8BitVALUE = 255;
   public static final int INT12BitVALUE = 4095;
   public static final int INT14BitVALUE = 16383;
   public static final int INT16BitVALUE = 65535;

   public static final int VALUE8Bit = 0xff;
   public static final int VALUE12Bit = 0xfff;
   public static final int VALUE14Bit = 0x3fff;
   public static final int VALUE16Bit = 0xffff;

   public static final int FACTOR8Bit = 1;
   public static final int FACTOR12Bit = 10;
   public static final int FACTOR14Bit = 40;
   public static final int FACTOR16Bit = 100;
   // for orientation, value that corresponds to 180 deg.
   public static final int FACTORMAX8Bit = 180;
   public static final int FACTORMAX12Bit = 1800;
   public static final int FACTORMAX14Bit = 7200;
   public static final int FACTORMAX16Bit = 18000;

//   public  float[] getIntensityArray(ImageStack stack, int length, int index) {
//      byte[] pixB;
//      short[] pixS;
//      float[] intensity = new float[length];
//      int bitDepth = stack.getBitDepth();
//      // TODO Do only the roi or the visible rect., not the whole image, 
//      //if ("Yes".equals(ratioLengthChoice)) {
//      // get the ratio values of 1st slice in the stack, ratio value is fraction of maximum value (255 or 4095)
//
//      if (bitDepth == STR_KEY_8Bit) {
//         pixB = (byte[]) stack.getPixels(1 + index);
//         for (int j = 0; j < length; j++) {
//            intensity[j] = (VALUE8Bit & pixB[j]);
//            intensity[j] = intensity[j] / INT8BitVALUE;
//         }
//      } else if (bitDepth == STR_KEY_14Bit) {
//         pixS = (short[]) stack.getPixels(1 + index);
//         for (int j = 0; j < length; j++) {
//            intensity[j] = (VALUE14Bit & pixS[j]);
//            intensity[j] = intensity[j] / INT14BitVALUE;
//         }
//      } else if (bitDepth == STR_KEY_16Bit) {
//         pixS = (short[]) stack.getPixels(1 + index);
//         for (int j = 0; j < length; j++) {
//            intensity[j] = (VALUE16Bit & pixS[j]);
//            intensity[j] = intensity[j] / INT16BitVALUE;
//         }
//      } else {
//         pixS = (short[]) stack.getPixels(1 + index);
//         for (int j = 0; j < length; j++) {
//            intensity[j] = (VALUE12Bit & pixS[j]);
//            intensity[j] = intensity[j] / INT12BitVALUE;
//         }
//      }
//      return intensity;
//   }
   public float[] getIntensityArray(ImageStack stack, int length, int index) {
      return getArray(stack, length, index, 3);
   }

   public float[] getAnisotropyArray(ImageStack stack, int length, int index) {
      return getArray(stack, length, index, 1);
   }

   public float[] getArray(ImageStack stack, int length, int index, int offset) {
      byte[] pixB;
      short[] pixS;
      float[] anisotropy = new float[length];
      int bitDepth = stack.getBitDepth();
      // TODO Do only the roi or the visible rect., not the whole image, 
      //if ("Yes".equals(ratioLengthChoice)) {
      // get the ratio values of 1st slice in the stack, ratio value is fraction of maximum value (255 or 4095)

      if (bitDepth == STR_KEY_8Bit) {
         pixB = (byte[]) stack.getPixels(1 + index);
         for (int j = 0; j < length; j++) {
            anisotropy[j] = (VALUE8Bit & pixB[j]);
            anisotropy[j] = anisotropy[j] / INT8BitVALUE;
         }
      } else if (bitDepth == STR_KEY_14Bit) {
         pixS = (short[]) stack.getPixels(1 + index);
         for (int j = 0; j < length; j++) {
            anisotropy[j] = (VALUE14Bit & pixS[j]);
            anisotropy[j] = anisotropy[j] / INT14BitVALUE;
         }
      } else if (bitDepth == STR_KEY_16Bit) {
         pixS = (short[]) stack.getPixels(1 + index);
         for (int j = 0; j < length; j++) {
            anisotropy[j] = (VALUE16Bit & pixS[j]);
            anisotropy[j] = anisotropy[j] / INT16BitVALUE;
         }
      } else {
         pixS = (short[]) stack.getPixels(1 + index);
         for (int j = 0; j < length; j++) {
            anisotropy[j] = (VALUE12Bit & pixS[j]);
            anisotropy[j] = anisotropy[j] / INT12BitVALUE;
         }
      }
      return anisotropy;
   }

   // get the azimuth values of orientation slice in the stack and convert to radians
   public float[] getOrientationArray(ImageStack stack, int length, int index, int offset) {
      byte[] pixB;
      short[] pixS;
      float[] orient = new float[length];
      int bitDepth = stack.getBitDepth();
      if (bitDepth == STR_KEY_8Bit) {
         pixB = (byte[]) stack.getPixels(offset + index);
         for (int j = 0; j < length; j++) {
            orient[j] = (float)Math.PI * (VALUE8Bit & pixB[j]) / FACTORMAX8Bit;
         }
      } else if (bitDepth == STR_KEY_14Bit) {
         pixS = (short[]) stack.getPixels(offset + index);
         for (int j = 0; j < length; j++) {
            orient[j] = (float)Math.PI * ((float) pixS[j]) / FACTORMAX14Bit;
         }
      } else if (bitDepth == STR_KEY_16Bit) {
         pixS = (short[]) stack.getPixels(offset + index);
         for (int j = 0; j < length; j++) {
            orient[j] = (float)Math.PI * ((float) pixS[j]) / FACTORMAX16Bit;
         }
      } else {
         pixS = (short[]) stack.getPixels(offset + index);
         for (int j = 0; j < length; j++) {
            orient[j] = (float)Math.PI * ((float) pixS[j]) / FACTORMAX12Bit;
         }
      }
      return orient;
   }
}
