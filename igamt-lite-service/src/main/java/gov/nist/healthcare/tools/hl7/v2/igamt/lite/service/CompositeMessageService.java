package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeMessage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentOrGroup;

public interface CompositeMessageService {
	CompositeMessage findById(String id);
	CompositeMessage save(CompositeMessage compositeMessage);
	SegmentOrGroup saveSegOrGrp(SegmentOrGroup segmentOrGroup);
	SegmentOrGroup getSegOrGrp(String id);
}
