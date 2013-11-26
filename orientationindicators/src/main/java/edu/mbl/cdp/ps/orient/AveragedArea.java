package edu.mbl.cdp.ps.orient;

/**
 * // AveragedArea(x, y, anisotropy, orientation, orientationVariance, intensity); Holds values of
 * average and variance for an area.
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
   public float orientationVariance;
   public float intensity;

   // TODO: add variance of anisotropy
   public AveragedArea(float x, float y, float intensity, float anisotropy, float orientation, float orientationVariance) {
      this.x = x;
      this.y = y;
      this.intensity = intensity;
      this.anisotropy = anisotropy;
      this.orientation = orientation;
      this.orientationVariance = orientationVariance;
   }

   @Override
   public String toString() {
      return "" + x + "," + y + ": " + intensity + "  " +  anisotropy + "  " + orientation + " (" + orientationVariance +")";
   }
   
}
