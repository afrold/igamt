package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.io.Serializable;
import java.util.Set;

public class ConformanceContext implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1051694737992020403L;

	private long id;
	private String uuid;
	
	//META-Description
	private String description;
	
	//META-Author
	private String firstNameAuthor;
	private String lastNameAuthor;
	private String email;
	
	//META-Standard 
	private String standardId;
	private String standardVersion;
	private String standardDate;
	private String standardURL;
	private String standardDescription;
	
	//Constraints
	private Set<Constraint> datatypeConstraints;
	private Set<Constraint> segmentConstraints;
	private Set<Constraint> groupConstraints;
	
	
	
	public ConformanceContext() {
		// TODO Auto-generated constructor stub
	}



	public ConformanceContext(long id, String uuid, String description,
			String firstNameAuthor, String lastNameAuthor, String email,
			String standardId, String standardVersion, String standardDate,
			String standardURL, String standardDescription,
			Set<Constraint> datatypeConstraints,
			Set<Constraint> segmentConstraints, Set<Constraint> groupConstraints) {
		super();
		this.id = id;
		this.uuid = uuid;
		this.description = description;
		this.firstNameAuthor = firstNameAuthor;
		this.lastNameAuthor = lastNameAuthor;
		this.email = email;
		this.standardId = standardId;
		this.standardVersion = standardVersion;
		this.standardDate = standardDate;
		this.standardURL = standardURL;
		this.standardDescription = standardDescription;
		this.datatypeConstraints = datatypeConstraints;
		this.segmentConstraints = segmentConstraints;
		this.groupConstraints = groupConstraints;
	}



	public long getId() {
		return id;
	}



	public void setId(long id) {
		this.id = id;
	}



	public String getUuid() {
		return uuid;
	}



	public void setUuid(String uuid) {
		this.uuid = uuid;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public String getFirstNameAuthor() {
		return firstNameAuthor;
	}



	public void setFirstNameAuthor(String firstNameAuthor) {
		this.firstNameAuthor = firstNameAuthor;
	}



	public String getLastNameAuthor() {
		return lastNameAuthor;
	}



	public void setLastNameAuthor(String lastNameAuthor) {
		this.lastNameAuthor = lastNameAuthor;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



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



	public Set<Constraint> getDatatypeConstraints() {
		return datatypeConstraints;
	}



	public void setDatatypeConstraints(Set<Constraint> datatypeConstraints) {
		this.datatypeConstraints = datatypeConstraints;
	}



	public Set<Constraint> getSegmentConstraints() {
		return segmentConstraints;
	}



	public void setSegmentConstraints(Set<Constraint> segmentConstraints) {
		this.segmentConstraints = segmentConstraints;
	}



	public Set<Constraint> getGroupConstraints() {
		return groupConstraints;
	}



	public void setGroupConstraints(Set<Constraint> groupConstraints) {
		this.groupConstraints = groupConstraints;
	}



	@Override
	public String toString() {
		return "ConformanceContext [id=" + id + ", uuid=" + uuid
				+ ", description=" + description + ", firstNameAuthor="
				+ firstNameAuthor + ", lastNameAuthor=" + lastNameAuthor
				+ ", email=" + email + ", standardId=" + standardId
				+ ", standardVersion=" + standardVersion + ", standardDate="
				+ standardDate + ", standardURL=" + standardURL
				+ ", standardDescription=" + standardDescription
				+ ", datatypeConstraints=" + datatypeConstraints
				+ ", segmentConstraints=" + segmentConstraints
				+ ", groupConstraints=" + groupConstraints + "]";
	}

	
}
