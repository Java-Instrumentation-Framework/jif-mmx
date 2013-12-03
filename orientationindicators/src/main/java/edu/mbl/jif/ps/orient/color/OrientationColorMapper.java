package edu.mbl.jif.ps.orient.color;

import edu.mbl.jif.ps.orient.color.OrientationColorModel;

/**
 * Orientation Mapper
 * Maps orientation to color from Anisotropy and, optionally, Intensity.
 * @author GBH
 */
public class OrientationColorMapper {
   
   private OrientationColorModel colorModel;
   
   // Alpha and compositing mode.... ??
   
   // Color Model 
   public void setColorModel(OrientationColorModel model) {
      this.colorModel = model;
   }
     
   
   // Image applyColorMap(int w, int h, Object anisotropy, Object intensity, Object orientation) {
     // return new BufferedImage(w, h, ImageType.TYPE_INT_ARGB)
   // }
   
}
