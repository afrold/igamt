package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

public class NameAndVersionWrapper {
	private static final long serialVersionUID = -8337269625916897011L;

	public NameAndVersionWrapper() {
		super();
		// TODO Auto-generated constructor stub
	}
	private String name;
	private String version;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
}
