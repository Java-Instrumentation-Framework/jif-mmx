/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mbl.jif.datasetconvert.job;

import java.awt.BorderLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 *
 * @author GBH
 */

   public class JobRunnerDialog extends JDialog {

      public JobRunnerDialog(JobMonitorPanel jobMon, JFrame owner) {
         super(owner, "Run Task", true);
         add(jobMon, BorderLayout.CENTER);
         // Close button closes the dialog
//         JButton closeButton = new JButton("Close");
//         closeButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent event) {
//               setVisible(false);
//            }
//         });
//         // add Ok button to southern border
//         JPanel panel = new JPanel();
//         panel.add(closeButton, BorderLayout.CENTER);
//         add(panel, BorderLayout.SOUTH);
         
         setLocationRelativeTo(owner);
         //        setSize(250, 150);
         this.pack();
      }
   }