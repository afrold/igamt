package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ProfileMetaData implements java.io.Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	
	public ProfileMetaData() {
		super();
	}

	/* XSD Attributes */
	
	private String profileID = ""; 				//ConformanceProfile/@ID
	
	private String type = ""; 					//ConformanceProfile/@Type
	
	private String hl7Version = ""; 			//ConformanceProfile/@HL7Version
	
	private String schemaVersion = ""; 			//ConformanceProfile/@SchemaVersion
	
	private String name; 						//ConformanceProfile/MetaData/@Name
	
	private String orgName = ""; 				//ConformanceProfile/MetaData/@OrgName
	
	private String version = ""; 				//ConformanceProfile/MetaData/@Version
	
	private String date = ""; 					//ConformanceProfile/MetaData/@Date
	
	private String specificationName = ""; 		//ConformanceProfile/MetaData/@SpecificationName
	
	private String status = ""; 				//ConformanceProfile/MetaData/@Status
	
	private String topics = ""; 				//ConformanceProfile/MetaData/@Topics
	
	/* XSD Attributes END */
	
	

	@JsonIgnore
	private String subTitle = "";

	@JsonIgnore
	private String ext = "";

	private Set<String> encodings = new HashSet<String>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTopics() {
		return topics;
	}

	public void setTopics(String topics) {
		this.topics = topics;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHl7Version() {
		return hl7Version;
	}

	public void setHl7Version(String hl7Version) {
		this.hl7Version = hl7Version;
	}

	public String getSchemaVersion() {
		return schemaVersion;
	}

	public void setSchemaVersion(String schemaVersion) {
		this.schemaVersion = schemaVersion;
	}

	public Set<String> getEncodings() {
		return encodings;
	}

	public void setEncodings(Set<String> encodings) {
		this.encodings = encodings;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	@Override
	public ProfileMetaData clone() throws CloneNotSupportedException {
		ProfileMetaData clonedProfileMetaData = new ProfileMetaData();

		clonedProfileMetaData.setEncodings(new HashSet<String>());
		for (String s : this.encodings) {
			clonedProfileMetaData.getEncodings().add(s);
		}

		clonedProfileMetaData.setHl7Version(hl7Version);
		clonedProfileMetaData.setProfileID(profileID);
		clonedProfileMetaData.setName(name);
		clonedProfileMetaData.setOrgName(orgName);
		clonedProfileMetaData.setSchemaVersion(schemaVersion);
		clonedProfileMetaData.setStatus(status);
		clonedProfileMetaData.setTopics(topics);
		clonedProfileMetaData.setType(type);
		clonedProfileMetaData.setDate(date);
		clonedProfileMetaData.setSubTitle(subTitle);
		clonedProfileMetaData.setVersion(hl7Version);
		return clonedProfileMetaData;
	}

	@Override
	public String toString() {
		return "ProfileMetaData [name=" + name
				+ ", subTitle=" + subTitle + ", version=" + version
				+ ", orgName=" + orgName + ", status=" + status + ", topics="
				+ topics + ", type=" + type + ", hl7Version=" + hl7Version
				+ ", schemaVersion=" + schemaVersion + ", date=" + date
				+ ", ext=" + ext + ", encodings=" + encodings + "]";
	}

	public String getSpecificationName() {
		return specificationName;
	}

	public void setSpecificationName(String specificationName) {
		this.specificationName = specificationName;
	}

	public String getProfileID() {
		return profileID;
	}

	public void setProfileID(String profileID) {
		this.profileID = profileID;
	}

}
