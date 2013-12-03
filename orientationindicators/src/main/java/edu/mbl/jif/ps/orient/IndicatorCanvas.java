package edu.mbl.jif.ps.orient;

import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.Roi;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

public class IndicatorCanvas extends ImageCanvas {

   private Vector<Indicator> indicators;
   private double scale = 1.0D;
   private Stroke stroke = new BasicStroke(1);
   private Color defaultColor = Color.green;
   private float alpha = 1.0f;
   private boolean forExport = false;
   private boolean enabled = true;
   private boolean hideImage = false;

   public IndicatorCanvas(ImagePlus imp, Vector<Indicator> indicators, double scale,
           int transparency, double orderCoh) {

      super(imp);
      this.imp = imp;
      this.indicators = indicators;
      this.addCursorListener();
//      // make this the ImageCanvas of the ImagePlus by replacing the window.
//      if (imp.getStackSize() == 1) {
//         imp.setWindow(new ImageWindow(imp, this));
//      } else {
//         imp.setWindow(new StackWindow(imp, this));
//      }
//      ImageWindow win = imp.getWindow();
//      Panel p = new Panel();
//      p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
//      // TODO add readout for anisotropy/orientation values by pixel
//      // CheckBox for enable/disable indicators
//      Label l = new Label("(x,y)  AA.A, OO");
//      l.setPreferredSize(new Dimension(40,24));
//      Checkbox ch = new Checkbox("Indicators");
//      p.add(l);
//      p.add(ch);
//      p.add(new Button("Set"));
//      win.add(p);
   }
   private Orientation_Indicators indicatorMaker = null;

   void setIndicatorMaker(Orientation_Indicators indicatorMaker) {
      this.indicatorMaker = indicatorMaker;
   }

   public void setStrokeWidth(float width) {
      stroke = new BasicStroke(width);
   }

   public void setIndicatorsColor(Color color) {
      this.defaultColor = color;
   }

   public void setIndicators(Vector<Indicator> indicator) {
      this.indicators = indicator;
   }

   @Override
   public void setMagnification(double magnification) {
      super.setMagnification(magnification);
      //if (magnification < 10) {
      if (indicatorMaker != null) {
         indicatorMaker.rescaleIndicators((float) magnification);
      }

      //}
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public void setHideImage(boolean hide) {
      this.hideImage = hide;
   }

   public void setForExport(boolean forExport) {
      this.forExport = forExport;
   }

   public void addCursorListener() {
      //this.indicatorMaker;
      // TODO determine the slice selected, set the factor for it.
      // Factors for each slice
      this.addMouseMotionListener(new MouseMotionAdapter() {
         @Override
         public void mouseMoved(MouseEvent evt) {
            super.mouseMoved(evt);
            int sx = evt.getX();
            int sy = evt.getY();
            int ox = offScreenX(sx);
            int oy = offScreenY(sy);
            flags = evt.getModifiers();
            if (indicatorMaker != null) {
               indicatorMaker.cursorMoved(ox, oy);
            }
         }
      });

   }

   @Override
   public void paint(Graphics g) {
      if (!forExport) {
         super.paint(g);
      }
      if (!enabled) {
         return;
      }
      if (forExport) {
         //Roi roi = imp.getRoi();
         Image img = imp.getImage();
         if (img != null) {
            g.drawImage(img, 0, 0, (int) (srcRect.width * magnification), (int) (srcRect.height
                    * magnification),
                    srcRect.x, srcRect.y, srcRect.x + srcRect.width, srcRect.y + srcRect.height,
                    null);
         }
//         if (roi != null) {
//            drawRoi(roi, g);
//         }
      }
      Graphics2D g2 = (Graphics2D) g;
      RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
              RenderingHints.VALUE_ANTIALIAS_ON);
      rh.add(new RenderingHints(RenderingHints.KEY_INTERPOLATION,
              RenderingHints.VALUE_INTERPOLATION_BILINEAR));
      g2.setRenderingHints(rh);

      if (hideImage) {
         g.setColor(Color.LIGHT_GRAY);
         g.fillRect(0, 0, this.getWidth(), this.getHeight());
         g.setColor(defaultColor);
      }
      if (this.indicators == null) {
         return;
      }
      AffineTransform aTx = (((Graphics2D) g).getDeviceConfiguration()).getDefaultTransform();
      if (stroke != null) {
         g2.setStroke(getScaledStroke((BasicStroke) stroke));
      }
      double mag = getMagnification();
      Rectangle srcRect = getSrcRect();
      int basex = srcRect.x;
      int basey = srcRect.y;
      aTx.setTransform(mag, 0.0, 0.0, mag, -basex * mag, -basey * mag);
      //  aTx.translate(x, y);
      for (int i = 0; i < this.indicators.size(); i++) {
         Indicator indicator = indicators.get(i);
         // TODO Only draw indicators on the visible canvas
         if (srcRect.contains(indicator.shape.getBounds2D())) {
            if (indicator.color != null) {
               g2.setColor(indicator.color);
            } else {
               g.setColor(defaultColor);
            }
            if (indicator.stroke != null) {
               g2.setStroke(getScaledStroke((BasicStroke) indicator.stroke));
            }
            //g2.fill(aTx.createTransformedShape(indicator.shape));
            if (indicator.shape instanceof Line2D.Float) { // if a line
               g2.draw(aTx.createTransformedShape(indicator.shape));
            } else {
               g2.fill(aTx.createTransformedShape(indicator.shape));
            }
         }
      }
      if (srcRect.width < imageWidth || srcRect.height < imageHeight) {
         drawZoomIndicator(g);
      }
//      if (stroke != null) {
//         g2d.setStroke(defaultStroke);
//      }
   }

   private void drawRoi(Roi roi, Graphics g) {
      roi.draw(g);
   }

   void drawZoomIndicator(Graphics g) {
      int x1 = 10;
      int y1 = 10;
      double aspectRatio = (double) imageHeight / imageWidth;
      int w1 = 64;
      if (aspectRatio > 1.0) {
         w1 = (int) (w1 / aspectRatio);
      }
      int h1 = (int) (w1 * aspectRatio);
      if (w1 < 4) {
         w1 = 4;
      }
      if (h1 < 4) {
         h1 = 4;
      }
      int w2 = (int) (w1 * ((double) srcRect.width / imageWidth));
      int h2 = (int) (h1 * ((double) srcRect.height / imageHeight));
      if (w2 < 1) {
         w2 = 1;
      }
      if (h2 < 1) {
         h2 = 1;
      }
      int x2 = (int) (w1 * ((double) srcRect.x / imageWidth));
      int y2 = (int) (h1 * ((double) srcRect.y / imageHeight));
      Color zoomIndicatorColor = new Color(128, 128, 255);
      g.setColor(zoomIndicatorColor);
      ((Graphics2D) g).setStroke(Roi.onePixelWide);
      g.drawRect(x1, y1, w1, h1);
      if (w2 * h2 <= 200 || w2 < 10 || h2 < 10) {
         g.fillRect(x1 + x2, y1 + y2, w2, h2);
      } else {
         g.drawRect(x1 + x2, y1 + y2, w2, h2);
      }
   }

   protected BasicStroke getScaledStroke(BasicStroke stroke) {
      double mag = getMagnification();
      if (mag != 1.0) {
         float width = stroke.getLineWidth();
         return new BasicStroke((float) (width * mag), BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
      } else {
         return stroke;
      }
   }
}
