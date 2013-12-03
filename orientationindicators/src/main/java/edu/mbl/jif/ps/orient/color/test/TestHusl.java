/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mbl.jif.ps.orient.color.test;

import edu.mbl.jif.ps.orient.color.HuslConverter;
import edu.mbl.jif.ps.orient.test.ZoomWindow;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
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
      float litMax = 80;
      for (int i = 0; i < 100; i++) {
         for (int y = 0; y < h; y++) {
            hue = (float) (3.6 * i);
            sat = (float) (y);
            lit = (float) y / (float) h * litMax;
            float[] rgb = HuslConverter.convertHuslToRgb(hue, sat, lit);
            Color color = new Color(rgb[0], rgb[1], rgb[2]);
            image.setRGB(i, y, color.getRGB());
         }
      }
      
      win.setImage(image);
      win.setVisible(true);
   }
   
   


}
