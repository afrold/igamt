package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonView;

@Embeddable
public class ProfileMetaData implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@JsonView({ View.Summary.class })
	@NotNull
	@Column(nullable = false, name = "PROFILE_NAME")
	private String name;

	@JsonView({ View.Summary.class })
	@NotNull
	@Column(nullable = false, name = "ORGNAME")
	private String orgName;

	@JsonView({ View.Summary.class })
	@Column(name = "STATUS")
	private String status;

	@JsonView({ View.Summary.class })
	@Column(name = "TOPICS")
	private String topics;

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

}
