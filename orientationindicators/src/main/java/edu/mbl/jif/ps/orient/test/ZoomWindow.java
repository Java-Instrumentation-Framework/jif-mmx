package edu.mbl.jif.ps.orient.test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.io.File;
import javax.swing.*;

/**
 * a JFrame that presents a given image at some initial magnification which can be stretched and
 * shrunk by the user without changing its aspect ratio.
 *
 * @author Melinda Green
 */
public class ZoomWindow extends JFrame {

   private float zoomMagnification;

   /**
    * creates a ZoomWindow which displays given images which are always scaled to the window size
    * and tracks changes to window size.
    */
   public ZoomWindow(String title, float zoomMagnification) {
      super(title);
      this.zoomMagnification = zoomMagnification;
   }

   public void setImage(final Image zoomimage) {
      final BufferedImage imageToDisplay = toCompatibleImage((BufferedImage)zoomimage);

      int imageWidth = imageToDisplay.getWidth(null);
      int imageHeight = imageToDisplay.getHeight(null);
      final float imageAspectRatio = imageWidth / (float) imageHeight;
      final Dimension minimumImageSize = new Dimension(imageWidth,
              imageHeight);

      //System.out.println("min image size = " + minimumImageSize);
      Component minbox = javax.swing.Box.createRigidArea(minimumImageSize);
      getContentPane().add(minbox); // just for measurement. removed below
      pack(); // just to measure the min window size. true size is set at end.

      Dimension minimumWindowSize = new Dimension(getSize());
      int winXdiff = minimumWindowSize.width - minimumImageSize.width;
      int winYdiff = minimumWindowSize.height
              - minimumImageSize.height;

      //System.out.println("min window size: " + minimumWindowSize + " diffs: " + winXdiff + "," + winYdiff);
      int scaledImageWidth = Math.round(imageWidth * zoomMagnification);
      int scaledImageHeight = Math.round(imageHeight * zoomMagnification);
      final Dimension preferedImageSize = new Dimension(scaledImageWidth, scaledImageHeight);

      final JPanel canvas = new JPanel() {
         public void paint(Graphics g) {
            super.paint(g);

            // get the requested new size
            Dimension canvasSize = getSize();
            Dimension largestImage = new Dimension(canvasSize);

            if ((largestImage.width / (float) largestImage.height) > imageAspectRatio) {
               largestImage.width = (int) Math.ceil(largestImage.height * imageAspectRatio);
            } else {
               largestImage.height = (int) Math.ceil(largestImage.width / imageAspectRatio);
            }

            int xpadding = canvasSize.width - largestImage.width;
            int ypadding = canvasSize.height - largestImage.height;
            g.drawImage(imageToDisplay, xpadding / 2, ypadding / 2,
                    largestImage.width, largestImage.height, null);
         }

         public Dimension getMinimumSize() {
            return minimumImageSize;
         }

         public Dimension getPreferedSize() {
            return preferedImageSize;
         }
      };

      //System.out.println("requested image size " + preferedImageSize);
      getContentPane().removeAll();
      getContentPane().add(canvas);

//      Dimension initialWinSize = new Dimension(preferedImageSize.width
//              + winXdiff, preferedImageSize.height + winYdiff);
      Dimension initialWinSize = new Dimension(800,600);
      setSize(initialWinSize);

      //System.out.println("new window size " + this.getSize());
   }
   
   
   private static final GraphicsConfiguration CONFIGURATION =
           GraphicsEnvironment.getLocalGraphicsEnvironment().
           getDefaultScreenDevice().getDefaultConfiguration();

   public static BufferedImage toCompatibleImage(BufferedImage image) {
      if (image.getColorModel().equals(CONFIGURATION.getColorModel())) {
         return image;
      }
      BufferedImage compatibleImage = CONFIGURATION.createCompatibleImage(
              image.getWidth(), image.getHeight(), image.getTransparency());
      Graphics g = compatibleImage.getGraphics();
      g.drawImage(image, 0, 0, null);
      g.dispose();

      return compatibleImage;
   }

   /**
    * a simple example program puts up a ZoomWindow displaying a named image expected to be found in
    * the classpath.
    */
   public static void main(String[] args) {
      //String image_file_name = args.length == 1 ? args[0] : ".\\test.jpg";
      //Image testImage = getToolkit().getImage(image_file_name);
      // Image testImage = Toolkit.getDefaultToolkit().createImage(image_file_name);
      String imageFile = "C:\\_Dev\\_Dev_Data\\TestImages\\toucan.png";
      File infile = new File(imageFile);
      BufferedImage testImage = null;

      try {
         testImage = javax.imageio.ImageIO.read(infile);
      } catch (IOException ex) {
      }

      //Image testImage = new ImageIcon(
      //ClassLoader.getSystemResource(image_file_name)).getImage();
      ZoomWindow zoomwin = new ZoomWindow("ZoomWindow Example", 1);
      zoomwin.setImage(testImage);
      zoomwin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      zoomwin.setVisible(true);
   }
}


// end class ZoomWindow
