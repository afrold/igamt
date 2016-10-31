package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public class ApplyInfo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	  public ApplyInfo() {
	    
	  }
	  private String id;
	  private String name;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	  
}
