package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetBinding;

public class DatatypeValueSetBindingFound {
  private DatatypeFound datatypeFound;
  private ValueSetBinding binding;

  public DatatypeFound getDatatypeFound() {
    return datatypeFound;
  }

  public void setDatatypeFound(DatatypeFound datatypeFound) {
    this.datatypeFound = datatypeFound;
  }

  public ValueSetBinding getBinding() {
    return binding;
  }

  public void setBinding(ValueSetBinding binding) {
    this.binding = binding;
  }
}
