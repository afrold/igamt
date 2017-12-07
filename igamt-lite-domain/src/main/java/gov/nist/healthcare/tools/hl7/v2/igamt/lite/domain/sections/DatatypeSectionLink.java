package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections;

import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;

public class DatatypeSectionLink extends SectionDataLink {
	private String name;
	private String ext;
	private Integer numberOfchildren;
	private String description;
	private SCOPE scope;
	private STATUS status;
	private int publicationVersion;
	private String hl7Version; 
	
	private List<String> hl7Versions;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public Integer getNumberOfchildren() {
		return numberOfchildren;
	}

	public void setNumberOfchildren(Integer numberOfchildren) {
		this.numberOfchildren = numberOfchildren;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public SCOPE getScope() {
		return scope;
	}

	public void setScope(SCOPE scope) {
		this.scope = scope;
	}

	public STATUS getStatus() {
		return status;
	}

	public void setStatus(STATUS status) {
		this.status = status;
	}

	public int getPublicationVersion() {
		return publicationVersion;
	}

	public void setPublicationVersion(int publicationVersion) {
		this.publicationVersion = publicationVersion;
	}

	public DatatypeSectionLink() {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<String> getHl7Versions() {
		return hl7Versions;
	}

	public void setHl7Versions(List<String> list) {
		this.hl7Versions = list;
	}

	public String getHl7Version() {
		return hl7Version;
	}

	public void setHl7Version(String hl7Version) {
		this.hl7Version = hl7Version;
	}

	
	
}
