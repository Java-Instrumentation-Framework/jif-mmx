package edu.mbl.jif.mfmconverter;

import edu.mbl.jif.gui.file.PathChooserSourceDestination;
import edu.mbl.jif.gui.file.FileChooserPanel;
import edu.mbl.jif.datasetconvert.job.JobMonitorPanel;
import edu.mbl.jif.datasetconvert.job.JobRunnerDialog;
import edu.mbl.jif.gui.DialogBox;
import edu.mbl.jif.utils.FileUtils;
import edu.mbl.jif.utils.Prefs;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import net.miginfocom.swing.MigLayout;

// TODO Allow source to be a file or directory.
// 
public class MfmZExpander {

   JLabel pmLabel = new JLabel("");
   JobMonitorPanel jobMon;
   QuickFrame f;
   private final String instructions
           = "<html>MultiFocusMicroscope Z-Section Expander: <p>"
           + "Choose source and destination folders <p>"
           + "and the coordinates file.  (Assumes NxN)</html>";

   public MfmZExpander() {
      f = new QuickFrame("MultiFocusMicroscope Z-Section Expander");
      f.getContentPane().setLayout(new MigLayout("", "[grow]", "[grow]"));

      f.add(new JLabel(instructions), "span 2, wrap");
      final PathChooserSourceDestination dirChoos
              = new PathChooserSourceDestination(
                      Prefs.get("sourceDir", null), Prefs.get("destDir", null));
      f.add(dirChoos, "wrap");
      //
      final FileChooserPanel fileChoos
              = new FileChooserPanel("Coord File", Prefs.get("coordFile", "."), "coord", 
                      FileChooserPanel.Type.OPEN);
      f.add(fileChoos, "wrap");
      //
      JButton goButton = new JButton("Go");
      goButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            String source = dirChoos.getSourcePath();
            String dest = dirChoos.getDestPath();
            Prefs.put("sourceDir", source);
            Prefs.put("destDir", dest);
            String subImageCoordFile = fileChoos.getFilePath();
            Prefs.put("coordFile", subImageCoordFile);
            if (FileUtils.checkExists(source, "Source ") && 
                    FileUtils.checkExists(dest, "Destination ") && 
                    FileUtils.checkFileExists(subImageCoordFile, "Coordinates ")) {
               runConversion(source, dest, subImageCoordFile);
            }
         }
      });

      goButton.setMaximumSize(new Dimension(50, 32));
      goButton.setMinimumSize(new Dimension(50, 32));
      f.add(goButton);
      f.pack();
      f.setVisible(true);
   }



   public void runConversion(final String source, final String dest, final String subImageCoordFile) {

      Runnable rnbl = new Runnable() {
         public void run() {
            File sourceF = new File(source);
            File destF = new File(dest);
            String dir = sourceF.getParent();
            String prefixIn = sourceF.getName();
            String outDir = destF.getAbsolutePath();
            // String outPrefix = sourceF.getName();
            String outPrefix = prefixIn + "_Z";
            //
            Rectangle[] subImageRectangles = CoordinatesFileReader.readPointsFromFile(
                    subImageCoordFile);

            if (subImageRectangles == null) {
               DialogBox.boxError("Error", "Unable to read coordinate file.");
               return;
            }
            int numSubImgsHorizontal = (int) Math.sqrt(subImageRectangles.length);
            int numSubImgsVertical = (int) Math.sqrt(subImageRectangles.length);
            //printRects(subImageRectangles);
            //System.out.println("");
            //
            jobMon = new JobMonitorPanel();
            ConvertMultiFocusToZSections cmfzs = new ConvertMultiFocusToZSections(
                    jobMon,
                    dir, prefixIn, outDir, outPrefix,
                    numSubImgsHorizontal, numSubImgsVertical, subImageRectangles);
            //
            String jobDescription
                    = "<html>Expand MFM Z-sections: <p>"
                    + "Sections " + numSubImgsHorizontal + " X " + numSubImgsVertical
                    + "<p>From: " + dir + "/" + prefixIn + "<p>"
                    + "To: " + outDir + "<p>"
                    + "</html>";
            //
            jobMon.setTheJobToRun(cmfzs, jobDescription);
            final JobRunnerDialog dialog = new JobRunnerDialog(jobMon, f);
            dialog.setVisible(true); // pop up dialog
         }
      };
      rnbl.run();

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
      new MfmZExpander();

   }

   // OLD...
//   public static Rectangle[] pointsToRects() {
//      //
//      int Right = 80;
//      int Left = 290 - Right;
//      int Down = 200;
//      int Up = 290 - Down;
//      // rawMFMimage( 
//      //  c(slice,1)-Left:c(slice,1)+Right);
//      //  c(slice,2)-Up:c(slice,2)+Down, 
//      Point[] points = new Point[]{
//         new Point(216, 162),
//         new Point(516, 165),
//         new Point(815, 166),
//         new Point(215, 463),
//         new Point(514, 465),
//         new Point(813, 466),
//         new Point(213, 762),
//         new Point(512, 764),
//         new Point(811, 766)};
//      Rectangle[] subImageRect = new Rectangle[points.length];
//      for (int i = 0; i < points.length; i++) {
//         Point point = points[i];
//         int x0 = point.x - Left;
//         int x1 = point.x + Right;
//         int y0 = point.y - Up;
//         int y1 = point.y + Down;
//         subImageRect[i] = new Rectangle(x0, y0, x1 - x0, y1 - y0);
//      }
////      printRects(subImageRect);
//      return subImageRect;
//   }
   private static void printRects(Rectangle[] subImageRect) {
      for (int i = 0; i < subImageRect.length; i++) {
         System.out.println("" + subImageRect[i].toString());
      }
   }

   public static void runTest() {
//      String pathIn = "G:/data/SMS_2013_0823_0017_1";
//      String pathOut = "G:/data/Output";
//      MfmZExpander app = new MfmZExpander();
//      app.runConversion(pathIn, pathOut);

   }

   // TODO Persist frame bounds.
   public static class QuickFrame extends JFrame {

      public QuickFrame(String title) {
         super(title);
         setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
         setSize(640, 480);
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         setLocation(
                 Math.max(0, screenSize.width / 2 - getWidth() / 2),
                 Math.max(0, screenSize.height / 2 - getHeight() / 2));
      }
   }
}
