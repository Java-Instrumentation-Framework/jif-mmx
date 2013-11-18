package edu.mbl.jif.imaging.dataset.linked;

import edu.mbl.jif.imaging.dataset.MMgrDatasetAccessor;
import edu.mbl.jif.imaging.mmtiff.MDUtils;
import edu.mbl.jif.imaging.mmtiff.TaggedImage;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;

/**
 * For getting linked, antecedent images. Attempts to get all of the antecedent images as an array.
 * Some could be null.
 *
 *
 * For Antecedents at Summary-level, The Antecedent Indices indicate: {dimensionIndex, 0... n} To
 * get the antecedent images for image {c,z,t,p}, pass dimIndices = {c,z,t,p} if
 * Antecedent.DIMENSION_CHANNEL, return {0,z,t,p}, {<...>,z,t,p}, {n,z,t,p}. if
 * Antecedent.DIMENSION_SLICE, return {c,0,t,p}, {c,<...>,t,p}, {c,n,t,p}.
 *
 * int[] indices = new int[]{Antecedent.DIMENSION_CHANNEL, 0, 1, 2, 3, 4}; Antecedent[] antecedents
 * = new Antecedent[1]; antecedents[0] = new Antecedent(uuidSrc, uriSrc, indices);
 *
 *  * For Antecedents at Frame-level, the Antecedent Indices indicate: {c,z,t,p}.
 * 
 * @author GBH
 */
public class AntecedentRetriever {

    Map<URI, MMgrDatasetAccessor> mdas = new HashMap<URI, MMgrDatasetAccessor>();

    public TaggedImage[] getAntecedentImagesFromParameters(
            Antecedent[] ants, String dir, boolean summary) {
        // TODO
        TaggedImage[] images = new TaggedImage[0];
        return images;
    }

    // Attempts to get all of the antecedent images as an array.  Some could be null.
    public TaggedImage[] getAntecedentImages(DerivedFrom df, String dir, boolean summary) {
        Antecedent[] ants = df.getAntecedents();
        return getAntecedentImages(ants, dir, summary);
    }

    public TaggedImage[] getAntecedentImages(Antecedent[] ants, String dir, boolean summary) {
        TaggedImage[] images = new TaggedImage[ants.length];
        int n = 0;
        for (Antecedent ant : ants) {
            // reuse the open file if it is that same as the last 
            MMgrDatasetAccessor mda = openMdaFor(getBasePath(dir), ant.getUri(), ant.getUuid());
            if (mda != null) {
                int[] indices = ant.getIndices();
                // Indices indicate: c, z, t, p
                images[n] = mda.getImageCache().getImage(indices[0], indices[1], indices[3], indices[3]);
                if (images[n] != null) {
                    boolean isCorrectImage = checkUUID(ant, images[n]);
                    if (!isCorrectImage) {
                        System.out.println("Image UUIDs do not match.");
                    }
                }
            }
            // may leave images[n] == null;
            n++;
        }
        return images;

    }

    /*
     For Antecedents at Summary-level,
     The Antecedent Indices indicate: {dimensionIndex, 0... n}
     To get the antecedent images for image {c,z,t,p}, pass dimIndices = {c,z,t,p}
     if Antecedent.DIMENSION_CHANNEL, return {0,z,t,p}, {<...>,z,t,p}, {n,z,t,p}.
     if Antecedent.DIMENSION_SLICE, return {c,0,t,p}, {c,<...>,t,p}, {c,n,t,p}.
     */
    public TaggedImage[] getAntecedentImagesRelative(Antecedent[] ants, String dir, int[] dimIndices) {
        int numImages = ants[0].getIndices().length - 1;
        TaggedImage[] images = new TaggedImage[numImages];
        int n = 0;
        for (Antecedent ant : ants) {
            // reuse the open file if it is that same as the last 
            MMgrDatasetAccessor mda = openMdaFor(getBasePath(dir), ant.getUri(), ant.getUuid());
            if (mda == null) {
                return null;
            }
            int[] indices = ant.getIndices();
            for (int i = 1; i < indices.length; i++) {
                if (indices[0] == Antecedent.DIMENSION_CHANNEL) { // Replace channel index
                    dimIndices[0] = indices[i];
                }
                if (indices[0] == Antecedent.DIMENSION_SLICE) {// Replace slice index
                    dimIndices[1] = indices[i];
                }
                images[i-1] = mda.getImageCache().getImage(dimIndices[0], dimIndices[1], dimIndices[3], dimIndices[3]);
            }
            n++;
        } // may leave images[n] == null;
        return images;
    }

    MMgrDatasetAccessor openMdaFor(URI dir, URI relUri, UUID uuid) {
        System.out.println("dir: " + dir.toString());
        System.out.println("relUri: " + relUri.toString());
        URI uri = dir.resolve(relUri);

        System.out.println("uri: " + uri.toString());

        if (mdas.containsKey(uri)) { // already open
            return mdas.get(uri);
        } else {
            try {
                File f = new File(uri);
                if (!f.exists()) {
                    System.err.println("No File/URI exists: " + uri.toString());
                    return null;
                }
                MMgrDatasetAccessor mda = new MMgrDatasetAccessor(f.getAbsolutePath(), false, false);
                if (mda != null) {
                    UUID fileUuid = MDUtils.getUUID(mda.getImageCache().getSummaryMetadata());
                    if (fileUuid.compareTo(uuid) != 0) {
                        System.err.println("UUIDs do not match: "
                                + fileUuid.toString() + " != " + uuid.toString());
                    } else {
                        mdas.put(uri, mda);
                    }
                }
                return mda;
            } catch (Exception e) {
                System.err.println("Unable to open MMgrDatasetAccessor: " + uri.toString());
                e.printStackTrace();
            }
            return null;
        }
    }

    boolean checkUUID(Antecedent ant, TaggedImage img) {
        try {
            UUID antUUID = ant.getUuid();
            UUID imgUUID = MDUtils.getUUID(img.tags);
            return antUUID.equals(imgUUID);
        } catch (JSONException ex) {
            Logger.getLogger(AntecedentRetriever.class
                    .getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public void closeAll() {
        for (Map.Entry<URI, MMgrDatasetAccessor> entry : mdas.entrySet()) {
            URI uri = entry.getKey();
            MMgrDatasetAccessor mda = entry.getValue();
            if (mda != null) {
                mda.close();
            }
        }
    }

    private URI getBasePath(String dir) {
        URI basePath = null;
        try {
            basePath = new URI("file:///" + dir + "/"); // assumes localhost
        } catch (URISyntaxException ex) {
            System.err.println("Unable to get basePath for " + dir);
        }
        return basePath;
    }
}
