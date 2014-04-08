/*
 * GBH, 2013
 */
package edu.mbl.jif.ps.orient.test;

import edu.mbl.jif.ps.orient.CircularStatistics;
import edu.mbl.jif.ps.orient.OrientationIndicators;
import java.awt.*;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
///import org.jfree.ui.RectangleAnchor;

public class CircStatTest
        extends JComponent
        implements Runnable {

   ArrayList<Point2D> pts;
   OrientationIndicators og;
   private final int w;
   private final int h;

   public CircStatTest(int w, int h) {
      this.w = w;
      this.h = h;
      float increment = 20f;
      //pts = generateGridCoordList(1, 1, increment);
      //og = new OrientationIndicators();

      //dumpData(result);
      //repaint();
      //(new Thread(this)).start();
   }

   public static float[][] makeDataAngles() {
      float[] angles = new float[]{0, (float) Math.PI / 4, (float) Math.PI / 2, (float) Math.PI * 3
         / 4};
      float[][] result = new float[256][4];
      int n = 0;
      for (int i = 0; i < 4; i++) {
         for (int j = 0; j < 4; j++) {
            for (int k = 0; k < 4; k++) {
               for (int l = 0; l < 4; l++) {
                  result[n][0] = angles[i];
                  result[n][1] = angles[j];
                  result[n][2] = angles[k];
                  result[n][3] = angles[l];
                  n++;
               }
            }
         }
      }
      return result;
   }

   public static float[][] makeDataAnisotropy() {
      float[] anisos = new float[]{1.0f, 0.75f, 0.5f, 0.25f};
      float[][] result = new float[256][4];
      int n = 0;
      for (int i = 0; i < 4; i++) {
         for (int j = 0; j < 4; j++) {
            for (int k = 0; k < 4; k++) {
               for (int l = 0; l < 4; l++) {
                  result[n][0] = anisos[i];
                  result[n][1] = anisos[j];
                  result[n][2] = anisos[k];
                  result[n][3] = anisos[l];
                  n++;
               }
            }
         }
      }
      return result;
   }

   public void calculateAverage(float[][] data) {
      // new AveragedArea(x,y,orientation, variance, anisotropy);
   }

   private void dumpData(float[][] result) {
      System.out.println("len = " + result.length);
      for (int i = 0; i < result.length; i++) {
         System.out.println("["
                 + result[i][0] + ","
                 + result[i][1] + ","
                 + result[i][2] + ","
                 + result[i][3] + "]");

      }
   }

   public void run() {
      try {
         for (;;) {
            Thread.sleep(500);
            repaint();
         }
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      }
   }

   private AlphaComposite makeComposite(float alpha) {
      int type = AlphaComposite.SRC_OVER;
      return (AlphaComposite.getInstance(type, alpha));
   }

   public void paint(Graphics graphics) {
      super.paint(graphics);
      Graphics2D g = (Graphics2D) graphics;
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//		size = getSize(size);
//		insets = getInsets(insets);
//		int radius = Math.min(size.width - insets.left - insets.top,
//				size.height - insets.top - insets.bottom) / 2;
//		//g.translate((double) size.width / 2D, (double) size.height / 2D);
//		g.translate(100, 100);
      // Set transparency...
      Composite originalComposite = g.getComposite();
      float alpha = 1.0f;
      g.setComposite(makeComposite(alpha));
      g.setColor(Color.blue);
      generateTestGrid(g);
   }

   public void generateTestGrid(Graphics2D g) {
      int offX = 24;
      int offY = 24;
      int dX = 50;
      int nX = w / dX;
      int dY = 50;
      int nY = h / dY;
      int iterations = nX * nY;
      System.out.println("iterations = " + iterations);
      float dAngle = (float) Math.PI / (float) iterations;
      System.out.println("dAngle = " + dAngle);
      //
      float angle = 0f;
      float length = 40f;
      float variance = 5f;
      //
      for (int j = 0; j < nY; j++) {
         int y = j * dY + offY;
         for (int i = 0; i < nX; i++) {
            int x = i * dX + offX;
            angle = angle + dAngle;
            //variance = variance + 180.0f / (float)iterations;
            //float l = (float) length * (1 - variance / 180);
            float l = (float) length;
            System.out.println("" + x + ", " + y + ", " + angle + ", " + l + ", " + variance);
            //og.drawEllipseGlyph(g, (float) x, (float) y, angle, l, variance);
            Shape shape = og.createEllipseAt((float) x, (float) y, angle, l, variance);
            g.fill(shape);

         }
      }
   }

   static ArrayList<Point2D> generateGridCoordList(int nX, int nY, float increment) {
      ArrayList<Point2D> pts = new ArrayList<Point2D>();
      for (int i = 0; i < nX; i++) {
         for (int j = 0; j < nY; j++) {
            float x = i * increment;
            float y = j * increment;
            pts.add(new Point2D.Float(x, y));
            System.out.println("= " + x + ", " + y);
         }
      }
      return pts;
   }
   
   /* [0,0,0,0]
    /* [45,0,0,0]
    /* [90,0,0,0]
    /* [135,0,0,0]
    /* [45,45,0,0]
    * ...
    */
   /* [0,45,90,135]
    *
    */
   /* [0,45,90,135]
    /* [0,45,90,135]
    *  [0,0,0,0]
    *  [0,0,0,0]
    * 180 / 4
    */
   private static Stroke SEC_STROKE = new BasicStroke();
   private Dimension size = null;
   private Insets insets = new Insets(0, 0, 0, 0);

   public static void main(String[] args) {

      //test2(null);
      
      test3();
      
//      JFrame f = new JFrame("Glyph Test");
//      CircStatTest clock = new CircStatTest(1200, 800);
//      f.getContentPane().add(clock);
//      f.addWindowListener(new WindowAdapter() {
//         public void windowClosing(WindowEvent e) {
//            System.exit(0);
//         }
//      });
//      f.setBounds(50, 50, 1232, 832);
//      f.setVisible(true);
   }

   public static void test2(String[] args) {
      float[][] orients = makeDataAngles();
      //MathUtils.displayArrayAsTableFrame("Result orients", orients);
      float[][] anisos = makeDataAnisotropy();
      //MathUtils.displayArrayAsTableFrame("Result anisos", anisos);

      CircularStatistics cs = new CircularStatistics();
//      ArrayList<float[]> testSetAngles = new ArrayList<float[]>();
//      ArrayList<float[]> testSetAnisotropy = new ArrayList<float[]>();
//      ArrayList<float[]> testSetIntensity = new ArrayList<float[]>();
      //
      float[] intensity0 = new float[]{1, 1, 1, 1};
      float[] anglesDeg = new float[4];
      //float[] anisotropy= new float[4];
      float[] anisotropy = new float[]{1, 1, 1, 1};

      List results = new ArrayList<Object[]>();

      for (int i = 0; i < 100; i++) {
         for (int j = 0; j < 4; j++) {
            anglesDeg[j] = orients[i][j];
            //anisotropy[j] = anisos[i][j];
         }
         float[] circStat = cs.process(anglesDeg, anisotropy, intensity0);
         cs.test("", anglesDeg, anisotropy, intensity0);

         DecimalFormat df2 = new DecimalFormat("#.00");
         DecimalFormat df3 = new DecimalFormat("#.000");
         String angleMean = df2.format(circStat[1] * 180 / Math.PI);
         String rMean = df3.format(circStat[0]);
         String iMean = df3.format(circStat[0]);
         String std = df3.format(circStat[2]);

         results.add(
                 new Object[]{Arrays.toString(anglesDeg), Arrays.toString(anisotropy), Arrays.toString(
                            intensity0),
                    rMean, iMean, angleMean, std
                 });

      }
      // Show results in table
      String[] colHeaders = new String[]{
         "#", "Angles", "Anisotropies", "Intensities", "meanR", "meanI", "meanAngle", "Std"};
      MathUtils.displayListOfArraysAsTableFrame("Results", colHeaders, results);
   }

   public static void test1() {
      CircularStatistics cs = new CircularStatistics();
      ArrayList<float[]> testSetAngles = new ArrayList<float[]>();
      ArrayList<float[]> testSetAnisotropy = new ArrayList<float[]>();
      ArrayList<float[]> testSetIntensity = new ArrayList<float[]>();
      //
      float[] anglesDeg0 = new float[]{
         90, 78, 87,
         89, 90, 84,
         93, 81, 88};
      float[] anisotropy0 = new float[]{
         1, 1, 1,
         1, 1, 1,
         1, 1, 1};
      float[] intensity0 = new float[]{
         1, 1, 1,
         1, 1, 1,
         1, 1, 1};
      cs.test("Near 90, 1", anglesDeg0, anisotropy0, intensity0);
      anglesDeg0 = new float[]{
         90, 90, 90,
         90, 90, 90,
         90, 90, 90};
      anisotropy0 = new float[]{
         1, 1, 1,
         1, 1, 1,
         1, 1, 1};
      cs.test(" 90, 1", anglesDeg0, anisotropy0, intensity0);
      anglesDeg0 = new float[]{
         0, 0, 0,
         0, 0, 0,
         0, 0, 0};
      anisotropy0 = new float[]{
         1, 1, 1,
         1, 1, 1,
         1, 1, 1};
      cs.test("  0, 1", anglesDeg0, anisotropy0, intensity0);
      anglesDeg0 = new float[]{
         135, 135, 135,
         135, 135, 135,
         135, 135, 135};
      anisotropy0 = new float[]{
         1, 1, 1,
         1, 1, 1,
         1, 1, 1};
      cs.test("135, 1", anglesDeg0, anisotropy0, intensity0);
      anglesDeg0 = new float[]{
         175, 175, 175,
         175, 175, 175,
         175, 175, 175};
      anisotropy0 = new float[]{
         1, 1, 1,
         1, 1, 1,
         1, 1, 1};
      cs.test("175, 1", anglesDeg0, anisotropy0, intensity0);
      anglesDeg0 = new float[]{
         0, 45, 90, 135};
      anisotropy0 = new float[]{
         1, 1, 1, 1};
      intensity0 = new float[]{1, 1, 1, 1};
      cs.test("all, 1", anglesDeg0, anisotropy0, intensity0);
      anglesDeg0 = new float[]{
         0, 90, 0, 90};
      anisotropy0 = new float[]{
         1, 1, 1, 1};
      intensity0 = new float[]{1, 1, 1, 1};
      cs.test("opo, 1", anglesDeg0, anisotropy0, intensity0);
   }
   /*
    *  Results:
    Near 90, 1:  meanR= 0.98760366  meanTheta= 86.67822606202601  Std= 0.42733008
    90, 1:  meanR= 1.0  meanTheta= 90.00000265626015  Std= 8.6313015E-8
    0, 1:  meanR= 1.0  meanTheta= 0.0  Std= 0.0
    135, 1:  meanR= 1.0  meanTheta= 135.00000398439022  Std= 1.7167391E-8
    175, 1:  meanR= 1.0000001  meanTheta= 175.00001056164297  Std= 2.706181E-4
    all, 1:  meanR= 1.908218E-8  meanTheta= 115.67124618801981  Std= 1.1892071
    opo, 1:  meanR= 4.371139E-8  meanTheta= 135.00000398439022  Std= 1.0

    */
//==================================================================================
   public static void test3() {
      ///////////////////////
      // case 1: All angles are isotropic, but last one is 12 deg
      float[] orientation = new float[]{
         0,
         45,
         90,
         135,
         0,
         90,
         45,
         135,
         0,
         90,
         135,
         45,
         12};

      float[] anisotropy = new float[]{
         1,
         1,
         1,
         1,
         1,
         1,
         1,
         1,
         1,
         1,
         1,
         1,
         1};

      float[] intensity = new float[]{
         1,
         1,
         1,
         1,
         1,
         1,
         1,
         1,
         1,
         1,
         1,
         1,
         1};

      compute(orientation, anisotropy, intensity);

      // Results:
      // mean Angle	12
      // Anisotropy	0.076923077
      // var   	0.923076923

      ///////////////////////
      // case 2: All angles are isotropic, but anisotropy at 45 degrees dominates
      orientation = new float[]{
         0,
         45,
         90,
         135,
         0,
         90,
         45,
         135,
         0,
         90,
         135,
         45,
         0};

      anisotropy = new float[]{
         0.5f,
         1,
         0.5f,
         0.5f,
         0.5f,
         0.5f,
         1,
         0.5f,
         0.5f,
         0.5f,
         0.5f,
         1,
         0};

      // mean Angle	45
      // Anisotropy	0.115384615
      // var	0.884615385
      compute(orientation, anisotropy, intensity);
      ///////////////////////
      // case 3: Spread of 1 degree around 31.5 degress, anisotropy 0.5
      orientation = new float[]{
         31,
         31.16666667f,
         31.25f,
         31.33333333f,
         31.41666667f,
         31.5f,
         31.58333333f,
         31.66666667f,
         31.75f,
         31.83333333f,
         31.91666667f,
         32,
         0};

      anisotropy = new float[]{
         0.5f,
         0.5f,
         0.5f,
         0.5f,
         0.5f,
         0.5f,
         0.5f,
         0.5f,
         0.5f,
         0.5f,
         0.5f,
         0.5f,
         0};

      // Results:
      // mean Angle	31.5347229 
      // Anisotropy	0.461513253
      // var	0.538486747        
      
      
      compute(orientation, anisotropy, intensity);
      
      showResults();
   }
   
  static List results = new ArrayList<Object[]>();

   private static void compute(float[] orientation, float[] anisotropy, float[] intensity) {
      CircularStatistics cs = new CircularStatistics();
      for (int i = 0; i < orientation.length; i++) {
         orientation[i] = orientation[i]*(float)Math.PI/180f;
      }

      float[] circStat = cs.process(orientation, anisotropy, intensity);
      cs.test("", orientation, anisotropy, intensity);

      DecimalFormat df2 = new DecimalFormat("#.00");
      DecimalFormat df3 = new DecimalFormat("#.000");
      String angleMean = df2.format(circStat[1] * 180 / Math.PI);
      String rMean = df3.format(circStat[0]);
      String iMean = df3.format(circStat[0]);
      

      results.add(
              new Object[]{
                 Arrays.toString(orientation),
                 Arrays.toString(anisotropy),
                 Arrays.toString(intensity),
                 rMean, iMean, angleMean
              });
      
   }
   
   public static void showResults() {
      // Show results in table
      String[] colHeaders = new String[]{
         "#", "Angles", "Anisotropies", "Intensities", "meanR", "meanI", "meanAngle"};
      MathUtils.displayListOfArraysAsTableFrame("Results", colHeaders, results);
   }


}
