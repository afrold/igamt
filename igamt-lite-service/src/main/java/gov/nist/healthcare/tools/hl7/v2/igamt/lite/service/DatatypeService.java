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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;

public interface DatatypeService {

  public Datatype findById(String id);

  public Datatype save(Datatype datatype);

  public List<Datatype> findAll();

  public List<Datatype> findByIds(Set<String> ids);

  public List<Datatype> findByScopesAndVersion(List<SCOPE> scopes, String hl7Version);

  public void delete(Datatype dt);

  public void delete(String id);

  public void save(List<Datatype> datatypes);

  public void delete(List<Datatype> datatypes);

  public Set<Datatype> collectDatatypes(Datatype datatype);

  public List<Datatype> findByScope(String scope);

  public List<Datatype> findShared(Long accountId);

  public List<Datatype> findPendingShared(Long accountId);

  public List<Datatype> findByNameAndVersionAndScope(String name, String version, String scope);

  public List<Datatype> findByNameAndScope(String name, String scope);

  public Datatype findByNameAndVesionAndScope(String name, String version, String scope);

  public Datatype findOneByNameAndVersionAndScope(String name, String version, String scope);

  public Datatype findByCompatibleVersion(String name, String version, String scope)
      throws Exception;


  public Datatype findByNameAndVersionsAndScope(String name, String[] string, String string2);

  public List<Datatype> findAllByNameAndVersionsAndScope(String name, List<String> versions,
      String string);

  public Date updateDate(String id, Date date) throws IGDocumentException;

  public Datatype save(Datatype datatype, Date date);

  public void updateStatus(String id, STATUS status);

  /**
   * @return
   */

  public List<Datatype> findByScopeAndVersionAndParentVersion(SCOPE scope, String hl7Version,
      String id);

  public List<Datatype> findByScopeAndVersion(String name, String hl7Version);

  void updateAttribute(String id, String attributeName, Object value);



}
