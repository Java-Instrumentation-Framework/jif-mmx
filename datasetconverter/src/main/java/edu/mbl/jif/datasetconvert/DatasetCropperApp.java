package edu.mbl.jif.datasetconvert;

import edu.mbl.jif.gui.file.PathChooserSourceDestination;
import edu.mbl.jif.datasetconvert.job.JobMonitorPanel;
import edu.mbl.jif.datasetconvert.job.JobMonTest;
import edu.mbl.jif.datasetconvert.job.JobRunnerDialog;
import edu.mbl.jif.mfmconverter.*;
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
import net.miginfocom.swing.MigLayout;

/*
 * TODO: Prefs...
 * Creates a cropped copy of a Micro-Manager dataset based on an input ROI
 * 
 * @Author: Grant B. Harris
 * 
 */
public class DatasetCropperApp {

   JLabel pmLabel = new JLabel("");
   JobMonitorPanel jobMon;
   QuickFrame f;
   private final String instructions =
           "Dataset Cropper generates a new cropped copy of a dataset.\n"
           + "Select the source dataset and the distination folder.\n"
           + "Then input the ROI rectangle and press 'Go' button.";

   public DatasetCropperApp() {
      f = new QuickFrame("Micro-Manager Dataset Cropper");
      f.getContentPane().setLayout(new MigLayout("", "[grow]", "[grow]"));
      JTextArea instructionsArea = new JTextArea();
      instructionsArea.setText(instructions);
      instructionsArea.setEditable(false);
      instructionsArea.setFocusable(false);
      //f.add(instructionsArea, "gap para, span 2, wrap");
      f.add(instructionsArea, "span 2, wrap");
      
      final PathChooserSourceDestination dirChoos = new PathChooserSourceDestination(null,null);
      f.add(dirChoos, "wrap");
      JLabel inputRoiLabel = new JLabel("Crop to ROI:");
      f.add(inputRoiLabel, "gapbefore 10, wrap");
      final RectangleInputPanel rectInPanel = new RectangleInputPanel();
      f.add(rectInPanel, "gapbefore 10, wrap");
      JCheckBox multipageCheckBox = new JCheckBox("Save in multi-page OME-Tiff format");
      f.add(multipageCheckBox, "gapbefore 20, wrap");
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
      goButton.setMaximumSize(new Dimension(50, 32));
      goButton.setMinimumSize(new Dimension(50, 32));
      f.add(goButton, "wrap");
      //f.add(jobMon, "wrap");
      //jobMon.setPreferredSize(new Dimension(300, 96));
      f.pack();
      f.setVisible(true);
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
                    "<html>Crop the dataset to: <p>"
                    + "x: " + roi.x + ", y: " + roi.y + 
                    ", width: " + roi.width + ", height: " + roi.height + "</html>";
            jobMon = new JobMonitorPanel();
            jobMon.setTheJobToRun(cropper, jobDescription);
            final JobRunnerDialog dialog = new JobRunnerDialog(jobMon, f);
            dialog.setVisible(true); // pop up dialog
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
      new DatasetCropperApp();
      //runTest();

   }
   
   public static class QuickFrame extends JFrame {

      public QuickFrame(String title) {
         super(title);
         //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         setSize(640, 480);
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         setLocation(
                 Math.max(0, screenSize.width / 2 - getWidth() / 2),
                 Math.max(0, screenSize.height / 2 - getHeight() / 2));
      }
   }
}
