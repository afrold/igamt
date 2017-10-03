package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.Set;

public class IGDocumentExportConfig {


  protected Set<String> valueSetsToExport; // bindingIdentifier

  public Set<String> getValueSetsToExport() {
    return valueSetsToExport;
  }

  public void setValueSetsToExport(Set<String> valueSetsToExport) {
    this.valueSetsToExport = valueSetsToExport;
  }



}
