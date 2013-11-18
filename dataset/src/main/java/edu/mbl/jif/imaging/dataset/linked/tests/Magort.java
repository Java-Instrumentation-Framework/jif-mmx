package edu.mbl.jif.imaging.dataset.linked.tests;

import edu.mbl.jif.imaging.dataset.linked.Antecedent;
import edu.mbl.jif.imaging.dataset.linked.Xform;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author GBH
 */
public class Magort implements Xform {

	@Override
	public Map<String, Object> getParameterMap() {
		Map<String, Object> map = new ConcurrentHashMap<String, Object>();
		map.put("wavelength", 546.0f);
		map.put("swingFraction", 0.3f);
		map.put("retCeiling", 100.0f);
		map.put("azimuthRef", 90f);
		map.put("zeroIntensity", 0f);
		map.put("doBkgdCorrect", true);
		map.put("algorithm", 5);
		Antecedent[] ants = new Antecedent[5];
		map.put("BackgroundImages", ants);
		return map;
	}

}
