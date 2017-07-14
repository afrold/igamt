package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetBinding;

public class SegmentValueSetBindingFound {
  private SegmentFound segmentFound;
  private ValueSetBinding binding;

  public SegmentFound getSegmentFound() {
    return segmentFound;
  }

  public void setSegmentFound(SegmentFound segmentFound) {
    this.segmentFound = segmentFound;
  }

  public ValueSetBinding getBinding() {
    return binding;
  }

  public void setBinding(ValueSetBinding binding) {
    this.binding = binding;
  }
}
