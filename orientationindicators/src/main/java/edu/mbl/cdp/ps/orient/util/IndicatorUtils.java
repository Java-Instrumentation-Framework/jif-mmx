package edu.mbl.cdp.ps.orient.util;

import edu.mbl.cdp.ps.orient.util.RectangleAnchor;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 *
 * @author GBH
 */
public class IndicatorUtils {

   public static Shape createTranslatedShape(final Shape shape,
           final double transX,
           final double transY) {
      if (shape == null) {
         throw new IllegalArgumentException("Null 'shape' argument.");
      }
      final AffineTransform transform = AffineTransform.getTranslateInstance(
              transX, transY);
      return transform.createTransformedShape(shape);
   }

   /**
    * Translates a shape to a new location such that the anchor point (relative to the rectangular
    * bounds of the shape) aligns with the specified (x, y) coordinate in Java2D space.
    *
    * @param shape the shape (<code>null</code> not permitted).
    * @param anchor the anchor (<code>null</code> not permitted).
    * @param locationX the x-coordinate (in Java2D space).
    * @param locationY the y-coordinate (in Java2D space).
    *
    * @return A new and translated shape.
    */
   public static Shape createTranslatedShape(final Shape shape,
           final RectangleAnchor anchor,
           final double locationX,
           final double locationY) {
      if (shape == null) {
         throw new IllegalArgumentException("Null 'shape' argument.");
      }
      if (anchor == null) {
         throw new IllegalArgumentException("Null 'anchor' argument.");
      }
      Point2D anchorPoint = RectangleAnchor.coordinates(
              shape.getBounds2D(), anchor);
      final AffineTransform transform = AffineTransform.getTranslateInstance(
              locationX - anchorPoint.getX(), locationY - anchorPoint.getY());
      return transform.createTransformedShape(shape);
   }

   /**
    * Rotates a shape about the specified coordinates.
    *
    * @param base the shape (<code>null</code> permitted, returns <code>null</code>).
    * @param angle the angle (in radians).
    * @param x the x coordinate for the rotation point (in Java2D space).
    * @param y the y coordinate for the rotation point (in Java2D space).
    *
    * @return the rotated shape.
    */
   public static Shape rotateShape(final Shape base, final double angle,
           final float x, final float y) {
      if (base == null) {
         return null;
      }
      final AffineTransform rotate = AffineTransform.getRotateInstance(
              angle, x, y);
      final Shape result = rotate.createTransformedShape(base);
      return result;
   }

   public static Shape rotateShape(final Shape base, final float angleDeg,
           final float x, final float y) {
      if (base == null) {
         return null;
      }
      float angle = angleDeg * (float) Math.PI / 180;
      final AffineTransform rotate = AffineTransform.getRotateInstance(
              angle, x, y);
      final Shape result = rotate.createTransformedShape(base);
      return result;
   }
}
