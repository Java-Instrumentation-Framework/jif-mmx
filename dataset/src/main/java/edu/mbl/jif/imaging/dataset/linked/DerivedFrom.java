package edu.mbl.jif.imaging.dataset.linked;

import edu.mbl.jif.imaging.mmtiff.TaggedImage;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * DerivedFrom: used as metadata to indicate the image(s) (antecedents) and the
 * transform from which a derived image was derived.
 *
 * This may apply to the entire file if it is in the SummaryMetadata or it may
 * apply to each image if it is in the FrameMetadata
 *
 * ? Diff. xforms for each channel, e.g. Mag and Orient. ? Bkgd changes with Z
 *
 * E.g. Reference to background is an Antecendent[] passed as a parameter...
 *
 * Get DerivedFrom from FrameMetadata DerivedFrom in FrameMetadata should
 * over-ride DerivedFrom in SummaryMetadata
 *
 * Summary-Level uses the source dataset's UUID and URI, and the indices ind
 *
 * Also see doc. for Antecedent and Transform.
 * 
 * 
 * @author GBH
 */
public class DerivedFrom {

    public static final String JSON_KEY = "DerivedFrom";
    Antecedent[] antecedents;
    Transform transform;
    // + Background 
    //static Gson gson = new Gson();

    public DerivedFrom() {
    }

    public DerivedFrom(Antecedent[] antecedents, Transform transform) {
        this.antecedents = antecedents;
        this.transform = transform;
    }

    public Antecedent[] getAntecedents() {
        return antecedents;
    }

    public void setAntecedents(Antecedent[] antecedents) {
        this.antecedents = antecedents;
    }

    public Transform getTransform() {
        return transform;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
    }

    // createDerivedFrom.....
    /// relPath: Relative pathname 
    public static DerivedFrom createDerivedFrom(String relPath, List<TaggedImage> images, Transform xform) {
        Antecedent[] ants = Antecedent.createAntecedentsFor(relPath, images);
        DerivedFrom df = new DerivedFrom(ants, xform);
        return df;
    }

    public void addToMetadata(JSONObject meta) {
        try {
            JSONObject parameters_ = new JSONObject();
            for (Map.Entry<String, Object> entry : transform.parameters.entrySet()) {
                //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                if (entry.getValue() instanceof Antecedent[]) {
                    JSONArray antecedents_ = new JSONArray();
                    Antecedent[] antsParam = (Antecedent[]) entry.getValue();
                    for (Antecedent antecedent : antsParam) {
                        JSONObject antecedent_ = new JSONObject();
                        antecedent_.put("uuid", antecedent.getUuid());
                        antecedent_.put("uri", antecedent.getUri());
                        JSONArray indices = new JSONArray();
                        for (int i = 0; i < antecedent.getIndices().length; i++) {
                            indices.put(antecedent.getIndices()[i]);
                        }
                        antecedent_.put("indices", indices);
                        antecedents_.put(antecedent_);
                    }
                    parameters_.put(entry.getKey(), antecedents_);
                } else {
                    parameters_.put(entry.getKey(), entry.getValue());
                }
            }
            //
            JSONObject transform_ = new JSONObject();
            transform_.put("transformName", getTransform().transformName);
            transform_.put("parameters", parameters_);
            //
            JSONArray antecedents_ = new JSONArray();
            for (Antecedent antecedent : antecedents) {
                JSONObject antecedent_ = new JSONObject();
                antecedent_.put("uuid", antecedent.getUuid());
                antecedent_.put("uri", antecedent.getUri());
                JSONArray indices = new JSONArray();
                for (int i = 0; i < antecedent.getIndices().length; i++) {
                    indices.put(antecedent.getIndices()[i]);
                }
                antecedent_.put("indices", indices);
                antecedents_.put(antecedent_);
            }
            JSONObject derivedFrom = new JSONObject();
            derivedFrom.put(Antecedent.JSON_KEY, antecedents_);
            derivedFrom.put(Transform.JSON_KEY, transform_);
            // System.out.println(derivedFrom.toString(4));
            meta.put(DerivedFrom.JSON_KEY, derivedFrom);

        } catch (JSONException ex) {
            Logger.getLogger(DerivedFrom.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //=======================================================================================
    // Retrieving DerivedFrom from metadata...
    //
    // get DerivedFrom from SummaryMetadata
    // using the Parameter map for the transform.
    
    public static DerivedFrom getDerivedFrom(JSONObject map) throws JSONException {
        return getDerivedFrom(map, null);
    }

    public static DerivedFrom getDerivedFrom(JSONObject map, Map<String, Object> parMap) throws JSONException {
        if (map.has("DerivedFrom")) {

            JSONObject derivedFrom_ = map.getJSONObject("DerivedFrom");
            // Antecedents
            JSONArray antecedents_ = derivedFrom_.getJSONArray(Antecedent.JSON_KEY);
            Antecedent[] ants = parseAntecedents(antecedents_);
            // Transform
            JSONObject transform_ = derivedFrom_.getJSONObject(Transform.JSON_KEY);
            String tranformName = transform_.getString("transformName");
            // Parameters
            // TODO
            JSONObject parameters_ = transform_.getJSONObject("parameters");
            Map<String, Object> parameterMap;
            if (parMap == null) {
                parameterMap = getXformParameterMap(tranformName);
            } else {
                parameterMap = parMap;
            }

            for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
                //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                if (entry.getValue() instanceof Antecedent[]) {
                    Antecedent[] antsParam = parseAntecedents(
                            parameters_.getJSONArray(entry.getKey()));
                    parameterMap.put(entry.getKey(), antsParam);
                } else {
                    parameterMap.put(entry.getKey(), entry.getValue());
                }
            }
            Transform transform = new Transform(tranformName, parameterMap);
            // We need to define the parameters for the Transform: 
            // this will require a way to find the transform definition.
            // find the class with the transformName 
            // for test 
            // String xformName = "edu.mbl.jif.imaging.dataset.linked.tests.Magort";
            // e.g. edu.mbl.ps.xform.MagOrt

            return new DerivedFrom(ants, transform);
        } else {
            return null;
        }
    }

    static Map<String, Object> getXformParameterMap(String transformName) {
        System.out.println("Finding getXformParameterMap for " + transformName);
        Xform xform = null;
        try {
            xform = (Xform) Class.forName(transformName).newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (xform != null) {
            return xform.getParameterMap();
        } else {
            return null;
        }
    }

    static Antecedent[] parseAntecedents(JSONArray antecedents_) {
        try {
            Antecedent[] ants = new Antecedent[antecedents_.length()];
            for (int a = 0; a < antecedents_.length(); a++) {
                JSONObject antecedent_ = antecedents_.getJSONObject(a);
                //
                URI uri = URI.create(antecedent_.getString("uri"));
                //
                UUID uuid = UUID.fromString(antecedent_.getString("uuid"));
                //
                JSONArray indices_ = antecedent_.optJSONArray("indices");
                int[] indices = new int[indices_.length()];
                for (int i = 0; i < indices_.length(); i++) {
                    indices[i] = indices_.getInt(i);
                }
                ants[a] = new Antecedent(uuid, uri, indices);
            }
            return ants;
        } catch (JSONException ex) {
            Logger.getLogger(DerivedFrom.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    static Antecedent[] parseAntecedents(JSONArray antecedents_, String sourcePath) {
        try {
            Antecedent[] ants = new Antecedent[antecedents_.length()];
            for (int a = 0; a < antecedents_.length(); a++) {
                JSONObject antecedent_ = antecedents_.getJSONObject(a);
                //
                URI basePath = null;
                try {
                    basePath = new URI("file:///" + sourcePath + "/"); // assumes localhost
                } catch (URISyntaxException ex) {
                    Logger.getLogger(DerivedFrom.class.getName()).log(Level.SEVERE, null, ex);
                }
                URI uri = basePath.resolve(antecedent_.getString("uri"));
                //
                UUID uuid = UUID.fromString(antecedent_.getString("uuid"));
                //
                JSONArray indices_ = antecedent_.optJSONArray("indices");
                int[] indices = new int[indices_.length()];
                for (int i = 0; i < indices_.length(); i++) {
                    indices[i] = indices_.getInt(i);
                }
                ants[a] = new Antecedent(uuid, uri, indices);
            }
            return ants;
        } catch (JSONException ex) {
            Logger.getLogger(DerivedFrom.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}


/*  Example:
 * Frame-Level
{
    "Transform": {
        "transformName": "edu.mbl.ps.magort",
        "parameters": {
            "wavelength": 546,
            "azimuthRef": 90,
            "BackgroundImages": [
                {
                    "indices": [
                        2,
                        9,
                        9,
                        0
                    ],
                    "uuid": "77f64b5f-b8b2-43fe-8f7e-6b9585872ec2",
                    "uri": "file:/C:/MicroManagerData/TestDatasetGen/pssynth_100/pssynth_100_MMImages.ome.tif"
                },
                {
                    "indices": [
                        3,
                        9,
                        9,
                        0
                    ],
                    "uuid": "2ec6def1-5593-486b-b5e2-09ff5d9f5941",
                    "uri": "file:/C:/MicroManagerData/TestDatasetGen/pssynth_100/pssynth_100_MMImages.ome.tif"
                },
                {
                    "indices": [
                        4,
                        9,
                        9,
                        0
                    ],
                    "uuid": "9902bb03-b3eb-43c9-80f6-45efd277443f",
                    "uri": "file:/C:/MicroManagerData/TestDatasetGen/pssynth_100/pssynth_100_MMImages.ome.tif"
                },
                {
                    "indices": [
                        5,
                        9,
                        9,
                        0
                    ],
                    "uuid": "a1d9022d-6003-4799-8adf-09c63deafdbe",
                    "uri": "file:/C:/MicroManagerData/TestDatasetGen/pssynth_100/pssynth_100_MMImages.ome.tif"
                },
                {
                    "indices": [
                        6,
                        9,
                        9,
                        0
                    ],
                    "uuid": "b8015a1d-4e2f-463b-ada1-74a91e094244",
                    "uri": "file:/C:/MicroManagerData/TestDatasetGen/pssynth_100/pssynth_100_MMImages.ome.tif"
                }
            ],
            "retCeiling": 100,
            "doBkgdCorrect": true,
            "zeroIntensity": 0,
            "swingFraction": 0.3,
            "algorithm": 5
        }
    },
    "Antecedent": [
        {
            "indices": [
                2,
                9,
                9,
                0
            ],
            "uuid": "77f64b5f-b8b2-43fe-8f7e-6b9585872ec2",
            "uri": "file:/C:/MicroManagerData/TestDatasetGen/pssynth_100/pssynth_100_MMImages.ome.tif"
        },
        {
            "indices": [
                3,
                9,
                9,
                0
            ],
            "uuid": "2ec6def1-5593-486b-b5e2-09ff5d9f5941",
            "uri": "file:/C:/MicroManagerData/TestDatasetGen/pssynth_100/pssynth_100_MMImages.ome.tif"
        },
        {
            "indices": [
                4,
                9,
                9,
                0
            ],
            "uuid": "9902bb03-b3eb-43c9-80f6-45efd277443f",
            "uri": "file:/C:/MicroManagerData/TestDatasetGen/pssynth_100/pssynth_100_MMImages.ome.tif"
        },
        {
            "indices": [
                5,
                9,
                9,
                0
            ],
            "uuid": "a1d9022d-6003-4799-8adf-09c63deafdbe",
            "uri": "file:/C:/MicroManagerData/TestDatasetGen/pssynth_100/pssynth_100_MMImages.ome.tif"
        },
        {
            "indices": [
                6,
                9,
                9,
                0
            ],
            "uuid": "b8015a1d-4e2f-463b-ada1-74a91e094244",
            "uri": "file:/C:/MicroManagerData/TestDatasetGen/pssynth_100/pssynth_100_MMImages.ome.tif"
        }
    ]
}
* ======================================================================================
* Example Summary-Level
* 
* {
  "Slices": 10,
  "Binning": 1,
  "UUID": "8c954e0e-5995-4a8e-ace6-8f64012e0f47",
  "UserName": "GBH",
  "Depth": 1,
  "PixelType": "GRAY8",
  "Time": "Thu Mar 07 12:24:35 EST 2013",
  "DerivedFrom": {
    "Transform": {
      "transformName": "edu.mbl.jif.ps.PsCalcProcess.ProcessMagOrt",
      "parameters": {
        "azimuthRef": 90,
        "wavelength": 546,
        "retCeiling": 100,
        "BackgroundImages": [{
          "indices": [
            3,
            0,
            1,
            2,
            3,
            4
          ],
          "uuid": "cb779522-65e4-4bc3-a799-76b233f32747",
          "uri": "pssynthBG"
        }],
        "doBkgdCorrect": true,
        "zeroIntensity": 0,
        "swingFraction": 0.3,
        "algorithm": 5
      }
    },
    "Antecedent": [{
      "indices": [
        3,
        0,
        1,
        2,
        3,
        4
      ],
      "uuid": "c2a740db-3d20-4315-9e1e-13624af2622b",
      "uri": "pssynth_100"
    }]
  },
  "Date": "2013-03-07",
  "MetadataVersion": 10,
  "ChContrastMin": [
    0,
    0
  ],
  "Width": 201,
  "SlicesFirst": true,
  "PixelAspect": 1,
  "MicroManagerVersion": "1.4",
  "ChNames": [
    "Mag",
    "Ort"
  ],
  "IJType": 0,
  "Comment": "",
  "Height": 181,
  "Frames": 10,
  "Prefix": "magort",
  "PixelSize_um": 0,
  "BitDepth": 8,
  "Source": "undefined",
  "ComputerName": "GBH-VAIO",
  "Channels": 2,
  "ChColors": [
    -1,
    -1
  ],
  "TimeFirst": false,
  "ChContrastMax": [
    255,
    255
  ],
  "Positions": 1,
  "Directory": "C:/MicroManagerData/TestDatasetGen"
}
 */