package edu.mbl.jif.ps.orient.io;

import edu.mbl.jif.imaging.dataset.viewer.DatasetHyperstack;
import ij.io.OpenDialog;
import java.io.File;

/**
 * Micro-Manager Dataset to hyperstack ImageJ plugin
 *
 * @author GBH
 */
/**
 *
 * @author GBH
 */
public class MMgr_DatasetOpener implements ij.plugin.PlugIn {

   private static String defaultDirectory = null;

   public void run(String arg) {
      OpenDialog od = new OpenDialog("Open Micro-Manager Dataset...", arg);
      String directory = od.getDirectory();
      String fileName = od.getFileName();
      if (fileName == null) {
         return;
      }
      
      File file = new File(directory + "/" + fileName);
      file = file.getParentFile();
      String rootDir = file.getAbsolutePath();
      String name = file.getName();
      rootDir = rootDir.substring(0, rootDir.length() - (name.length() + 1));
      new DatasetHyperstack(rootDir, name).createImagePlus().show();
   }
}
