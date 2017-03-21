package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

/**
 * @author Jungyub Woo
 *
 */
public class SingleElementValue {
	protected String location;
	protected String value;
	protected String profilePath;
	protected String name;

	public SingleElementValue() {
		super();
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getProfilePath() {
		return profilePath;
	}

	public void setProfilePath(String profilePath) {
		this.profilePath = profilePath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
