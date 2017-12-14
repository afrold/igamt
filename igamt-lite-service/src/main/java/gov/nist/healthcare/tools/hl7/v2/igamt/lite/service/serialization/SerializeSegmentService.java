package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization;


import java.util.Map;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.UsageConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableSection;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.SegmentSerializationException;

/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 * <p>
 * Created by Maxence Lefort on 12/13/16.
 */
public interface SerializeSegmentService {
    public SerializableSection serializeSegment(SegmentLink segmentLink, String prefix,
        Integer position, Integer headerLevel, UsageConfig segmentUsageConfig, Boolean duplicateOBXDataTypeWhenFlavorNull) throws SegmentSerializationException;
    public SerializableSection serializeSegment(SegmentLink segmentLink, String prefix,
        Integer position, Integer headerLevel, UsageConfig segmentUsageConfig, Map<String, Segment> compositeProfileSegments, Map<String, Datatype> compositeProfileDatatypes, Map<String, Table> compositeProfileTables, Boolean duplicateOBXDataTypeWhenFlavorNull ,Boolean includeTemporay) throws SegmentSerializationException;
	public SerializableSection serializeSegment(Segment segment, String host) throws SegmentSerializationException;
}
