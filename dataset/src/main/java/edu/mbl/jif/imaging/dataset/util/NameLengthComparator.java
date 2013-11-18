package edu.mbl.jif.imaging.dataset.util;

import java.io.File;
import java.util.Comparator;

/**
 *
 * @author GBH
 */
////////////////////////////////////////////////// 
// NameLengthComparator
// To sort by length of file/directory name (longest first).
//
	public class NameLengthComparator implements Comparator<File> {

		// Comparator interface requires defining compare method.
		public int compare(File filea, File fileb) {
			int comp = fileb.getName().length() - filea.getName().length();
			if (comp != 0) {
				//... If different lengths, we're done.
				return comp;
			} else {
				//... If equal lengths, sort alphabetically.
				return filea.getName().compareToIgnoreCase(fileb.getName());
			}
		}

	}