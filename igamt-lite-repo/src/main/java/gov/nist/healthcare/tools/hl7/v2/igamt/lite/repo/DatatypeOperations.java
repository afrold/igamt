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
import java.util.Set;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;

/**
 * @author gcr1
 *
 */
public interface DatatypeOperations {

  // List<Datatype> findByLibIds(String dtLibId);

  // List<Datatype> findFullDTsByLibIds(String dtLibId);

  public List<Datatype> findAll();

  public Datatype findById(String id);

  public List<Datatype> findByIds(Set<String> ids);

  public List<Datatype> findByScopesAndVersion(List<SCOPE> scopes, String hl7Version);

  // List<Datatype> findDups(Datatype dt);

  public List<Datatype> findUserDatatypesByIds(Set<String> ids);

  public List<Datatype> findByScope(String scope);

  public List<Datatype> findShared(Long accountId);

  public List<Datatype> findByNameAndVersionAndScope(String name, String version, String scope);

  public Datatype findOneByNameAndVersionAndScope(String name, String version, String scope);

  public Datatype findByNameAndVersionsAndScope(String name, String[] versions, String scope);

  public Date updateDate(String id, Date date);

  public List<Datatype> findAllByNameAndVersionsAndScope(String name, List<String> versions,
      String scope);

  /**
   * @param name
   * @param scope
   * @return
   */
  List<Datatype> findByNameAndScope(String name, String scope);

  public void updateStatus(String id, STATUS status);

  List<Datatype> findByScopeAndVersionAndParentVersion(SCOPE scope, String hl7Version, String id);

  List<Datatype> findByScopeAndVersion(String scope, String hl7Version);

  void updateAttribute(String id, String attributeName, Object value);


}
