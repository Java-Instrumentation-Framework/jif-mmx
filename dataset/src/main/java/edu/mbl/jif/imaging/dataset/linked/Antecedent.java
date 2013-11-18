package edu.mbl.jif.imaging.dataset.linked;

import edu.mbl.jif.imaging.dataset.MMgrDataset;
import edu.mbl.jif.imaging.dataset.linked.tests.TestDerivedFrom;
import edu.mbl.jif.imaging.mmtiff.MDUtils;
import edu.mbl.jif.imaging.mmtiff.TaggedImage;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;


/**
 *
 * When summary-level, the indices array starts with the dimension flag, followed by the indices of
 * that dimension, eg. the channels of the images...
 * 
 * TODO Doc...
 
  For Antecedents at Summary-level,
  The Antecedent Indices indicate: {dimensionIndex, 0... n}
   To get the antecedent images for image {c,z,t,p}, pass dimIndices = {c,z,t,p}
   if Antecedent.DIMENSION_CHANNEL, return {0,z,t,p}, {<...>,z,t,p}, {n,z,t,p}.
   if Antecedent.DIMENSION_SLICE, return {c,0,t,p}, {c,<...>,t,p}, {c,n,t,p}.
     
 * int[] indices = new int[]{Antecedent.DIMENSION_CHANNEL, 0, 1, 2, 3, 4}; 
 * Antecedent[] antecedents = new Antecedent[1]; 
 * antecedents[0] = new Antecedent(uuidSrc, uriSrc, indices);
 *
 *
 * @author GBH
 */
public class Antecedent {

    public static final String JSON_KEY = "Antecedent";
    private UUID uuid;
    private URI uri;
    private int[] indices;  // assume C, Z, T, P
    public static final int DIMENSION_BY_INDEX = 0;  // never used
    public static final int DIMENSION_CHANNEL = 3;
    public static final int DIMENSION_SLICE = 4;

    // When Frame-level...
    public Antecedent(UUID uuid, URI uri, int[] indices) {
        this.uuid = uuid;
        this.uri = uri;
        this.indices = indices;
    }

    // if at Summary-level... UUID of anticedent file (from it's Summary metadata)
    // if at Frame-level... UUID of anticedent frame
    public UUID getUuid() {
        return uuid;
    }

    public URI getUri() {
        return uri;
    }

    // Indices...
    // if at Summary-level... set of Cs, or set of Zs...?
    // 
    // if at Frame-level... typically, [c, z, t, p]
    public int[] getIndices() {
        return indices;
    }

    //+================================
    public static Antecedent[] createAntecedentsForSummary(
            MMgrDataset dsIn, MMgrDataset dsOut,
            List<TaggedImage> images, int dimension) {
        // when summary-level, the indices array starts with the dimension flag,
        // followed by the indices of that dimension, eg. the channels of the images... 
        int[] indices = new int[images.size()+1];
        indices[0] = Antecedent.DIMENSION_CHANNEL;
        for (int i = 0; i < images.size(); i++) {
            try {
                TaggedImage img = images.get(i);
                int c = MDUtils.getChannelIndex(img.tags);
                indices[i + 1] = c;
            } // e.g. end up with {Antecedent.DIMENSION_CHANNEL, 0, 1, 2, 3, 4};
            catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        return createAntecedentsForSummary(dsIn, dsOut, indices);
    }

    public static Antecedent[] createAntecedentsForSummary(
            MMgrDataset dsIn, MMgrDataset dsOut, int[] indices) {
        try {
            JSONObject sumMDSrc = dsIn.getImageCache().getSummaryMetadata();
            UUID uuidSrc = MDUtils.getUUID(sumMDSrc);
            String relPathA = PathUtils.getRelativePath(
                    dsIn.getImageCache().getDiskLocation(),
                    dsOut.getImageCache().getDiskLocation());
            //URI uriSrc = URI.create(relPathA + "/" + prefixSrc + "_MMImages.ome.tif");
            URI uriSrc = URI.create(relPathA);
            Antecedent[] antecedents = new Antecedent[1];
            antecedents[0] = new Antecedent(uuidSrc, uriSrc, indices);
            return antecedents;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // Frame-level
    public static Antecedent[] createAntecedentsFor(String filePath, List<TaggedImage> images) {
        Antecedent[] ants = new Antecedent[images.size()];
        for (int i = 0; i < images.size(); i++) {
            TaggedImage img = images.get(i);
            if (img == null) {
                System.err.println("img==null i=" + i);
            } else {
                Antecedent ant = createAntecedentFor(filePath, img.tags);
                ants[i] = ant;
            }
        }
        return ants;
    }

    private static Antecedent createAntecedentFor(String filePath, JSONObject imageTags) {
        try {
            // Need to change this...
            URI basePath = new URI("file:///" + filePath + "/"); // assumes localhost
            String filename = MDUtils.getFileName(imageTags);
            URI uri = basePath.resolve(filename);
            int c = MDUtils.getChannelIndex(imageTags);
            int t = MDUtils.getFrameIndex(imageTags);
            int z = MDUtils.getSliceIndex(imageTags);
            int p = MDUtils.getPositionIndex(imageTags);
            int[] indices = {c, z, t, p};
            //
            UUID frameUUID = MDUtils.getUUID(imageTags);
            Antecedent ant = new Antecedent(frameUUID, uri, indices);
            return ant;
        } catch (Exception ex) {
            Logger.getLogger(TestDerivedFrom.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}