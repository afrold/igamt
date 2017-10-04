package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashSet;
import java.util.Set;

public class IGDocumentExportConfig implements java.io.Serializable {

  private static final long serialVersionUID = 1L;
  protected Set<String> valueSetsToExport = new HashSet<String>(); // bindingIdentifier

  public Set<String> getValueSetsToExport() {
    return valueSetsToExport;
  }

  public void setValueSetsToExport(Set<String> valueSetsToExport) {
    this.valueSetsToExport = valueSetsToExport;
  }



}
