package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public abstract class SectionModel extends DataModel {

  Integer sectionPosition;

  public Integer getSectionPosition() {
    return sectionPosition;
  }

  public void setSectionPosition(Integer sectionPosition) {
    this.sectionPosition = sectionPosition;
  }
}
