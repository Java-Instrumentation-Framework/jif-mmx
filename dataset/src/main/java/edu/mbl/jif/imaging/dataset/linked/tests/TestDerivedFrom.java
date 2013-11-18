package edu.mbl.jif.imaging.dataset.linked.tests;

import edu.mbl.jif.imaging.dataset.MMgrDatasetAccessor;
import edu.mbl.jif.imaging.dataset.metadata.FrameEvent;
import edu.mbl.jif.imaging.dataset.metadata.FrameMetadata;
import edu.mbl.jif.imaging.dataset.metadata.MMDatasetDefinition;
import edu.mbl.jif.imaging.dataset.metadata.SummaryMetadata;
import edu.mbl.jif.imaging.dataset.linked.Antecedent;
import edu.mbl.jif.imaging.dataset.linked.DerivedFrom;
import edu.mbl.jif.imaging.dataset.linked.PathUtils;
import edu.mbl.jif.imaging.dataset.linked.Transform;
import edu.mbl.jif.imaging.dataset.metadata.SumMetadata;
import edu.mbl.jif.imaging.mmtiff.TaggedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Testing of DerivedFrom...
 *
 * @author GBH
 */
public class TestDerivedFrom {

    public static void dump(DerivedFrom df) {
        Transform xform = df.getTransform();
        Antecedent[] ants = df.getAntecedents();
        for (Antecedent ant : ants) {
            System.out.println(ant.getUri());
            System.out.println(ant.getUuid());
            for (int i = 0; i < ant.getIndices().length; i++) {
                System.out.print(ant.getIndices()[i] + "  ");
            }
            System.out.println("");
        }
    }

    public static void main(String[] args) {
        try {
           //Gson gson = new GsonBuilder().setPrettyPrinting().create();
           //
           String dir = "C:/MicroManagerData/TestDatasetGen";
           String prefixIn = "generated";
           MMgrDatasetAccessor mda = new MMgrDatasetAccessor(dir, prefixIn, false, false);
           String bkgdFile = "pssynthBG";
           MMgrDatasetAccessor bkgdDS = new MMgrDatasetAccessor(dir, bkgdFile, false, false);
           //

           List<TaggedImage> bkgdImgs = new ArrayList<TaggedImage>();
           for (int i = 0; i < 5; i++) {
               TaggedImage img = bkgdDS.getImageCache().getImage(i, 0, 0, 0);
               if (img == null) {
                   System.out.println("**** " + "img==null");
               }
               bkgdImgs.add(img);
           }

           Map<String, Object> parameters = new HashMap<String, Object>();

           // Add parameter to carry Background images as Antecedents within that parameters...
           // Get a relative path for the antecedent images
           String antecentPath = bkgdDS.getImageCache().getDiskLocation();
           // TODO +++ this isn't really the result path....
           String resultPath = mda.getImageCache().getDiskLocation();
           String relPath = PathUtils.getRelativePath(antecentPath, resultPath);
           //
           Antecedent[] antsBG = Antecedent.createAntecedentsFor(relPath, bkgdImgs);
           parameters.put("BackgroundImages", antsBG);
           //
           // Create Transform with parameters...
           parameters.put("wavelength", 546.0f);
           parameters.put("swingFraction", 0.3f);
           parameters.put("retCeiling", 100.0f);
           parameters.put("azimuthRef", 0f);
           parameters.put("zeroIntensity", 0f);
           parameters.put("doBkgdCorrect", true);
           parameters.put("algorithm", 5);

           String xformName = "edu.mbl.jif.imaging.dataset.linked.tests.Magort";
           Transform xform = new Transform(xformName, parameters);
           //

           JSONObject sumMDj = mda.getImageCache().getSummaryMetadata();
           //
           //TaggedImage[] images = new TaggedImage[4];
           List<TaggedImage> images = new ArrayList<TaggedImage>();
           for (int i = 0; i < 4; i++) {
               TaggedImage img = mda.getImageCache().getImage(i, 0, 0, 0);
               if (img == null) {
                   System.out.println("**** " + "img==null");
               }
               images.add(img);
           }
           String antecentPath2 = mda.getImageCache().getDiskLocation();
           String resultPath2 = mda.getImageCache().getDiskLocation();
           String relPath2 = PathUtils.getRelativePath(antecentPath2, resultPath2);
           DerivedFrom df = DerivedFrom.createDerivedFrom(relPath2, images, xform);
           //
           
           // Create summary metadata same as the input...
   //        MMDatasetDefinition dsd = new MMDatasetDefinition(sumMDj);
   //        JSONObject jSumMD = SumMetadata.newCopyOfSummaryMetadata(sumMDj, dir, prefixIn, true, xformName)
   //                SummaryMetadata.createSummaryMetadata(dsd).createJsonSummaryMetadata();
           df.addToMetadata(sumMDj);
           try {
               System.out.println("=========== Summary =======================\n" +sumMDj.toString(2));
           } catch (JSONException ex) {}
           //
           //
           //
           // Create Frame metadata...
           FrameEvent evt = new FrameEvent();
           evt.frame = 0;
           evt.frameIndex = 0;
           evt.channel = "";
           evt.channelIndex = 0;
           evt.slice = 0;
           evt.sliceIndex = evt.slice;
           evt.positionIndex = 0;
           JSONObject frameMD = FrameMetadata.generateFrameMetadata(sumMDj, evt);
           // Add DerivedFrom metadata to the Frame metadata
           df.addToMetadata(frameMD);
           //
           try {
               System.out.println("=========== Frame =======================\n" +frameMD.toString(2));
           } catch (JSONException ex) {}
           //==================================================

       } catch (IOException ex) {Logger.getLogger(TestDerivedFrom.class.getName()).log(Level.SEVERE, null, ex);
}      catch (Exception ex) {
          Logger.getLogger(TestDerivedFrom.class.getName()).log(Level.SEVERE, null, ex);
       }
        //==================================================

    }
//	static String uriString(String path) {
//		try {
//			String s;
//			//URI basePath = new URI("file:///" + dir + "/" + prefix + "/"); // assumes localhost
//			URI basePath = new URI(path); // assumes localhost
//			s = basePath.toString();
//			return s;
//		} catch (URISyntaxException ex) {
//			Logger.getLogger(TestDerivedFrom.class.getName()).log(Level.SEVERE, null, ex);
//			return null;
//		}
//	}
}
