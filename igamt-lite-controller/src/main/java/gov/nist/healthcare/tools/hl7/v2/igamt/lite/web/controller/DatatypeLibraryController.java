/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import gov.nist.healthcare.nht.acmgt.dto.domain.Account;
import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.exception.LibraryException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.exception.LibraryNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.LibrarySaveResponse;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller.wrappers.BindingWrapper;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller.wrappers.LibraryCreateWrapper;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller.wrappers.ScopesAndVersionWrapper;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DatatypeSaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.LibrarySaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.NotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.UserAccountNotFoundException;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Harold Affo (harold.affo@nist.gov) Mar 17, 2015
 */

@RestController
@RequestMapping("/datatype-library")
public class DatatypeLibraryController extends CommonController {

	Logger log = LoggerFactory.getLogger(DatatypeLibraryController.class);

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
		List<DatatypeLibrary> datatypeLibraries = datatypeLibraryService
				.findAll();
		return datatypeLibraries;
	}

	@RequestMapping(value = "/{dtLibId}/datatypes", method = RequestMethod.GET, produces = "application/json")
	public List<Datatype> getDatatypesByLibrary(
			@PathVariable("dtLibId") String dtLibId) {
		log.info("Fetching datatypeByLibrary..." + dtLibId);
		List<Datatype> result = datatypeService.findByLibIds(dtLibId);
		return result;
	}

	@RequestMapping(value = "/findByScopes", method = RequestMethod.POST, produces = "application/json")
	public List<DatatypeLibrary> findByScopes(@RequestBody List<String> scopes) {
		log.info("Fetching datatype libraries...");
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
		List<DatatypeLibrary> datatypeLibraries = datatypeLibraryService
				.findByScopes(scopes1);
		return datatypeLibraries;
	}

	@RequestMapping(value = "/findByScopesAndVersion", method = RequestMethod.POST, produces = "application/json")
	public List<DatatypeLibrary> findByScopesAndVersion(
			@RequestBody ScopesAndVersionWrapper scopesAndVersion) {
		log.info("Fetching the datatype library. scope="
				+ scopesAndVersion.getScopes() + " hl7Version="
				+ scopesAndVersion.getHl7Version());
		List<DatatypeLibrary> datatypes = null;
		try {
			datatypes = datatypeLibraryService.findByScopesAndVersion(
					scopesAndVersion.getScopes(),
					scopesAndVersion.getHl7Version());
			if (datatypes == null) {
				throw new NotFoundException(
						"Datatype not found for scopesAndVersion="
								+ scopesAndVersion);
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return datatypes;
	}

	@RequestMapping(value = "/findHl7Versions", method = RequestMethod.GET, produces = "application/json")
	public List<String> findHl7Versions() {
		log.info("Fetching all HL7 versions.");
		List<String> result = datatypeLibraryService.findHl7Versions();
		return result;
	}

	@RequestMapping(value = "/{accountId}/{hl7Version}/findByAccountId", method = RequestMethod.GET)
	public List<DatatypeLibrary> findByAccountId(
			@PathVariable("accountId") Long accountId,
			@PathVariable("hl7Version") String hl7Version)
			throws LibraryNotFoundException, UserAccountNotFoundException,
			LibraryException {
		log.info("Fetching the datatype libraries...");
		List<DatatypeLibrary> result = datatypeLibraryService.findByAccountId(
				accountId, hl7Version);
		return result;
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public DatatypeLibrary create(@RequestBody LibraryCreateWrapper dtlcw) {
		SCOPE scope = SCOPE.valueOf(dtlcw.getScope());

		return datatypeLibraryService.create(dtlcw.getName(), dtlcw.getExt(),
				scope, dtlcw.getHl7Version(), dtlcw.getAccountId());
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public LibrarySaveResponse save(@RequestBody DatatypeLibrary datatypeLibrary)
			throws LibrarySaveException {
		log.debug("datatypeLibrary=" + datatypeLibrary);
		log.debug("datatypeLibrary.getId()=" + datatypeLibrary.getId());
		log.info("Saving the " + datatypeLibrary.getScope()
				+ " datatype library.");
		// TODO This is necessary for a cascading save. For now we are
		// having the user save dts one at a time.
		// for (Datatype dt : datatypeLibrary.getChildren()) {
		// dt.setAccountId(account.getId());
		// datatypeService.save(dt);
		// }
		DatatypeLibrary saved = datatypeLibraryService.save(datatypeLibrary);
		log.debug("saved.getId()=" + saved.getId());
		log.debug("saved.getScope()=" + saved.getScope());
		return new LibrarySaveResponse(saved.getDate(), saved.getScope().name());
	}

	@RequestMapping(value = "/bindDatatypes", method = RequestMethod.POST)
	public List<Datatype> bindDatatypes(@RequestBody BindingWrapper binding)
			throws DatatypeSaveException {
		log.debug("Binding datatypes=" + binding.getDatatypeIds().size());
		List<Datatype> bound = datatypeLibraryService.bindDatatypes(
				binding.getDatatypeIds(), binding.getDatatypeLibraryId(),
				binding.getDatatypeLibraryExt(), binding.getAccountId());
		return bound;
	}

	@RequestMapping(value = "/{libId}/addChild", method = RequestMethod.POST)
	public DatatypeLink addChild(@PathVariable String libId,
			@RequestBody DatatypeLink datatypeLink)
			throws DatatypeSaveException {
		log.debug("Adding a link to the library");
		DatatypeLibrary lib = datatypeLibraryService.findById(libId);
		lib.addDatatype(datatypeLink);
		datatypeLibraryService.save(lib);
		return datatypeLink;
	}

	@RequestMapping(value = "/{libId}/updateChild", method = RequestMethod.POST)
	public DatatypeLink updateChild(@PathVariable String libId,
			@RequestBody DatatypeLink datatypeLink)
			throws DatatypeSaveException {
		log.debug("Adding a link to the library");
		DatatypeLibrary lib = datatypeLibraryService.findById(libId);
		DatatypeLink found = lib.findOne(datatypeLink.getId());
		if (found != null) {
			found.setExt(datatypeLink.getExt());
		}
		datatypeLibraryService.save(lib);
		return datatypeLink;
	}

	@RequestMapping(value = "/{libId}/deleteChild", method = RequestMethod.POST)
	public boolean deleteChild(@PathVariable String libId,
			@RequestParam("id") String id) throws DatatypeSaveException {
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
			@RequestParam("hl7Version") String hl7Version,
			@RequestParam("scope") SCOPE scope) {
		log.info("Finding flavors of datatype, name=" + name + ", hl7Version="
				+ hl7Version + ", scope=" + scope + "...");
		org.springframework.security.core.userdetails.User u = userService
				.getCurrentUser();
		Account account = accountRepository.findByTheAccountsUsername(u
				.getUsername());
		List<DatatypeLink> datatypes = datatypeLibraryService.findFlavors(
				scope, hl7Version, name, account.getId());
		return datatypes;
	}

}
