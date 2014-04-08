/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mbl.jif.ps.orient;

import edu.mbl.jif.ps.orient.test.MathUtils;
import edu.mbl.jif.imaging.dataset.viewer.DatasetHyperstack;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;

/**
 *
 * @author GBH
 */
public class Test {
   // ====================================================================================
   // Testing as an ImageJ Plugin... 
   //
   
   
   public static void loadMollys() {
      String rootDir = "C:\\MicroManagerData\\Molly";
      String name = "SM_2013_0814_2012_1_Z_Processed";
      ImagePlus image = new DatasetHyperstack(rootDir, name).createImagePlus();
      image.show();
   }
   public static void loadBrainSlice() {
      String rootDir = "C:/MicroManagerData/project/2048x2048";
      String name = "SM_2012_1126_1447_1";
      ImagePlus image = new DatasetHyperstack(rootDir, name).createImagePlus();
      image.show();
   }
   
   public static void loadCalciteFluor() {
      String rootDir = "C:/MicroManagerData/DarkfieldBiref/2012_09_13_CalciteCrystal40x";
      String name = "SM_2012_0915_0047_1";
      ImagePlus image = new DatasetHyperstack(rootDir, name).createImagePlus();
      image.show();
   }
   public static void loadBlobsFluor() {
      String rootDir = "C:/MicroManagerData/FluorPol_DataSet";
      String name = "SM_2012_0928_1724_1";
      ImagePlus image = new DatasetHyperstack(rootDir, name).createImagePlus();
      image.show();
   }

   public static void loadDatasetXMT15() {
      String rootDir = "C:\\MicroManagerData\\Test\\dataXMT15";
      String name = "SM_2012_1206_1606_1";
      ImagePlus image = new DatasetHyperstack(rootDir, name).createImagePlus();
      image.show();
      // test if we can get the metadata...
//      if (DatasetUtils.isMMDataset(image)) {
//         try {
//            JSONObject sumMD = DatasetUtils.getSummaryMetadata(image);
//            String[] names = SumMetadata.getChannelNames(sumMD);
//            System.out.println(Arrays.toString(names));
//            if (sumMD != null) {
//               String s = SumMetadata.toOrderedString(sumMD);
//               System.out.println(s);
//            }
//         } catch (JSONException ex) {
//         }
//      }
   }

   public static void loadMDimDataset() {
      String rootDir = "C:/MicroManagerData/dataOct13";
      String name = "SMS_2013_1024_1857_12";
      ImagePlus image = new DatasetHyperstack(rootDir, name).createImagePlus();
      image.show();
   }
   public static void loadFluorDataset() {
      String rootDir = "C:/MicroManagerData/Samples/Fluorescence/MDCK_Cell";
      String name = "SM_MDCK_Cell_10-07-06_02-38";
      ImagePlus image = new DatasetHyperstack(rootDir, name).createImagePlus();
      image.show();
   }
   public static void loadDiattenDataset() {
      String rootDir = "C:/MicroManagerData/Samples/Diattenuation/CalibrationQuadSlide";
      String name = "SM_2013_0426_1454_2";
      ImagePlus image = new DatasetHyperstack(rootDir, name).createImagePlus();
      image.show();
   }
   public static void loadBirefrDataset() {
      String rootDir = "C:/MicroManagerData/Samples/Birefringence/Spermatocyte";
      String name = "SM_PS_05_0519_1450_57";
      ImagePlus image = new DatasetHyperstack(rootDir, name).createImagePlus();
      image.show();
   }
   public static void loadBirefrMultiDim() {
      String rootDir = "C:/MicroManagerData/project/testdata/2012_08_27";
      String name = "SMT_2012_0827_1643_1";
      ImagePlus image = new DatasetHyperstack(rootDir, name).createImagePlus();
      image.show();
   }

   public static void loadIJHyperstack() {
      String file = "C:/MicroManagerData/dataOct13/SMS_2013_1024_1857_12/SMS_2013_1024_1857_12_MMStack_Pos0.ome.tif";
      ImagePlus image = IJ.openImage(file);
      image.show();
   }

   public static void loadNonPolStack() {
      String file = "C:/_Dev/_Dev_Data/TestImages/testData/Z1_T10.tif";
      ImagePlus image = IJ.openImage(file);
      image.show();
   }
      public static void loadOldFluorDataset() {
      String rootDir = "C:/MicroManagerData/Samples/GFP_Crystals/2012_07_11";
      String name = "SM_2012_0711_1749_1";
      ImagePlus image = new DatasetHyperstack(rootDir, name).createImagePlus();
      image.show();
   }
   
   public static void testAsImageJPlugin(Class<?> clazz) {
      String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
      String pluginsDir = url.substring(5, url.length() - clazz.getName().length() - 6);
      System.setProperty("plugins.dir", pluginsDir);
      // start ImageJ
      ImageJ imagej = new ImageJ();

//      loadDatasetXMT15();
//      loadDataset2048();
//      loadIJHyperstack();
 //    loadMDimDataset();
//      loadNonPolStack();
      loadFluorDataset();
//      loadDiattenDataset();
//      loadBirefrDataset();
//      loadOldFluorDataset();
//      loadCalciteFluor();
//      loadBlobsFluor();
//      loadBrainSlice();
//      loadBirefrMultiDim();
//      loadMollys();
      // run the plugin
      try {
         Thread.sleep(500);
      } catch (InterruptedException ex) {
      }
      //IJ.runPlugIn(clazz.getName(), "");
      // testDisplayImageFloat();
   }

   public static void testDisplayImageFloat() {
      int wid = 50;
      int ht = 50;
      float max = 1.0f;
      int len = wid * ht;
      double scale = max / (float) len;
      float[] data = new float[wid * ht];
      for (int i = 0; i < len; i++) {
         data[i] = -max + 2 * (float) ((float) i * (float) scale);
      }
      ij.ImagePlus ip = MathUtils.createPosNegImagePlus(wid, ht, data);
      ip.show();
      ij.ImagePlus ip2 = MathUtils.createImagePlus(wid, ht, data);
      ip2.show();
      for (int i = 0; i < len; i++) {
         data[i] = -max + 2 * (float) ((float) Math.random() * (float) max);
      }
      ij.ImagePlus ip3 = MathUtils.createPosNegImagePlus(wid, ht, data);
      ip3.show();
   }

   /**
    * Test as ImageJ Plugin
    *
    * For debugging, it is convenient to have a method that starts ImageJ, loads an image and calls
    * the plugin, e.g. after setting breakpoints.
    *
    */
   static Class<?> clazz;
   
   public static void main(String[] args) {
      // set the plugins.dir property to make the plugin appear in the Plugins menu
      //Class<?> clazz = edu.mbl.jif.ps.plugins.Orientation_Indicators.class;
      clazz = OrientationIndicatorsPlugin.class;
      testAsImageJPlugin(clazz);
      JFrame runPluginFrame = new QuickFrame("IJPluginTest");
      JButton runButton = new JButton("Re-run Plugin");
      runButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
                  IJ.runPlugIn(clazz.getName(), "");
         }
      });
      runPluginFrame.add(runButton);
      runPluginFrame.pack();
      runPluginFrame.setVisible(true);
   }
      public static class QuickFrame extends JFrame {

      public QuickFrame(String title) {
         super(title);
         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         setSize(640, 480);
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         setLocation(
                 Math.max(0, screenSize.width / 2 - getWidth() / 2),
                 Math.max(0, screenSize.height / 2 - getHeight() / 2));
      }
   }
}
