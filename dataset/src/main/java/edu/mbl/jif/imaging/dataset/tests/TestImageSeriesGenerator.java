package edu.mbl.jif.imaging.dataset.tests;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import java.text.SimpleDateFormat;

import java.util.Locale;

/**
 * <p>Title: TestImageSeriesGenerator</p> * <p>Description: Test Image Sequence Generator for 3,4
 * and 5 dimensions </p> * <p>Copyright: Copyright (c) 2012</p>
 *
 * @author GBH
 * @version 1.0
 */
public class TestImageSeriesGenerator {

	static int bitDepth;
	static long timeStamp = 0;
	static BufferedImage image = null;
	static WritableRaster wr;
	static Graphics2D graphics;
	static Font f1B = new Font("Dialog", Font.BOLD, 24);
	static Font f2B = new Font("Dialog", Font.BOLD, 18);
	static Font f3B = new Font("Dialog", Font.BOLD, 14);
	//int screenRes = Toolkit.getDefaultToolkit().getScreenResolution();
	//int fontSize = (int)Math.round(12.0 * screenRes / 72.0);
	static Font f1 = new Font("Dialog", Font.PLAIN, 24);
	static Font f2 = new Font("Dialog", Font.PLAIN, 18);
	static Font f3 = new Font("Dialog", Font.PLAIN, 14);
	static SimpleDateFormat formatter = new SimpleDateFormat(
			"yyyy.MM.dd HH:mm:ss.SSS",
			Locale.getDefault());
	static String dateStr;
	static RenderingHints hints = new RenderingHints(null);
	static int yPos = 100;
	static Color fillColor = Color.gray;
	static final int SHADOW_OFFSET = 1;
//	static int width;
//	static int height;
//	static int sections;
//	static int timePoints;
//	static int channels;

	public static BufferedImage generateImage(
			int width, int height, int _bitDepth,
			int i, int timePoints,
			int j, int sections,
			int c, int channels,
			int p, int positions) {
		Color channelColor = Color.gray;
		StringBuffer id = new StringBuffer();
		BufferedImage image = null;
		if (_bitDepth == 8) {
			try {
				image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
			} catch (Exception e) {
			}
		}
		if (_bitDepth == 16) {
			try {
				image = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
			} catch (Exception e) {
			}
		}
		hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics = (Graphics2D) image.getGraphics();
		graphics.setRenderingHints(hints);
		if (channels > 1) {
			channelColor = new Color(Color.HSBtoRGB((float) c / channels, 0.5f, 0.80f));
			graphics.setColor(channelColor);
		} else {
			graphics.setColor(fillColor);
		}
		graphics.fillRect(0, 0, width, height);
		int dx = width / timePoints;
		int dy = height / sections;
		int offY = j * dy;
		int offX = i * dx;
		graphics.setColor(Color.black);
		graphics.fillRect(offX, offY, dx, dy);

		if (channels > 1) {
			id.append("  C: " + String.valueOf(c));
		}
		if (sections > 1) {
			id.append("  Z: " + String.valueOf(j));
		}
		if (timePoints > 1) {
			id.append("  T: " + String.valueOf(i));
		}
		if (positions > 1) {
			id.append("  Pos: " + String.valueOf(p));
		}
		graphics.setFont(f3);
		writeShadowed(id.toString(), 10, 50);
		return image;
	}

	public static BufferedImage labelImage(BufferedImage img,
			int c,
			int z,
			int t,
			int p) {
		BufferedImage i = deepCopy(img);
		StringBuffer id = new StringBuffer();
		id.append("  C: " + String.valueOf(c));
		id.append("  Z: " + String.valueOf(z));
		id.append("  T: " + String.valueOf(t));
		id.append("  Pos: " + String.valueOf(p));
		graphics = (Graphics2D) i.getGraphics();
		graphics.setFont(f3);
		writeShadowed(id.toString(), 10, 50);
		return i;
	}

	static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

//    public static void writeLines(String[] lines) {
//        int numberOfLines = lines.length;
//        int perLine = h / numberOfLines;
//        int xPos = 10;
//        for (int i = 0; i < lines.length; i++) {
//            writeShadowed(lines[i], xPos, yPos);
//            yPos = yPos + perLine;
//        }
//    }
	public static void writeShadowed(String s, int x, int y) {
		graphics.setColor(Color.black);
		graphics.drawString(s, x + SHADOW_OFFSET, y + SHADOW_OFFSET);
		graphics.setColor(Color.white);
		graphics.drawString(s, x, y);
	}

}
