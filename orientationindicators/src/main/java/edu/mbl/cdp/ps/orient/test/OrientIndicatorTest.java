
package edu.mbl.cdp.ps.orient.test;

import edu.mbl.cdp.ps.orient.Orientation_Indicators;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;

/**
 *
 * @author GBH
 */
public class OrientIndicatorTest {

   public void runTest() {
      //System.out.println(System.getProperty("java.class.path"));
      Orientation_Indicators ol = new Orientation_Indicators();
            new ImageJ();
      // load image stack into ImagePlus
      ImagePlus imp = IJ.openImage("C:\\_Dev\\_Dev_Data\\TestImages\\testData\\PS_Aster\\PS_03_0825_1753_24.tif");
	if (imp!=null) imp.show();
      //imp.setRoi(100,100, 300, 399);
//      GlyphCanvas gc = new GlyphCanvas(imp, null, 1, 1, 1);
//      OrientationGlyphs.Type type = OrientationGlyphs.Type.ELLIPSE;
//      int cellSize = 15;
      //Vector<Glyph> glyphs = generateGlyphs(imp, cellSize, type);
      //gc.setGlyphs(glyphs);  // does the repaint too.
      int timePoint = 0;
      ol.updateIndicators(imp, timePoint);
      
      
   }
   
   public static void main(String[] args) {
      new OrientIndicatorTest().runTest();
   }

}
