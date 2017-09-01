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
import gov.nist.healthcare.nht.acmgt.dto.ResponseMessage.Type;
import gov.nist.healthcare.nht.acmgt.dto.domain.Account;
import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ForbiddenOperationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.exception.LibraryException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.DateUtils;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.LibrarySaveResponse;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DatatypeSaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.LibraryNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.LibrarySaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.NotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.SegmentSaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.UserAccountNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers.LibraryCreateWrapper;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers.ScopesAndVersionWrapper;

/**
 * @author Harold Affo (harold.affo@nist.gov) Mar 17, 2015
 */

@RestController
@RequestMapping("/segment-library")
public class SegmentLibraryController extends CommonController {

  Logger log = LoggerFactory.getLogger(SegmentLibraryController.class);

  @Autowired
  private SegmentLibraryService segmentLibraryService;

  @Autowired
  private SegmentService segmentService;

  @Autowired
  UserService userService;

  @Autowired
  AccountRepository accountRepository;

  @RequestMapping(method = RequestMethod.GET)
  public List<SegmentLibrary> getSegmentLibraries() {
    log.info("Fetching all segment libraries.");
    List<SegmentLibrary> segmentLibraries = segmentLibraryService.findAll();
    return segmentLibraries;
  }

  @RequestMapping(value = "/{segLibId}/segments", method = RequestMethod.GET,
      produces = "application/json")
  public List<Segment> getSegmentByLibrary(@PathVariable("segLibId") String segLibId) {
    log.info("Fetching segmentByLibrary..." + segLibId);
    List<Segment> result = segmentLibraryService.findSegmentsById(segLibId);
    return result;
  }

  @RequestMapping(value = "/findByScopes", method = RequestMethod.POST,
      produces = "application/json")
  public List<SegmentLibrary> findByScopes(@RequestBody List<String> scopes) {
    log.info("Fetching segment libraries...");
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
    List<SegmentLibrary> segmentLibraries = segmentLibraryService.findByScopes(scopes1);
    return segmentLibraries;
  }

  @RequestMapping(value = "/findByScopesAndVersion", method = RequestMethod.POST,
      produces = "application/json")
  public List<Segment> findByScopesAndVersion(
      @RequestBody ScopesAndVersionWrapper scopesAndVersion) {
    log.info("Fetching the segment library. scope=" + scopesAndVersion.getScopes() + " hl7Version="
        + scopesAndVersion.getHl7Version());
    List<Segment> segments = null;
    try {
      segments = segmentService.findByScopesAndVersion(scopesAndVersion.getScopes(),
          scopesAndVersion.getHl7Version());
      if (segments == null) {
        throw new NotFoundException("Segment not found for scopesAndVersion=" + scopesAndVersion);
      }
    } catch (Exception e) {
      log.error("", e);
    }
    return segments;
  }

  @RequestMapping(value = "/findHl7Versions", method = RequestMethod.GET,
      produces = "application/json")
  public List<String> findHl7Versions() {
    log.info("Fetching all HL7 versions.");
    List<String> result = segmentLibraryService.findHl7Versions();
    return result;
  }

  @RequestMapping(value = "/{accountId}/{hl7Version}/findByAccountId", method = RequestMethod.GET)
  public List<SegmentLibrary> findByAccountId(@PathVariable("accountId") Long accountId,
      @PathVariable("hl7Version") String hl7Version)
      throws LibraryNotFoundException, UserAccountNotFoundException, LibraryException {
    log.info("Fetching the segment libraries...");
    List<SegmentLibrary> result = segmentLibraryService.findByAccountId(accountId, hl7Version);
    return result;
  }

  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public SegmentLibrary create(@RequestBody LibraryCreateWrapper dtlcw) {
    SCOPE scope = SCOPE.valueOf(dtlcw.getScope());

    return segmentLibraryService.create(dtlcw.getName(), dtlcw.getExt(), scope,
        dtlcw.getHl7Version(), dtlcw.getAccountId());
  }

  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public LibrarySaveResponse save(@RequestBody SegmentLibrary segmentLibrary)
      throws LibrarySaveException {
    log.debug("segmentLibrary=" + segmentLibrary);
    log.debug("segmentLibrary.getId()=" + segmentLibrary.getId());
    log.info("Saving the " + segmentLibrary.getScope() + " segment library.");
    User u = userService.getCurrentUser();
    Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
    segmentLibrary.setAccountId(account.getId());
    // TODO This is necessary for a cascading save. For now we are
    // having the user save dts one at a time.
    // for (Segment dt : segmentLibrary.getChildren()) {
    // dt.setAccountId(account.getId());
    // segmentService.save(dt);
    // }
    SegmentLibrary saved = segmentLibraryService.save(segmentLibrary);
    log.debug("saved.getId()=" + saved.getId());
    log.debug("saved.getScope()=" + saved.getScope());
    return new LibrarySaveResponse(saved.getDateUpdated().getTime() + "", saved.getScope().name());
  }

  @RequestMapping(value = "/{segLibId}/addSegment", method = RequestMethod.POST)
  public ResponseMessage addSegment(@PathVariable("segLibId") String segLibId,
      @RequestParam("segId") String segId)
      throws SegmentSaveException, ForbiddenOperationException {
    log.info("Adding segment " + segId + " segment.");
    Segment segment = segmentService.findById(segId);
    if (segment != null) {
      Constant.SCOPE scope = segment.getScope();
      STATUS status = segment.getStatus();
      if (Constant.SCOPE.HL7STANDARD.equals(scope) || STATUS.PUBLISHED.equals(status))
        throw new ForbiddenOperationException();
       segmentService.save(segment);
      SegmentLibrary library = segmentLibraryService.findById(segLibId);
      SegmentLink link = library.findOneSegmentById(segId);
      if (link != null) {
        library.getChildren().remove(link);
        segmentLibraryService.save(library);
      }
      return new ResponseMessage(Type.success, "segmentAdded");
    }
    throw new IllegalArgumentException("segmentNotFound");
  }

  @RequestMapping(value = "/{libId}/addChild", method = RequestMethod.POST)
  public SegmentLink addChild(@PathVariable String libId, @RequestBody SegmentLink child)
      throws SegmentSaveException {
    log.debug("Adding a link to the library");
    SegmentLibrary lib = segmentLibraryService.findById(libId);
    lib.addSegment(child);
    segmentLibraryService.save(lib);
    return child;
  }

  @RequestMapping(value = "/{libId}/updateChild", method = RequestMethod.POST)
  public SegmentLink updateChild(@PathVariable String libId, @RequestBody SegmentLink child)
      throws DatatypeSaveException {
    log.debug("Adding a link to the library");
    SegmentLibrary lib = segmentLibraryService.findById(libId);
    SegmentLink found = lib.findOneSegmentById(child.getId());
    if (found != null) {
      found.setExt(child.getExt());
    }
    segmentLibraryService.save(lib);
    return child;
  }

  @RequestMapping(value = "/findFlavors", method = RequestMethod.GET, produces = "application/json")
  public List<SegmentLink> findFlavors(@RequestParam("name") String name,
      @RequestParam("hl7Version") String hl7Version, @RequestParam("scope") SCOPE scope) {
    log.info("Finding flavors of datatype, name=" + name + ", hl7Version=" + hl7Version + ", scope="
        + scope + "...");
    org.springframework.security.core.userdetails.User u = userService.getCurrentUser();
    Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
    List<SegmentLink> links =
        segmentLibraryService.findFlavors(scope, hl7Version, name, account.getId());
    return links;
  }

  @RequestMapping(value = "/findLibrariesByFlavorName", method = RequestMethod.GET,
      produces = "application/json")
  public List<SegmentLibrary> findLibrariesByFlavorName(@RequestParam("name") String name,
      @RequestParam("hl7Version") String hl7Version, @RequestParam("scope") SCOPE scope) {
    log.info("Finding flavors of datatype, name=" + name + ", hl7Version=" + hl7Version + ", scope="
        + scope + "...");
    org.springframework.security.core.userdetails.User u = userService.getCurrentUser();
    Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
    List<SegmentLibrary> libraries =
        segmentLibraryService.findLibrariesByFlavorName(scope, hl7Version, name, account.getId());
    return libraries;
  }

  @RequestMapping(value = "/{libId}/deleteChild/{id}", method = RequestMethod.POST)
  public boolean deleteChild(@PathVariable String libId, @PathVariable String id)
      throws SegmentSaveException {
    log.debug("Deleting a link to the library");
    SegmentLibrary lib = segmentLibraryService.findById(libId);
    SegmentLink found = lib.findOne(id);
    if (found != null) {
      lib.getChildren().remove(found);
      segmentLibraryService.save(lib);
    }
    return true;
  }

  @RequestMapping(value = "/{libId}/addChildren", method = RequestMethod.POST)
  public boolean addChild(@PathVariable String libId, @RequestBody Set<SegmentLink> segmentLinks)
      throws SegmentSaveException {
    log.debug("Adding a link to the library");
    SegmentLibrary lib = segmentLibraryService.findById(libId);
    lib.addSegments(segmentLinks);
    segmentLibraryService.save(lib);
    return true;
  }

}
