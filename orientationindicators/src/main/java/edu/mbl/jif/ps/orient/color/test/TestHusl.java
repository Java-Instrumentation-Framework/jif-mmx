package edu.mbl.jif.ps.orient.color.test;

import edu.mbl.jif.ps.orient.color.HuslConverter;
import edu.mbl.jif.ps.orient.test.ZoomWindow;
import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author GBH
 */
public class TestHusl {

   static float hue, sat, lit;

   public static void main(String[] args) {
      int w = 100;
      int h = 100;
      int type = BufferedImage.TYPE_INT_ARGB;

      BufferedImage image = new BufferedImage(w, h, type);

      ZoomWindow win = new ZoomWindow("HUSL Colors", 1.0f);

      sat = 99;
      float litMax = 100;
      
         for (int y = 0; y < h; y++) {
            for (int x = 0; x < h; x++) {
            hue = (float) (3.6 * y);
            //sat = (float) (y); 
            lit = (float) x / (float) w * litMax;
            float[] rgb = HuslConverter.convertHuslToRgb(hue, sat, lit);
            Color color = new Color(rgb[0], rgb[1], rgb[2]);
            image.setRGB(x, y, color.getRGB());
         }
      }
      
      win.setImage(image);
      win.setVisible(true);
   }
   
   


}
