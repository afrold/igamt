package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

public class XorAssertion extends Assertion{
	private Assertion firstChildAssertion;
	private Assertion SecondChildAssertion;
	public XorAssertion() {
		super();
	}
	public Assertion getFirstChildAssertion() {
		return firstChildAssertion;
	}
	public void setFirstChildAssertion(Assertion firstChildAssertion) {
		this.firstChildAssertion = firstChildAssertion;
	}
	public Assertion getSecondChildAssertion() {
		return SecondChildAssertion;
	}
	public void setSecondChildAssertion(Assertion secondChildAssertion) {
		SecondChildAssertion = secondChildAssertion;
	}


	
	
	
}
