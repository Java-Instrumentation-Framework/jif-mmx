package edu.mbl.cdp.ps.orient;

import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;

/**
 *
 * @author GBH
 */
public class Indicator {

   Shape shape;
   Color color;
   Stroke stroke;

   public Indicator(Shape shape, Color color, Stroke stroke) {
      this.shape = shape;
      this.color = color;
      this.stroke = stroke;
   }
}
