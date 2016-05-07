package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller.wrappers;

import java.util.List;

public class BindingWrapper {

	private List<String> datatypeIds;

	private String datatypeLibraryId;

	public List<String> getDatatypeIds() {
		return datatypeIds;
	}

	public void setDatatypeIds(List<String> datatypeIds) {
		this.datatypeIds = datatypeIds;
	}

	public String getDatatypeLibraryId() {
		return datatypeLibraryId;
	}

	public void setDatatypeLibraryId(String datatypeLibraryId) {
		this.datatypeLibraryId = datatypeLibraryId;
	}
}
