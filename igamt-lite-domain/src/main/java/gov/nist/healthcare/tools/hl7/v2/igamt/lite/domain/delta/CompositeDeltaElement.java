package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.delta;

import java.util.List;

public class CompositeDeltaElement extends DeltaElement {
	List<DeltaElement>  children;

	public List<DeltaElement> getChildren() {
		return children;
	}

	public void setChildren(List<DeltaElement> children) {
		this.children = children;
	}

	public CompositeDeltaElement(List<DeltaElement> children) {
		super();
		this.children = children;
	}

}
