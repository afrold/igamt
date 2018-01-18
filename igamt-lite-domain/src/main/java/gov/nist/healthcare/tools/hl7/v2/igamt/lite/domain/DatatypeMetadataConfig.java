package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public class DatatypeMetadataConfig {
	private boolean hl7version = false;
	private boolean publicationDate = false;
	private boolean publicationVersion = false;
	private boolean scope = false;
	
	public DatatypeMetadataConfig() {
		super();
	}
	
	public DatatypeMetadataConfig(boolean hl7version, boolean publicationDate, boolean publicationVersion,
			boolean scope) {
		super();
		this.hl7version = hl7version;
		this.publicationDate = publicationDate;
		this.publicationVersion = publicationVersion;
		this.scope = scope;
	}
	public boolean isHl7version() {
		return hl7version;
	}
	public void setHl7version(boolean hl7version) {
		this.hl7version = hl7version;
	}
	public boolean isPublicationDate() {
		return publicationDate;
	}
	public void setPublicationDate(boolean publicationDate) {
		this.publicationDate = publicationDate;
	}
	public boolean isPublicationVersion() {
		return publicationVersion;
	}
	public void setPublicationVersion(boolean publicationVersion) {
		this.publicationVersion = publicationVersion;
	}
	public boolean isScope() {
		return scope;
	}
	public void setScope(boolean scope) {
		this.scope = scope;
	}

	
}
