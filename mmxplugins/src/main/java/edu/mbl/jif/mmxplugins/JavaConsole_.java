package edu.mbl.jif.mmxplugins;

import ij.*;
import ij.plugin.PlugIn;
import javax.swing.UIManager;

public class JavaConsole_
    implements PlugIn {

  public void run(String arg) {
    try {
      UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    ConsoleJava jConsole = new ConsoleJava();

  }

  void showAbout() {
    IJ.showMessage("");
  }
}
