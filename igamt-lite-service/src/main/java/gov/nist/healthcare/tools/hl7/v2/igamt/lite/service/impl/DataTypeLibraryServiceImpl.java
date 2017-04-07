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
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibraryMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DatatypeLibraryRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DatatypeRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.DateUtils;

/**
 * @author gcr1
 *
 */
@Service
public class DataTypeLibraryServiceImpl implements DatatypeLibraryService {

  Logger log = LoggerFactory.getLogger(DataTypeLibraryServiceImpl.class);

  @Autowired
  private DatatypeLibraryRepository datatypeLibraryRepository;

  @Autowired
  private DatatypeRepository datatypeRepository;

  @Autowired
  private DatatypeService datatypeService;

  private Random rand = new Random();

  @Override
  public List<DatatypeLibrary> findAll() {
    List<DatatypeLibrary> datatypeLibrary = datatypeLibraryRepository.findAll();
    log.debug("DatatypeLibraryRepository.findAll datatypeLibrary=" + datatypeLibrary.size());
    return datatypeLibrary;
  }

  @Override
  public List<DatatypeLibrary> findByScope(SCOPE scope, Long accountId) {
    List<DatatypeLibrary> datatypeLibrary = datatypeLibraryRepository.findByScope(scope, accountId);
    log.debug("DatatypeLibraryRepository.findByScope datatypeLibrary=" + datatypeLibrary.size());
    return datatypeLibrary;
  }

  @Override
  public List<String> findHl7Versions() {
    return datatypeLibraryRepository.findHl7Versions();
  }

  @Override
  public DatatypeLibrary findById(String id) {
    return datatypeLibraryRepository.findOne(id);
  }

  @Override
  public List<DatatypeLibrary> findByScopesAndVersion(List<SCOPE> scopes, String hl7Version) {
    log.info("DataTypeLibraryibServiceImpl.findByScopesAndVersion. start");
    List<DatatypeLibrary> datatypeLibraries =
        datatypeLibraryRepository.findScopesNVersion(scopes, hl7Version);
    log.info("DataTypeLibraryibServiceImpl.findByScopesAndVersion datatypeLibraries="
        + datatypeLibraries.size());
    return datatypeLibraries;
  }

  @Override
  public List<DatatypeLibrary> findByAccountId(Long accountId, String hl7Version) {
    List<DatatypeLibrary> datatypeLibrary =
        datatypeLibraryRepository.findByAccountId(accountId, hl7Version);
    log.info("datatypeLibrary=" + datatypeLibrary.size());
    return datatypeLibrary;
  }

  @Override
  public DatatypeLibrary save(DatatypeLibrary library) {
    return save(library, DateUtils.getCurrentDate());
  }

  @Override
  public DatatypeLibrary save(DatatypeLibrary library, Date dateUpdated) {
    library.setDateUpdated(dateUpdated);
    DatatypeLibrary datatypeLibrary = datatypeLibraryRepository.save(library);
    return datatypeLibrary;
  }

  @Override
  public DatatypeLibrary saveMetaData(String libId,
      DatatypeLibraryMetaData datatypeLibraryMetaData) {
    log.info("DataypeServiceImpl.save=" + datatypeLibraryMetaData.getName());
    DatatypeLibrary dataTypeLibrary = datatypeLibraryRepository.findOne(libId);
    dataTypeLibrary.setMetaData(datatypeLibraryMetaData);
    return datatypeLibraryRepository.save(dataTypeLibrary);
  }

  DatatypeLibraryMetaData defaultMetadata() {
    DatatypeLibraryMetaData metaData = new DatatypeLibraryMetaData();
    metaData.setName("Master data type library");
    metaData.setOrgName("NIST");
    return metaData;
  }

  @Override
  public DatatypeLibrary create(String name, String ext, SCOPE scope, String hl7Version,
      Long accountId) {
    DatatypeLibraryMetaData metaData = defaultMetadata();
    metaData.setName(name);
    metaData.setHl7Version(hl7Version);
    metaData.setDatatypeLibId(UUID.randomUUID().toString());
    metaData.setExt(ext);
    DatatypeLibrary datatypeLibrary = new DatatypeLibrary();
    datatypeLibrary.setMetaData(metaData);
    datatypeLibrary.setScope(scope);
    datatypeLibrary.setAccountId(accountId);
    datatypeLibrary.setSectionDescription("Default description");
    datatypeLibrary.setSectionTitle("Default title");
    datatypeLibrary.setSectionContents("Default contents");
    return datatypeLibraryRepository.save(datatypeLibrary);

  }

  @Override
  public void delete(String dtLibId) {
    List<Datatype> datatypes = findDatatypesById(dtLibId);
    for (Datatype datatype : datatypes) {
      Set<String> libIds = datatype.getLibIds();
      libIds.remove(dtLibId);
      if (libIds.isEmpty()) {
        datatypeRepository.delete(datatype);
      }
    }
    datatypeLibraryRepository.delete(dtLibId);
  }

  @Override
  public List<DatatypeLink> bindDatatypes(Set<String> datatypeIds, String datatypeLibraryId,
      String datatypeLibraryExt, Long accountId) {

    DatatypeLibrary dtLib = datatypeLibraryRepository.findById(datatypeLibraryId);
    dtLib.setExt(deNull(datatypeLibraryExt));
    List<DatatypeLibrary> dtLibDups = datatypeLibraryRepository.findDups(dtLib);
    if (dtLibDups != null) {
      String ext = decorateExt(dtLib.getExt());
      dtLib.setExt(ext);
    }
    dtLib.getMetaData().setExt(dtLib.getExt());
    dtLib.setAccountId(accountId);

    List<Datatype> datatypes = datatypeRepository.findByIds(datatypeIds);
    List<DatatypeLink> datatypeLinks = new ArrayList<DatatypeLink>();
    for (Datatype dt : datatypes) {
      dt.setId(null);
      if (SCOPE.HL7STANDARD == dt.getScope()) {
        dt.getLibIds().clear();
      }
      dt.getLibIds().add(datatypeLibraryId);
      dt.setExt(decorateExt(dtLib.getExt()));
      dt.setType(Constant.DATATYPE);
      dt.setScope(dtLib.getScope());
      dt.setHl7Version(dtLib.getMetaData().getHl7Version());
      dt.setAccountId(accountId);
      // We save at this point in order to have an id for the link.
      datatypeService.save(dt);
      DatatypeLink dtLink = dtLib.addDatatype(dt);
      datatypeLinks.add(dtLink);
    }
    datatypeLibraryRepository.save(dtLib);
    return datatypeLinks;
  }

  boolean checkDup(Datatype dt, DatatypeLibrary dtLib, String ext) {
    return dtLib.getChildren().contains(new DatatypeLink(dt.getId(), dt.getName(), ext));
  }

  String decorateExt(String ext) {
    return ext + "-" + genRand();
  }

  String deNull(String ext) {
    return (ext != null && ext.trim().length() > 0) ? ext : genRand();
  }

  String genRand() {
    return Integer.toString(rand.nextInt(100));
  }

  class DatatypeByLabel implements Comparator<Datatype> {

    @Override
    public int compare(Datatype thisDt, Datatype thatDt) {
      return thatDt.getLabel().compareTo(thisDt.getLabel());
    }
  }

  @Override
  public List<DatatypeLink> findFlavors(SCOPE scope, String hl7Version, String name,
      Long accountId) {
    return datatypeLibraryRepository.findFlavors(scope, hl7Version, name, accountId);
  }

  @Override
  public List<DatatypeLibrary> findLibrariesByFlavorName(SCOPE scope, String hl7Version,
      String name, Long accountId) {
    return datatypeLibraryRepository.findByNameAndHl7VersionAndScope(name, hl7Version,
        scope.toString());
  }

  @Override
  public List<Datatype> getChildren(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void delete(DatatypeLibrary library) {
    if (library != null) {
      Set<DatatypeLink> links = library.getChildren();
      if (links != null && links.size() > 0) {
        Set<String> ids = new HashSet<String>();
        for (DatatypeLink link : links) {
          ids.add(link.getId());
        }
        List<Datatype> datatypes = datatypeRepository.findUserDatatypesByIds(ids);
        List<Datatype> tmp = new ArrayList<Datatype>();

        if (datatypes != null) {
          for (Datatype dt : datatypes) {
            if (dt.getStatus() == null || !dt.getStatus().equals(STATUS.PUBLISHED)) {
              tmp.add(dt);
            }
          }
        }

        if (tmp.size() > 0) {
          datatypeRepository.delete(tmp);
        }
      }
      if (library.getId() != null)
        datatypeLibraryRepository.delete(library);
    }
  }

  @Override
  public List<Datatype> findDatatypesById(String libId) {
    Set<DatatypeLink> datatypeLinks = datatypeLibraryRepository.findChildrenById(libId);
    if (datatypeLinks != null && !datatypeLinks.isEmpty()) {
      Set<String> ids = new HashSet<String>();
      for (DatatypeLink link : datatypeLinks) {
        ids.add(link.getId());
      }
      return datatypeRepository.findByIds(ids);
    }
    return new ArrayList<Datatype>(0);
  }

}
