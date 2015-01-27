package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

public class NotAssertion extends Assertion{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5005735274430476015L;
	private Assertion childAssertion;

	
	
	public NotAssertion() {
		super();
	}

	public Assertion getChildAssertion() {
		return childAssertion;
	}

	public void setChildAssertion(Assertion childAssertion) {
		this.childAssertion = childAssertion;
	}
	
	
}
