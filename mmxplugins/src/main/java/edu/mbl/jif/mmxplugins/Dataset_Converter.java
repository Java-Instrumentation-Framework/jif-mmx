package edu.mbl.jif.mmxplugins;

import edu.mbl.jif.datasetconvert.DatasetCropperApp;
import javax.swing.UIManager;

/**
 * MFM_Zexpander ImageJ plugin
 *
 * @author GBH
 */
public class Dataset_Converter implements ij.plugin.PlugIn {

   public void run(String arg) {
      try {
         for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
               UIManager.setLookAndFeel(info.getClassName());
               break;
            }
         }
      } catch (Exception e) {
         // If Nimbus is not available, you can set the GUI to another look and feel.
      }
      new DatasetCropperApp();

   }

}
