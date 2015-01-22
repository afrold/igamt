package gov.nist.healthcare.hl7tools.igmatlite.domain;

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
