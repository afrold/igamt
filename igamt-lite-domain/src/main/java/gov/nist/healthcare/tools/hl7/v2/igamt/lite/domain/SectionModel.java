package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;


public abstract class SectionModel extends DataModel{

	protected String sectionTitle;
	protected String sectionDescription;
	protected String sectionContents;
	
	protected int sectionPosition;
	
	protected String contents = "";

	public String getSectionTitle() {
		return sectionTitle;
	}

	public void setSectionTitle(String sectionTitle) {
		this.sectionTitle = sectionTitle;
	}

	public String getSectionDescription() {
		return sectionDescription;
	}

	public void setSectionDescription(String sectionDescription) {
		this.sectionDescription = sectionDescription;
	}

	public String getSectionContents() {
		return sectionContents;
	}

	public void setSectionContents(String sectionContents) {
		this.sectionContents = sectionContents;
	}

	public int getSectionPosition() {
		return sectionPosition;
	}

	public void setSectionPosition(int sectionPosition) {
		this.sectionPosition = sectionPosition;
	}
	
	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}
}
