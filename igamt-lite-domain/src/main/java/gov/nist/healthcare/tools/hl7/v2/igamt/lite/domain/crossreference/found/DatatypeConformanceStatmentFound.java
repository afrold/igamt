package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;

public class DatatypeConformanceStatmentFound {
  private DatatypeFound datatypeFound;
  private ConformanceStatement conformanceStatement;


  public ConformanceStatement getConformanceStatement() {
    return conformanceStatement;
  }

  public void setConformanceStatement(ConformanceStatement conformanceStatement) {
    this.conformanceStatement = conformanceStatement;
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
