/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mbl.jif.mmxplugins;

import edu.mbl.jif.ps.orient.Orientation_Indicators;
import edu.mbl.jif.imaging.dataset.util.DatasetUtils;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import static ij.plugin.filter.PlugInFilter.DOES_16;
import static ij.plugin.filter.PlugInFilter.DOES_8G;
import static ij.plugin.filter.PlugInFilter.STACK_REQUIRED;
import static ij.plugin.filter.PlugInFilter.SUPPORTS_MASKING;
import ij.process.ImageProcessor;

/**
 *
 * @author GBH
 */
public class OrientationIndicatorsPlugin implements PlugInFilter {

   private ImagePlus imagePlus;

   // ImageJ Plugin... 
   @Override  // Plugin
   public int setup(String arg, ImagePlus imp) {
      // TODO - test type... stack or hyperstack?
      // Determine PolScope type: Birefringence, FluorPol, Diattenuation
      //  
      this.imagePlus = imp;
      return DOES_8G + DOES_16 + SUPPORTS_MASKING + STACK_REQUIRED;
   }

   @Override // Plugin
   public void run(ImageProcessor ip) {
      imagePlus = IJ.getImage();
      // Test if it is a MMDataset
      if (!DatasetUtils.isMMDataset(imagePlus)) {
         IJ.showMessage("Error", "Not a Micro-Manager Dataset");
         return;
      }
      new Orientation_Indicators().run(imagePlus);
   }

}
