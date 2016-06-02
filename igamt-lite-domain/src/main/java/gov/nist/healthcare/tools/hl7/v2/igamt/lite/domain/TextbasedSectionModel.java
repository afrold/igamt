package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;


public abstract class TextbasedSectionModel extends SectionModel {

  protected String sectionTitle;
  protected String sectionDescription;
  protected String sectionContents;

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
}
