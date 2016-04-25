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

import java.util.ArrayList;
import java.util.List;

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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.DatatypeLibrarySaveResponse;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller.wrappers.DatatypeLibraryCreateWrapper;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DatatypeLibrarySaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DatatypeNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.UserAccountNotFoundException;

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
		List<DatatypeLibrary> datatypeLibraries = datatypeLibraryService.findAll();
		return datatypeLibraries;
	}

	@RequestMapping(value = "/{dtLibId}/datatypes", method = RequestMethod.GET, produces = "application/json")
	public List<Datatype> getDatatypesByLibrary(@PathVariable("dtLibId") String dtLibId) {
		log.info("Fetching datatypeByLibrary..." + dtLibId);
		List<Datatype> result = datatypeService.findByLibIds(dtLibId);
		return result;
	}

	@RequestMapping(value = "/findByScopes", method = RequestMethod.POST, produces = "application/json")
	public List<DatatypeLibrary> findByScopes(@RequestBody List<String> scopes) {
		log.info("Fetching datatype libraries.");
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
		List<DatatypeLibrary> datatypeLibraries = datatypeLibraryService.findByScopes(scopes1);
		return datatypeLibraries;
	}

	@RequestMapping(value = "/findByScopeAndVersion", method = RequestMethod.POST, produces = "application/json")
	public List<Datatype> findByScopeAndVersion(@RequestBody List<String> scopeAndVersion) {
		String scope = scopeAndVersion.get(0);
		String hl7Version = scopeAndVersion.get(1);
		log.info("Fetching the datatype library. scope=" + scope + " hl7Version=" + hl7Version);
		Constant.SCOPE scope1 = Constant.SCOPE.valueOf(scope);
		List<Datatype> datatypes = null;
		try {
			datatypes = datatypeService.findByScopeAndVersion(scope1, hl7Version);
			if (datatypes == null) {
				throw new DatatypeNotFoundException(
						"Datatype not found for scope=" + scope + " hl7Version=" + hl7Version);
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

	@RequestMapping(value = "/findLibraryByScopeAndVersion", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public DatatypeLibrary findLibraryByScopeAndVersion(@RequestBody List<String> scopeAndVersion) {
		String scope = scopeAndVersion.get(0);
		String hl7Version = (scopeAndVersion.size() > 1 ? scopeAndVersion.get(1) : null);
		log.info("Fetching the datatype library. scope=" + scopeAndVersion.get(0) + " hl7Version=" + hl7Version);
		Constant.SCOPE scope1 = Constant.SCOPE.valueOf(scope);
		DatatypeLibrary datatypeLibrary = null;
		try {
			datatypeLibrary = datatypeLibraryService.findByScopeAndVersion(scope1, hl7Version);
			if (datatypeLibrary == null) {
				throw new DatatypeLibraryNotFoundException("scope=" + scope + " hl7Version=" + hl7Version);
			}
		} catch (DatatypeLibraryNotFoundException e) {
			log.error("", e);
		}
		log.debug("datatypeLibrary.getId()=" + datatypeLibrary.getId());
		return datatypeLibrary;
	}

	@RequestMapping(value = "/{accountId}/{hl7Version}/findByAccountId", method = RequestMethod.GET)
	public List<DatatypeLibrary> findByAccountId(@PathVariable("accountId") Long accountId,
			@PathVariable("hl7Version") String hl7Version)
			throws DatatypeLibraryNotFoundException, UserAccountNotFoundException, DatatypeLibraryException {
		log.info("Fetching the datatype libraries...");
		List<DatatypeLibrary> result = datatypeLibraryService.findByAccountId(accountId, hl7Version);
		return result;
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public DatatypeLibrary create(@RequestBody DatatypeLibraryCreateWrapper dtlcw) {
		SCOPE scope = SCOPE.valueOf(dtlcw.getScope());

		return datatypeLibraryService.create(scope, dtlcw.getHl7Version(), dtlcw.getAccountId());
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public DatatypeLibrarySaveResponse save(@RequestBody DatatypeLibrary datatypeLibrary)
			throws DatatypeLibrarySaveException {
		log.debug("datatypeLibrary=" + datatypeLibrary);
		log.debug("datatypeLibrary.getId()=" + datatypeLibrary.getId());
		log.info("Saving the " + datatypeLibrary.getScope() + " datatype library.");
		User u = userService.getCurrentUser();
		Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
		datatypeLibrary.setAccountId(account.getId());
		// TODO This is necessary for a cascading save. For now we are
		// having the user save dts one at a time.
		// for (Datatype dt : datatypeLibrary.getChildren()) {
		// dt.setAccountId(account.getId());
		// datatypeService.save(dt);
		// }
		DatatypeLibrary saved = datatypeLibraryService.save(datatypeLibrary);
		log.debug("saved.getId()=" + saved.getId());
		log.debug("saved.getScope()=" + saved.getScope());
		return new DatatypeLibrarySaveResponse(saved.getDate(), saved.getScope().name());
	}
}
