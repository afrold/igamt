package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DataModel;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;

public class ProfileComponent implements java.io.Serializable, Cloneable,
Comparable<ProfileComponent>{
	
	private static final long serialVersionUID = 1L;

	  public ProfileComponent() {
	    
	  }

	  @Id
	  private String id;
	  private String name;
	  private String type;
	  private List<SubProfileComponent> children = new ArrayList<SubProfileComponent>(); 
	  

	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


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


	public List<SubProfileComponent> getChildren() {
		return children;
	}


	public void setChildren(List<SubProfileComponent> children) {
		this.children = children;
	}


	@Override
	public int compareTo(ProfileComponent o) {
		// TODO Auto-generated method stub
		return 0;
	}

}

