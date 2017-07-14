package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;

public class SegmentConformanceStatmentFound {
  private SegmentFound segmenteFound;
  private ConformanceStatement conformanceStatement;

  public SegmentFound getSegmenteFound() {
    return segmenteFound;
  }

  public void setSegmenteFound(SegmentFound segmenteFound) {
    this.segmenteFound = segmenteFound;
  }

  public ConformanceStatement getConformanceStatement() {
    return conformanceStatement;
  }

  public void setConformanceStatement(ConformanceStatement conformanceStatement) {
    this.conformanceStatement = conformanceStatement;
  }


}
