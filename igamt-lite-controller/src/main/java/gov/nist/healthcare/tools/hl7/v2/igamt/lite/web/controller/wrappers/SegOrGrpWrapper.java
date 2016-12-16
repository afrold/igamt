package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller.wrappers;

import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentOrGroupLink;

public class SegOrGrpWrapper {
  public SegOrGrpWrapper() {
    super();
}

private List<SegmentOrGroupLink> segmentOrGroupLinks;

public List<SegmentOrGroupLink> getSegmentOrGroupLinks() {
  return segmentOrGroupLinks;
}

public void setSegmentOrGroupLinks(List<SegmentOrGroupLink> segmentOrGroupLinks) {
  this.segmentOrGroupLinks = segmentOrGroupLinks;
}


}
