/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mbl.jif.datasetconvert;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author GBH
 */
class RectangleInputPanel extends JPanel {

   private Rectangle rect;
   private int x;
   private javax.swing.JFormattedTextField fieldX;
   private int y;
   private javax.swing.JFormattedTextField fieldY;
   private int w;
   private javax.swing.JFormattedTextField fieldW;
   private int h;
   private javax.swing.JFormattedTextField fieldH;

   // TODO add constructor with initial values...
   
   public RectangleInputPanel() {
      super(new MigLayout());
      //Mig.addSeparator(this, "ROI for Cropping");
      this.setBorder(new EtchedBorder());
//      JCheckBox check = new JCheckBox("Crop Images");
//      add(check, "span, wrap");
      JLabel xLabel = new JLabel("x: ", SwingConstants.LEADING);
      JLabel yLabel = new JLabel("y: ", SwingConstants.LEADING);
      JLabel wLabel = new JLabel("w: ", SwingConstants.LEADING);
      JLabel hLabel = new JLabel("h: ", SwingConstants.LEADING);
      fieldX = new JFormattedTextField(4);
      fieldY = new JFormattedTextField(4);
      fieldW = new JFormattedTextField(4);
      fieldH = new JFormattedTextField(4);

      this.add(xLabel, "gap para");
      this.add(fieldX, "w 40!");
      this.add(yLabel, "gap para");
      this.add(fieldY, "w 40!");
      this.add(wLabel, "gap para");
      this.add(fieldW, "w 40!");
      this.add(hLabel, "gap para");
      this.add(fieldH, "w 40!");
      //this.add(fieldH, "span, wrap para");

      fieldX.setValue(x);
      fieldY.setValue(y);
      fieldW.setValue(w);
      fieldH.setValue(h);
      //this.setPreferredSize(new Dimension(200, 24));
   }

   public Rectangle getRectangle() {
      // get from fields.
      x = ((Number) fieldX.getValue()).intValue();
      y = ((Number) fieldY.getValue()).intValue();
      w = ((Number) fieldW.getValue()).intValue();
      h = ((Number) fieldH.getValue()).intValue();
      rect = new Rectangle(x, y, w, h);
      return rect;
   }
   
      public static void main(String[] args)
           throws Exception {
      try {
         for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
               UIManager.setLookAndFeel(info.getClassName());
               break;
            }
         }
      } catch (Exception e) {
         // If Nimbus is not available, you can set the GUI to another look and feel.
      }
      QuickFrame f = new QuickFrame("");
      RectangleInputPanel rectPanel = new RectangleInputPanel();
      f.add(rectPanel);
      f.pack();
      f.setVisible(true);
      
   }

   public static class QuickFrame extends JFrame {

      public QuickFrame(String title) {
         super(title);
         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         setSize(640, 480);
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         setLocation(
                 Math.max(0, screenSize.width / 2 - getWidth() / 2),
                 Math.max(0, screenSize.height / 2 - getHeight() / 2));
      }
   }
}
