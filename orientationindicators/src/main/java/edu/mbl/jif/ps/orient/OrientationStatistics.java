package edu.mbl.jif.ps.orient;
import ij.measure.*;
import ij.process.ImageProcessor;
import java.awt.*;
import java.awt.geom.Point2D;

/** Statistics, including the histogram, of an image or selection. */
public class OrientationStatistics implements Measurements {

	
	public double xCentroid;
	public double yCentroid;
	public double roiX, roiY, roiWidth, roiHeight;
   protected int rx, ry, rw, rh;
	protected double pw, ph;
	protected Calibration cal;
   protected int width, height;
	
void setup(ImageProcessor ip, Calibration cal) {
		width = ip.getWidth();
		height = ip.getHeight();
		this.cal = cal;
		Rectangle roi = ip.getRoi();
		if (roi != null) {
			rx = roi.x;
			ry = roi.y;
			rw = roi.width;
			rh = roi.height;
		}
		else {
			rx = 0;
			ry = 0;
			rw = width;
			rh = height;
		}
		
		if (cal!=null) {
			pw = cal.pixelWidth;
			ph = cal.pixelHeight;
		} else {
			pw = 1.0;
			ph = 1.0;
		}
		
		roiX = cal!=null?cal.getX(rx):rx;
		roiY = cal!=null?cal.getY(ry, height):ry;
		roiWidth = rw*pw;
		roiHeight = rh*ph;
	}
	
	
	
	public Point2D getCentroid(ImageProcessor ip) {
		byte[] mask = ip.getMaskArray();
		int count=0, mi;
		double xsum=0.0, ysum=0.0;
		for (int y=ry,my=0; y<(ry+rh); y++,my++) {
			mi = my*rw;
			for (int x=rx; x<(rx+rw); x++) {
				if (mask==null||mask[mi++]!=0) {
					count++;
					xsum += x;
					ysum += y;
				}
			}
		}
		xCentroid = xsum/count+0.5;
		yCentroid = ysum/count+0.5;
		if (cal!=null) {
			xCentroid = cal.getX(xCentroid);
			yCentroid = cal.getY(yCentroid, height);
		}
      Point2D centroid = new Point2D.Double(xCentroid,yCentroid);
      return centroid;
	}
	
}
