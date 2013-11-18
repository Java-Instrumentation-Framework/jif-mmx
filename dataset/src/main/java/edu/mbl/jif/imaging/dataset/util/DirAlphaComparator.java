package edu.mbl.jif.imaging.dataset.util;

import java.io.File;
import java.util.Comparator;

/**
 *
 * @author GBH
 */

////////////////////////////////////////////////// 
// DirAlphaComparator
// To sort directories before files, then alphabetically.
//
	public class DirAlphaComparator implements Comparator<File> {

		// Comparator interface requires defining compare method.
		public int compare(File filea, File fileb) {
			//... Sort directories before files,
			//    otherwise alphabetical ignoring case.
			if (filea.isDirectory() && !fileb.isDirectory()) {
				return -1;

			} else if (!filea.isDirectory() && fileb.isDirectory()) {
				return 1;

			} else {
				return filea.getName().compareToIgnoreCase(fileb.getName());
			}
		}

	}
