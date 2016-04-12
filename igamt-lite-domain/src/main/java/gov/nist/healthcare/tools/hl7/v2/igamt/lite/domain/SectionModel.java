package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public abstract class SectionModel extends DataModel {

	int sectionPosition;

	protected Constant.SCOPE scope;
	
	public Constant.SCOPE getScope() {
		return scope;
	}

	public void setScope(Constant.SCOPE scope) {
		this.scope = scope;
	}
	
	public int getSectionPosition() {
		return sectionPosition;
	}

	public void setSectionPosition(int sectionPosition) {
		this.sectionPosition = sectionPosition;
	}
}
