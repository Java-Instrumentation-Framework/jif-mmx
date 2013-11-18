package edu.mbl.jif.imaging.dataset.metadata;

import edu.mbl.jif.imaging.mmtiff.MDUtils;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Parameters for opening an ImageCache dataset
 *
 * @author GBH
 */
public class MMDatasetDefinition {
//
//   public String directory;
//   public String prefix;
//   // Default dataset type is multipage OME-Tiff file, all positions in same file.
//   public boolean isMultipageTiff = true;
//   public boolean seperateMDFile = false;
//   public boolean seperateFilesForPositions = false;
//   //
//   public ImageAttributes attribs;
//   //
//   // Dimensions
//   public String[] channelNames = {};
//   public int numFrames = 1;
//   public int numSlices = 1;
//   public int numPositions = 1;
//   public boolean timeFirst = false;
//   //
//   // default is for byte grayscale
//   public int displayMin = 255;
//   public int displayMax = 255;
//   public int displayHistMax = 255;
//   //
//   public Map summaryMetadataMap;
//
//   public MMDatasetDefinition() {
//   }
//
//   public MMDatasetDefinition(JSONObject sumMD) {
//      try {      
//         this.directory = SummaryMetadata.getDirectory(sumMD);
//         this.prefix = SummaryMetadata.getPrefix(sumMD);
//         this.attribs = new ImageAttributes(sumMD);
//         // setDimension(z,t,p)
//         this.numFrames = MDUtils.getNumFrames(sumMD);
//         this.numSlices = MDUtils.getNumSlices(sumMD);
//         this.numPositions = MDUtils.getNumPositions(sumMD);
//         this.timeFirst = SummaryMetadata.isTimeFirst(sumMD);
//         this.channelNames = SummaryMetadata.getChannelNames(sumMD);
//         // TODO Get Display and comments from 
//         JSONObject dsettings = MMgrDisplayAndComments.getDisplaySettingsFromSummary(sumMD);
//         this.displayMin = 0;
//         this.displayMax = 255;
//         this.displayHistMax = 255;
//      } catch (JSONException ex) {
//         Logger.getLogger(MMDatasetDefinition.class.getName()).log(Level.SEVERE, null, ex);
//      }
//   }
}
