package edu.mbl.jif.imaging.dataset.linked.tests;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tests using URIs...
 *
 * @author GBH
 */
public class TestURI {

    public static void main(String[] args) {
        TestURI t = new TestURI();
        //t.testRootFolder();
        //t.testJavaFilePath();
        t.test();
        // t.testURIs();
//		String baseDir = "C:/MicroManagerData/TestDatasetGen/";
//		String target = "C:/MicroManagerData/TestDatasetGen/generated/generated_MMImages.ome.tif";
//		String rel = relativize(new File(baseDir), new File(target));
//		System.out.println(rel);
//
//		String[] paths = {
//			"/home/user1/tmp/coverage/test",
//			"/home/user1/tmp/covert/operator",
//			"/home/user1/tmp/coven/members"};
//		System.out.println(commonPath(paths));
//
//		String[] paths2 = {
//			"/hame/user1/tmp/coverage/test",
//			"/home/user1/tmp/covert/operator",
//			"/home/user1/tmp/coven/members"};
//		System.out.println(commonPath(paths2));
//		String[] paths3 = {
//			"C:/MicroManagerData/TestDatasetGen/",
//			"C:/MicroManagerData/TestDatasetGen/generated/generated_MMImages.ome.tif"};
//		System.out.println(commonPath(paths3));
    }

    void testRootFolder() {
        //To get root folder of the application
        String path = getClass().getClassLoader().getResource(".").getPath();
        System.out.println("path = " + path);
    }

    public static void FileURIURL(String[] args) throws IOException {

        File file = new File("file name with spaces.txt");

        URI fileUri = file.toURI();
        System.out.println("URI:" + fileUri);

        URL fileUrl = file.toURI().toURL();
        System.out.println("URL:" + fileUrl);

        URL fileUrlWithoutSpecialCharacterHandling = file.toURL();
        System.out.println("URL (no special character handling):" + fileUrlWithoutSpecialCharacterHandling);

        /*
         URI:file:/C:/projects/workspace/testing/file%20name%20with%20spaces.txt
         URL:file:/C:/projects/workspace/testing/file%20name%20with%20spaces.txt
         URL (no special character handling):file:/C:/projects/workspace/testing/file name with spaces.txt
         */
    }

    // final URL u = new File("g:/something.jar").toURI().toURL();
    public void test() {
//        try {
//            //String baseDir = "/MicroManagerData/TestDatasetGen/generated/";
//            String baseDir = "C:/MicroManagerData/TestDatasetGen\\generated/";
//            String filename = "generated_MMImages.ome.tif";
//            URI basePath = new URI("file:///" + baseDir + "/"); // assumes localhost
//            URI uri = basePath.resolve(filename);
//            System.out.println("uri = " + uri.toString());
//            File f = new File(uri);
//            System.out.println("Exists: "
//                    + f.exists() + "  IsFile: "
//                    + f.isFile());
//        } catch (URISyntaxException ex) {
//            ex.printStackTrace();
//        }
        try {
            //String baseDir = "/MicroManagerData/TestDatasetGen/generated/";
            String baseDir = "C:/MicroManagerData/TestDatasetGen/";
            String filename = "generated/generated_MMImages.ome.tif";
            URI basePath = new URI("file:///" + baseDir + "/"); // assumes localhost
            URI uri = basePath.resolve(filename);
            System.out.println("uri = " + uri.toString());
            File f = new File(uri);
            System.out.println("f.getAbsolutePath() = " + f.getAbsolutePath());
            System.out.println("Exists: "
                    + f.exists() + "  IsFile: "
                    + f.isFile());
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }


    }

    public void testJavaFilePath() {
        System.out.println("Roots ============");
        File[] roots = File.listRoots();
        for (int i = 0; i < roots.length; i++) {
            printPaths(roots[i]);
        }
        System.out.println("==================");
        String rootPath = getClass().getClassLoader().getResource(".").getPath();
        System.out.println("application rootPath = " + rootPath);
        File f = new File(rootPath);
        printPaths(f);
        System.out.println("==================");
        File file = new File("/Users/pankaj/test.txt");
        printPaths(file);
        //relative path
        file = new File("test.xsd");
        printPaths(file);
        //complex relative paths
        file = new File("/Users/pankaj/../pankaj/test.txt");
        printPaths(file);
        try {
            //URI paths
            file = new File(new URI("file:///Users/pankaj/test.txt"));
        } catch (URISyntaxException ex) {
            Logger.getLogger(TestURI.class.getName()).log(Level.SEVERE, null, ex);
        }
        printPaths(file);
    }

    private static void printPaths(File file) {
        System.out.println("Absolute Path: " + file.getAbsolutePath());
        try {
            System.out.println("Canonical Path: " + file.getCanonicalPath());
        } catch (IOException ex) {
            Logger.getLogger(TestURI.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Path: " + file.getPath() + "\n");
    }
    /*
     * 
     Absolute Path:  /Users/pankaj/test.txt
     Canonical Path: /Users/pankaj/test.txt
     Path:           /Users/pankaj/test.txt
     Absolute Path:  /Users/pankaj/CODE/r12-3-cranberry/JavaImageProcessor/test.xsd
     Canonical Path: /Users/pankaj/CODE/r12-3-cranberry/JavaImageProcessor/test.xsd
     Path: test.xsd
     Absolute Path:  /Users/pankaj/../pankaj/test.txt
     Canonical Path: /Users/pankaj/test.txt
     Path:           /Users/pankaj/../pankaj/test.txt
     Absolute Path:  /Users/pankaj/test.txt
     Canonical Path: /Users/pankaj/test.txt
     Path:           /Users/pankaj/test.txt
     */

    public void testURIs() {

// Building an absolute URI:
        try {
            URI uri_absolute = new URI("http://www.java.sun.com/");
            URI uri_relative = new URI("index.html");
            URI uri_absolute_result = uri_absolute.resolve(uri_relative);
            System.out.println(uri_absolute_result);
        } catch (URISyntaxException e) {
            System.out.println(e.getMessage());
        }
        //The result:
        //http://www.java.sun.com/index.html

        //Building a relative URI:
        try {
            URI uri = new URI("/docs/imagini/mare/");
            URI uri_relative = new URI("eforie/discoteca.jpg");
            URI uri_relative_result = uri.resolve(uri_relative);
            System.out.println(uri_relative_result);
        } catch (URISyntaxException e) {
            System.out.println(e.getMessage());
        }
        //The result:
        ///docs/imagini/mare/eforie/discoteca.jpg


        //Building an absolute URI:
        try {
            URI uri_absolute = new URI("http://www.java.sun.com/");
            URI uri_absolute_result = uri_absolute.resolve("index.html");
            System.out.println(uri_absolute_result);
        } catch (URISyntaxException e) {
            System.out.println(e.getMessage());
        }
        //The result:
        //http://www.java.sun.com/index.html


        //Building a relative URI:
        try {
            URI uri = new URI("/docs/imagini/mare/");
            URI uri_relative_result = uri.resolve("eforie/discoteca.jpg");
            System.out.println("relative result: " + uri_relative_result);
        } catch (URISyntaxException e) {
            System.out.println(e.getMessage());
        }
        //The result:
        ///docs/imagini/mare/eforie/discoteca.jpg


        //Getting a relative URI:
        try {
            URI uri_absolute_1 = new URI("http://www.java.sun.com/index.html");
            URI uri_absolute_2 = new URI("http://www.java.sun.com/");
            URI uri_relative_result = uri_absolute_2.relativize(uri_absolute_1);
            System.out.println(uri_relative_result);
        } catch (URISyntaxException e) {
            System.out.println(e.getMessage());
        }
        //Result:
        //index.html	
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
