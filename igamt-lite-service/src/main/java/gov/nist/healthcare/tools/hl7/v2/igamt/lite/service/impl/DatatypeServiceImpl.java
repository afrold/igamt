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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ShareParticipantPermission;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DatatypeRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;

/**
 * @author gcr1
 *
 */
@Service
public class DatatypeServiceImpl implements DatatypeService {

  Logger log = LoggerFactory.getLogger(DatatypeServiceImpl.class);

  @Autowired
  private DatatypeRepository datatypeRepository;

  @Override
  public List<Datatype> findAll() {
    List<Datatype> datatypes = datatypeRepository.findAll();
    log.info("DataypeServiceImpl.findAll=" + datatypes.size());
    return datatypes;
  }

  @Override
  public Datatype findById(String id) {
    log.info("DataypeServiceImpl.findById=" + id);
    Datatype datatype;
    datatype = datatypeRepository.findOne(id);
    return datatype;
  }

  @Override
  public List<Datatype> findByIds(Set<String> ids) {
    log.info("DataypeServiceImpl.findByIds=" + ids);
    return datatypeRepository.findByIds(ids);
  }

  @Override
  public List<Datatype> findByScopesAndVersion(List<SCOPE> scopes, String hl7Version) {
    List<Datatype> datatypes = datatypeRepository.findByScopesAndVersion(scopes, hl7Version);
    log.info("DataypeServiceImpl.findByScopesAndVersion=" + datatypes.size());
    return datatypes;
  }

  @Override
  public Datatype save(Datatype datatype) {
    log.info("DataypeServiceImpl.save=" + datatype.getId());
    return datatypeRepository.save(datatype);
  }

  @Override
  public void delete(Datatype dt) {
    datatypeRepository.delete(dt);
  }

  @Override
  public void delete(String id) {
    datatypeRepository.delete(id);
  }

  @Override
  public void save(List<Datatype> datatypes) {
    // TODO Auto-generated method stub
    datatypeRepository.save(datatypes);
  }

  @Override
  public Set<Datatype> collectDatatypes(Datatype datatype) {
    Set<Datatype> datatypes = new HashSet<Datatype>();
    if (datatype != null) {
      datatypes.add(datatype);
      List<Component> components = datatype.getComponents();
      for (Component component : components) {
        DatatypeLink link = component.getDatatype();
        datatypes.addAll(collectDatatypes(this.findById(link.getId())));
      }
    }
    return datatypes;
  }

  @Override
  public List<Datatype> findByScope(String scope) {
    // TODO Auto-generated method stub
    return datatypeRepository.findByScope(scope);
  }

  @Override
  public List<Datatype> findShared(Long accountId) {
    // TODO Auto-generated method stub
    List<Datatype> datatypes = datatypeRepository.findShared(accountId);
    List<Datatype> sharedWithAccount = new ArrayList<Datatype>();
    for (Datatype d : datatypes) {
      for (ShareParticipantPermission p : d.getShareParticipantIds()) {
        if (p.getAccountId() == accountId && !p.isPendingApproval()) {
          sharedWithAccount.add(d);
        }
      }
    }
    return sharedWithAccount;
  }

  @Override
  public List<Datatype> findPendingShared(Long accountId) {
    // TODO Auto-generated method stub
    List<Datatype> datatypes = datatypeRepository.findShared(accountId);
    List<Datatype> sharedWithAccount = new ArrayList<Datatype>();
    for (Datatype d : datatypes) {
      for (ShareParticipantPermission p : d.getShareParticipantIds()) {
        if (p.getAccountId() == accountId && p.isPendingApproval()) {
          sharedWithAccount.add(d);
        }
      }
    }
    return sharedWithAccount;
  }

  @Override
  public List<Datatype> findByNameAndVersionAndScope(String name, String version, String scope) {
    // TODO Auto-generated method stub
    return datatypeRepository.findByNameAndVersionAndScope(name, version, scope);
  }

  @Override
  public Datatype findByNameAndVersionsAndScope(String name, String[] versions, String scope) {
    // TODO Auto-generated method stub
    return datatypeRepository.findByNameAndVersionsAndScope(name, versions, scope);
  }

  @Override
  public Datatype save(Datatype datatype, Date date) {
    log.info("DataypeServiceImpl.save=" + datatype.getId());
    datatype.setDateUpdated(date);
    return datatypeRepository.save(datatype);
  }

  @Override
  public List<Datatype> findAllByNameAndVersionsAndScope(String name, List<String> versions,
      String string) {
    return datatypeRepository.findAllByNameAndVersionsAndScope(name, versions, string);
  }

  @Override
  public Date updateDate(String id, Date date) {
    return datatypeRepository.updateDate(id, date);
  }

  @Override
  public void updateStatus(String id, STATUS status) {
    datatypeRepository.updateStatus(id, status);
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService#
   * findByCompatibleVersion( java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public Datatype findByNameAndVesionAndScope(String name, String version, String scope) {
    List<Datatype> datatypes = datatypeRepository.findByNameAndScope(name, scope);
    for (Datatype dt : datatypes) {
      for (String s : dt.getHl7versions()) {
        if (version.equals(s)) {
          return dt;
        }
      }
    }

    if (datatypes != null && datatypes.size() > 0)
      return datatypes.get(0);

    return null;
  }

  @Override
  public Datatype findOneByNameAndVersionAndScope(String name, String version, String scope) {
    return datatypeRepository.findOneByNameAndVersionAndScope(name, version, scope);
  }

  @Override
  public Datatype findByCompatibleVersion(String name, String version, String scope)
      throws Exception {
    // TODO Auto-generated method stub
    Datatype result = null;
    List<Datatype> datatypes = datatypeRepository.findByNameAndScope(name, scope);
    for (Datatype dt : datatypes) {
      for (String s : dt.getHl7versions()) {
        if (version.equals(s)) {
          result = dt;
        }
      }
    }

    return result;
  }

  @Override
  public void delete(List<Datatype> datatypes) {
    datatypeRepository.delete(datatypes);
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService#
   * findByScopeAndVersionAndParentVersion(gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.
   * Constant.SCOPE, java.lang.String, java.lang.String)
   */
  @Override
  public List<Datatype> findByScopeAndVersionAndParentVersion(SCOPE scope, String hl7Version,
      String id) {
    return datatypeRepository.findByScopeAndVersionAndParentVersion(scope, hl7Version, id);
  }

  @Override
  public List<Datatype> findByNameAndScope(String name, String scope) {
    return datatypeRepository.findByNameAndScope(name, scope);
  }

  @Override
  public List<Datatype> findByScopeAndVersion(String scope, String hl7Version) {
    return datatypeRepository.findByScopeAndVersion(scope, hl7Version);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService#updateAttribute(java.lang.
   * String, java.lang.String, java.lang.Object)
   */
  @Override
  public void updateAttribute(String id, String attributeName, Object value) {
    // TODO Auto-generated method stub
    datatypeRepository.updateAttribute(id, attributeName, value);
  }
}
