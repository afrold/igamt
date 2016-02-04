package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;


public abstract class SectionModel extends DataModel{
	
	protected int sectionPosition;

	public int getSectionPosition() {
		return sectionPosition;
	}

	public void setSectionPosition(int sectionPosition) {
		this.sectionPosition = sectionPosition;
	}
	
}
