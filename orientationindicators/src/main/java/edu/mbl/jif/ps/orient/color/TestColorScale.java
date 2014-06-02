/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mbl.jif.ps.orient.color;

import edu.mbl.jif.ps.orient.Test;
import static ij.gui.Line.getWidth;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import javax.swing.JPanel;

/**
 *
 * @author GBH
 */
public class TestColorScale {
   private static ImageDispPanel iColorPanel;

   public static void main(String[] args) {
      ImageDispPanel ip = getIColorPanel();
      Test.QuickFrame f = new Test.QuickFrame("color");
      f.add(ip);
      f.pack();
      f.setVisible(true);
   }


//code that generates color map adn displaying it - based on ur code
   private static ImageDispPanel getIColorPanel() {
      if (iColorPanel == null) {
         iColorPanel = new ImageDispPanel();
         iColorPanel.setPreferredSize(new java.awt.Dimension(185, 185));
         iColorPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(
                 javax.swing.border.EtchedBorder.RAISED));
         int[] OneDimImage = new int[256 * 256];

         int[] colors = new int[256];
         for (int i = 0; i < 256; i++) {
            int r = i; // Red goes from 0 to 255 once
            int g = (i * 2) & 255; // Green goes from 0 to 255 twice
            int b = (i * 3) & 255; // Blue does it three times. This should be a good effect.
            colors[i] = 0xff000000 | (r << 16) | (g << 8) | b;
         }
         for (int k = 0; k < 256; k++) {
            for (int j = 0; j < 256; j++) {
               OneDimImage[k + (j * 256)] = colors[k];
            }
         }

         Toolkit tk = Toolkit.getDefaultToolkit();
         Image img = tk.createImage(new MemoryImageSource(256, 256, OneDimImage, 0, 256));
         iColorPanel.setImage(img);
         iColorPanel.repaint();
      }
      return iColorPanel;
   }

   
}
//Panel class for displaying
   class ImageDispPanel extends JPanel {

      Image image;
      private boolean imageSet = false;

      public ImageDispPanel() {
         super();
      }

      public ImageDispPanel(Image image) {
         super();
         this.image = image;
         imageSet = true;
      }

      public boolean setImage(Image image) {
         this.image = image;
         imageSet = true;
         return true;
      }

      protected void paintComponent(Graphics g) {
         Graphics2D g2 = (Graphics2D) g;
         super.paintComponent(g);
         if (imageSet) {
            int w = getWidth();
            int h = getHeight();
            int imageWidth = image.getWidth(this);
            int imageHeight = image.getHeight(this);
            int x = (w - imageWidth) / 2;
            int y = (h - imageHeight) / 2;
            g.drawImage(image, x, y, this);
         }
      }
   }
