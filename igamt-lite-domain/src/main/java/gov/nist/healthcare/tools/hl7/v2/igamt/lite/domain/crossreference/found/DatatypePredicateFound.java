package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

public class DatatypePredicateFound {
  private DatatypeFound datatypeFound;
  private Predicate predicate;



  public Predicate getPredicate() {
    return predicate;
  }

  public void setPredicate(Predicate predicate) {
    this.predicate = predicate;
  }

  /**
   * @return the datatypeFound
   */
  public DatatypeFound getDatatypeFound() {
    return datatypeFound;
  }

  /**
   * @param datatypeFound the datatypeFound to set
   */
  public void setDatatypeFound(DatatypeFound datatypeFound) {
    this.datatypeFound = datatypeFound;
  }

  /**
   * @param dt
   */



}
