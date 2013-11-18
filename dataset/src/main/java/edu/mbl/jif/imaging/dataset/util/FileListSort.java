/*
 * FileListSort.java
 *
 * Created on May 24, 2007, 12:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.mbl.jif.imaging.dataset.util;

// File: arrays/filelist/Filelistsort.java
// Purpose: List contents of user home directory.
//          Demonstrates use of Comparators to sort the
//          same array by two different criteria.
// Author: Fred Swartz 2006-Aug-23  Public domain.
import edu.mbl.jif.imaging.dataset.util.DirAlphaComparator;
import java.util.Arrays;
import java.util.Comparator;
import java.io.*;


public class FileListSort {
	//======================================================= main
	public static void main(String[] args) {
		//... Create comparators for sorting.

		Comparator<File> byDirThenAlpha = new DirAlphaComparator();
		Comparator<File> byNameLength = new NameLengthComparator();

		//... Create a File object for user directory.
		//File dir = new File(System.getProperty("user.home"));
		File dir = new File("C:/MicroManagerData/shalins test data");
		File[] children = dir.listFiles();

		System.out.println("Files by directory, then alphabetical");
		Arrays.sort(children, byDirThenAlpha);
		printFileNames(children);

		System.out.println("Files by length of name (long first)");
		Arrays.sort(children, byNameLength);
		printFileNames(children);
	}

	//============================================= printFileNames
	private static void printFileNames(File[] fa) {
		for (File oneEntry : fa) {
			System.out.print("   " + oneEntry.getPath());

			if (oneEntry.isFile()) {
				System.out.println(" - FILE");
			} else {
				System.out.println(" - DIR");
			}
		}
	}



}