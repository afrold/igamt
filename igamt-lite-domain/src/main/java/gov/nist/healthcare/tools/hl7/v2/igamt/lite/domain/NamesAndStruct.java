package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.Set;

public class NamesAndStruct {
	public NamesAndStruct() {
		super();
	}

	private String name;
	private String parentStructId;
	
	public String getNames() {
		return name;
	}
	public void setNames(String name) {
		this.name = name;
	}
	public String getStructID() {
		return parentStructId;
	}
	public void setStructID(String parentStructId) {
		this.parentStructId = parentStructId;
	}

}
