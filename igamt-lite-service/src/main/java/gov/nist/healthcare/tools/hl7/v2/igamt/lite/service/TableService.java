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

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.exception.DynTable0396Exception;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;

public interface TableService {

  Table findById(String id);

  Table save(Table table);

  List<Table> findAll();

  List<Table> findByScopesAndVersion(List<SCOPE> scopes, String hl7Version);

  Table findByScopeAndVersionAndBindingIdentifier(SCOPE scope, String hl7Version,
      String bindingIdentifier);

  List<Table> findShared(Long accountId);

  List<Table> findPendingShared(Long accountId);

  void delete(Table table);

  void delete(List<Table> tables);

  void delete(String id);

  void save(List<Table> tables);

  List<Table> findAllByIds(Set<String> ids);

  List<Table> findShortAllByIds(Set<String> ids);

  Date updateDate(String id, Date date);

  Table save(Table table, Date date);

  void updateStatus(String id, STATUS status);

  Table findOneShortById(String id);

  List<Table> findByScope(String name);

  List<Table> findByBindingIdentifierAndScope(String bindingIdentifier, String scope);

  Table findOneByScopeAndBindingIdentifier(String scope, String bindingIdentifier);

  List<Table> findByScopeAndVersion(String scope, String hl7Version);

  public void updateDescription(String id, String description);
  
  public void updateAllDescription(String id, String description, String defPreText, String defPostText);

  void updateCodeSystem(String id, Set<String> codesSystemtoAdd);

  void updateAttributes(String id, String attributeName, Object value);

  List<Table> findShortByScope(String scope);

  Table findShortById(String id);

  Table findDynamicTable0396();

  List<Table> findByScopeAndVersionAndBindingIdentifier(String scope, String version,
      String bindingIdentifier);
  
  Table updateTable(Table table,InputStream io) throws DynTable0396Exception;

}
