package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.List;

public class DTMConstraints {

  private List<DTMComponentDefinition> dtmComponentDefinitions = new ArrayList<DTMComponentDefinition>();

  public List<DTMComponentDefinition> getDtmComponentDefinitions() {
    return dtmComponentDefinitions;
  }

  public void setDtmComponentDefinitions(List<DTMComponentDefinition> dtmComponentDefinitions) {
    this.dtmComponentDefinitions = dtmComponentDefinitions;
  }
  
  
}
