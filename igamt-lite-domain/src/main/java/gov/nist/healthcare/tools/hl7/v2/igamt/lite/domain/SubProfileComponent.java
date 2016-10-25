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
	  private String path;
	  private String type;
	  private String name;
	  
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public HashMap<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(HashMap<String, Object> attributes) {
		this.attributes = attributes;
	}
	  
}
