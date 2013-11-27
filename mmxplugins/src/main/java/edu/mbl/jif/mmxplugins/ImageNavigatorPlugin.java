/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mbl.jif.mmxplugins;

import edu.mbl.jif.imaging.nav.ImageNavigator;
import ij.IJ;
import ij.plugin.PlugIn;
import javax.swing.SwingUtilities;

/**
 *
 * @author GBH
 */
public class ImageNavigatorPlugin implements PlugIn {

   public void run(String arg) {
      // TODO: pass in path...
      
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               final String pathStart = IJ.getDirectory("default");
               //final String pathStart = "C:/MicroManagerData/";
               ImageNavigator explorer = new ImageNavigator(true,pathStart, true);
               Thread.currentThread().setContextClassLoader(IJ.getClassLoader());
            }
         });
   }
   
}
