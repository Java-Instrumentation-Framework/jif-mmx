package edu.mbl.jif.imaging.dataset.metadata;

import edu.mbl.jif.imaging.mmtiff.MDUtils;
import edu.mbl.jif.imaging.mmtiff.MMImageCache;
import edu.mbl.jif.imaging.mmtiff.ReportingUtils;
import java.awt.Color;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 *
 * @author GBH
 */
public class MMgrDisplayAndComments {

	final static Color[] rgb = {Color.red, Color.green, Color.blue};
	final static String[] rgbNames = {"Red", "Blue", "Green"};

	public static JSONObject getDisplaySettingsFromSummary(JSONObject summaryMetadata) {
		try {
			JSONObject displaySettings = new JSONObject();
			JSONArray chColors = MDUtils.getJSONArrayMember(summaryMetadata, "ChColors");
			JSONArray chNames = MDUtils.getJSONArrayMember(summaryMetadata, "ChNames");
			JSONArray chMaxes, chMins;
			if (summaryMetadata.has("ChContrastMin")) {
				chMins = MDUtils.getJSONArrayMember(summaryMetadata, "ChContrastMin");
			} else {
				chMins = new JSONArray();
				for (int i = 0; i < chNames.length(); i++) {
					chMins.put(0);
				}
			}
			if (summaryMetadata.has("ChContrastMax")) {
				chMaxes = MDUtils.getJSONArrayMember(summaryMetadata, "ChContrastMax");
			} else {
				int max = 65536;
				if (summaryMetadata.has("BitDepth")) {
					max = (int) (Math.pow(2, summaryMetadata.getInt("BitDepth")) - 1);
				}
				chMaxes = new JSONArray();
				for (int i = 0; i < chNames.length(); i++) {
					chMaxes.put(max);
				}
			}
			int numComponents = MDUtils.getNumberOfComponents(summaryMetadata);
			JSONArray channels = new JSONArray();
			if (numComponents > 1) //RGB
			{
				int rgbChannelBitDepth;
				try {
					rgbChannelBitDepth = MDUtils.getBitDepth(summaryMetadata);
				} catch (Exception e) {
					rgbChannelBitDepth = summaryMetadata.getString("PixelType").endsWith("32") ? 8 : 16;
				}
				for (int k = 0; k < 3; k++) {
					JSONObject channelObject = new JSONObject();
					channelObject.put("Color", rgb[k].getRGB());
					channelObject.put("Name", rgbNames[k]);
					channelObject.put("Gamma", 1.0);
					channelObject.put("Min", 0);
					channelObject.put("Max", Math.pow(2, rgbChannelBitDepth) - 1);
					channelObject.put("DisplayMode", 3);
					channels.put(channelObject);
				}
			} else {
				for (int k = 0; k < chNames.length(); ++k) {
					String name = (String) chNames.get(k);
					int color = 0;
					if (k < chColors.length()) {
						color = chColors.getInt(k);
					}
					int min = 0;
					if (k < chMins.length()) {
						min = chMins.getInt(k);
					}
					int max = chMaxes.getInt(0);
					if (k < chMaxes.length()) {
						max = chMaxes.getInt(k);
					}
					JSONObject channelObject = new JSONObject();
					channelObject.put("Color", color);
					channelObject.put("Name", name);
					channelObject.put("Gamma", 1.0);
					channelObject.put("Min", min);
					channelObject.put("Max", max);
					channelObject.put("DisplayMode", 3);
					channels.put(channelObject);
				}
			}
			displaySettings.put("Channels", channels);
			JSONObject comments = new JSONObject();
			String summary = "";
			try {
				summary = summaryMetadata.getString("Comment");
			} catch (JSONException ex) {
				summaryMetadata.put("Comment", "");
			}
			comments.put("Summary", summary);
			displaySettings.put("Comments", comments);
			return displaySettings;
		} catch (Exception e) {
			ReportingUtils.showError("Summary metadata not found or corrupt.  Is this a Micro-Manager dataset?");
			return null;
		}
	}

	static public void storeChannelDisplaySettings(JSONObject displayAndComments,
			int channelIndex, String name, int min, int max,
			double gamma, int histMax, int color, int displayMode) {
		try {
			JSONObject settings = getChannelSetting(channelIndex, displayAndComments);
			settings.put("Name", name);
			settings.put("Max", max);
			settings.put("Min", min);
			settings.put("Gamma", gamma);
			settings.put("HistogramMax", histMax);
			settings.put("Color", color);
			settings.put("DisplayMode", displayMode);
		} catch (Exception ex) {
			ReportingUtils.logError(ex);
		}
	}

	static public JSONObject getChannelSetting(int channel, JSONObject displayAndComments) {
		try {
			JSONArray array = displayAndComments.getJSONArray("Channels");
			if (channel >= array.length()) {
				//expand size
				array.put(channel, new JSONObject(array.getJSONObject(0).toString()));
			}
			if (array != null && !array.isNull(channel)) {
				return array.getJSONObject(channel);
			} else {
				return null;
			}
		} catch (Exception ex) {
			ReportingUtils.logError(ex);
			return null;
		}
	}
	
      

	public void method(MMImageCache iCache) {
		JSONObject displayAndComments = iCache.getDisplayAndComments();


		// for each channel...
//		try {
//			JSONObject channel0setting = displayAndComments.getJSONArray("Channels").getJSONObject(0);
//			if (numChannels_ == 1) {
//				double min = channel0setting.getInt("Min");
//				double max = channel0setting.getInt("Max");
//				sb.append("min=" + min + "\n");
//				sb.append("max=" + max + "\n");
//			} else {
//				int displayMode = channel0setting.getInt("DisplayMode");
//				//COMPOSITE=1, COLOR=2, GRAYSCALE=3
//				if (displayMode == 1) {
//					sb.append("mode=composite\n");
//				} else if (displayMode == 2) {
//					sb.append("mode=color\n");
//				} else if (displayMode == 3) {
//					sb.append("mode=gray\n");
//				}
//			}
//		} catch (JSONException ex) {
//		}

		// Name and Color...
		int channel = 0;

		if (displayAndComments.length() > 0) {
			try {
				JSONArray channelSettings = displayAndComments.getJSONArray("Channels");
				JSONObject imageTags = null; //taggedImg.tags;
				int chanIndex = MDUtils.getChannelIndex(imageTags);
				if (chanIndex >= channelSettings.length()) {
					JSONObject newChanObject = new JSONObject();
					newChanObject.put("Name", MDUtils.getChannelName(imageTags));
					newChanObject.put("Color", MDUtils.getChannelColor(imageTags));
					channelSettings.put(chanIndex, newChanObject);
				}
			} catch (JSONException ex) {
			}
		}

	}
	// Comments...

	private JSONObject getCommentsJSONObject(JSONObject displayAndComments) {
		JSONObject comments;
		try {
			comments = displayAndComments.getJSONObject("Comments");
		} catch (JSONException ex) {
			comments = new JSONObject();
			try {
				displayAndComments.put("Comments", comments);
			} catch (JSONException ex1) {
				ReportingUtils.logError(ex1);
			}
		}
		return comments;
	}

      
	public void setComment(String text, JSONObject displayAndComments) {
		JSONObject comments = getCommentsJSONObject(displayAndComments);
		try {
			comments.put("Summary", text);
		} catch (JSONException ex) {
			ReportingUtils.logError(ex);
		}
	}

	public String getComment(JSONObject displayAndComments) {
		try {
			return getCommentsJSONObject(displayAndComments).getString("Summary");
		} catch (Exception ex) {
			return "";
		}
	}
      
      // ??? Summary Comment
	// metadata_.setImageDescription(comments.getString("Summary"), seriesIndex);

	String getComments(JSONObject displayAndComments) {
		JSONObject comments;
		try {
			comments = displayAndComments.getJSONObject("Comments");
		} catch (JSONException ex) {
			comments = new JSONObject();
		}
		String commentsString = comments.toString();
		return commentsString;
	}

	public void setImageComment(String comment, JSONObject tags, JSONObject displayAndComments) {
		JSONObject comments = getCommentsJSONObject(displayAndComments);
		String label = MDUtils.getLabel(tags);
		try {
			comments.put(label, comment);
		} catch (JSONException ex) {
			ReportingUtils.logError(ex);
		}
	}

	public String getImageComment(JSONObject tags, JSONObject displayAndComments) {
		if (tags == null) {
			return "";
		}
		try {
			String label = MDUtils.getLabel(tags);
			return getCommentsJSONObject(displayAndComments).getString(label);
		} catch (Exception ex) {
			return "";
		}
	}



}
