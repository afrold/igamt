package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.assertion;

public class OrAssertion extends Assertion{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5797477434585384785L;
	private Assertion firstChildAssertion;
	private Assertion SecondChildAssertion;
	public OrAssertion() {
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
	@Override
	public String toString() {
		return "OrAssertion [firstChildAssertion=" + firstChildAssertion
				+ ", SecondChildAssertion=" + SecondChildAssertion + "]";
	}


	
	
	
}
