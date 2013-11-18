package edu.mbl.jif.imaging.dataset.linked;

import java.util.Map;

/**
 * Records the Transform used to create this derived image.
 * And parameters used...
 * 
 * @author GBH
 */
public class Transform {

	public static final String JSON_KEY = "Transform";
	
	String transformName;  
	// e.g. 
	// "edu.mbl.jif.ps.mag5"
	// "edu.mbl.jif.ps.ort5"
	// "edu.mbl.jif.ps.mag4"
	// "edu.mbl.jif.ps.ort4"
	// "edu.mbl.jif.ps.fluorpol"
	// "edu.mbl.jif.ps.dichro"
	
		Map<String, Object> parameters;
	// e.g. "swing" : 0.03 ...
	
		
		

	public Transform(String transformName, Map<String, Object> parameters) {
		this.transformName = transformName;
		this.parameters = parameters;
	}

	
	
	// parameters might include anticedent references, e.g. background images.
	// "background"=[]

	public String getTransformName() {
		return transformName;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}
	
}