package gov.nist.healthcare.hl7tools.igmatlite.domain;

public class Assertion {
	private AssertionType type;
	
	private Assertion childAssertion;

	public Assertion() {
		super();
	}

	public Assertion(AssertionType type, Assertion childAssertion) {
		super();
		this.type = type;
		this.childAssertion = childAssertion;
	}

	public Assertion getChildAssertion() {
		return childAssertion;
	}

	public void setChildAssertion(Assertion childAssertion) {
		this.childAssertion = childAssertion;
	}

	public AssertionType getType() {
		return type;
	}

	public void setType(AssertionType type) {
		this.type = type;
	}
	
	
}
