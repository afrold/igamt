package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.Set;

public class LibraryExportConfig implements java.io.Serializable {

  private static final long serialVersionUID = 1L;
  protected Set<String> include = null; // id

  public Set<String> getInclude() {
    return include;
  }

  public void setInclude(Set<String> include) {
    this.include = include;
  }



}
