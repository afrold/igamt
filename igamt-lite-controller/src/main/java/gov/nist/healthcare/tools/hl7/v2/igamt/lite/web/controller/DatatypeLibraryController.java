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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.nht.acmgt.dto.ResponseMessage;
import gov.nist.healthcare.nht.acmgt.dto.domain.Account;
import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibraryMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.exception.LibraryException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.exception.LibraryNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.LibrarySaveResponse;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DatatypeSaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.LibrarySaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.NotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.UserAccountNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers.BindingWrapper;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers.LibraryCreateWrapper;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers.ScopesAndVersionWrapper;

/**
 * @author Harold Affo (harold.affo@nist.gov) Mar 17, 2015
 */

@RestController
@RequestMapping("/datatype-library")
public class DatatypeLibraryController extends CommonController {

  Logger log = LoggerFactory.getLogger(DatatypeLibraryController.class);
  HashMap<String, Datatype> checked = new HashMap<String, Datatype>();

  @Autowired
  private DatatypeLibraryService datatypeLibraryService;

  @Autowired
  private DatatypeService datatypeService;

  @Autowired
  UserService userService;

  @Autowired
  AccountRepository accountRepository;

  @RequestMapping(method = RequestMethod.GET)
  public List<DatatypeLibrary> getDatatypeLibraries() {
    log.info("Fetching all datatype libraries.");
    List<DatatypeLibrary> datatypeLibraries = datatypeLibraryService.findAll();
    return datatypeLibraries;
  }

  @RequestMapping(value = "/{dtLibId}/datatypes", method = RequestMethod.GET,
      produces = "application/json")
  public List<Datatype> getDatatypesByLibrary(@PathVariable("dtLibId") String dtLibId) {
    log.info("Fetching datatypeByLibrary..." + dtLibId);
    List<Datatype> result = datatypeLibraryService.findDatatypesById(dtLibId);
    return result;
  }

  @RequestMapping(value = "/{dtLibId}/publishedDts", method = RequestMethod.POST,
      produces = "application/json")
  public List<Datatype> getPublishedDatatypesByLibraryPublished(
      @PathVariable("dtLibId") String dtLibId, @RequestBody String version) {

    log.info("Fetching datatypeByLibrary..." + dtLibId);
    DatatypeLibrary dtlib=datatypeLibraryService.findById(dtLibId);
    

    List<Datatype> temp = datatypeLibraryService.findDatatypesById(dtLibId);
    List<Datatype> result = new ArrayList<Datatype>();

    for (Datatype dt : temp) {
      if (!dt.getHl7versions().isEmpty()) {

        if (dt.getScope().equals(dtlib.getScope())
            && dt.getStatus().equals(STATUS.PUBLISHED)) {
          if (dt.getHl7versions() != null && !dt.getHl7versions().isEmpty()) {
            Collections.sort(dt.getHl7versions());
            if (dt.getHl7versions().contains(version)
                || dt.getHl7versions().get(0).compareTo(version) > 0)
              result.add(dt);
          }
        }
      }

    }
    return result;
  }

  @RequestMapping(value = "/{dtLibId}/publishedDtsForLibrary", method = RequestMethod.POST,
      produces = "application/json")
  public List<Datatype> getPublishedDatatypesByLibraryPublished(
      @PathVariable("dtLibId") String dtLibId) {

    log.info("Fetching datatypeByLibrary..." + dtLibId);

    List<Datatype> temp = datatypeLibraryService.findDatatypesById(dtLibId);
    List<Datatype> result = new ArrayList<Datatype>();

    for (Datatype dt : temp) {
      if (!dt.getScope().toString().equals("INTERMASTER")
          && dt.getStatus().equals(STATUS.PUBLISHED)) {
        result.add(dt);
      }
    }
    return result;
  }

  @RequestMapping(value = "/findByScope", method = RequestMethod.POST,
      produces = "application/json")
  public List<DatatypeLibrary> findByScope(@RequestBody String scope_) {
    log.info("Fetching datatype libraries...");
    List<DatatypeLibrary> datatypeLibraries = null;
    try {
      Long accountId = null;
      SCOPE scope = SCOPE.valueOf(scope_);
      User u = userService.getCurrentUser();
      Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
      if (account == null) {
        throw new UserAccountNotFoundException();
      }
      accountId = account.getId();
      datatypeLibraries = datatypeLibraryService.findByScope(scope, accountId);
    } catch (Exception e) {
      log.error("", e);
    }
    return datatypeLibraries;
  }

  @RequestMapping(value = "/findByScopesAndVersion", method = RequestMethod.POST,
      produces = "application/json")
  public List<DatatypeLibrary> findByScopesAndVersion(
      @RequestBody ScopesAndVersionWrapper scopesAndVersion) {
    log.info("Fetching the datatype library. scope=" + scopesAndVersion.getScopes() + " hl7Version="
        + scopesAndVersion.getHl7Version());
    List<DatatypeLibrary> datatypes = new ArrayList<DatatypeLibrary>();
    try {
      Long accountId = null;
      User u = userService.getCurrentUser();
      Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
      if (account == null) {
        throw new UserAccountNotFoundException();
      }
      accountId = account.getId();
      // When USER is one of the scopes, we must use the accountId to
      // narrow the results.
      // We then remove the USER scope before running the second query.
      if (scopesAndVersion.getScopes().contains(SCOPE.USER)) {
        datatypes.addAll(
            datatypeLibraryService.findByAccountId(accountId, scopesAndVersion.getHl7Version()));
        scopesAndVersion.getScopes().remove(SCOPE.USER);
      }
      datatypes.addAll(datatypeLibraryService.findByScopesAndVersion(scopesAndVersion.getScopes(),
          scopesAndVersion.getHl7Version()));
      if (datatypes.isEmpty()) {
        throw new NotFoundException("Datatype not found for scopesAndVersion=" + scopesAndVersion);
      }
    } catch (Exception e) {
      log.error("", e);
    }
    return datatypes;
  }

  @RequestMapping(value = "/findHl7Versions", method = RequestMethod.GET,
      produces = "application/json")
  public List<String> findHl7Versions() {
    log.info("Fetching all HL7 versions.");
    List<String> result = datatypeLibraryService.findHl7Versions();
    return result;
  }

  @RequestMapping(value = "/{accountId}/{hl7Version}/findByAccountId", method = RequestMethod.GET)
  public List<DatatypeLibrary> findByAccountId(@PathVariable("accountId") Long accountId,
      @PathVariable("hl7Version") String hl7Version)
      throws LibraryNotFoundException, UserAccountNotFoundException, LibraryException {
    log.info("Fetching the datatype libraries...");
    List<DatatypeLibrary> result = datatypeLibraryService.findByAccountId(accountId, hl7Version);
    return result;
  }

  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public DatatypeLibrary create(@RequestBody LibraryCreateWrapper dtlcw) {
    SCOPE scope = SCOPE.valueOf(dtlcw.getScope());

    return datatypeLibraryService.create(dtlcw.getName(), dtlcw.getExt(), scope,
        dtlcw.getHl7Version(), dtlcw.getAccountId());
  }

  @RequestMapping(value = "/{libId}/saveMetaData", method = RequestMethod.POST)
  public LibrarySaveResponse saveMetaData(@PathVariable("libId") String libId,
      @RequestBody DatatypeLibraryMetaData datatypeLibraryMetaData) throws LibrarySaveException {
    log.info("Saving the " + datatypeLibraryMetaData.getName() + " datatype library.");
    DatatypeLibrary saved = datatypeLibraryService.saveMetaData(libId, datatypeLibraryMetaData);
    return new LibrarySaveResponse(saved.getDateUpdated().getTime() + "", saved.getScope().name());
  }

  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public LibrarySaveResponse save(@RequestBody DatatypeLibrary datatypeLibrary)
      throws LibrarySaveException {
    log.info("Saving the " + datatypeLibrary.getMetaData().getName() + " datatype library.");
    DatatypeLibrary saved = datatypeLibraryService.save(datatypeLibrary);
    return new LibrarySaveResponse(saved.getDateUpdated().getTime() + "", saved.getScope().name());
  }

  @RequestMapping(value = "/{id}/delete", method = RequestMethod.GET)
  public ResponseMessage delete(@PathVariable String id) {
    datatypeLibraryService.delete(id);
    return new ResponseMessage(ResponseMessage.Type.success, "datatypeLibraryDeletedSuccess", null);
  }

  @RequestMapping(value = "/bindDatatypes", method = RequestMethod.POST)
  public List<DatatypeLink> bindDatatypes(@RequestBody BindingWrapper binding)
      throws DatatypeSaveException {
    log.debug("Binding datatypes=" + binding.getDatatypeIds().size());
    List<DatatypeLink> bound = datatypeLibraryService.bindDatatypes(binding.getDatatypeIds(),
        binding.getDatatypeLibraryId(), binding.getDatatypeLibraryExt(), binding.getAccountId());
    return bound;
  }

  @RequestMapping(value = "/{libId}/addChild", method = RequestMethod.POST)
  public DatatypeLink addChild(@PathVariable String libId, @RequestBody DatatypeLink datatypeLink)
      throws DatatypeSaveException {
    log.debug("Adding a link to the library");
    DatatypeLibrary lib = datatypeLibraryService.findById(libId);
    lib.addDatatype(datatypeLink);
    datatypeLibraryService.save(lib);
    return datatypeLink;
  }

  @RequestMapping(value = "/{libId}/updateChild", method = RequestMethod.POST)
  public DatatypeLink updateChild(@PathVariable String libId,
      @RequestBody DatatypeLink datatypeLink) throws DatatypeSaveException {
    log.debug("Adding a link to the library");
    DatatypeLibrary lib = datatypeLibraryService.findById(libId);
    DatatypeLink found = lib.findOne(datatypeLink.getId());
    if (found != null) {
      found.setExt(datatypeLink.getExt());
      found.setName(datatypeLink.getName());
    }
    datatypeLibraryService.save(lib);
    return datatypeLink;
  }

  @RequestMapping(value = "/{libId}/deleteChild/{id}", method = RequestMethod.POST)
  public boolean deleteChild(@PathVariable String libId, @PathVariable String id)
      throws DatatypeSaveException {
    log.debug("Deleting a link to the library");
    DatatypeLibrary lib = datatypeLibraryService.findById(libId);
    DatatypeLink found = lib.findOne(id);
    if (found != null) {
      lib.getChildren().remove(found);
      datatypeLibraryService.save(lib);
    }
    return true;
  }

  @RequestMapping(value = "/findFlavors", method = RequestMethod.GET, produces = "application/json")
  public List<DatatypeLink> findFlavors(@RequestParam("name") String name,
      @RequestParam("hl7Version") String hl7Version, @RequestParam("scope") SCOPE scope) {
    log.info("Finding flavors of datatype, name=" + name + ", hl7Version=" + hl7Version + ", scope="
        + scope + "...");
    org.springframework.security.core.userdetails.User u = userService.getCurrentUser();
    Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
    List<DatatypeLink> datatypes =
        datatypeLibraryService.findFlavors(scope, hl7Version, name, account.getId());
    return datatypes;
  }

  @RequestMapping(value = "/findLibrariesByFlavorName", method = RequestMethod.GET,
      produces = "application/json")
  public List<DatatypeLibrary> findLibrariesByFlavorName(@RequestParam("name") String name,
      @RequestParam("hl7Version") String hl7Version, @RequestParam("scope") SCOPE scope) {
    log.info("Finding flavors of datatype, name=" + name + ", hl7Version=" + hl7Version + ", scope="
        + scope + "...");
    org.springframework.security.core.userdetails.User u = userService.getCurrentUser();
    Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
    List<DatatypeLibrary> libraries =
        datatypeLibraryService.findLibrariesByFlavorName(scope, hl7Version, name, account.getId());
    if (libraries != null) {
      for (int i = 0; i < libraries.size(); i++) {
        DatatypeLibrary lib = libraries.get(i);
        Set<DatatypeLink> results = new HashSet<DatatypeLink>();
        Set<DatatypeLink> links = lib.getChildren();
        Iterator<DatatypeLink> it = links.iterator();
        while (it.hasNext()) {
          DatatypeLink link = it.next();
          if (link.getName().equals(name)) {
            results.add(link);
          }
        }
        lib.setChildren(results);
      }
    }
    return libraries;
  }

  @RequestMapping(value = "/{libId}/addChildren", method = RequestMethod.POST)
  public Set<DatatypeLink> addChild(@PathVariable String libId,
      @RequestBody Set<DatatypeLink> datatypeLinks) throws DatatypeSaveException {
    log.debug("Adding a link to the library");
    DatatypeLibrary lib = datatypeLibraryService.findById(libId);
    lib.addDatatypes(datatypeLinks);
    datatypeLibraryService.save(lib);
    return datatypeLinks;
  }

  @RequestMapping(value = "/{libId}/addChildrenFromDatatypes", method = RequestMethod.POST)
  public Set<Datatype> addDatatypeFromLibrary(@PathVariable String libId,
      @RequestBody Set<Datatype> datatypes) throws Exception {
    log.debug("Adding a link to the library");
    checked = new HashMap<String, Datatype>();
    List<Datatype> datatypeInLib = new ArrayList<Datatype>();
    DatatypeLibrary lib = datatypeLibraryService.findById(libId);
    if (!lib.getChildren().isEmpty()) {
      for (DatatypeLink link : lib.getChildren()) {
        Datatype temp = datatypeService.findById(link.getId());

        if (temp != null) {
          if (temp.getParentVersion() != null) {
            String v = (temp.getParentVersion() + temp.getHl7Version()).replace(".", "V");
            checked.put(v, temp);
          }
          datatypeInLib.add(temp);
        }
      }
    }
    Set<String> sts = checked.keySet();
    System.out.println(sts);
    Set<Datatype> datatypesToAdd = new HashSet<Datatype>();
    for (Datatype dt : datatypes) {
      processDatatypes(dt, datatypesToAdd);
    }
    datatypesToAdd.addAll(datatypesToAdd);
    HashMap<DatatypeLink, Datatype> result = new HashMap<DatatypeLink, Datatype>();
    Set<Datatype> finallyAdded = new HashSet<Datatype>();
    Set<DatatypeLink> finallAddesLinks = new HashSet<DatatypeLink>();
    for (Datatype dt : datatypesToAdd) {
      if (!datatypeInLib.contains(dt)) {
        finallyAdded.add(dt);
        DatatypeLink link = new DatatypeLink(dt.getId(), dt.getName(), dt.getExt());
        finallAddesLinks.add(link);
        Set<DatatypeLink> links = lib.getChildren();
        links.add(link);
        lib.setChildren(links);
        result.put(link, dt);
      }
    }
    datatypeLibraryService.save(lib);
    return finallyAdded;
  }

  /**
   * @param dt
   * @throws Exception
   */
  private void processDatatypes(Datatype dt, Set<Datatype> temp) throws Exception {

    if (dt.getParentVersion() != null) {
      String v = (dt.getParentVersion() + dt.getHl7Version()).replace(".", "V");
      if (!checked.containsKey(v)) {
        temp.add(dt);
        if (!dt.getComponents().isEmpty()) {
          processComponents(dt, temp);
        }
        String v2 = (dt.getParentVersion() + dt.getHl7Version()).replace(".", "V");
        checked.put(v2, dt);

      } else {
        return;

      }

    } else {
      temp.add(dt);
      if (!dt.getComponents().isEmpty()) {
        processComponents(dt, temp);
      }
    }

    datatypeService.save(dt);

  }

  private void processComponents(Datatype dt, Set<Datatype> temp) throws Exception {
    for (Component c : dt.getComponents()) {
      if (c.getDatatype() != null) {
        if (c.getDatatype().getId() != null) {
          Datatype dtInside = datatypeService.findById(c.getDatatype().getId());

          if (dtInside != null) {
            if (dtInside.getParentVersion() != null) {
              String v = (dtInside.getParentVersion() + dtInside.getHl7Version()).replace(".", "V");
              if (checked.containsKey(v)) {

                DatatypeLink link = new DatatypeLink();
                link.setId(checked.get(v).getId());
                link.setName(checked.get(v).getName());
                link.setExt(checked.get(v).getExt());
                c.setDatatype(link);

              } else {
                // checked.put(v, dtInside);
                processDatatypes(dtInside, temp);
              }
            } else {
              processDatatypes(dtInside, temp);
            }



          } else {

            throw new Exception("datatpe not found");


          }



        }
      }

    }

  }



}
