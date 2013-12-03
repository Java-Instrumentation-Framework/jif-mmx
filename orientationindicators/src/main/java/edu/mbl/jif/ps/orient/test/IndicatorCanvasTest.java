/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mbl.jif.ps.orient.test;

import edu.mbl.jif.ps.orient.Indicator;
import edu.mbl.jif.ps.orient.IndicatorCanvas;
import edu.mbl.jif.ps.orient.OrientationIndicators;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import java.awt.Color;
import java.util.Vector;

/**
 *
 * @author GBH
 */
public class IndicatorCanvasTest {
   
   public void test() {
   

      // launch ImageJ
      new ImageJ();
      // load image stack into ImagePlus
      ImagePlus imp = IJ.openImage("C:\\_Dev\\_Dev_Data\\TestImages\\testData\\PS_Aster\\PS_03_0825_1753_24.tif");
	if (imp!=null) imp.show();
      IndicatorCanvas gc = new IndicatorCanvas(imp, null, 1, 1, 1);
      OrientationIndicators.Type type = OrientationIndicators.Type.ELLIPSE;
      int cellSize = 15;
      Vector<Indicator> indicators = generateIndicators(imp, cellSize, type);
      gc.setIndicators(indicators);  // does the repaint too.

   }

   private Vector<Indicator> generateIndicators(ImagePlus imp, int cellSize, OrientationIndicators.Type type) {
      Vector<Indicator> glyphs = new Vector<Indicator>();
      OrientationIndicators og = new OrientationIndicators();
      int w = imp.getWidth();
      int h = imp.getHeight();
      int nX = w / cellSize;
      int nY = h / cellSize;
      for (int i = 0; i < nX; i++) {
         for (int j = 0; j < nY; j++) {
            glyphs.add(new Indicator(og.createEllipseAt(cellSize*i, cellSize*j,0,10,4), Color.BLUE, null));
         }
      }
      return glyphs;
   }   
   
   
   public static void main(String[] args) {
      new IndicatorCanvasTest().test();
   }
}
