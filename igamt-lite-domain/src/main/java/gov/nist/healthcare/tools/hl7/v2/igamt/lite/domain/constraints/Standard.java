package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

public class Standard implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@NotNull
	@Column(nullable = false)
	private String standardId;
	
	@NotNull
	@Column(nullable = false)
	private String standardVersion;
	
	private String standardDate;
	private String standardURL;
	private String standardDescription;
	
	public String getStandardId() {
		return standardId;
	}
	public void setStandardId(String standardId) {
		this.standardId = standardId;
	}
	public String getStandardVersion() {
		return standardVersion;
	}
	public void setStandardVersion(String standardVersion) {
		this.standardVersion = standardVersion;
	}
	public String getStandardDate() {
		return standardDate;
	}
	public void setStandardDate(String standardDate) {
		this.standardDate = standardDate;
	}
	public String getStandardURL() {
		return standardURL;
	}
	public void setStandardURL(String standardURL) {
		this.standardURL = standardURL;
	}
	public String getStandardDescription() {
		return standardDescription;
	}
	public void setStandardDescription(String standardDescription) {
		this.standardDescription = standardDescription;
	}
	@Override
	public String toString() {
		return "Standard [standardId=" + standardId + ", standardVersion="
				+ standardVersion + ", standardDate=" + standardDate
				+ ", standardURL=" + standardURL + ", standardDescription="
				+ standardDescription + "]";
	}
	
	

}
