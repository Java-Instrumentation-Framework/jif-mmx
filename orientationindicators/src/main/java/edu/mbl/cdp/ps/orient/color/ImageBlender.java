package edu.mbl.cdp.ps.orient.color;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * AlphaCompositing of two images....
 * @author GBH
 */
public class ImageBlender {

   public BufferedImage blend(BufferedImage bi1, BufferedImage bi2,
           double weight) {
      if (bi1 == null) {
         throw new NullPointerException("bi1 is null");
      }

      if (bi2 == null) {
         throw new NullPointerException("bi2 is null");
      }

      int width = bi1.getWidth();
      if (width != bi2.getWidth()) {
         throw new IllegalArgumentException("widths not equal");
      }

      int height = bi1.getHeight();
      if (height != bi2.getHeight()) {
         throw new IllegalArgumentException("heights not equal");
      }

      BufferedImage bi3 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      Graphics2D g2d = bi3.createGraphics();
      g2d.drawImage(bi1, null, 0, 0);
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (1.0 - weight)));
      g2d.drawImage(bi2, null, 0, 0);
      g2d.dispose();

      return bi3;
   }
}
