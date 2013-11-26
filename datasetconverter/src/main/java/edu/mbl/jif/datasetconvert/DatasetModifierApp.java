
package edu.mbl.jif.datasetconvert;

import edu.mbl.jif.gui.file.PathChooserSourceDestination;
import edu.mbl.jif.job.JobMonitorPanel;
import edu.mbl.jif.mfmconverter.*;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/*
 * TODO: Prefs...
 * Creates a cropped copy of a Micro-Manager dataset based on an input ROI
 * 
"Convert between Single- and Multi-pageTiff format"
"Extract Sub-Image (Crop)"
"Extract Subset (z,t,p)"

 * @Author: Grant B. Harris
 * 
 */
public class DatasetModifierApp {

   JLabel pmLabel = new JLabel("");
   JobMonitorPanel jobMon;
   private final String instructions =
           "Dataset Cropper generates a new cropped copy of a dataset.\n"
           + "Select the source dataset and the distination folder.\n"
           + "Then input the ROI rectangle and press 'Go' button.";

   static {
   }

   public DatasetModifierApp() {
      QuickFrame f = new QuickFrame("Micro-Manager Dataset Modifier");
      f.getContentPane().setLayout(new BoxLayout(f.getContentPane(), BoxLayout.Y_AXIS));
      f.add(Box.createVerticalStrut(5));
      JTextArea instructionsArea = new JTextArea();
      instructionsArea.setText(instructions);
      instructionsArea.setEditable(false);
      instructionsArea.setFocusable(false);
      f.add(instructionsArea);
      f.add(Box.createVerticalStrut(5));
      final PathChooserSourceDestination dirChoos = new PathChooserSourceDestination(null, null);
      f.add(dirChoos);
      Box box = Box.createVerticalBox();
      box.add(Box.createVerticalStrut(5));
      JCheckBox extractCheckBox = new JCheckBox("Extract specific channels, sections, or time points");
      extractCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
      box.add(extractCheckBox);
      //box.add(Box.createVerticalStrut(5));
      JCheckBox cropCheckBox = new JCheckBox("Crop images");
      cropCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
      box.add(cropCheckBox);
      f.add(box);
      final RectangleInputPanel rectInPanel = new RectangleInputPanel();
      rectInPanel.setMaximumSize(new Dimension(300, 24));
      rectInPanel.setMinimumSize(new Dimension(300, 24));   
      f.add(rectInPanel);
      f.add(Box.createVerticalStrut(5));
      JCheckBox multipageCheckBox = new JCheckBox("Save as multi-page OME-Tiff format");
      multipageCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
      f.add(multipageCheckBox);
      f.add(Box.createVerticalStrut(5));

      JButton goButton = new JButton("Go");
      goButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            String source = dirChoos.getSourcePath();
            String dest = dirChoos.getDestPath();
            Rectangle roi = rectInPanel.getRectangle();
            if ((new File(source)).exists() && (new File(dest)).exists()) {
               System.out.println("From: " + source);
               System.out.println("  to: " + dest);
               runConversion(source, dest, roi);
            } else {
               System.err.println("Error");
            }
         }
      });
      jobMon = new JobMonitorPanel();
      f.add(jobMon);
      jobMon.setPreferredSize(new Dimension(300, 96));
      f.add(Box.createVerticalStrut(5));
      //f.pack();
      f.setVisible(true);
   }

   public static void main(String[] args)
           throws Exception {
      try {
         for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
               UIManager.setLookAndFeel(info.getClassName());
               break;
            }
         }
      } catch (Exception e) {
         // If Nimbus is not available, you can set the GUI to another look and feel.
      }
      new DatasetModifierApp();
      //runTest();

   }

   public void runConversion(final String source, final String dest, final Rectangle roi) {

      Runnable rnbl = new Runnable() {
         public void run() {
            File sourceF = new File(source);
            File destF = new File(dest);
            String dir = sourceF.getParent();
            String prefixIn = sourceF.getName();
            String outDir = destF.getAbsolutePath();
            // String outPrefix = sourceF.getName();
            String outPrefix = prefixIn + "_crop";
            DatasetCropper cropper = new DatasetCropper(
                    jobMon,
                    dir, prefixIn, outDir, outPrefix,
                    roi, true, true, true);
            String jobDescription =
                    "<html><h3><i>Task to Run</i></h3><hr>Description of the task<p>"
                    + "And another line</html>";
            jobMon.setTheJobToRun(cropper, jobDescription);
         }
      };
      rnbl.run();

   }

   public static void runTest() {
//      String pathIn = "G:/data/SMS_2013_0823_0017_1";
//      String pathOut = "G:/data/Output";
//      MfmZExpander app = new MfmZExpander();
//      app.runConversion(pathIn, pathOut);

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
