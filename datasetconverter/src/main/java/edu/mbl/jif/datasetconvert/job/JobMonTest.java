/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mbl.jif.datasetconvert.job;

import edu.mbl.jif.datasetconvert.job.JobRunnerDialog;
import foxtrot.Job;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author GBH
 */
public class JobMonTest {

   public void test(JFrame frame) {
      final JobMonitorPanel jobMon = new JobMonitorPanel();
      Job theJob = new Job() {
         public Object run() {
            StringBuffer buffer = new StringBuffer();
            // A repetitive operation that updates a progress bar.
            // Determine how many steps to do
            int max = 20;
            int done = 0;
            for (int i = 1; i <= max; ++i) {
               // Do the task
               // Simulate a heavy operation to retrieve data
               try {
                  Thread.sleep(250);
               } catch (InterruptedException ignored) {
               }
               done++;
               // Prepare the progress bar string
               buffer.setLength(0);
               buffer.append("Step ").append(i).append(" of ").append(max);
               // Update the progress bar
               if (jobMon.isTaskInterrupted()) {
                  buffer.append(" - Interrupted !");
                  jobMon.update(i, max, buffer.toString());
                  break;
               } else {
                  jobMon.update(i, max, buffer.toString());
               }
            }
            // either done or cancelled...
            if (jobMon.isTaskInterrupted()) {
               // cleanup
               System.out.println("Process was interupted... time to clean-up");
               return false;
            } else {
               return true;
            }
         }
      };
      String jobDescription =
              "<html>Description of the task<p>"
              + "And another line</html>";
      jobMon.setTheJobToRun(theJob, jobDescription);
      //jobMon.setPreferredSize(new Dimension(300, 200));
      final JobRunnerDialog dialog = new JobRunnerDialog(jobMon, frame);
      dialog.setVisible(true); // pop up dialog
   }

   public void testIndeterminate(JFrame frame) {
      final JobMonitorPanel jobMon = new JobMonitorPanel();
      jobMon.setIndeterminate(true);
      Job theJob = new Job() {
         public Object run() {
            int max = 10;
            for (int i = 1; i <= max; ++i) {
               // <<<<<<<<<<<<
               // Do a step in the task 
               {// 
                  // Simulate a heavy operation to retrieve data
                  try {
                     Thread.sleep(250);
                  } catch (InterruptedException ignored) { }
               }
               // >>>>>>>>>>>>
               if (jobMon.isTaskInterrupted()) {
                  break;
               }
            }
            // either done or cancelled...
            if (jobMon.isTaskInterrupted()) {
               // cleanup
               System.out.println("Process was interupted... time to clean-up");
               return false;
            } else {
               return true;
            }
         }
      };
      String jobDescription =
              "<html><h3><i>Task to Run</i></h3><hr>Description of the task<p>"
              + "And another line</html>";
      jobMon.setTheJobToRun(theJob, jobDescription);
      //jobMon.setPreferredSize(new Dimension(300, 200));
      final JobRunnerDialog dialog = new JobRunnerDialog(jobMon, frame);
      dialog.setVisible(true); // pop up dialog

   }

   // Test ===============================================
   public static void main(String[] args) {
      final QuickFrame f = new QuickFrame("");
      JButton buttonTest = new JButton("test");
      buttonTest.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            new JobMonTest().test(f);
         }
      });
      f.add(buttonTest, BorderLayout.NORTH);
      JButton buttonTestIndeterminate = new JButton("test Indeterminate");
      buttonTestIndeterminate.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            new JobMonTest().testIndeterminate(f);
         }
      });
      f.add(buttonTestIndeterminate, BorderLayout.SOUTH);
      //f.add(jobMon, BorderLayout.CENTER);
      f.setVisible(true);

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
