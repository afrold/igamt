package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public class ExternalUsers {
	private String id="";
	private String type="";
	public ExternalUsers() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ExternalUsers(String id, String type) {
		super();
		this.id = id;
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	

}
