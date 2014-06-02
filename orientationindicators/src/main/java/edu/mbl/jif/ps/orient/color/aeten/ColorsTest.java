package edu.mbl.jif.ps.orient.color.aeten;
// from https://github.com/aeten/net.aeten.core.ui.swing/blob/dfeb2dddb9801548954cd94a24415aeac9ca9486/test/net.aeten.core.ui.swing.test/net/aeten/core/gui/test/ColorsTest.java
// package net.aeten.core.gui.test;

import java.awt.Color;
import java.awt.GridLayout;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ColorsTest {

	public static void main (String[] args) throws InvocationTargetException,
														InterruptedException {
		SwingUtilities.invokeAndWait (new Runnable () {

			@Override
			public void run () { 
				JFrame frame = new JFrame ();
				frame.setLayout (new GridLayout (3, 2));
				final HueSelector hue = new HueSelector ();
				frame.add (hue);
				final JPanel colorPanel = new JPanel ();
				frame.add (colorPanel);
				final HSxSelector[] selectors = new HSxSelector[Colors.HSx.values ().length];
				for (Colors.HSx hsx: Colors.HSx.values ()) {
					final HSxSelector selector = new HSxSelector (hsx, hue, false);
					selectors[hsx.ordinal ()] = selector;
					selector.addChangeListener (new ChangeListener () {
						@Override
						public void stateChanged (ChangeEvent event) {
							Color color = ((HSxSelector) event.getSource ()).getColor ();
							colorPanel.setBackground (color);
							for (HSxSelector s: selectors) {
								if (s != selector) {
									s.setColor (color);
								}
							}
						}
					});
					frame.add (selector);
				}
				hue.addChangeListener (new ChangeListener () {
					@Override
					public void stateChanged (ChangeEvent event) {
						colorPanel.setBackground (selectors[0].getColor ());
					}
				});
				frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
				frame.pack ();
				frame.setVisible (true);
			}
		});
	}
}