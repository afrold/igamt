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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;


public interface TableOperations {

  List<Table> findByScopesAndVersion(List<SCOPE> scopes, String hl7Version);

  List<Table> findByScopeAndVersion(String scope, String hl7Version);

  List<Table> findBindingIdentifiers(List<String> tableIds);

  List<Table> findUserTablesByIds(Set<String> ids);

  List<Table> findAllByIds(Set<String> ids);

  List<Table> findShared(Long accountId);

  List<Table> findShortAllByIds(Set<String> ids);

  Table findByBindingIdentifierAndHL7VersionAndScope(String bindingIdentifier, String hl7Version,
      SCOPE scope);

  public Date updateDate(String id, Date date);

  public void updateStatus(String id, STATUS status);

  public void updateDescription(String id, String description);
  
  public void updateAllDescription(String id, String description, String defPreText, String defPostText);

  public void updateCodeSystem(String id, Set<String> codesSystemtoAdd);

  public void updateAttributes(String id, String attributeName, Object value);


  Table findOneShortById(String id);

  List<Table> findByScope(String scope);

  public List<Table> findByBindingIdentifierAndScope(String bindingIdentifier, String scope);

  public Table findOneByScopeAndBindingIdentifier(String scope, String bindingIdentifier);

  public List<Table> findShortByScope(String scope);

  public List<Table> findByScopeAndVersionAndBindingIdentifier(String scope, String version,
      String bindingIdentifier);


  public Table findShortById(String id); 
  
  public Table findDynamicTable0396(); 

  

}
