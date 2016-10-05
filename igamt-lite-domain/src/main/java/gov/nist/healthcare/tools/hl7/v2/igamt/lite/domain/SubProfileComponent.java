package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.data.annotation.Id;

public class SubProfileComponent implements java.io.Serializable, Cloneable{

	private static final long serialVersionUID = 1L;

	  public SubProfileComponent() {
	    
	  }
	 
	  private HashMap<String,Object> attributes=new HashMap<String,Object>();

	public HashMap<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(HashMap<String, Object> attributes) {
		this.attributes = attributes;
	}
	  
}
