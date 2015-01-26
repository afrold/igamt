package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.util.Set;

public class ForAllAssertion extends Assertion{
	private Set<Assertion> childAssertions;
	
	public ForAllAssertion() {
		super();
	}

	public Set<Assertion> getChildAssertions() {
		return childAssertions;
	}

	public void setChildAssertions(Set<Assertion> childAssertions) {
		this.childAssertions = childAssertions;
	}
	
	
	
}
