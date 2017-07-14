package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

public class SegmentPredicateFound {
  private SegmentFound segmenteFound;
  private Predicate predicate;

  public SegmentFound getSegmenteFound() {
    return segmenteFound;
  }

  public void setSegmenteFound(SegmentFound segmenteFound) {
    this.segmenteFound = segmenteFound;
  }

  public Predicate getPredicate() {
    return predicate;
  }

  public void setPredicate(Predicate predicate) {
    this.predicate = predicate;
  }


}
