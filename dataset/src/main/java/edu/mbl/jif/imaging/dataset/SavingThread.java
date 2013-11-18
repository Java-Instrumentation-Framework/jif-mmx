package edu.mbl.jif.imaging.dataset;

import edu.mbl.jif.imaging.mmtiff.ImageCache;
import edu.mbl.jif.imaging.mmtiff.MMScriptException;
import edu.mbl.jif.imaging.mmtiff.ReportingUtils;
import edu.mbl.jif.imaging.mmtiff.TaggedImage;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Derived from MMgr LiveAcq
 *
 * @author arthur
 */
public class SavingThread {

  private static int untitledID_ = 0;
  private final BlockingQueue<TaggedImage> imageProducingQueue_;
  private ImageCache imageCache_ = null;

  public SavingThread(
          BlockingQueue imageProducingQueue,
          ImageCache imageCache)
          throws MMScriptException {
    imageProducingQueue_ = imageProducingQueue;
    imageCache_ = imageCache;
  }

  public void start() {
    Thread savingThread = new Thread("MultipageTiff saving thread.") {
      @Override
      public void run() {
        long t1 = System.currentTimeMillis();
        int imageCount = 0;
        try {
          while (true) {
            TaggedImage image = imageProducingQueue_.poll(1, TimeUnit.SECONDS);
            if (image != null) {
              if (TaggedImageQueue.isPoison(image)) {
                break;
              }
              ++imageCount;
              imageCache_.putImage(image);
            }
          }
        } catch (Exception ex2) {
          ReportingUtils.logError(ex2);
        }
        long t2 = System.currentTimeMillis();
        ReportingUtils.logMessage(imageCount + " images saved in " + (t2 - t1) + " ms.");
        imageCache_.finished();
      }
    };
    savingThread.start();
  }

  public ImageCache getImageCache() {
    return imageCache_;
  }
}
