package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashSet;
import java.util.Set;

public class ProfileMetaData implements java.io.Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	
	public ProfileMetaData() {
		super();
		this.type = "profileMetaData";
	}

	// //@NotNull
	private String name;

	private String identifier = "";

	private String subTitle = "";

	private String version = "";

	// //@NotNull
	private String orgName = "";

	private String status = "";

	private String topics = "";

	private String type = "";

	private String hl7Version = "";

	private String schemaVersion = "";

	private String date = "";

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

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
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
		clonedProfileMetaData.setIdentifier(identifier);
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
		return "ProfileMetaData [name=" + name + ", identifier=" + identifier
				+ ", subTitle=" + subTitle + ", version=" + version
				+ ", orgName=" + orgName + ", status=" + status + ", topics="
				+ topics + ", type=" + type + ", hl7Version=" + hl7Version
				+ ", schemaVersion=" + schemaVersion + ", date=" + date
				+ ", ext=" + ext + ", encodings=" + encodings + "]";
	}

}
