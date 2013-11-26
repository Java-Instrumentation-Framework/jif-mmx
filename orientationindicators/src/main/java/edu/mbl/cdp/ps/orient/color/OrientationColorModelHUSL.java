/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mbl.cdp.ps.orient.color;

import java.awt.Color;

/**
 *
 * @author GBH
 */
public class OrientationColorModelHUSL implements OrientationColorModel {

   @Override
   public Color getColor(float hue, float saturation, float lightness) {
      float[] rgb = edu.mbl.cdp.ps.orient.color.HuslConverter.convertHuslToRgb(hue, saturation, lightness);
      Color color = new Color(rgb[0], rgb[1], rgb[2]);
      return color;
   }

   @Override
   public Color getColor(float hue, float saturation, float lightness, float alpha) {
      float[] rgb = edu.mbl.cdp.ps.orient.color.HuslConverter.convertHuslToRgb(hue, saturation, lightness);
      Color color = new Color(rgb[0], rgb[1], rgb[2], alpha);
      return color;
   }
}
