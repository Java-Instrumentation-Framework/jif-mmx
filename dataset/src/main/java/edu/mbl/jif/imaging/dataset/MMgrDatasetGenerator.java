/*
 * MMgrDatasetGenerator
 */
package edu.mbl.jif.imaging.dataset;

import edu.mbl.jif.imaging.dataset.metadata.FrameEvent;
import edu.mbl.jif.imaging.dataset.metadata.MMDatasetDefinition;
import edu.mbl.jif.imaging.dataset.metadata.MMgrDisplayAndComments;
import edu.mbl.jif.imaging.dataset.metadata.FrameMetadata;
import edu.mbl.jif.imaging.dataset.metadata.SummaryMetadata;
import edu.mbl.jif.imaging.dataset.linked.DerivedFrom;
import edu.mbl.jif.imaging.dataset.metadata.ImageAttributes;
import edu.mbl.jif.imaging.mmtiff.MDUtils;
import edu.mbl.jif.imaging.mmtiff.MMImageCache;
import edu.mbl.jif.imaging.mmtiff.MMScriptException;
import edu.mbl.jif.imaging.mmtiff.TaggedImage;
import edu.mbl.jif.imaging.mmtiff.TaggedImageStorage;
import edu.mbl.jif.imaging.mmtiff.TaggedImageStorageDiskDefault;
import edu.mbl.jif.imaging.mmtiff.TaggedImageStorageMultipageTiff;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;

/**
 * Generates (creates) Micro-Manager Datasets...
 *
 * Is SavingThread neccessary? When generating a dataset, the use of an ImageQueue/LiveAcq is important
 * to finish() the ImageCache and close() * the file storage. So something, if it isn't LivaAcq,
 * needs to do this.
 *
 * Cancel and Clean-up...
 *
 * @author GBH
 */
public class MMgrDatasetGenerator implements MMgrDataset {

    MMImageCache imageCache;
    TaggedImageStorage imageFileManager;
    BlockingQueue<TaggedImage> taggedImageQueue;
    JSONObject sumMDj;
    //private MMDatasetDefinition dsd;

//    public MMgrDatasetGenerator(MMDatasetDefinition dsd) {
//        this.dsd = dsd;
//        initFromDatesetDefinition();
//    }
    
    public MMgrDatasetGenerator(JSONObject sumMD) throws Exception {
       this(sumMD, true, false, true);
    }

    
    public MMgrDatasetGenerator(JSONObject sumMD, 
            boolean multipageTiff,
            boolean separateMDFile, 
            boolean seperateFilesForPositions) throws Exception {
       
       try {
          this.sumMDj = sumMD;
          openImageCache(sumMD, multipageTiff, separateMDFile, seperateFilesForPositions);
       } catch (Exception ex) {
          throw ex;
       }
    }
  
            
//    void initFromDatesetDefinition() {
//        SummaryMetadata sumMD = SummaryMetadata.createSummaryMetadata(dsd);
//        sumMDj = sumMD.createJsonSummaryMetadata();
//        if (dsd.summaryMetadataMap != null) {
//            SummaryMetadata.addToSummaryMetadata(dsd.summaryMetadataMap, sumMDj);
//        }
//        // this should overwrite the binning and pixelsize values.
//        addDisplaySettings(sumMDj);
//        try {
//            openImageCache(sumMDj, dsd.isMultipageTiff, dsd.seperateMDFile, dsd.seperateFilesForPositions);
//        } catch (MMScriptException ex) {
//            // error
//        }
//    }

    // TODO this is MAG/ORT specific...
//    public static MMgrDatasetGenerator createCompatibleDataset(
//            MMgrDatasetAccessor mda, String dir, String prefixOut) {
//       
//        // Create MMDatasetDefinition based on the input dataset...
//        JSONObject sumMD_In = mda.getImageCache().getSummaryMetadata();
//        MMDatasetDefinition dsd = new MMDatasetDefinition(sumMD_In);
//        //
//        // TODO: Add displayAndComments... but need to change for only 2 channels
//        JSONObject dispMD = mda.getImageCache().getDisplayAndComments();
//        //
//        // Change /add metadata...
//        dsd.attribs = new ImageAttributes(sumMD_In);  // Same image attributes
//        dsd.channelNames = new String[]{"Mag", "Ort"};  // 2 channels
//        dsd.directory = dir;
//        dsd.prefix = prefixOut;  // file output
//        // same dimensions, other than channel...
//        //	dsd.numFrames = MDUtils.getNumFrames(sumMD_In);
//        //	dsd.numSlices = MDUtils.getNumSlices(sumMD_In);
//        //	dsd.numPositions = MDUtils.getNumPositions(sumMD_In);
//        // dsd.summaryMetadataMap = null;
//        dsd.isMultipageTiff = true;
//        dsd.displayMin = 0;
//        dsd.displayMax = 255;
//        dsd.displayHistMax = 255;
//        //
//        return new MMgrDatasetGenerator(dsd);
//    }

    public void openImageCache(JSONObject sumMD, 
            boolean multipageTiff,
            boolean seperateMDFile, 
            boolean seperateFilesForPositions) throws MMScriptException, Exception {
        // assumes (virtual_ && !existing_)
        try {
            String dirName = sumMD.get("Directory") + "/" + sumMD.get("Prefix");
            // Create a unique dataset name
            if ((new File(dirName)).exists()) {
                dirName = createAcqPath("" + sumMD.get("Directory"), "" + sumMD.get("Prefix"));
            }
            if (multipageTiff) {
                imageFileManager = new TaggedImageStorageMultipageTiff(dirName, true, sumMD,
                        seperateMDFile, seperateFilesForPositions);
            } else {
                imageFileManager = new TaggedImageStorageDiskDefault(dirName, true, sumMD);
            }
            imageCache = new MMImageCache(imageFileManager);
        } catch (Exception ex) {
            Logger.getLogger(MMgrDatasetGenerator.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        imageCache.setSummaryMetadata(sumMD);
        imageCache.setDisplayAndComments(MMgrDisplayAndComments.getDisplaySettingsFromSummary(sumMD));
        System.out.println("Opened dataset: " + imageCache.getDiskLocation());
        //
       MDUtils.getFileName(imageCache.getSummaryMetadata());
        taggedImageQueue = new LinkedBlockingQueue<TaggedImage>(10);
        SavingThread liveAcq = new SavingThread(taggedImageQueue, imageCache);
        liveAcq.start();
    }
    
    
    public MMImageCache getImageCache() {
        return imageCache;
    }

    public void stop() {
        try {
            Thread.sleep(250);
        } catch (InterruptedException ex) {}
        writeDisplaySettings();
        sendPoison();
        //imageCache.close();
        System.out.println("Closed dataset: " + imageCache.getDiskLocation());
    }

    // TODO add other dimensions...
    public void putImageToChannel(int channel, String name,
            int slice, int frameIndex, int position, Object pix) {
        putImageToChannel(channel, name, slice, frameIndex, position, pix, null);
    }

    public void putImageToChannel(int channel, String name,
            int slice, int frameIndex, int position, Object pix, DerivedFrom df) {
        FrameEvent evt = new FrameEvent();
        evt.frame = frameIndex;
        evt.frameIndex = frameIndex;
        evt.channel = name;
        evt.channelIndex = channel;
        evt.slice = slice;
        evt.sliceIndex = slice;
        evt.positionIndex = position;
        JSONObject tags = FrameMetadata.generateFrameMetadata(sumMDj, evt);
        
        if (df != null) {
            df.addToMetadata(tags);
        }
        //
        TaggedImage img = new TaggedImage(pix, tags);
        putImage(img);
    }
    
    public void putImage(  Object pix, JSONObject frameMD) {
        TaggedImage img = new TaggedImage(pix, frameMD);
        putImage(img);
    }

   
    public void putImage(TaggedImage taggedImage) {
        try {
            taggedImageQueue.put(taggedImage);
        } catch (InterruptedException ex) {
            Logger.getLogger(MMgrDatasetGenerator.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void sendPoison() {
        try {
            taggedImageQueue.put(TaggedImageQueue.POISON);
        } catch (InterruptedException ex) {
            Logger.getLogger(MMgrDatasetGenerator.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Display settings... 
//    private void addDisplaySettings(JSONObject summaryMD) {
//        JSONObject displayMD;
//        displayMD = MMgrDisplayAndComments.getDisplaySettingsFromSummary(summaryMD);
//        
//        for (int i = 0; i < dsd.channelNames.length; i++) {
//            String name = dsd.channelNames[i];
//            int channelIndex = i;
//            double gamma = 1.0;
//            int color = -1;
//            int displayMode = 3; // GARYSCALE
//            MMgrDisplayAndComments.storeChannelDisplaySettings(displayMD,
//                    channelIndex, name,
//                    dsd.displayMin, dsd.displayMax, gamma, dsd.displayHistMax,
//                    color, displayMode);
//        }
//    }

    private void writeDisplaySettings() {
        imageCache.writeDisplaySettings();
    }

    // Directory Creation for new dataset...
    private String createAcqPath(String root, String prefix) throws Exception {
        File rootDir = createDirectory(root);
        int curIndex = getCurrentMaxDirIndex(rootDir, prefix + "_");
        File acqDir = new File(root + "/" + prefix + "_" + (1 + curIndex));
        String path = acqDir.getAbsolutePath();
        return path.replace("\\", "/");
    }

    private int getCurrentMaxDirIndex(File rootDir, String prefix) throws NumberFormatException {
        int maxNumber = 0;
        int number;
        String theName;
        for (File acqDir : rootDir.listFiles()) {
            theName = acqDir.getName();
            if (theName.startsWith(prefix)) {
                try {
                    //e.g.: "blah_32.ome.tiff"
                    Pattern p = Pattern.compile("\\Q" + prefix + "\\E" + "(\\d+).*+");
                    Matcher m = p.matcher(theName);
                    if (m.matches()) {
                        number = Integer.parseInt(m.group(1));
                        if (number >= maxNumber) {
                            maxNumber = number;
                        }
                    }
                } catch (NumberFormatException e) {
                } // Do nothing.
            }
        }
        return maxNumber;
    }

    // Cancellation..........................
    public void cancel() {
        stop();
        // delete the file(s) and directory that were created
        System.out.println("Cancelled, cleaning up...");

        String dirToDelete = imageCache.getDiskLocation();
        removeDirectory(new File(dirToDelete));
//		File f = new File(fileToDelete);
//		if (f.delete()) {
//			System.out.println("Deleted file " + fileToDelete);
//		} else {
//			System.out.println("Failed to Delete file " + fileToDelete);
//		}
//		File parent = new File(f.getParent());
//		if (parent.delete()) {
//			System.out.println("Deleted dir " + parent);
//		}
    }

    public static boolean removeDirectory(File directory) {
        // System.out.println("removeDirectory " + directory);
        if (directory == null) {
            return false;
        }
        if (!directory.exists()) {
            return true;
        }
        if (!directory.isDirectory()) {
            return false;
        }
        String[] list = directory.list();
        // Some JVMs return null for File.list() when the
        // directory is empty.
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                File entry = new File(directory, list[i]);
                //        System.out.println("\tremoving entry " + entry);
                if (entry.isDirectory()) {
                    if (!removeDirectory(entry)) {
                        return false;
                    }
                } else {
                    if (!entry.delete()) {
                        return false;
                    }
                }
            }
        }
        return directory.delete();
    }

    /*  Determine file name(s) for deletion.
	 
     baseFilename_ = createBaseFilename(firstImageTags);
     currentTiffFilename_ = baseFilename_ + (omeTiff_ ? ".ome.tif" : ".tif");
				 
     private String createBaseFilename(JSONObject firstImageTags) {
     String baseFilename = "";
     try {
     String prefix = summaryMetadata_.getString("Prefix");
     if (prefix.length() == 0) {
     baseFilename = "MMImages";
     } else {
     baseFilename = prefix + "_MMImages";
     }
     } catch (JSONException ex) {
     ReportingUtils.logError("Can't find Prefix in summary metadata");
     baseFilename = "MMImages";
     }

     if (numPositions_ > 1 && splitByXYPosition_) {
     String positionName;
     try {
     positionName = MDUtils.getPositionName(firstImageTags);
     } catch (JSONException ex) {
     ReportingUtils.logError("Couldn't find position name in image metadata");
     try {
     positionName = "pos" + MDUtils.getPositionIndex(firstImageTags);
     } catch (JSONException ex1) {
     positionName = "pos" + 0;
     ReportingUtils.showError("Couldnt find position index in image metadata");
     }
     }
     baseFilename += "_" + positionName;
     }
     return baseFilename;
     }
     */
    // ================================================
    public static Object createPix(BufferedImage img)
            throws UnsupportedImageModelException2 {
        int w = img.getWidth();
        int h = img.getHeight();
        DataBuffer buffer = img.getData().getDataBuffer();
        if (buffer.getOffset() != 0) {
            throw new UnsupportedImageModelException2("Expecting BufferData with no offset.");
        }
        switch (buffer.getDataType()) {
            case DataBuffer.TYPE_BYTE:
                return ((DataBufferByte) buffer).getData();
            case DataBuffer.TYPE_USHORT:
                return ((DataBufferUShort) buffer).getData();
            case DataBuffer.TYPE_SHORT:
                short[] pixels = ((DataBufferShort) buffer).getData();
                for (int i = 0; i < pixels.length; ++i) {
                    pixels[i] = (short) (pixels[i] + 32768);
                }
                return pixels;
            case DataBuffer.TYPE_INT:
                return ((DataBufferInt) buffer).getData();
            case DataBuffer.TYPE_FLOAT: {
                DataBufferFloat dbFloat = (DataBufferFloat) buffer;
                return dbFloat.getData();
            }
            case DataBuffer.TYPE_DOUBLE:
                return ((DataBufferDouble) buffer).getData();
            case DataBuffer.TYPE_UNDEFINED:
                // ENH: Should this be reported as data problem?
                throw new UnsupportedImageModelException2("Pixel type is undefined.");
            default:
                throw new UnsupportedImageModelException2("Unrecognized DataBuffer data type");
        }
    }

    private static class UnsupportedImageModelException2 extends Exception {

        public UnsupportedImageModelException2(String msg) {
        }
    }

    public static File createDirectory(String dirPath) throws Exception {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new Exception("Unable to create directory.");
            }
        }
        return dir;
    }
}
