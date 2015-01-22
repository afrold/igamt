package gov.nist.healthcare.hl7tools.igmatlite.domain;

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
