package edu.mbl.cdp.ps.orient.color;

import java.awt.Color;

/**
 * Color Model for Orientation Color Mapping
 * @author GBH
 */
public interface OrientationColorModel {
   
   /* 
    * generates a color based on 3 inputs 
    * hue
    * saturation
    * lightness
    * 
    * If using HSV, map V to lightness/2.
    * 
    * for example: HSV/HSB, HSL, HUSL, or ...
   */
      
   Color getColor(float hue, float saturation, float lightness);
   
   Color getColor(float hue, float saturation, float lightness, float alpha);
 
   
}
