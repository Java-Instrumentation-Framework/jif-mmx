package edu.mbl.jif.datasetconvert.job;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import foxtrot.Job;
import foxtrot.Worker;
import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;

/**
 * An example of how to use progress indication with Foxtrot. The main advantage is that there is no
 * more need to create a separate thread for the progressive operation, but just use the Foxtrot
 * API. And, of course, with Foxtrot the GUI can be interrupted in any moment.
 *
 * @version $Revision: 260 $
 */
public class JobMonitorPanel extends JPanel {

   private JButton button;
   JLabel descriptionLabel;
   private JProgressBar bar;
   private boolean running;
   private boolean taskInterrupted;
   private boolean indeterminate;
   private Job theJobToRun;

      
   public void setTheJobToRun(Job theJobToRun, String jobDescription) {
      this.theJobToRun = theJobToRun;
      descriptionLabel.setText(jobDescription);
      button.setEnabled(true);
   }

   
   public JobMonitorPanel() {
//      MigLayout ml = new MigLayout("",
//                                "[pref!][grow,fill]",
//                                "[]15[]");
      MigLayout ml = new MigLayout("","","[]15[]");
      setLayout(ml);
      //setLayout(new MigLayout("", "[grow]", "[grow]"));
      button = new JButton("Run Task !");
      button.setSize(85, 32);
      button.setAlignmentX(CENTER_ALIGNMENT);
      button.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (running) {
               onCancelClicked();
            } else {
               onRunClicked();
            }
         }
      });
      button.setEnabled(false);
      descriptionLabel =  new JLabel("- - -");
      bar = new JProgressBar();
      bar.setStringPainted(true);
      add(descriptionLabel, "width 200:null:null, span, wrap para");
      add(button, "center, wrap");
      add(bar, "center, grow");
      
      
      //add(p);
   }

   void setIndeterminate(boolean b) {
      indeterminate = b;
   }

   private void onRunClicked() {
      if (theJobToRun == null) {
         return;
      }
      // We are running
      running = true;
      // We just started, set the task as not interrupted, to
      // clear any eventual previous status
      setTaskInterrupted(false);
      // We will execute a long operation, change the text signaling
      // that the user can interrupt the operation
      button.setText("Cancel");
      // getData() will block until the heavy operation is finished
      // and the AWT-Swing events will be dequeued and processed
      bar.setString("running...");
      if (indeterminate) {
         bar.setIndeterminate(true);
      }
      boolean successfullyCompleted = runTheJob();
      if (indeterminate) {
         bar.setIndeterminate(false);
      }
      if (successfullyCompleted) {
         bar.setString("done.");
      } else {
         bar.setString("Cancelled.");
      }
      // Restore the button's text
      button.setText("Run Task !");
      // We're not running anymore
      running = false;
      button.setEnabled(false);
      // If was interrupted we get back a null list

   }

   private void onCancelClicked() {
      // Here if we want to interrupt the Task
      setTaskInterrupted(true);
   }

   private boolean runTheJob() {
      if (theJobToRun == null) {
         return false;
      }
      return (Boolean) Worker.post(theJobToRun);
   }

   public void update(final int index, final int max, final String string) {
      // This method is called by the Foxtrot Worker thread, but I want to
      // update the GUI, so I use SwingUtilities.invokeLater, as the Task
      // is not finished yet.

      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            bar.setMaximum(max);
            bar.setValue(index);
            bar.setString(string);
         }
      });
   }

   public synchronized boolean isTaskInterrupted() {
      // Called from the Foxtrot Worker Thread.
      // Must be synchronized, since the variable taskInterrupted is accessed from 2 threads.
      // While it is easier just to change the variable value without synchronizing, it is possible
      // that the Foxtrot worker thread doesn't see the change (it may cache the value of the variable
      // in a registry).
      return taskInterrupted;
   }

   private synchronized void setTaskInterrupted(boolean value) {
      // Called from the AWT Event Dispatch Thread.
      // See comments above on why it must be synchronized.
      taskInterrupted = value;
   }
}
