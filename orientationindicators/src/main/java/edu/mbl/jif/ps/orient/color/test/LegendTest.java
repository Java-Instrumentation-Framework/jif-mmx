/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mbl.jif.ps.orient.color.test;
import edu.mbl.jif.ps.orient.color.HuslConverter;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class Surface extends JPanel {

    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        this.drawLegend(g2d, 100,100, 100);

//        g2d.setColor(Color.blue);
//
//        Dimension size = getSize();
//        Insets insets = getInsets();
//
//        int w = size.width - insets.left - insets.right;
//        int h = size.height - insets.top - insets.bottom;
//
//        Random r = new Random();
//
//        for (int i = 0; i < 1000; i++) {
//
//            int x = Math.abs(r.nextInt()) % w;
//            int y = Math.abs(r.nextInt()) % h;
//            g2d.drawLine(x, y, x, y);
//        }
    }
    
    
   public void drawLegend(Graphics2D g2, double x0, double y0, double totalR) {
      boolean half = false;
      // half, 180 or full, 360
      // ranges for hue, sat, lit
      // hue varies with angle
      // sat varies radially
      int radialElements = 100;
      double rIncr = totalR / (double)radialElements;
      double satMax = 100;
      double satIncr = satMax / (double)radialElements;
      //
      int angularElements = 360;
      double angularIncr = Math.PI / angularElements;
      double hueMax = 360;
      double hueIncr = hueMax / (double)angularElements;
      double lit = 50;
      //
      for (int r = 0; r < radialElements; r++) {
         double radius = r * rIncr;
         double sat = r * satIncr;
         //double sat = 70;
         for (int a = 0; a < angularElements; a++) {
            double angle = 2* a * angularIncr;
            double hue = 2* a * hueIncr;
            double x = x0 + radius * Math.cos(angle);
            double y = y0 + radius * Math.sin(angle);
            float[] rgb = HuslConverter.convertHuslToRgb((float) hue, (float) sat, (float) lit);
            Color color = new Color(rgb[0], rgb[1], rgb[2]);
            g2.setColor(color);
            Ellipse2D.Double dot = new Ellipse2D.Double(x,y,2.0,2.0);
            g2.fill(dot);
         }
      }
   }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }
}

public class LegendTest extends JFrame {

    public LegendTest() {

        initUI();
    }

    private void initUI() {
        
        setTitle("Legend Test");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(new Surface());

        setSize(350, 250);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LegendTest ps = new LegendTest();
                ps.setVisible(true);
            }
        });
    }
}