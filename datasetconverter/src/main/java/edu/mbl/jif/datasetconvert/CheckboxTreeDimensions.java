/*
 * Copyright 2007-2010 Enrico Boldrini, Lorenzo Bigagli This file is part of
 * CheckboxTree. CheckboxTree is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. CheckboxTree is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with CheckboxTree; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA
 */
package edu.mbl.jif.datasetconvert;

import edu.mbl.jif.imaging.dataset.metadata.DimensionalExtents;
import edu.mbl.jif.imaging.dataset.metadata.ImageAttributes;
import edu.mbl.jif.imaging.dataset.metadata.SumMetadata;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingEvent;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingListener;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingModel;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.json.JSONObject;

/**
 * A simple example of the CheckboxTree with key listeners: 'a' for add a node 'r' for remove
 * 'SPACE' toggles the checking of the given node.
 *
 * @author bigagli
 * @author boldrini
 */
public class CheckboxTreeDimensions {

   private CheckboxTree checkboxTree = null;
   //private javax.swing.JPanel jContentPane = null;
   private final JSONObject sumMD;

   /**
    * This is the default constructor
    */
   public CheckboxTreeDimensions(JSONObject sumMD) {
      this.sumMD = sumMD;
   }

   /**
    * Initialize the tree.
    *
    */
   private JScrollPane getCheckboxTree() {
      if (this.checkboxTree == null) {
         this.checkboxTree = new CheckboxTree(getDimensionsTreeModel(sumMD));
         //this.checkboxTree.addKeyListener(new RefreshListener());

         System.out.println(this.checkboxTree.toString());

         this.checkboxTree.getCheckingModel().setCheckingMode(TreeCheckingModel.CheckingMode.PROPAGATE);
         this.checkboxTree.setRootVisible(true);
         this.checkboxTree.setEnabled(true);
         this.checkboxTree.expandAll();

         DefaultMutableTreeNode mn = (DefaultMutableTreeNode) this.checkboxTree.getModel().getRoot();
//         mn = (DefaultMutableTreeNode) mn.getChildAt(2);
//         mn = (DefaultMutableTreeNode) mn.getChildAt(2);

//         System.out.println("row number: " + this.checkboxTree.getRowForPath(new TreePath(mn.getPath())));

         this.checkboxTree.addCheckingPath(new TreePath(mn.getPath()));

         this.checkboxTree.addTreeCheckingListener(new TreeCheckingListener() {
            public void valueChanged(TreeCheckingEvent e) {
               System.out.println("checking set changed, leading path: "
                       + ((TreeNode) e.getPath().getLastPathComponent()).toString());
               System.out.println("checking roots: ");
               TreePath[] cr = CheckboxTreeDimensions.this.checkboxTree.getCheckingRoots();
               for (TreePath path : cr) {
                  System.out.println(path.getLastPathComponent());
               }
               System.out.println("\nPaths: ");
               TreePath[] cp = CheckboxTreeDimensions.this.checkboxTree.getCheckingPaths();
               for (TreePath path : cp) {
                  System.out.println(path.toString());
               }
            }
         });
      }
      return new JScrollPane(this.checkboxTree);
   }
   String[] channelNamesOriginal;
   int[] channels;
   int[] slices;
   int[] frames;
   int[] positions;

   public void getSelectedIndices() {
//      channels = new int[]{0, 1, 2, 3, 4, 5, 6};
//      slices = new int[]{0};
//      frames = new int[]{0, 2};
//      positions = new int[]{0};

      List listChannels = new ArrayList();
      List listSlices = new ArrayList();
      List listFrames = new ArrayList();
      List listPositions = new ArrayList();

      // Check if root "All" selected, if so, not need to go further
      TreePath[] cr = CheckboxTreeDimensions.this.checkboxTree.getCheckingRoots();
      for (TreePath path : cr) {
         System.out.println(path.getLastPathComponent());
      }
      System.out.println("\nPaths: ");
      TreePath[] cp = CheckboxTreeDimensions.this.checkboxTree.getCheckingPaths();
      for (TreePath path : cp) {
         //Object[] pathObjs = path.getPath();
         System.out.println(path.toString());
         if (path.getPathCount() == 3) {
            DefaultMutableTreeNode nodeDim = (DefaultMutableTreeNode) path.getPathComponent(1);
            String dimension = (String) nodeDim.getUserObject();
            DefaultMutableTreeNode nodeIndex = (DefaultMutableTreeNode) path.getPathComponent(2);
            Object uo = nodeIndex.getUserObject();
            if (dimension.equalsIgnoreCase("Channel")) {
               String chan = (String) nodeIndex.getUserObject();
               listChannels.add(chan);
            }
            if (dimension.equalsIgnoreCase("Z-Section")) {
               int index = (Integer) nodeIndex.getUserObject();
               listSlices.add(index);
            }
            if (dimension.equalsIgnoreCase("TimePoint")) {
               int index = (Integer) nodeIndex.getUserObject();
               listFrames.add(index);
            }
            if (dimension.equalsIgnoreCase("Position")) {
               int index = (Integer) nodeIndex.getUserObject();
               listPositions.add(index);
            }
         }
      }
      // sort the list
      Collections.sort(listChannels);
      Collections.sort(listSlices);
      Collections.sort(listFrames);
      Collections.sort(listPositions);



      System.out.println("\nChannels: ");
      Iterator<Object> it = listChannels.iterator();
      while (it.hasNext()) {
         Object obj = it.next();
         System.out.print("  " + obj);
      }
      // Change list of channel names to an array of channel indices
      System.out.println("\nChannel Indices: ");
      Iterator<Object> itc = listChannels.iterator();
      channels = new int[listChannels.size()];
      int c = 0;
      while (itc.hasNext()) {
         Object obj = itc.next();
         String channelName = (String) obj;
         int index = indexOfChannel(channelNamesOriginal, channelName, 0);
         channels[c] = index;
         c++;
         System.out.print("  " + channelName + " ::: " + index);
      }


//      String[] strArray = {"abcd", "abdc", "bcda"};
//for (String s : strArray)
//    if (s.startsWith(searchTerm))
//        System.out.println(s);
// Swap startsWith for contains you wish to simply look for containment.

      System.out.println("\nSlices: ");
      it = listSlices.iterator();
      slices = new int[listSlices.size()];
      c = 0;
      while (it.hasNext()) {
         Object obj = it.next();
         int index = (Integer) obj;
         slices[c] = index;
         c++;
         System.out.print("  " + obj);
      }


      System.out.println("\nFrames: ");
      it = listFrames.iterator();
      frames = new int[listFrames.size()];
      c = 0;
      while (it.hasNext()) {
         Object obj = it.next();
         int index = (Integer) obj;
         frames[c] = index;
         c++;
         System.out.print("  " + obj);
      }

      System.out.println("\nPositions: ");
      it = listPositions.iterator();
      positions = new int[listPositions.size()];
      c = 0;
      while (it.hasNext()) {
         Object obj = it.next();
         int index = (Integer) obj;
         positions[c] = index;
         c++;
         System.out.print("  " + obj);
      }

      if (channels.length < 1) {
         promptForNoChoice("Channel");
      }
      System.out.println(Arrays.toString(channels));
      System.out.println(Arrays.toString(slices));
      System.out.println(Arrays.toString(frames));
      System.out.println(Arrays.toString(positions));
      //CheckboxTreeExample_1.this.checkboxTree.;
   }

   public static int indexOfChannel(Object[] array, Object objectToFind, int startIndex) {
      if (array == null) {
         return -1;
      }
      if (startIndex < 0) {
         startIndex = 0;
      }
      if (objectToFind == null) {
         for (int i = startIndex; i < array.length; i++) {
            if (array[i] == null) {
               return i;
            }
         }
      } else {
         for (int i = startIndex; i < array.length; i++) {
            if (objectToFind.equals(array[i])) {
               return i;
            }
         }
      }
      return -1;
   }

   protected TreeModel getDimensionsTreeModel(JSONObject sumMD_In) {
      try {
         DefaultMutableTreeNode root = new DefaultMutableTreeNode("All");
         DefaultMutableTreeNode parent;
         DimensionalExtents dsdIn = SumMetadata.getDimensionalExtents(sumMD_In);
         parent = new DefaultMutableTreeNode("Channel");
         root.add(parent);
         channelNamesOriginal = SumMetadata.getChannelNames(sumMD_In);
         for (int chan = 0; chan < dsdIn.numChannels; chan++) {
            parent.add(new DefaultMutableTreeNode(channelNamesOriginal[chan]));
         }
         if (dsdIn.numSlices > 1) {
            parent = new DefaultMutableTreeNode("Z-Section");
            root.add(parent);
            for (int slice = 0; slice < dsdIn.numSlices; slice++) {
               parent.add(new DefaultMutableTreeNode(slice));
            }
         }
         if (dsdIn.numFrames > 1) {
            parent = new DefaultMutableTreeNode("TimePoint");
            root.add(parent);
            for (int frame = 0; frame < dsdIn.numFrames; frame++) {
               parent.add(new DefaultMutableTreeNode(frame));
            }
         }
         if (dsdIn.numPositions > 1) {
            parent = new DefaultMutableTreeNode("Position");
            root.add(parent);
            for (int pos = 0; pos < dsdIn.numPositions; pos++) {
               parent.add(new DefaultMutableTreeNode(pos));
            }
         }
         return new DefaultTreeModel(root);
      } catch (Exception ex) {
         Logger.getLogger(CheckboxTreeDimensions.class.getName()).log(Level.SEVERE, null, ex);
      }
      return null;
   }

   public static void main(String[] args) {

      test();
   }

   // For test...
   public static void test() {
      JSONObject sumMD = SumMetadata.newSummaryMetadata("", "", new ImageAttributes(100, 100),
              new String[]{"Aniso", "Orient", "Sample0", "Sample1"}, 5, 10, 1, "source", "comment");
      final CheckboxTreeDimensions tree = new CheckboxTreeDimensions(sumMD);
      QuickFrame f = new QuickFrame("Test");

      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setSize(300, 600);
      Container jContentPane = new JPanel();
         jContentPane.setLayout(new BorderLayout());
         JScrollPane cbt = tree.getCheckboxTree();
         cbt.setPreferredSize(new Dimension(200, 400));
         jContentPane.add(cbt, BorderLayout.CENTER);
      
      JButton buttonTest = new JButton("test");
      buttonTest.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            tree.getSelectedIndices();
         }
      });
      jContentPane.add(buttonTest, BorderLayout.SOUTH);
      f.setContentPane(jContentPane);
      f.setTitle("CheckboxTree");
      f.pack();
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

   private void promptForNoChoice(String dimension) {
      System.out.println("At least one " + dimension + " must be selected.");
   }
}
