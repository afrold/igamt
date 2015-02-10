package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.codehaus.jackson.map.annotate.JsonView;

@Embeddable
public class ProfileMetaData implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@JsonView({Views.Profile.class})
	@NotNull
	@Column(nullable = false)
	private String name;

	@JsonView({Views.Profile.class})
	@NotNull
	@Column(nullable = false)
	private String orgName;

	@JsonView({Views.Profile.class})
	private String version;
	
	@JsonView({Views.Profile.class})
	private String status;
	
	@JsonView({Views.Profile.class})
	private String topics;

	@Override
	public boolean equals(Object that) {
		return EqualsBuilder.reflectionEquals(this, that, "name", "orgName",
				"version", "status", "topics");
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, "name", "orgName",
				"version", "status", "topics");
	}

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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
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
