package mymmplugin;

import mmcorej.CMMCore;
import org.micromanager.api.MMPlugin;
import org.micromanager.api.ScriptInterface;

public class MyMmPlugin implements MMPlugin {
   // Micro-Manager Plugin template

   public static final String menuName = "_Plugin_";
   public static final String tooltipDescription = "_Description of the plugin_";
   //-----------------------------------------------------------
   static ScriptInterface app;
   static CMMCore core;

   @Override
   public void setApp(ScriptInterface si) {
      app = si;
      core = app.getMMCore();
   }

   @Override
   public void show() {
   }

   @Override
   public void configurationChanged() {
   }

   @Override
   public String getDescription() {
      return "";
   }

   @Override
   public String getInfo() {
      return "_Plug info_";
   }

   @Override
   public String getVersion() {
      return "1.0";
   }

   @Override
   public String getCopyright() {
      return "2012";
   }

   @Override
   public void dispose() {
   }
}
