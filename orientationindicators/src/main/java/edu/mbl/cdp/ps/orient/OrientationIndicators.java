/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mbl.cdp.ps.orient;

//import edu.mbl.jif.gui.imaging.zoom.core.ZoomGraphics;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author GBH
 */
public class OrientationIndicators {

   public enum Type {
      FAN, ELLIPSE, LINE
   }
   
   
   public Shape createFanAt(float x, float y, float angle, float length, float dAngle) {
      float overlap = 0f;
      float dAngleDeg = (float)(dAngle*360/Math.PI*2);
      Arc2D.Float arc1 = createArcAt(-overlap, 0, length + overlap, -dAngleDeg/2, dAngleDeg);
      Arc2D.Float arc2 = createArcAt(overlap, 0, length + overlap, 180f-dAngleDeg/2, dAngleDeg);
      Area fanArea = new Area(arc1);
      Area area2 = new Area(arc2);
      fanArea.add(area2);
      Rectangle2D rect = fanArea.getBounds2D();
      float cX = (float) rect.getCenterX();
      float cY = (float) rect.getCenterY();
      Shape rotatedFan = rotateShape(fanArea, -angle, cX, cY);
      Shape xlated = createTranslatedShape(rotatedFan, x, y);
      return xlated;
   }
   // TODO: if dAngle too small, it disappears... use a line instead.


   private Arc2D.Float createArcAt(float x, float y, float r, float angle0, float angle1) {
      //float d = (float) (r / Math.sqrt(2));
      float d = r/2;
      float x0 = x - d;
      float y0 = y - d;
      float w = 2 * d;
      return new Arc2D.Float(x0, y0, w, w, angle0, angle1, Arc2D.PIE);
   }

   // Ellipse ===================================================================
   
   public Shape createEllipseAt(float x, float y, float angle, float length, float dAngle) {
      float eccentricity = dAngle;
      Ellipse2D.Float ellipse = new Ellipse2D.Float(
              0 - length, -length * eccentricity,
              length, length * eccentricity);
//      Ellipse2D.Float ellipse = new Ellipse2D.Float(
//              0 - length / 2, -length * eccentricity / 2,
//              length/2, length * eccentricity/2);
      Rectangle2D rect = ellipse.getBounds2D();
      float cX = (float) rect.getCenterX();
      float cY = (float) rect.getCenterY();
      Shape rotatedEllipse = rotateShape(ellipse, -angle, cX, cY);
      Shape xlated = createTranslatedShape(rotatedEllipse, x-cX, y-cY);
      return xlated;
   }

   public Shape createLinePointAt(float x, float y, float angle, float length, float dAngle) {
      Shape line = createLineAt(x, y, angle, length, dAngle);
      Area lineArea = new Area(line);
      // create point/circle
      Shape point = null;
      Area pointArea = new Area(point);
      lineArea.add(pointArea);
      return lineArea;
   }

   public Shape createLineAt(float x, float y, float angle, float length, float dAngle) {
      float dX = (float) (length * Math.cos(angle)/2);
      float dY = (float) (length * Math.sin(angle)/2);
      Line2D.Float line = new Line2D.Float(x + dX, y - dY, x - dX, y + dY);
      return line;
   }

   //=======================================================================================
   // Shape xforms...
   public static Shape rotateShape(final Shape base, final float angle,
           final float x, final float y) {
      if (base == null) {
         return null;
      }
      //float angle = angleDeg * (float) Math.PI / 180;
      final AffineTransform rotate = AffineTransform.getRotateInstance(angle, x, y);
      final Shape result = rotate.createTransformedShape(base);
      return result;
   }

   public static Shape createTranslatedShape(final Shape shape,
           final double transX,
           final double transY) {
      if (shape == null) {
         throw new IllegalArgumentException("Null 'shape' argument.");
      }
      final AffineTransform transform = AffineTransform.getTranslateInstance(transX, transY);
      return transform.createTransformedShape(shape);
   }
}
