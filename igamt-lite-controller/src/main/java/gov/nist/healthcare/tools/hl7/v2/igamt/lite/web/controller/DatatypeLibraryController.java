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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.nht.acmgt.dto.domain.Account;
import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DataTypeLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.DatatypeLibrarySaveResponse;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DatatypeLibrarySaveException;

/**
 * @author Harold Affo (harold.affo@nist.gov) Mar 17, 2015
 */

@RestController
@RequestMapping("/datatype-library")
public class DatatypeLibraryController extends CommonController {

	Logger log = LoggerFactory.getLogger(DatatypeLibraryController.class);

	@Autowired
	private DataTypeLibraryService datatypeLibraryService;

	@Autowired
	UserService userService;

	@Autowired
	AccountRepository accountRepository;

	@RequestMapping(method = RequestMethod.POST)
	public List<DatatypeLibrary> getDatatypeLibraries() {
		log.info("Fetching all datatype libraries.");
		List<DatatypeLibrary> datatypeLibraries = datatypeLibraryService.findAll();
		return datatypeLibraries;
	}

	@RequestMapping(value = "/getDataTypeLibraryByScope", method = RequestMethod.POST)
	public DatatypeLibrary getDataTypeLibraryByScope(@RequestBody String sScope) {
		log.info("Fetching the " + sScope + " datatype library.");
		DatatypeLibrary.SCOPE scope = DatatypeLibrary.SCOPE.valueOf(sScope);
		DatatypeLibrary datatypeLibrary = datatypeLibraryService.findByScope(scope);
		return datatypeLibrary;
	}

	@RequestMapping(value = "/createUpdate", method = RequestMethod.POST)
	public DatatypeLibrary createUpdate(@RequestBody DatatypeLibrary datatypeLibrary) {
		log.info("Creating of updating the " + datatypeLibrary.getScope() + " datatype library.");
		User u = userService.getCurrentUser();
		Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
		DatatypeLibrary createdUpdated = datatypeLibraryService.createFrom(account.getId(), datatypeLibrary);
		return createdUpdated;
	}

	// gcr not used at present but will be in the future.
	// @RequestMapping(value = "/{accountId}", method = RequestMethod.POST)
	// public List<DatatypeLibrary>
	// getDatatypeLibraryByAccountId(@PathVariable("accountId") Long accountId)
	// throws DatatypeLibraryNotFoundException, UserAccountNotFoundException,
	// DatatypeLibraryException {
	// log.info("Fetching the USER datatype library...");
	// List<DatatypeLibrary> result =
	// datatypeLibraryService.findByAccountId(accountId);
	// return result;
	// }

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public DatatypeLibrarySaveResponse save(@RequestBody DatatypeLibrary datatypeLibrary)
			throws DatatypeLibrarySaveException {
		log.info("Saving the " + datatypeLibrary.getScope() + " datatype library.");
		User u = userService.getCurrentUser();
		Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
		datatypeLibrary.setAccountId(account.getId());
		DatatypeLibrary saved = datatypeLibraryService.apply(datatypeLibrary);
		return new DatatypeLibrarySaveResponse(saved.getDate(), saved.getScope().name());
	}
}
