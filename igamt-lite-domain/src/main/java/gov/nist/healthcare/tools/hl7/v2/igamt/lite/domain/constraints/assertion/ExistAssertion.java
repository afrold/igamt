package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.assertion;

import java.util.HashSet;
import java.util.Set;

public class ExistAssertion extends Assertion{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2528490313092950672L;
	private Set<Assertion> childAssertions = new HashSet<Assertion>();
	
	public ExistAssertion() {
		super();
	}

	public Set<Assertion> getChildAssertions() {
		return childAssertions;
	}

	public void setChildAssertions(Set<Assertion> childAssertions) {
		this.childAssertions = childAssertions;
	}

	@Override
	public String toString() {
		return "ExistAssertion [childAssertions=" + childAssertions + "]";
	}
	
}
