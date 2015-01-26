package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.util.Set;

public class ExistAssertion extends Assertion{
	private Set<Assertion> childAssertions;
	
	public ExistAssertion() {
		super();
	}

	public Set<Assertion> getChildAssertions() {
		return childAssertions;
	}

	public void setChildAssertions(Set<Assertion> childAssertions) {
		this.childAssertions = childAssertions;
	}
}
