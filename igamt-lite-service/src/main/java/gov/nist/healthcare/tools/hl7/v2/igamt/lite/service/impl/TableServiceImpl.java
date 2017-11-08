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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ShareParticipantPermission;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.TableRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;

/**
 * @author gcr1
 *
 */
@Service
public class TableServiceImpl implements TableService {

  Logger log = LoggerFactory.getLogger(TableServiceImpl.class);

  @Autowired
  private TableRepository tableRepository;

  @Override
  public List<Table> findAll() {
    return tableRepository.findAll();
  }

  @Override
  public Table findById(String id) {
    if (id != null) {
      log.info("TableServiceImpl.findById=" + id);
      return tableRepository.findOne(id);
    }
    return null;
  }

  @Override
  public Table findOneShortById(String id) {
    if (id != null) {
      log.info("TableServiceImpl.findOneShortById=" + id);
      return tableRepository.findOneShortById(id);
    }
    return null;
  }

  @Override
  public List<Table> findByScopesAndVersion(List<SCOPE> scopes, String hl7Version) {
    List<Table> tables = tableRepository.findByScopesAndVersion(scopes, hl7Version);
    log.info("TableServiceImpl.findByScopeAndVersion=" + tables.size());
    return tables;
  }

  @Override
  public Table findByScopeAndVersionAndBindingIdentifier(SCOPE scope, String hl7Version,
      String bindingIdentifier) {
    List<SCOPE> scopes = new ArrayList<SCOPE>();
    scopes.add(scope);

    List<Table> tables = this.findByScopesAndVersion(scopes, hl7Version);

    for (Table t : tables) {
      if (t.getBindingIdentifier().equals(bindingIdentifier))
        return t;
    }
    return null;
  }

  @Override
  public List<Table> findShared(Long accountId) {
    // TODO Auto-generated method stub
    List<Table> tables = tableRepository.findShared(accountId);
    List<Table> sharedWithAccount = new ArrayList<Table>();
    for (Table t : tables) {
      for (ShareParticipantPermission p : t.getShareParticipantIds()) {
        if (p.getAccountId() == accountId && !p.isPendingApproval()) {
          sharedWithAccount.add(t);
        }
      }
    }
    return sharedWithAccount;
  }

  @Override
  public List<Table> findPendingShared(Long accountId) {
    // TODO Auto-generated method stub
    List<Table> tables = tableRepository.findShared(accountId);
    List<Table> sharedWithAccount = new ArrayList<Table>();
    for (Table t : tables) {
      for (ShareParticipantPermission p : t.getShareParticipantIds()) {
        if (p.getAccountId() == accountId && p.isPendingApproval()) {
          sharedWithAccount.add(t);
        }
      }
    }
    return sharedWithAccount;
  }

  @Override
  public Table save(Table table) {
    log.info("TableServiceImpl.save=" + table.getBindingIdentifier());
    return tableRepository.save(table);
  }

  @Override
  public void delete(Table table) {
    log.info("TableServiceImpl.delete=" + table.getBindingIdentifier());
    tableRepository.delete(table);
  }

  @Override
  public void delete(String id) {
    log.info("TableServiceImpl.delete=" + id);
    tableRepository.delete(id);
  }

  @Override
  public void save(List<Table> tables) {
    // TODO Auto-generated method stub
    tableRepository.save(tables);
  }

  @Override
  public List<Table> findAllByIds(Set<String> ids) {
    // TODO Auto-generated method stub
    return tableRepository.findAllByIds(ids);
  }

  @Override
  public List<Table> findShortAllByIds(Set<String> ids) {
    // TODO Auto-generated method stub
    return tableRepository.findShortAllByIds(ids);
  }

  @Override
  public Table save(Table table, Date date) {
    log.info("TableServiceImpl.save=" + table.getBindingIdentifier());
    table.setDateUpdated(date);
    return tableRepository.save(table);
  }

  @Override
  public Date updateDate(String id, Date date) {
    return tableRepository.updateDate(id, date);
  }

  @Override
  public void updateStatus(String id, STATUS status) {
    tableRepository.updateStatus(id, status);
  }

  @Override
  public void delete(List<Table> tables) {
    tableRepository.delete(tables);
  }

  @Override
  public List<Table> findByScope(String scope) {
    return tableRepository.findByScope(scope);
  }

  @Override
  public List<Table> findByBindingIdentifierAndScope(String bindingIdentifier, String scope) {
    return tableRepository.findByBindingIdentifierAndScope(bindingIdentifier, scope);
  }

  @Override
  public Table findOneByScopeAndBindingIdentifier(String scope, String bindingIdentifier) {
    return tableRepository.findOneByScopeAndBindingIdentifier(scope, bindingIdentifier);
  }

  @Override
  public List<Table> findByScopeAndVersion(String scope, String hl7Version) {
    return tableRepository.findByScopeAndVersion(scope, hl7Version);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService#updateDescription(java.lang.
   * String, java.lang.String)
   */
  @Override
  public void updateDescription(String id, String description) {
    // TODO Auto-generated method stub
    tableRepository.updateDescription(id, description);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService#updateCodeSystem(java.lang.
   * String, java.util.Set)
   */
  @Override
  public void updateCodeSystem(String id, Set<String> codesSystemtoAdd) {
    // TODO Auto-generated method stub
    tableRepository.updateCodeSystem(id, codesSystemtoAdd);
  }


  @Override
  public void updateAttributes(String id, String attributeName, Object value) {
    // TODO Auto-generated method stub
    tableRepository.updateAttributes(id, attributeName, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService#findShortByScope(java.lang.
   * String)
   */
  @Override
  public List<Table> findShortByScope(String scope) {
    // TODO Auto-generated method stub
    return tableRepository.findShortByScope(scope);
  }

  @Override
  public Table findShortById(String id) {
    // TODO Auto-generated method stub
    return tableRepository.findShortById(id);
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService#
   * findByScopeAndVersionAndBindingIdentifier(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public List<Table> findByScopeAndVersionAndBindingIdentifier(String scope, String version,
      String bindingIdentifier) {
    // TODO Auto-generated method stub
    return tableRepository.findByScopeAndVersionAndBindingIdentifier(scope, version,
        bindingIdentifier);
  }
}
