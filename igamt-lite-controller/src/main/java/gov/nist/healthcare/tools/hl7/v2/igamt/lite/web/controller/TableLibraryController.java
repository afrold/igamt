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
import java.util.Date;
import java.util.HashSet;
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
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.nht.acmgt.dto.domain.Account;
import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.exception.LibraryException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.LibrarySaveResponse;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DatatypeSaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.LibraryNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.LibrarySaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.NotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.TableSaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.UserAccountNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers.LibraryCreateWrapper;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers.ScopesAndVersionWrapper;

/**
 * @author Harold Affo (harold.affo@nist.gov) Mar 17, 2015
 */

@RestController
@RequestMapping("/table-library")
public class TableLibraryController extends CommonController {

  Logger log = LoggerFactory.getLogger(TableLibraryController.class);

  @Autowired
  private TableLibraryService tableLibraryService;

  @Autowired
  private TableService tableService;

  @Autowired
  UserService userService;

  @Autowired
  AccountRepository accountRepository;

  @RequestMapping(method = RequestMethod.GET)
  public List<TableLibrary> getTableLibraries() {
    log.info("Fetching all table libraries.");
    List<TableLibrary> tableLibraries = tableLibraryService.findAll();
    return tableLibraries;
  }

  @RequestMapping(value = "/{tabLibId}/tables", method = RequestMethod.GET,
      produces = "application/json")
  public List<Table> getTablesByLibrary(@PathVariable("tabLibId") String tabLibId) {
    log.info("Fetching tableByLibrary..." + tabLibId);
    List<Table> result = tableLibraryService.findAllShortTablesByIds(tabLibId);
    return result;
  }

  @RequestMapping(value = "/findByScopes", method = RequestMethod.POST,
      produces = "application/json")
  public List<TableLibrary> findByScopes(@RequestBody List<String> scopes) {
    log.info("Fetching table libraries...");
    List<SCOPE> scopes1 = new ArrayList<SCOPE>();
    try {
      for (String s : scopes) {
        SCOPE scope = SCOPE.valueOf(s);
        if (scope != null) {
          scopes1.add(scope);
        }
      }
    } catch (Exception e) {
      log.error("", e);
    }
    List<TableLibrary> tableLibraries = tableLibraryService.findByScopes(scopes1);
    return tableLibraries;
  }

  @RequestMapping(value = "/findByScopesAndVersion", method = RequestMethod.POST,
      produces = "application/json")
  public List<Table> findByScopesAndVersion(@RequestBody ScopesAndVersionWrapper scopesAndVersion) {
    log.info("Fetching the table library. scope=" + scopesAndVersion.getScopes() + " hl7Version="
        + scopesAndVersion.getHl7Version());
    List<Table> tables = null;
    try {
      tables = tableService.findByScopesAndVersion(scopesAndVersion.getScopes(),
          scopesAndVersion.getHl7Version());
      if (tables == null) {
        throw new NotFoundException("Table not found for scopesAndVersion=" + scopesAndVersion);
      }
    } catch (Exception e) {
      log.error("", e);
    }
    return tables;
  }

  @RequestMapping(value = "/findHl7Versions", method = RequestMethod.GET,
      produces = "application/json")
  public List<String> findHl7Versions() {
    log.info("Fetching all HL7 versions.");
    List<String> result = tableLibraryService.findHl7Versions();
    return result;
  }

  @RequestMapping(value = "/{accountId}/{hl7Version}/findByAccountId", method = RequestMethod.GET)
  public List<TableLibrary> findByAccountId(@PathVariable("accountId") Long accountId,
      @PathVariable("hl7Version") String hl7Version)
      throws LibraryNotFoundException, UserAccountNotFoundException, LibraryException {
    log.info("Fetching the table libraries...");
    List<TableLibrary> result = tableLibraryService.findByAccountId(accountId, hl7Version);
    return result;
  }

  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public TableLibrary create(@RequestBody LibraryCreateWrapper dtlcw) {
    SCOPE scope = SCOPE.valueOf(dtlcw.getScope());

    return tableLibraryService.create(dtlcw.getName(), dtlcw.getExt(), scope, dtlcw.getHl7Version(),
        dtlcw.getAccountId());
  }

  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public LibrarySaveResponse save(@RequestBody TableLibrary tableLibrary)
      throws LibrarySaveException {
    log.info("Saving the " + tableLibrary.getScope() + " table library.");
    User u = userService.getCurrentUser();
    Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
    tableLibrary.setAccountId(account.getId());
    tableLibrary.setDateUpdated(new Date());
    TableLibrary saved = tableLibraryService.save(tableLibrary);
    return new LibrarySaveResponse(saved.getDateUpdated().getTime() + "", saved.getScope().name());
  }

  @RequestMapping(value = "/{id}/section", method = RequestMethod.POST)
  public LibrarySaveResponse saveExportConfig(@RequestBody TableLibrary library,
      @PathVariable("id") String id) throws LibrarySaveException {
    log.info("Saving the export config for table library with id=" + id);
    User u = userService.getCurrentUser();
    Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
    TableLibrary found = tableLibraryService.findById(id);
    // if (library.getAccountId().equals(account.getId())) {
    // throw new IllegalArgumentException();
    // }
    found.setExportConfig(library.getExportConfig());
    found.setSectionContent(library.getSectionContents());
    found.setSectionTitle(library.getSectionTitle());
    //found.setSectionDescription(library.getSectionDescription());
    found.setDateUpdated(new Date());
    TableLibrary saved = tableLibraryService.save(found);
    return new LibrarySaveResponse(saved.getDateUpdated().getTime() + "", saved.getScope().name());
  }


  @RequestMapping(value = "/{libId}/addChild", method = RequestMethod.POST)
  public TableLink addChild(@PathVariable String libId, @RequestBody TableLink child)
      throws TableSaveException {
    log.debug("Adding a link to the library");
    TableLibrary lib = tableLibraryService.findById(libId);

    lib.addTable(child);
    lib.getCodePresence().put(child.getId(), true);
    tableLibraryService.save(lib);
    return child;
  }

  @RequestMapping(value = "/{libId}/updateChild", method = RequestMethod.POST)
  public TableLink updateChild(@PathVariable String libId, @RequestBody TableLink child)
      throws DatatypeSaveException {
    log.debug("Adding a link to the library");
    TableLibrary lib = tableLibraryService.findById(libId);
    TableLink found = lib.findOneTableById(child.getId());
    if (found != null) {
      found.setBindingIdentifier(child.getBindingIdentifier());
    }
    tableLibraryService.save(lib);
    return child;
  }

  @RequestMapping(value = "/{libId}/deleteChild/{id}", method = RequestMethod.POST)
  public boolean deleteChild(@PathVariable String libId, @PathVariable String id)
      throws TableSaveException {
    log.debug("Deleting a link to the library");
    TableLibrary lib = tableLibraryService.findById(libId);
    TableLink found = lib.findOneTableById(id);
    lib.getCodePresence().remove(id);
    if (found != null) {
      lib.getChildren().remove(found);
      tableLibraryService.save(lib);
    }
    return true;
  }

  @RequestMapping(value = "/{libId}/updatePresence/{tableId}/{value}", method = RequestMethod.POST)
  public boolean updatePresence(@PathVariable String libId, @PathVariable String tableId,
      @PathVariable Boolean value) throws TableSaveException {
    TableLibrary lib = tableLibraryService.findById(libId);
    TableLink found = lib.findOneTableById(tableId);

    if (found != null) {
      lib.getCodePresence().put(tableId, value);
      tableLibraryService.save(lib);
    }
    return value;
  }

  @RequestMapping(value = "/{libId}/addChildren", method = RequestMethod.POST)
  public boolean addChild(@PathVariable String libId, @RequestBody Set<TableLink> tableLinks)
      throws DatatypeSaveException {
    log.debug("Adding a link to the library");
    TableLibrary lib = tableLibraryService.findById(libId);
    for (TableLink link : tableLinks) {
      lib.getCodePresence().put(link.getId(), true);
    }
    lib.addTables(tableLinks);
    tableLibraryService.save(lib);
    return true;
  }

  @RequestMapping(value = "/{libId}/addChildrenByIds", method = RequestMethod.POST)
  public List<Table> addChildrenByIds(@PathVariable String libId, @RequestBody Set<String> ids)
      throws DatatypeSaveException {
    Set<TableLink> links = new HashSet<TableLink>();
    log.debug("Adding a link to the library");
    TableLibrary lib = tableLibraryService.findById(libId);
    List<Table> tables = tableService.findAllByIds(ids);
    for (Table t : tables) {
      TableLink tbl = new TableLink();
      tbl.setBindingIdentifier(t.getBindingIdentifier());
      tbl.setId(t.getId());
      links.add(tbl);
      lib.getCodePresence().put(t.getId(), true);

    }


    lib.addTables(links);
    tableLibraryService.save(lib);
    return tables;
  }



}
