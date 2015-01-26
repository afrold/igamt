package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

public class NotAssertion extends Assertion{
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
