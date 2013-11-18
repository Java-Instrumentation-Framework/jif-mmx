package edu.mbl.jif.imaging.dataset.linked;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 *
 * @author GBH
 */
public class PathUtils {
	
	public static String getRelativePath(String antecentPath, String resultPath) {
		String commonPath = commonPath(new String[]{antecentPath, resultPath});
		String relPath = relativize(commonPath, antecentPath);
//		System.out.println("commonPath = " + commonPath);
//		System.out.println("relPath = " + relPath);
		return relPath;
	}
	
	/**
	 * Calculates the relative path between a specified root directory and a target path.
	 *
	 * @param root The absolute path of the root directory.
	 * @param target The path to the target file or directory.
	 * @return The relative path between the specified root directory and the target path.
	 * @throws IllegalArgumentException <ul><li>The root file cannot be null.</li><li>The target
	 * cannot be null.</li><li>The root file must be a directory.</li><li>The root file must be
	 * absolute.</li></ul>
	 */
	public static String relativize(final String root, final String target) throws IllegalArgumentException {
		return relativize(new File(root), new File(target));
	}
	
	public static String relativize(final File root, final File target) throws IllegalArgumentException {
		if (root == null) {
			throw new IllegalArgumentException("The root file cannot be null.");
		}
		if (target == null) {
			throw new IllegalArgumentException("The target cannot be null.");
		}
		if (!root.isDirectory()) {
			throw new IllegalArgumentException("The root file must be a directory.");
		}
		if (!root.isAbsolute()) {
			throw new IllegalArgumentException("The root file must be absolute.");
		}
		if (!target.isAbsolute()) {
			return target.toString();
		}

		if (root.equals(target)) {
			return ".";
		}

		// Deconstruct hierarchies
		final Deque<File> rootHierarchy = new ArrayDeque<File>();
		for (File f = root; f != null; f = f.getParentFile()) {
			rootHierarchy.push(f);
		}
		final Deque<File> targetHierarchy = new ArrayDeque<File>();
		for (File f = target; f != null; f = f.getParentFile()) {
			targetHierarchy.push(f);
		}

		// Trace common root
		while (rootHierarchy.size() > 0 && targetHierarchy.size() > 0
				&& rootHierarchy.peek().equals(targetHierarchy.peek())) {
			rootHierarchy.pop();
			targetHierarchy.pop();
		}
		// Create relative path
		final StringBuilder sb = new StringBuilder(rootHierarchy.size() * 3 + targetHierarchy.size() * 32);
		while (rootHierarchy.size() > 0) {
			sb.append("..");
			rootHierarchy.pop();
			if (rootHierarchy.size() > 0 || targetHierarchy.size() > 0) {
				sb.append("/");
			}
		}
		while (targetHierarchy.size() > 0) {
			sb.append(targetHierarchy.pop().getName());
			if (targetHierarchy.size() > 0) {
				sb.append("/");
			}
		}
		return sb.toString();
	}

	public static String commonPath(String... paths) {
		String commonPath = "";
		String[][] folders = new String[paths.length][];
		for (int i = 0; i < paths.length; i++) {
			folders[i] = paths[i].split("/"); //split on file separator
		}
		for (int j = 0; j < folders[0].length; j++) {
			String thisFolder = folders[0][j]; //grab the next folder name in the first path
			boolean allMatched = true; //assume all have matched in case there are no more paths
			for (int i = 1; i < folders.length && allMatched; i++) { //look at the other paths
				if (folders[i].length < j) { //if there is no folder here
					allMatched = false; //no match
					break; //stop looking because we've gone as far as we can
				}
				//otherwise
				allMatched &= folders[i][j].equals(thisFolder); //check if it matched
			}
			if (allMatched) { //if they all matched this folder name
				commonPath += thisFolder + "/"; //add it to the answer
			} else {//otherwise
				break;//stop looking
			}
		}
		return commonPath;
	}

	
}
