package edu.mbl.jif.imaging.dataset;

import edu.mbl.jif.imaging.mmtiff.MDUtils;
import edu.mbl.jif.imaging.mmtiff.TaggedImage;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;

/**
 *
 * @author GBH
 */
public class TaggedImageToBufferedImageConverter {
  
  public static BufferedImage convertTaggedImageToBufferedImage(TaggedImage tImg) {
    try {
      int h = MDUtils.getHeight(tImg.tags);
      int w = MDUtils.getWidth(tImg.tags);
      int type = 0;
      return new BufferedImage(w, h, type);
    } catch (JSONException ex) {
      Logger.getLogger(TaggedImageToBufferedImageConverter.class.getName()).log(Level.SEVERE, null, ex);
      return null;
    }
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
}
