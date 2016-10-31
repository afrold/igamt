package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	  private List<ApplyInfo> appliedTo;
	  public List<ApplyInfo> getAppliedTo() {
		return appliedTo;
	}


	public void setAppliedTo(List<ApplyInfo> appliedTo) {
		this.appliedTo = appliedTo;
	}

	private Set<SubProfileComponent> children=new HashSet<SubProfileComponent>();	  

	

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


	



	public Set<SubProfileComponent> getChildren() {
		return children;
	}


	public void setChildren(Set<SubProfileComponent> children) {
		this.children = children;
	}
	public void addChildren(Set<SubProfileComponent> children){
		this.children.addAll(children);
	}


	@Override
	public int compareTo(ProfileComponent o) {
		// TODO Auto-generated method stub
		return 0;
	}
	public ProfileComponent clone() {
		ProfileComponent clonedPc = new ProfileComponent();
		clonedPc.setName(this.name);
		clonedPc.setChildren(this.children);
		clonedPc.setId(this.getId());
	    return clonedPc;
	  }
}

