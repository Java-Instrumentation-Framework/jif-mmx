package edu.mbl.jif.ps.orient;

/**
 * // AveragedArea(x, y, anisotropy, orientation, orientationStd, intensity); Holds values of
 average and variance for an area.
 *
 *  x and y are the location of the center of the area.
 * 
 * @author GBH
 */
public class AveragedArea {
   
   public float x;
   public float y;
   public float anisotropy;
   public float orientation;
   public float orientationStd;
   public float intensity;

   // TODO: add variance of anisotropy
   public AveragedArea(float x, float y, float intensity, float anisotropy, float orientation, float orientationStd) {
      this.x = x;
      this.y = y;
      this.intensity = intensity;
      this.anisotropy = anisotropy;
      this.orientation = orientation;
      this.orientationStd = orientationStd;
   }

   @Override
   public String toString() {
      return "" + x + "," + y + ": " + intensity + "  " +  anisotropy + "  " + orientation + " (" + orientationStd +")";
   }
   
}
