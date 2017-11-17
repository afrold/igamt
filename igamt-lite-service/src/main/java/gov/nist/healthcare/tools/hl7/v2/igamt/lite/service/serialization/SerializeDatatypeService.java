package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.UsageConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableDatatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.DatatypeSerializationException;

import java.util.Map;

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
 * Created by Maxence Lefort on 12/12/16.
 */
public interface SerializeDatatypeService {

    public SerializableDatatype serializeDatatype(DatatypeLink datatypeLink, String prefix,
        Integer position, UsageConfig datatypeUsageConfig) throws DatatypeSerializationException;

    public SerializableDatatype serializeDatatype(DatatypeLink datatypeLink, String prefix,
        Integer position, UsageConfig datatypeUsageConfig, Map<String,Datatype> componentProfileDatatypes)
        throws DatatypeSerializationException;

    public SerializableDatatype serializeDatatype(Datatype datatype, String host)
        throws DatatypeSerializationException;
}
