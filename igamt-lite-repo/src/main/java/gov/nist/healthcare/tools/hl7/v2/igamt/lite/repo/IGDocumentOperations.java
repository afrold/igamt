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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.Date;
import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentScope;

/**
 * @author Harold Affo (harold.affo@nist.gov) Mar 30, 2015
 */
public interface IGDocumentOperations {

  public List<IGDocument> findPreloaded();

  public List<IGDocument> findStandard();

  public List<IGDocument> findUser();

  public List<IGDocument> findStandardByVersion(String hl7version);

  public List<IGDocument> findByScopesAndVersion(List<SCOPE> scopes, String hl7Version);

  public List<IGDocument> findByAccountIdAndScopesAndVersion(Long accountId, List<SCOPE> scopes,
      String hl7Version);

  public List<String> findHl7Versions();

  public List<IGDocument> findByScopeAndVersions(IGDocumentScope scope, List<String> hl7Versions);

  public List<IGDocument> findByScopeAndVersionsInIg(IGDocumentScope scope,
      List<String> hl7Versions);

  public List<IGDocument> findAllByScope(IGDocumentScope scope);

  public List<IGDocument> findByParticipantId(Long participantId);

  public Date updateDate(String id, Date date);

  public int updatePosition(String id, int position);



  /**
   * @param scope
   * @param hl7Version
   * @return
   */
  List<IGDocument> findByScopeAndVersion(IGDocumentScope scope, String hl7Version);

  /**
   * 
   * @param accountId
   * @param scope
   * @return
   */
  List<IGDocument> findByAccountIdAndScope(Long accountId, IGDocumentScope scope);


}
