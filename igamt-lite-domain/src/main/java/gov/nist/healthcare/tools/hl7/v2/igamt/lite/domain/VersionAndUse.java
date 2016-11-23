package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "versionAndUse")
public class VersionAndUse{
	  private String sourceId=null;
	  
	  private Long accountId;

	  @Id
	  private String id;
	  private List<String> derived= new ArrayList<String>();
	  private List<String> ancestors=new ArrayList<String>();
	  private boolean deprecated=false;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}

	private int	publicationVersion=0;
	public VersionAndUse(String sourceId, List<String> derived, List<String> ancestors, int publicationVersion,
			String publicationDate) {
		super();
		this.sourceId = sourceId;
		this.derived = derived;
		this.ancestors = ancestors;
		this.publicationVersion = publicationVersion;
		this.publicationDate = publicationDate;
	}

      private String publicationDate;
	  private List<ExternalUsers> usedBy=new ArrayList<ExternalUsers>();
	  
	  public List<ExternalUsers> getUsedBy() {
	  	return usedBy;
	  }

	  public void setUsedBy(List<ExternalUsers> usedBy) {
	  	this.usedBy = usedBy;
	  }

	
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

	public VersionAndUse() {
		// TODO Auto-generated constructor stub
	}
	public Long getAccountId() {
		return accountId;
	}
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

}
