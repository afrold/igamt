package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VersionNode {
	private String sourceId=null;
	private List<String> derived= new ArrayList<String>();
	private List<String> ancestors=new ArrayList<String>();
	private int	publicationVersion=0;
	public VersionNode(String sourceId, List<String> derived, List<String> ancestors, int publicationVersion,
			String publicationDate) {
		super();
		this.sourceId = sourceId;
		this.derived = derived;
		this.ancestors = ancestors;
		this.publicationVersion = publicationVersion;
		this.publicationDate = publicationDate;
	}

	private String publicationDate;
	
	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public List<String> getDerived() {
		return derived;
	}

	public void setDerived(List<String> derived) {
		this.derived = derived;
	}

	public List<String> getAncestors() {
		return ancestors;
	}

	public void setAncestors(List<String> ancestors) {
		this.ancestors = ancestors;
	}

	public int getPublicationVersion() {
		return publicationVersion;
	}

	public void setPublicationVersion(int publicationVersion) {
		this.publicationVersion = publicationVersion;
	}

	public String getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(String publicationDate) {
		this.publicationDate = publicationDate;
	}

	public VersionNode() {
		// TODO Auto-generated constructor stub
	}

}
