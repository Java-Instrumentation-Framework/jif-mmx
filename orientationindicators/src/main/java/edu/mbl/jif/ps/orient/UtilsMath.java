package edu.mbl.jif.ps.orient;

/*
 * Copyright © 2009 – 2013, Marine Biological Laboratory
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, 
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution.

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS “AS IS” AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those of 
 * the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of any organization.
 */
/**
 *
 * @author Amitabh
 */
public class UtilsMath {

   static final double pi = 3.1415926535897932384626433;        // pi
   static final double twopi = 2.0 * pi;                       // pi times 2
   static final double two_over_pi = 2.0 / pi;                 // 2/pi
   static final double halfpi = pi / 2.0;                      // pi divided by 2
   static final double threehalfpi = 3.0 * pi / 2.0;           // pi times 3/2, used in tan routines
   static final double four_over_pi = 4.0 / pi;                // 4/pi, used in tan routines
   static final double qtrpi = pi / 4.0;                       // pi/4.0, used in tan routines
   static final double sixthpi = pi / 6.0;                     // pi/6.0, used in atan routines
   static final double tansixthpi = Math.tan(sixthpi);         // tan(pi/6), used in atan routines
   static final double twelfthpi = pi / 12.0;                  // pi/12.0, used in atan routines
   static final double tantwelfthpi = Math.tan(twelfthpi);     // tan(pi/12), used in atan routines

   /**
    * ATAN2
    */
   private static final int ATAN2_BITS = 8;

   private static final int ATAN2_BITS2 = ATAN2_BITS << 1;
   private static final int ATAN2_MASK = ~(-1 << ATAN2_BITS2);
   private static final int ATAN2_COUNT = ATAN2_MASK + 1;
   private static final int ATAN2_DIM = (int) Math.sqrt(ATAN2_COUNT);
   private static final double INV_ATAN2_DIM_MINUS_1 = 1.0d / (ATAN2_DIM - 1);
   private static final double DEG = 180.0f / Math.PI;
   private static final double[] atan2 = new double[ATAN2_COUNT];

   static {
      for (int i = 0; i < ATAN2_DIM; i++) {
         for (int j = 0; j < ATAN2_DIM; j++) {
            double x0 = (double) i / ATAN2_DIM;
            double y0 = (double) j / ATAN2_DIM;

            atan2[j * ATAN2_DIM + i] = Math.atan2(y0, x0);
         }
      }
   }

   // http://www.java-gaming.org/topics/13-8x-faster-atan2-updated/14647/view.html
   public static double atan2(double y, double x) {

      double add, mul;
      if (x < 0.0d) {
         if (y < 0.0d) {
            x = -x;
            y = -y;

            mul = 1.0d;
         } else {
            x = -x;
            mul = -1.0d;
         }

         add = -3.141592653d;
      } else {
         if (y < 0.0d) {
            y = -y;
            mul = -1.0d;
         } else {
            mul = 1.0d;
         }

         add = 0.0d;
      }

      double invDiv = 1.0d / (((x < y) ? y : x) * INV_ATAN2_DIM_MINUS_1);

      int xi = (int) (x * invDiv);
      int yi = (int) (y * invDiv);

      return (atan2[yi * ATAN2_DIM + xi] + add) * mul;
   }

   public static double atan(double x) {
      double y; // return from atan__s function
      boolean complement = false; // true if arg was >1
      boolean region = false; // true depending on region arg is in
      boolean sign = false; // true if arg was < 0
      if (x < 0) {
         x = -x;
         sign = true; // arctan(-x)=-arctan(x)
      }
      if (x > 1.0) {
         x = 1.0 / x; // keep arg between 0 and 1
         complement = true;
      }
      if (x > tantwelfthpi) {
         x = (x - tansixthpi) / (1 + tansixthpi * x); // reduce arg to under tan(pi/12)
         region = true;
      }
      y = atan_66s(x); // run the approximation
      if (region) {
         y += sixthpi;
      } // correct for region we're in
      if (complement) {
         y = halfpi - y;
      } // correct for 1/x if we did that
      if (sign) {
         y = -y;
      } // correct for negative arg
      return (y);
   }

   // atan_66s computes atan(x)
   // Accurate to about 6.6 decimal digits over the range [0, pi/12].
   static final double c1 = 1.6867629106;
   static final double c2 = 0.4378497304;
   static final double c3 = 1.6867633134;

   private static double atan_66s(double x) {
      double x2 = x * x;
      return (x * (c1 + x2 * c2) / (c3 + x2));
   }

   public static void main(String[] args) {

      boolean atanRun = false;
      boolean atan2Run = false;
      boolean divParallel = true;

      long start = 0;
      long end = 0;
      double sum = 0; //to prevent the JVM from
      long jMathTime = 0;
      long codeTime = 0;
      float min = 0;
      float max = 0;
      float step = 0;
      String def = "";

      // ATAN
      System.out.println();
      if (atanRun) {
         min = -206000;
         max = +206000;
         System.out.println("----- aTan fnc optimization " + min + " < x,y < " + max + ") -----");
         def = "max(a) || max(b) == 2*Max Image Intensity \n"
                 + "according to formula: a = (pixF[2][j] + pixF[3][j]) - (2 * pixF[1][j])\n"
                 + "usage: atan((PI * swing / 2) * sqrt(a*a + b*b)) \n"
                 + "Considering a = 65535*2 and a high swing of 1, min/max value for atan would be ~ -/+ 206000";
         step = 25f;
         System.out.println(def);
         System.out.println();

         sum = 0.0d;
         start = System.nanoTime() / 1000000L;
         for (float y = min; y < max; y += step) {
            for (float x = min; x < max; x += step) {
               sum += Math.atan(y * x);
            }
         }
         end = System.nanoTime() / 1000000L;
         System.out.println("JavaMath: " + (jMathTime = (end - start)) + "ms, sum=" + sum);

         sum = 0.0d;
         start = System.nanoTime() / 1000000L;
         for (float y = min; y < max; y += step) {
            for (float x = min; x < max; x += step) {
               sum += atan(y * x);
            }
         }
         end = System.nanoTime() / 1000000L;
         System.out.println("Grant-Math1 (atan_66): " + (codeTime = (end - start)) + "ms, sum="
                 + sum + " >> Factor (faster than JavaMath): " + ((float) jMathTime / codeTime));

//        sum = 0.0d;
//        start = System.nanoTime() / 1000000L;
//        for (float y = min; y < max; y += step) {
//            for (float x = min; x < max; x += step) {
//                sum += FastMath.atan(y*x);
//            }
//        }
//        end = System.nanoTime() / 1000000L;
//        System.out.println("FastMath: " + (codeTime = (end - start)) + "ms, sum=" + sum 
//        + " >> Factor (faster than JavaMath): " + ((float) jMathTime/codeTime)) ;
      }

      if (atan2Run) {
         def = "max(a) || max(b) == 2*Max Image Intensity \n"
                 + "according to formula: a = (pixF[2][j] + pixF[3][j]) - (2 * pixF[1][j])\n"
                 + "usage: atan2(a, b) \n"
                 + "Considering a = 65535*2, min/max value for atan2 usage would be ~ -/+ 131070";
         // ATAN2
         min = -150000;
         max = -149999;
         step = 10.0f;
         System.out.println();
         System.out.println("----- aTan2 fnc optimization " + min + " < x,y < " + max + ") -----");
         System.out.println(def);
         System.out.println();

         sum = 0.0d;
         start = System.nanoTime() / 1000000L;
         for (float y = min; y < max; y += step) {
            for (float x = min; x < max; x += step) {
               sum += Math.atan2(y, x);
               //System.out.println(sum);
            }
         }
         end = System.nanoTime() / 1000000L;
         System.out.println("JavaMath: " + (jMathTime = (end - start)) + "ms, sum=" + sum);

         sum = 0.0d;
         start = System.nanoTime() / 1000000L;
         for (float y = min; y < max; y += step) {
            for (float x = min; x < max; x += step) {
               sum += atan2(y, x);
               //System.out.println(sum);
            }
         }
         end = System.nanoTime() / 1000000L;
         System.out.println("Web-code (atan2): " + (codeTime = (end - start)) + "ms, sum=" + sum
                 + " >> Factor (faster than JavaMath): " + ((float) jMathTime / codeTime));

      }
   }

   public static int max(int[] t) {
      int maximum = t[0];   // start with the first value
      for (int i = 1; i < t.length; i++) {
         if (t[i] > maximum) {
            maximum = t[i];   // new maximum
         }
      }
      return maximum;
   }
   
   // sin and cos with lookups

   
}
