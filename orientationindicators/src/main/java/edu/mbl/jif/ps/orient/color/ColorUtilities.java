/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.mbl.jif.ps.orient.color;

import java.awt.Color;

/**
 * @author Cameron Behar
 */
public class ColorUtilities {
  public static Color blend1(Color c0, Color c1) {
    double totalAlpha = c0.getAlpha() + c1.getAlpha();
    double weight0 = c0.getAlpha() / totalAlpha;
    double weight1 = c1.getAlpha() / totalAlpha;

    double r = weight0 * c0.getRed() + weight1 * c1.getRed();
    double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
    double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();
    double a = Math.max(c0.getAlpha(), c1.getAlpha());

    return new Color((int) r, (int) g, (int) b, (int) a);
  }

/**
   * Blend two colors.
   * 
   * @param color1  First color to blend.
   * @param color2  Second color to blend.
   * @param ratio   Blend ratio. 0.5 will give even blend, 1.0 will return
   *                color1, 0.0 will return color2 and so on.
   * @return        Blended color.
   */
  public static Color blend (Color color1, Color color2, double ratio)
  {
    float r  = (float) ratio;
    float ir = (float) 1.0 - r;

    float rgb1[] = new float[3];
    float rgb2[] = new float[3];    

    color1.getColorComponents (rgb1);
    color2.getColorComponents (rgb2);    

    Color color = new Color (rgb1[0] * r + rgb2[0] * ir, 
                             rgb1[1] * r + rgb2[1] * ir, 
                             rgb1[2] * r + rgb2[2] * ir);
    
    return color;
  }


  
  /**
   * Make an even blend between two colors.
   * 
   * @param c1     First color to blend.
   * @param c2     Second color to blend.
   * @return       Blended color.
   */
  public static Color blend (Color color1, Color color2)
  {
    return blend (color1, color2, 0.5);
  }

}
