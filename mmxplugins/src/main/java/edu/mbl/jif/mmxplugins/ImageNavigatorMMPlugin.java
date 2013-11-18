package edu.mbl.jif.mmxplugins;

//import edu.mbl.jif.imaging.nav.ImageNavigator;
//import mmcorej.CMMCore;
//import org.micromanager.api.MMPlugin;
//import org.micromanager.api.ScriptInterface;

/**
 * ImageNavigator for viewing images and MM datasets
 * 
 * To install with MicroManager
 * 
 * - Copy .jars from dist/lib to "C:\Micro-Manager-1.4\plugins\Micro-Manager"
 * - Copy ImgNav.jar from dist/ to "C:\Micro-Manager-1.4\mmplugins"
 * - Copy dir config/ to "C:\Micro-Manager-1.4
 * 
 * @author GBH
 * 
 */
public class ImageNavigatorMMPlugin {
//implements MMPlugin {
//	
//	public static final String menuName = "ImageNavigator";
//	public static final String tooltipDescription ="ImageNavigator";
//	//-----------------------------------------------------------
//	// MMPlugin implementation ...
//	// If launched as a MMgr plugin
//	static ScriptInterface app;
//	static CMMCore core;
//	//FakeCore core = new FakeCore();
//
//	@Override
//	public void setApp(ScriptInterface si) {
//		app = si;
//		core = app.getMMCore();
//	}
//	
//      // TODO: add path to open based on MM datapath
//      
//	@Override
//	public void show() {
//         //String openInDir = getPsSessionDir();
//         //ImageNavigator iNav = new ImageNavigator(openInDir, false);
//         ImageNavigator iNav = new ImageNavigator();
//
//	}
//	
//	@Override
//	public void configurationChanged() {
//	}
//	
//	@Override
//	public String getDescription() {
//		return "ImageNavigator";
//	}
//	
//	@Override
//	public String getInfo() {
//		return "ImageNavigator";
//	}
//	
//	@Override
//	public String getVersion() {
//		return "0.8";
//	}
//	
//	@Override
//	public String getCopyright() {
//		return "MBL, 2013";
//	}
//	
//	@Override
//	public void dispose() {
//	}
//      
//      public String getPsSessionDir() {
//   
//
////      polDataLocation = Prefs.get(
////         PolScope.IJ_Key_Prefix_ps + Mode + 
////         PolScope.IJ_Key_Suffix_PolDataLoc, 
////         PolScope.PolDataLocDef); // root level (absolute path)
////
////where;
////IJ_Key_Prefix_ps = "ps.";
////PolScope.Mode.acq.toString() = "acq." or "proc."; // both point to same location so either can be used
////IJ_Key_Suffix_PolDataLoc = "PolDataLoc"; // root level
////IJ_Key_Suffix_userprojChoice = "userprojChoice"; // user
////IJ_Key_Suffix_sessionChoice = "sessionChoice"; // session
////
////PolDataLocDef = System.getProperty("user.home"); // default when 'IJ_Key_Suffix_PolDataLoc' is not defined in IJ-Prefs
//         return null;
//      }
	
}
