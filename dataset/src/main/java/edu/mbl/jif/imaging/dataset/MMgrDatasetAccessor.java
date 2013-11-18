package edu.mbl.jif.imaging.dataset;

import edu.mbl.jif.imaging.dataset.metadata.DimensionalExtents;
import edu.mbl.jif.imaging.mmtiff.MMImageCache;
import edu.mbl.jif.imaging.mmtiff.MultipageTiffReader;
import edu.mbl.jif.imaging.mmtiff.TaggedImageStorage;
import edu.mbl.jif.imaging.mmtiff.TaggedImageStorageDiskDefault;
import edu.mbl.jif.imaging.mmtiff.TaggedImageStorageMultipageTiff;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 * For accessing MMgr Datasets...
 *
 * @author GBH
 */
public class MMgrDatasetAccessor implements MMgrDataset {

   private MMImageCache imageCache;

   public MMgrDatasetAccessor(String dir, String prefix,
           boolean seperateMDFile,
           boolean seperateFilesForPositions)
           throws IOException, Exception {
      this(dir + "/" + prefix, seperateMDFile, seperateFilesForPositions);

   }

   public MMgrDatasetAccessor(String path,
           boolean seperateMDFile,
           boolean seperateFilesForPositions)
           throws IOException, Exception {
      // assuming (virtual_ && existing_)...
      TaggedImageStorage imageFileManager = null;
      boolean multipageTiff = MultipageTiffReader.isMMMultipageTiff(path);
      if (multipageTiff) {
         imageFileManager = new TaggedImageStorageMultipageTiff(path,
                 false, null, seperateMDFile, seperateFilesForPositions);
      } else {
         imageFileManager = new TaggedImageStorageDiskDefault(path, false, null);
      }
      imageCache = new MMImageCache(imageFileManager);
   }

   public MMImageCache getImageCache() {
      return imageCache;
   }

   public void close() {
      if (imageCache != null) {
         try {
            imageCache.close();
         } catch (Exception e) {
         }
      }
   }

   public static void main(String[] args) {
      String outputDir = "C:/MicroManagerData/TestDatasetGen";
      String outputprefix = "generated";
      //
      MMgrDatasetAccessor mmdp;
      try {
         mmdp = new MMgrDatasetAccessor(outputDir, outputprefix, true, false);
      } catch (IOException ex) {
         System.err.println("Cannot open dataset: " + outputDir + "/" + outputprefix);
         return;
      } catch (Exception ex) {
         System.err.println("Cannot open dataset: " + outputDir + "/" + outputprefix);
         return;
      }
      //
      MMImageCache cache = mmdp.getImageCache();
      JSONObject summaryMetadata_ = mmdp.getImageCache().getSummaryMetadata();
      DimensionalExtents.showDims(summaryMetadata_);
      String comment = mmdp.getImageCache().getComment();
      JSONObject imgTags = mmdp.getImageCache().getImageTags(0, 0, 0, 0);
      String imgComment = mmdp.getImageCache().getImageComment(imgTags);
      Set<String> imageKeys = mmdp.getImageCache().imageKeys();
      mmdp.close();
      System.exit(0);

   }
}
