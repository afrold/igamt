package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller.wrappers;

import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;

public class ScopeAndNameAndVersionWrapper {

	
	private static final long serialVersionUID = -8337269625916897011L;

	public ScopeAndNameAndVersionWrapper() {
		super();
	}
	private String name;
	private String scope;

	private String hl7Version;
	private String[] versions;
	

	public String[] getVersions() {
		return this.versions;
	}

	public void setVersions(String[] versions) {
		this.versions = versions;
	}

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
	public String getHl7Version() {
		return hl7Version;
	}
	public void setHl7Version(String hl7Version) {
		this.hl7Version = hl7Version;
	}


}
