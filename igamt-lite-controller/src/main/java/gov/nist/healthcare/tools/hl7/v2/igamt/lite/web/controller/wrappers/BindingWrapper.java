package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller.wrappers;

import java.util.Set;

public class BindingWrapper {

	private Set<String> datatypeIds;

	private String datatypeLibraryId;

	private String datatypeLibraryExt;

	private Long accountId;

	public Set<String> getDatatypeIds() {
		return datatypeIds;
	}

	public void setDatatypeIds(Set<String> datatypeIds) {
		this.datatypeIds = datatypeIds;
	}

	public String getDatatypeLibraryId() {
		return datatypeLibraryId;
	}

	public void setDatatypeLibraryId(String datatypeLibraryId) {
		this.datatypeLibraryId = datatypeLibraryId;
	}

	public String getDatatypeLibraryExt() {
		return datatypeLibraryExt;
	}

	public void setDatatypeLibraryExt(String datatypeLibraryExt) {
		this.datatypeLibraryExt = datatypeLibraryExt;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
}
