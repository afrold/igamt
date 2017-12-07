package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public class CompositeProfileLink extends AbstractLink {
	private String name;

	public CompositeProfileLink() {
		
		super();
		this.type=Constant.COMPOSITEPROFILELINK;
	}
	  public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	
	

}
