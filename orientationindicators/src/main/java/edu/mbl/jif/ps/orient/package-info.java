package edu.mbl.jif.ps.orient;

/*
 Mapping Variables to Indicator Attributes
 
 length      ---     Anisotropy (or intensity)
 width       ---     Variance
 alpha       ---     Anisotropy (or intensity)
 hue         ---     Orientation
 saturation  ---     Variance
 brightness  ---     ?, fixed
 
 Types: FAN, ELLIPSE, LINE
  
 
 Inputs 
 ===================================
 ImagePlus/Dataset
 
 Types: FAN, ELLIPSE, LINE
 
 Dropshadow
 Center point marker
 
 Grid size
   (x,y offset?)
 
 Length (generally < grid size)
   Length mapped to anisotropy
   Change scale with magnification level
   
 Threshold (of anisotropy) for drawing (min, max)
 
 Color
   Fixed
   Hue mapped to orientation
 Transparency
   Fixed
      Alpha mapped to anisotropy (or intensity)
  
  Use listener (?)
 
      gd.addNumericField("lineWidth: ", lineWidth, 2, 8, "pixel");
      gd.addNumericField("lineLength: ", lineLength, 2, 8, "interval");
      gd.addChoice("lengthPropRatio: ", yesNoQL, ratioLengthChoice);
      gd.addNumericField("min. ratio: ", ratioMin, 2, 8, "(0... 1)");
      gd.addNumericField("max. ratio: ", ratioMax, 2, 8, "(0... 1)");
      gd.addChoice("Color: ", UtilsUI.colors, color);
      gd.addNumericField("opacity: ", lineOpacity, 2, 8, "(0... 1)");
      gd.addNumericField("xOffset: ", xOffset, 0, 8, "pixel");
      gd.addNumericField("yOffset: ", yOffset, 0, 8, "pixel");
      gd.addChoice("draw_outline:", yesNoQL, dropshadow);
      gd.addChoice("use_listener:", yesNoQL, "Yes");
      gd.addChoice("show_circle:", yesNoQL, ShowCircles);
      
 */
