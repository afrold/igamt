package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintColumnDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintIFColumnData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintTHENColumnData;

public class CoConstraintFound {
  private SegmentFound segmentFound;

  private CoConstraintColumnDefinition ifDefinition;
  private CoConstraintIFColumnData ifData;
  private CoConstraintColumnDefinition thenDefinition;
  private CoConstraintTHENColumnData thenData;

  public CoConstraintColumnDefinition getIfDefinition() {
    return ifDefinition;
  }

  public void setIfDefinition(CoConstraintColumnDefinition ifDefinition) {
    this.ifDefinition = ifDefinition;
  }

  public CoConstraintIFColumnData getIfData() {
    return ifData;
  }

  public void setIfData(CoConstraintIFColumnData ifData) {
    this.ifData = ifData;
  }

  public CoConstraintColumnDefinition getThenDefinition() {
    return thenDefinition;
  }

  public void setThenDefinition(CoConstraintColumnDefinition thenDefinition) {
    this.thenDefinition = thenDefinition;
  }

  public CoConstraintTHENColumnData getThenData() {
    return thenData;
  }

  public void setThenData(CoConstraintTHENColumnData thenData) {
    this.thenData = thenData;
  }

  public SegmentFound getSegmentFound() {
    return segmentFound;
  }

  public void setSegmentFound(SegmentFound segmentFound) {
    this.segmentFound = segmentFound;
  }


}
