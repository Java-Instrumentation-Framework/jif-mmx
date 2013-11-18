package edu.mbl.jif.mmxplugins;

import edu.mbl.jif.mfmconverter.MfmZExpander;

/**
 *  MFM_Zexpander ImageJ plugin
 * @author GBH
 */

public class MFM_Zexpander implements ij.plugin.PlugIn {
   
   public void run(String arg) {
      try {
         MfmZExpander.main(null);
      } catch (Exception ex) {
         
      }
   }
   
}


