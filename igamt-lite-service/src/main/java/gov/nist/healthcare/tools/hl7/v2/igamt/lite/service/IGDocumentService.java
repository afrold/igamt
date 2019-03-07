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
 */

/**
 * 
 * @author Jungyub Woo
 * 
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ElementVerification;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentScope;

@Service
public interface IGDocumentService {

  public IGDocument save(IGDocument ig) throws IGDocumentException;

  public void delete(String id);

  public IGDocument findOne(String id);

  public List<IGDocument> findAll();

  public List<IGDocument> findAllPreloaded();

  public List<IGDocument> findAllUser();

  public List<IGDocument> findByAccountId(Long accountId);

  public List<IGDocument> findByAccountIdAndScope(Long accountId, IGDocumentScope scope);

  public List<IGDocument> findByScopesAndVersion(List<SCOPE> scopes, String hl7Version);

  public List<IGDocument> findByScopeAndVersions(IGDocumentScope scope, List<String> hl7Versions);


  public List<IGDocument> findByScopeAndVersion(IGDocumentScope scope, String hl7Version);


  public List<IGDocument> findByAccountIdAndScopesAndVersion(Long accountId, List<SCOPE> scopes,
      String hl7Version);

  public IGDocument clone(IGDocument ig) throws CloneNotSupportedException;

  public IGDocument apply(IGDocument ig) throws IGDocumentException;

  // TODO NEED TO REVIEW

  public InputStream diffToPdf(IGDocument d);

  //
  // public InputStream diffToJson(Profile p);
  //
  // public Map<String, List<ElementChange>> delta(Profile p);
  //
  // public ElementVerification verifyMessages(Profile p, String id, String
  // type);
  //
  // public ElementVerification verifyMessage(Profile p, String id, String
  // type);
  //
  // public ElementVerification verifySegmentRefOrGroup(Profile p, String id,
  // String type);
  //
  // public ElementVerification verifySegments(Profile p, String id, String
  // type);
  //
  public ElementVerification verifySegment(IGDocument d, String id, String type);

  //
  // public ElementVerification verifyField(Profile p, String id, String
  // type);
  //
  // public ElementVerification verifyDatatypes(Profile p, String id, String
  // type);
  //
  public ElementVerification verifyDatatype(IGDocument d, String id, String type);

  //
  // public ElementVerification verifyComponent(Profile p, String id, String
  // type);
  //
  // public ElementVerification verifyValueSetLibrary(Profile p, String id,
  // String type);
  //
  public ElementVerification verifyValueSet(IGDocument p, String id, String type);
  //
  // public ElementVerification verifyUsage(Profile p, String id, String type,
  // String eltName,
  // String eltValue);
  //
  // public ElementVerification verifyCardinality(Profile p, String id, String
  // type, String eltName,
  // String eltValue);
  //
  // public ElementVerification verifyLength(Profile p, String id, String
  // type, String eltName,
  // String eltValue);

  public IGDocument findById(String id);

  public List<IGDocument> findByScopeAndVersionsInIg(IGDocumentScope scope,
      List<String> hl7Versions);

  public List<IGDocument> findAllByScope(IGDocumentScope scope);

  public List<IGDocument> findSharedIgDocuments(Long participantId);

  public Date updateDate(String id, Date date) throws IGDocumentException;

  List<IGDocument> save(Collection<IGDocument> igs) throws IGDocumentException;

  IGDocument save(IGDocument ig, Date date) throws IGDocumentException;

  public int updatePosition(String id, int position);
  
  public void makePreloaded(String id);
  
 
}
