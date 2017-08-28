/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Abdelghani EL OUAKILI (NIST) Aug 28, 2017
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.DatatypeCrossReference;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.MessageCrossReference;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.ProfileComponentCrossReference;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.SegmentCrossReference;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.ValueSetCrossReference;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers.DatatypeCrossRefWrapper;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers.MessageCrossRefWrapper;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers.ProfileComponentCrossRefWrapper;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers.SegmentCrossRefWrapper;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers.TableCrossRefWrapper;

/**
 * @author Abdelghani EL Ouakili (NIST)
 *
 */
public interface CrossReferenceService {

  public ProfileComponentCrossReference findProfileComponentReferences(
      ProfileComponentCrossRefWrapper wrapper) throws Exception;

  public MessageCrossReference findMessageReferences(MessageCrossRefWrapper wrapper)
      throws Exception;


  public SegmentCrossReference findSegmentReferences(SegmentCrossRefWrapper wrapper)
      throws Exception;

  public DatatypeCrossReference findDatatypeCrossReference(DatatypeCrossRefWrapper wrapper)
      throws Exception;

  public DatatypeCrossReference findDatatypeCrossReferenceInLibrary(DatatypeCrossRefWrapper wrapper)
      throws Exception;


  public ValueSetCrossReference findValueSetsCrossReference(TableCrossRefWrapper wrapper)
      throws Exception;



}
