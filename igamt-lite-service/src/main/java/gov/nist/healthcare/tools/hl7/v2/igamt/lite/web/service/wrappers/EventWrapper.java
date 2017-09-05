package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers;

import java.util.List;
import java.util.Set;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.NamesAndStruct;

public class EventWrapper {
	
	public EventWrapper() {
		super();
	}

	private String name;
	private String parentStructId;
	private String scope;
	private String hl7Version;

	
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	

	

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentStructId() {
		return parentStructId;
	}

	public void setParentStructId(String parentStructId) {
		this.parentStructId = parentStructId;
	}

	public String getHl7Version() {
		return hl7Version;
	}

	public void setHl7Version(String hl7Version) {
		this.hl7Version = hl7Version;
	}

	@Override
	public String toString() {
		return name + " " + parentStructId+ " " + scope+ " " + hl7Version;
	}
}
