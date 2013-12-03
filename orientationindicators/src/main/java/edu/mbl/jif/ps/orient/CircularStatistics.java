package edu.mbl.jif.ps.orient;

import java.util.ArrayList;

/**
 *
 * @author GBH
 */
public class CircularStatistics {

   public float[] process(float[] angles, float[] anisoptropy, float[] intensity) {
      if (!(angles.length == anisoptropy.length
              && angles.length == intensity.length
              && anisoptropy.length == intensity.length)) {
         
         System.err.println("Error: angles, anisoptropy and intensity arrays not of the same length");
         return null;
      }
      
      /* From Shalin (Matlab)
      orient=[pi/2, 0, pi/4, 0];%pi*rand(1,500); % Generate 500 random orientations between 0 and pi.
      R=ones(size(orient));
      IRCos2Angle=R.*cos(2*orient);
      IRSin2Angle=R.*sin(2*orient);
      * 
      cosMean=mean(IRCos2Angle)
      sinMean=mean(IRSin2Angle)
      *
      meanR=sqrt(cosMean^2+sinMean^2)
      */
      int n = angles.length;
      float sumCos = 0;
      float sumSin = 0;
      float sumIR = 0;
      float sumI = 0;
      double[] IRCos2Angle = new double[n];
      double[] IRSin2Angle = new double[n];
      for (int i = 0; i < angles.length; i++) {
         float angle =  angles[i];
         double IR = intensity[i] * anisoptropy[i];
         sumIR += IR;
         sumI += intensity[i];
         IRCos2Angle[i] = IR * Math.cos(2 * angle);
         IRSin2Angle[i] = IR * Math.sin(2 * angle);
         sumCos += IRCos2Angle[i];
         sumSin += IRSin2Angle[i];
      }

      // 2theta argument ensure periodicity at 180 degree. Also, pixels with
      // the same R but theta separated by 90 degrees will cancel. 
      // R is the background corrected anisotropy or retardance.

      // To calculate net anisotropy and orientation over chosen pixels:
      double sinMean = sumSin / n;
      double cosMean = sumCos / n;

      double meanR = Math.sqrt(sinMean * sinMean + cosMean * cosMean);
      if(meanR > 1) meanR = 1;
      float meanI = sumI / n;
      //double meanTheta = (0.5 * Math.atan2(sinMean, cosMean)) % Math.PI;
      
      //meanOrient=mod(0.5*atan2(sinMean,cosMean),pi)*(180/pi)
      
      double meanTheta = modulo((0.5 * Math.atan2(sinMean, cosMean)), Math.PI);

      // To calculate standard deviation in anisotropy and orientation:
      double sumCosDiff = 0;
      double sumSinDiff = 0;
      for (int i = 0; i < n; i++) {
         double cosDiff = IRCos2Angle[i] - cosMean;
         double sinDiff = IRSin2Angle[i] - sinMean;
         sumCosDiff += cosDiff * cosDiff;
         sumSinDiff += sinDiff * sinDiff;
      }
      double cosStd = Math.sqrt(sumCosDiff / n);
      double sinStd = Math.sqrt(sumSinDiff / n);
      double std = Math.sqrt(cosStd + sinStd);

      // stdR always varies between 0 and 1 behaving as expected in the extreme
      // cases noted above. Both stdS1 and stdS2 will be high or low together.
      
      //double stdTheta = 2 * atan2(stdS2, stdS1);
      
      // stdTheta should always be between 0 and 90 because both stdS1 and
      // stdS2 are positive values.

      // for now, returned intensity is 1.0
      
      //
      return new float[]{(float) meanR, (float) meanTheta, (float) std, (float) meanI};
   }

   

   public static  int modulo(int i, int j) {
      int rem = i % j;
      if (j < 0 && rem > 0) {
         return rem + j;
      }
      if (j > 0 && rem < 0) {
         return rem + j;
      }
      return rem;
   }
   
   public static double modulo(double i, double j) {
      double rem = i % j;
      if (j < 0 && rem > 0) {
         return rem + j;
      }
      if (j > 0 && rem < 0) {
         return rem + j;
      }
      return rem;
   }
   
   public static void testModulo(double a, double n) {
      
      System.out.println("" + a +" % " + n + "      = " + a % n);
      System.out.println("" + a +" modulo " + n + " = " + modulo((int)a,(int)n) + " (int)");
      System.out.println("" + a +" modulo " + n + " = " + modulo(a,n));
   }


   public void test(String msg, float[] anglesDeg, float[] anisotropy, float[] intensity) {
      float[] result = process(anglesDeg, anisotropy, intensity);
      System.out.println(msg + ":  meanR= " + result[0] + "  meanTheta= " + result[1] * 180 / Math.PI 
              + "  Std= " + result[2]);

   }
   
   public static void main(String[] args) {
      for (int i = -720; i < 720; i=i+16) {
         testModulo((double)i + 0.1, 180);
      }
      
   }
}
