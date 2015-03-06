package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.validation.constraints.NotNull;

@Embeddable
public class ProfileMetaData implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull
	@Column(nullable = false, name = "NAME")
	private String name;

	@NotNull
	@Column(nullable = false, name = "ORGNAME")
	private String orgName;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "TOPICS")
	private String topics;

	@Column(name = "TYPE")
	private String type;

	@Column(name = "HL7VERSION")
	private String hl7Version;

	@Column(name = "SCHEMAVERSION")
	private String schemaVersion;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "ENCODINGS")
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

}
