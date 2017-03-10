package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public class DTComponent {

	private String dtName;
	private int location;

	public DTComponent(String dtName, int location) {
		super();
		this.dtName = dtName;
		this.location = location;
	}

	public DTComponent() {
		super();
	}

	public String getDtName() {
		return dtName;
	}

	public void setDtName(String dtName) {
		this.dtName = dtName;
	}

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}

}
