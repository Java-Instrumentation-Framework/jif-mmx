/*
 * GBH, 2013
 */
package edu.mbl.jif.ps.orient.test;

import edu.mbl.jif.ps.orient.OrientationIndicators;
import edu.mbl.jif.ps.orient.OrientationIndicators.Type;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import javax.swing.*;
///import org.jfree.ui.RectangleAnchor;

public class IndicatorTest
        extends JComponent
        implements Runnable {

   ArrayList<Point2D> pts;
   OrientationIndicators og;
   private final int w;
   private final int h;

   public IndicatorTest(int w, int h) {
      this.w = w;
      this.h = h;
      float increment = 20f;
      pts = generateGridCoordList(1, 1, increment);
      og = new OrientationIndicators();
      repaint();
      //(new Thread(this)).start();
   }

   public void run() {
      try {
         for (;;) {
            Thread.sleep(500);
            repaint();
         }
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      }
   }

   private AlphaComposite makeComposite(float alpha) {
      int type = AlphaComposite.SRC_OVER;
      return (AlphaComposite.getInstance(type, alpha));
   }
   float hue = 250f / 360f;
   float sat = 1.0f;
   float bri = 1.0f;
   float alpha = 1.0f;

   public void paint(Graphics graphics) {
      super.paint(graphics);

      Graphics2D g = (Graphics2D) graphics;
//		ZoomGraphics g = (ZoomGraphics) graphics;
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.setStroke(new BasicStroke(1));
//		size = getSize(size);
//		insets = getInsets(insets);
//		int radius = Math.min(size.width - insets.left - insets.top,
//				size.height - insets.top - insets.bottom) / 2;
//		//g.translate((double) size.width / 2D, (double) size.height / 2D);
//		g.translate(100, 100);
      // Set transparency...
      //Composite originalComposite = g.getComposite();

      // setHSBA(g, hue,sat,bri,alpha);
      graphics.setColor(new Color(0f, .7f, .5f, .7f));
      generateTestGrid(g, OrientationIndicators.Type.ELLIPSE);
//      graphics.setColor(new Color(.5f, .7f, .5f, .5f));
//      generateTestGrid(g, OrientationIndicators.Type.FAN);
      graphics.setColor(new Color(.9f, .7f, 1f, 1f));
      generateTestGrid(g, OrientationIndicators.Type.LINE);
   }

   public void setHSBA(Graphics2D g, float h, float s, float b, float alpha) {
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
      int rgb = Color.HSBtoRGB(h, s, b); 
      g.setColor(new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF));
   }
   public Color colorHSBA(float h, float s, float b, float alpha) {
      int rgb = Color.HSBtoRGB(h, s, b); 
      return new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, alpha);
   }

   public void generateTestGrid(Graphics2D g, Type type) {
      int offX = 24;
      int offY = 24;
      int dX = 50;
      int nX = w / dX;
      int dY = 50;
      int nY = h / dY;
      int iterations = nX * nY;
      System.out.println("iterations = " + iterations);
      float dAngle = (float) Math.PI / (float) iterations;
      System.out.println("dAngle = " + dAngle);
      //
      float angle = 0f;
      float length = 40f;
      float variance = 0f;
      //
      for (int j = 0; j < nY; j++) {
         int y = j * dY + offY;
         for (int i = 0; i < nX; i++) {
            int x = i * dX + offX;
            angle = angle + dAngle;
            variance = variance + 1 / (float) iterations;
            //float l = (float) length * (1 - variance);
            float l = (float) length;
            System.out.println("" + x + ", " + y + ", " + angle + ", " + l + ", " + variance);
            if (type == Type.ELLIPSE) {
               Shape shape = og.createEllipseAt((float) x, (float) y, angle, l, variance);
               g.fill(shape);
            }
            if (type == Type.FAN) {
               Shape shape = og.createFanAt((float) x, (float) y, angle, l, variance);
               g.fill(shape);
            }
            if (type == Type.LINE) {
               Shape shape = og.createLineAt((float) x, (float) y, angle, l, variance);
               g.draw(shape);
            }
         }
      }
   }
   // HSB...
   // raster
   // length
   // angle
   // variance

//      Shape elli = createEllipseAt(100f, 100f, 0f, 50f, 0.2f);
//      g.fill(elli);
//      elli = createEllipseAt(200f, 100f, 90f, 50f, 0.2f);
//      g.fill(elli);
//      elli = createEllipseAt(300f, 100f, 90f, 50f, 0.5f);
//      g.fill(elli);
   //
   //g.translate((double) size.width / 20D, (double) size.height / 2D);
//		g.setColor(Color.DARK_GRAY);
//		//
//		float angle = 0;
//		float extent = .1f;
//		float dAngle = extent * 180f;
//		//float startAngle = angle - dAngle / 2;
//		float length = radius / 20;
//		int i = 1;
//		int n = pts.size();
//		for (Point2D pt : pts) {
//			angle = 2 * i;
//			extent = .1f; // *(float) i;
//			dAngle = extent * 180f;
//			float width = extent * 10f;
//			//startAngle = angle - dAngle / 2;
//			//length = radius / i + 1;
//			//length = radius / 6;
//			float x = (float) pt.getX();
//			float y = (float) pt.getY();
////			g.setColor(Color.red);
////			og.drawFanGlyph(g, x, y, angle, length, dAngle);
//			//
//
//			g.setColor(Color.blue);
//			Shape elli = createEllipseAt(x, y, angle, length, dAngle);
//			//g.draw(elli);
//			g.fill(elli);
//			
//			i++;
   //
//		}
//	public  void drawEllipseGlyph(ZoomGraphics g, float x, float y, float angle, float length, float dAngle) {
//		g.translate((double) x, (double) y);
//		g.rotate(angle);
//		float eccentricity = 1f * dAngle;
//		Ellipse2D.Float ellip = new Ellipse2D.Float(
//				0 - length / 2, -length * eccentricity / 2,
//				length, length * eccentricity);
//		g.fill(ellip);
//		g.rotate(-angle);
//		g.translate((double) -x, (double) -y);
//	}
//	private Area createEllipseArea(float r, float dAngle) {
//		Ellipse2D.Float ellip1 = createEllipseAt(0, 0, r + 2, -dAngle / 2, dAngle / 2);
//		Area ellipArea = new Area(ellip1);
//		return ellipArea;
//	}
//   private Shape createEllipseAt(float x, float y, float angle, float length, float dAngle) {
//      float eccentricity = 1f * dAngle;
//      Ellipse2D.Float ellipse = new Ellipse2D.Float(
//              0 - length / 2, -length * eccentricity / 2,
//              length, length * eccentricity);
//      Rectangle2D rect = ellipse.getBounds2D();
//      float cX = (float) rect.getCenterX();
//      float cY = (float) rect.getCenterY();
//      Shape rotatedEllipse = GlyphUtils.rotateShape(ellipse, angle, cX, cY);
//      Shape xlated = GlyphUtils.createTranslatedShape(rotatedEllipse, x, y);
//      return xlated;
//   }
   static ArrayList<Point2D> generateGridCoordList(int nX, int nY, float increment) {
      ArrayList<Point2D> pts = new ArrayList<Point2D>();
      for (int i = 0; i < nX; i++) {
         for (int j = 0; j < nY; j++) {
            float x = i * increment;
            float y = j * increment;
            pts.add(new Point2D.Float(x, y));
            System.out.println("= " + x + ", " + y);
         }
      }
      return pts;
   }
   private static Stroke SEC_STROKE = new BasicStroke();
   private Dimension size = null;
   private Insets insets = new Insets(0, 0, 0, 0);

   public static void main(String[] args) {
      JFrame f = new JFrame("Glyph Test");
      IndicatorTest clock = new IndicatorTest(1200, 800);
      f.getContentPane().add(clock);
      f.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
            System.exit(0);
         }
      });
      f.setBounds(50, 50, 1232, 832);
      f.setVisible(true);
   }
}