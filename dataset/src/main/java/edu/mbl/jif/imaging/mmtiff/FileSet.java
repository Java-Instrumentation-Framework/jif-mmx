package edu.mbl.jif.imaging.mmtiff;
/*
 * 
FILE:          TaggedImageStorageMultipageTiff.java
PROJECT:       Micro-Manager
SUBSYSTEM:     mmstudio
-----------------------------------------------------------------------------

 AUTHOR:       Henry Pinkard, henry.pinkard@gmail.com, 2012

 COPYRIGHT:    University of California, San Francisco, 2012

 LICENSE:      This file is distributed under the BSD license.
               License text is included with the source distribution.

               This file is distributed in the hope that it will be useful,
               but WITHOUT ANY WARRANTY; without even the implied warranty
               of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

               IN NO EVENT SHALL THE COPYRIGHT OWNER OR
               CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
               INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES.
*/

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.TreeSet;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * Class encapsulating a single File (or series of files)
 * Default is one file series per xy posititon
 * 
 * @author GBH
 */

public class FileSet {

    private LinkedList<MultipageTiffWriter> tiffWriters_;
    private FileWriter mdWriter_;
    private String baseFilename_;
    private String currentTiffFilename_;
    private String metadataFileFullPath_;
    private boolean finished_ = false;
    private int ifdCount_ = 0;
    private TaggedImageStorageMultipageTiff mpTiff_;
    int nextExpectedChannel_ = 0, nextExpectedSlice_ = 0, nextExpectedFrame_ = 0;

    public FileSet(JSONObject firstImageTags, TaggedImageStorageMultipageTiff mpt) {
        tiffWriters_ = new LinkedList<MultipageTiffWriter>();
        mpTiff_ = mpt;

        //get file path and name
        baseFilename_ = createBaseFilename(firstImageTags);
        currentTiffFilename_ = baseFilename_ + (mpt.omeTiff_ ? ".ome.tif" : ".tif");
        //make first writer
        tiffWriters_.add(new MultipageTiffWriter(mpTiff_.directory_,
                currentTiffFilename_, mpTiff_.summaryMetadata_, mpTiff_));

        try {
            if (mpTiff_.seperateMetadataFile_) {
                startMetadataFile();
            }
        } catch (JSONException ex) {
            ReportingUtils.showError("Problem with summary metadata");
        }
    }

       private String createBaseFilename(JSONObject firstImageTags) {
        String baseFilename = "";
        try {
            String prefix = mpTiff_.summaryMetadata_.getString("Prefix");
            if (prefix.length() == 0) {
                baseFilename = "MMImages";
            } else {
                baseFilename = prefix + "_MMImages";
            }
        } catch (JSONException ex) {
            ReportingUtils.logError("Can't find Prefix in summary metadata");
            baseFilename = "MMImages";
        }

        if (mpTiff_.numPositions_ > 1 && mpTiff_.splitByXYPosition_) {
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
       
    public MultipageTiffReader getCurrentReader() {
        return tiffWriters_.getLast().getReader();
    }

    public void writeImage(TaggedImage img) throws IOException {
        //check if current writer is out of space, if so, make a new one
        if (!tiffWriters_.getLast().hasSpaceToWrite(img, mpTiff_.omeTiff_ ? estimateOMEMDSize() : 0)) {
            currentTiffFilename_ = baseFilename_ + "_" + tiffWriters_.size() + (mpTiff_.omeTiff_ ? ".ome.tif" : ".tif");
            ifdCount_ = 0;
            tiffWriters_.add(new MultipageTiffWriter(mpTiff_.directory_, currentTiffFilename_, mpTiff_.summaryMetadata_, mpTiff_));
        }
        //Add filename to image tags
        try {
            img.tags.put("FileName", currentTiffFilename_);
        } catch (JSONException ex) {
            ReportingUtils.logError("Error adding filename to metadata");
        }
        //write image
        tiffWriters_.getLast().writeImage(img);

        if (mpTiff_.expectedImageOrder_) {
            if (mpTiff_.splitByXYPosition_) {
                checkForExpectedImageOrder(img.tags);
            } else {
                mpTiff_.expectedImageOrder_ = false;
            }
        }
        //write metadata
        if (mpTiff_.omeTiff_) {
            try {
                mpTiff_.omeMetadata_.addImageTagsToOME(img.tags, ifdCount_, baseFilename_, currentTiffFilename_);
            } catch (Exception ex) {
                ReportingUtils.logError("Problem writing OME metadata");
            }
        }
        try {
            int frame = MDUtils.getFrameIndex(img.tags);
            mpTiff_.lastFrame_ = Math.max(frame, mpTiff_.lastFrame_);
        } catch (JSONException ex) {
            ReportingUtils.showError("Couldn't find frame index in image tags");
        }
        try {
            if (mpTiff_.seperateMetadataFile_) {
                writeToMetadataFile(img.tags);
            }
        } catch (JSONException ex) {
            ReportingUtils.logError("Problem with image metadata");
        }
        ifdCount_++;
    }

    // <editor-fold defaultstate="collapsed" desc=" Metadata ">
    private int estimateOMEMDSize() {
        return mpTiff_.totalNumImagePlanes_ * mpTiff_.omeMetadata_.getOMEMetadataImageLength()
                + mpTiff_.numPositions_ * mpTiff_.omeMetadata_.getOMEMetadataBaseLenght();
    }

    private void writeToMetadataFile(JSONObject md) throws JSONException {
        try {
            mdWriter_.write(",\r\n\"FrameKey-" + MDUtils.getFrameIndex(md)
                    + "-" + MDUtils.getChannelIndex(md) + "-" + MDUtils.getSliceIndex(md) + "\": ");
            mdWriter_.write(md.toString(2));
        } catch (IOException ex) {
            ReportingUtils.logError("Problem writing to metadata.txt file");
        }
    }

    private void startMetadataFile() throws JSONException {
        metadataFileFullPath_ = mpTiff_.directory_ + "/" + baseFilename_ + "_metadata.txt";
        try {
            mdWriter_ = new FileWriter(metadataFileFullPath_);
            mdWriter_.write("{" + "\r\n");
            mdWriter_.write("\"Summary\": ");
            mdWriter_.write(mpTiff_.summaryMetadata_.toString(2));
        } catch (IOException ex) {
            ReportingUtils.logError("Problem creating metadata.txt file");
        }
    }

    private void finishMetadataFile() throws JSONException {
        try {
            mdWriter_.write("\r\n}\r\n");
            mdWriter_.close();
        } catch (IOException ex) {
            ReportingUtils.logError("Problem creating metadata.txt file");
        }
    }
    // </editor-fold>

    public void finished() throws IOException {
        if (finished_) {
            return;
        }

        try {
            if (mpTiff_.seperateMetadataFile_) {
                finishMetadataFile();
            }
        } catch (JSONException ex) {
            ReportingUtils.logError("Problem finishing metadata.txt");
        }
        if (mpTiff_.omeXML_ == null) {
            mpTiff_.omeXML_ = mpTiff_.omeMetadata_.toString();
        }
        for (MultipageTiffWriter w : tiffWriters_) {
            w.close(mpTiff_.omeXML_);
        }
        finished_ = true;
    }

    /**
     * Generate all expected labels for the last frame, and write dummy images for ones that weren't
     * written. Modify ImageJ and OME max number of frames as appropriate. This method only works if
     * xy positions are split across separate files
     */
    public void finishAbortedAcqIfNeeded() {
        if (mpTiff_.expectedImageOrder_ && mpTiff_.splitByXYPosition_ && !mpTiff_.timeFirst()) {
            try {
                //One position may be on the next frame compared to others. Complete each position
                //with blank images to fill this frame
                completeFrameWithBlankImages(mpTiff_.lastAcquiredFrame());
            } catch (Exception e) {
                ReportingUtils.logError("Problem finishing aborted acq with blank images");
            }
        }
    }

    /**
     * Completes the current time point of an aborted acquisition with blank images, so that it can
     * be opened correctly by ImageJ/BioForamts
     */
    private void completeFrameWithBlankImages(int frame) throws JSONException, MMScriptException {

        int numFrames = MDUtils.getNumFrames(mpTiff_.summaryMetadata_);
        int numSlices = MDUtils.getNumSlices(mpTiff_.summaryMetadata_);
        int numChannels = MDUtils.getNumChannels(mpTiff_.summaryMetadata_);
        if (numFrames > frame + 1) {
            TreeSet<String> writtenImages = new TreeSet<String>();
            for (MultipageTiffWriter w : tiffWriters_) {
                writtenImages.addAll(w.getIndexMap().keySet());
                w.setAbortedNumFrames(frame + 1);
            }
            int positionIndex = MDUtils.getIndices(writtenImages.first())[3];
            if (mpTiff_.omeTiff_) {
                mpTiff_.omeMetadata_.setNumFrames(positionIndex, frame + 1);
            }
            TreeSet<String> lastFrameLabels = new TreeSet<String>();
            for (int c = 0; c < numChannels; c++) {
                for (int z = 0; z < numSlices; z++) {
                    lastFrameLabels.add(MDUtils.generateLabel(c, z, frame, positionIndex));
                }
            }
            lastFrameLabels.removeAll(writtenImages);
            try {
                for (String label : lastFrameLabels) {
                    tiffWriters_.getLast().writeBlankImage(label);
                    if (mpTiff_.omeTiff_) {
                        JSONObject dummyTags = new JSONObject();
                        int channel = Integer.parseInt(label.split("_")[0]);
                        int slice = Integer.parseInt(label.split("_")[1]);
                        MDUtils.setChannelIndex(dummyTags, channel);
                        MDUtils.setFrameIndex(dummyTags, frame);
                        MDUtils.setSliceIndex(dummyTags, slice);
                        mpTiff_.omeMetadata_.addImageTagsToOME(dummyTags, ifdCount_, baseFilename_, currentTiffFilename_);
                    }
                }
            } catch (IOException ex) {
                ReportingUtils.logError("problem writing dummy image");
            }
        }
    }

    void checkForExpectedImageOrder(JSONObject tags) {
        try {
            //Determine next expected indices
            int channel = MDUtils.getChannelIndex(tags), frame = MDUtils.getFrameIndex(tags),
                    slice = MDUtils.getSliceIndex(tags);
            if (slice != nextExpectedSlice_ || channel != nextExpectedChannel_
                    || frame != nextExpectedFrame_) {
                mpTiff_.expectedImageOrder_ = false;
            }
            //Figure out next expected indices
            if (mpTiff_.slicesFirst()) {
                nextExpectedSlice_ = slice + 1;
                if (nextExpectedSlice_ == mpTiff_.numSlices_) {
                    nextExpectedSlice_ = 0;
                    nextExpectedChannel_ = channel + 1;
                    if (nextExpectedChannel_ == mpTiff_.numChannels_) {
                        nextExpectedChannel_ = 0;
                        nextExpectedFrame_ = frame + 1;
                    }
                }
            } else {
                nextExpectedChannel_ = channel + 1;
                if (nextExpectedChannel_ == mpTiff_.numChannels_) {
                    nextExpectedChannel_ = 0;
                    nextExpectedSlice_ = slice + 1;
                    if (nextExpectedSlice_ == mpTiff_.numSlices_) {
                        nextExpectedSlice_ = 0;
                        nextExpectedFrame_ = frame + 1;
                    }
                }
            }
        } catch (JSONException ex) {
            ReportingUtils.logError("Couldnt find channel, slice, or frame index in Image tags");
            mpTiff_.expectedImageOrder_ = false;
        }
    }
}