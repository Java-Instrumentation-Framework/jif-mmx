/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mbl.jif.ps.orient.color;

/**
 * colourlab – free mathematical perceptual colour model and other colour tools
 *
 * Copyright © 2012 Mattias Andrée (maandree@kth.se)
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

/**
 * @author Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class TestColour extends JFrame {

   public TestColour() {
      super("Free perceptual colour model");
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.pack();
      final Insets in = this.getInsets();
      this.setSize(new Dimension(451 + in.left + in.right, 451 + in.top + in.bottom));
   }

   public static void main(final String... args) {
      for (int il = 0, ln = imgs.length - 1; il <= ln; il++) {
         System.err.println("At image " + il);
         final BufferedImage img = imgs[il] = new BufferedImage(451, 451,
                 BufferedImage.TYPE_INT_ARGB);
         final Graphics2D gg = img.createGraphics();

         for (int x = 0; x <= 450; x++) {
            final Colour colour = new Colour(x / 450., 0., 0.);
            final int[] rgb = colour.getStandardRGB();
            int r = rgb[0],
                    g = rgb[1],
                    b = rgb[2];

            if ((r > 255) || (g > 255) || (b > 255) || (r < 0) || (g < 0) || (b < 0)) {
               r = r > 255 ? 255 : r < 0 ? 0 : r;
               g = g > 255 ? 255 : g < 0 ? 0 : g;
               b = b > 255 ? 255 : b < 0 ? 0 : b;
               r = 255 - r;
               g = 255 - g;
               b = 255 - b;
            }

            gg.setColor(new Color(r, g, b));
            gg.drawLine(x, 0, x, 450);
         }

         for (int y = -200; y <= 200; y++) {
            for (int x = -200; x <= 200; x++) {
               final double fy = y / 200.;
               final double fx = x / 200.;

               if (fy * fy + fx * fx > 1.) {
                  continue;
               }

               final double l = il / (double) ln,
                       s = Math.sqrt(fy * fy + fx * fx),
                       h = ((Math.atan2(y, x) * 200. / Math.PI) + 100.) % 400.;

               final double zz = (l - .5 < 0.) ? (.5 - l) : (l - .5);
               if (2. * zz + s > 1.) {
                  continue;
               }

               final Colour colour = new Colour(l, s, h);
               final int[] rgb = colour.getStandardRGB();
               int r = rgb[0],
                       g = rgb[1],
                       b = rgb[2];

               if ((r > 255) || (g > 255) || (b > 255) || (r < 0) || (g < 0) || (b < 0)) {
                  r = r > 255 ? 255 : r < 0 ? 0 : r;
                  g = g > 255 ? 255 : g < 0 ? 0 : g;
                  b = b > 255 ? 255 : b < 0 ? 0 : b;
                  r = 255 - r;
                  g = 255 - g;
                  b = 255 - b;
               }

               gg.setColor(new Color(r, g, b));
               gg.fillRect(225 + x, 225 + y, 1, 1);
            }
         }
      }

      final TestColour frame = new TestColour();
      frame.setVisible(true);

      final Thread thread = new Thread() {
         @Override
         public void run() {
            int diff = 1;
            try {
               for (;;) {
                  Thread.sleep(5000 / (TestColour.imgs.length - 1));
                  TestColour.lum += diff;
                  if (TestColour.lum >= TestColour.imgs.length - 1) {
                     TestColour.lum = TestColour.imgs.length - 1;
                     diff = -diff;
                  } else if (TestColour.lum <= 0) {
                     TestColour.lum = 0;
                     diff = -diff;
                  }
                  frame.repaint();
               }
            } catch (final Throwable err) {
            }
         }
      };
      thread.setDaemon(true);
      thread.start();
   }

   private static BufferedImage[] imgs = new BufferedImage[1 + 6];

   static int lum = 0;

   @Override
   public void paint(final Graphics gp) {
      final Insets in = this.getInsets();
      gp.drawImage(imgs[lum], in.left, in.top, null);
   }

}
