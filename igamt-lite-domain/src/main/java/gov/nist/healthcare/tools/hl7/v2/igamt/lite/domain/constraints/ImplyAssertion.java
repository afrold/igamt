package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

public class ImplyAssertion extends Assertion{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4785214553466976879L;
	private Assertion firstChildAssertion;
	private Assertion SecondChildAssertion;
	public ImplyAssertion() {
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
