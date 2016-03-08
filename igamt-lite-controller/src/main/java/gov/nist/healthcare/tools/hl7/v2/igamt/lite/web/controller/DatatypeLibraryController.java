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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DataTypeLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.DatatypeLibrarySaveResponse;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller.wrappers.DatatypeLibraryRequestWrapper;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DatatypeLibrarySaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.UserAccountNotFoundException;

/**
 * @author Harold Affo (harold.affo@nist.gov) Mar 17, 2015
 */

@RestController
@RequestMapping("/datatype-library")
public class DatatypeLibraryController extends CommonController {

	Logger log = LoggerFactory.getLogger(DatatypeLibraryController.class);

	@Autowired
	private DataTypeLibraryService datatypeLibraryService;

	@RequestMapping(value = "/{accountId}", method = RequestMethod.POST)
	public List<DatatypeLibrary> datatypeLibrary() {
		log.info("Fetching the HL7STANDARD datatype library...");
		List<DatatypeLibrary> result = datatypeLibraryService.findAll();
		return result;
	}

	@RequestMapping(value = "/getDTLibByScope", method = RequestMethod.POST)
	public DatatypeLibrary getDataTypeLibraryByScope(@RequestBody DatatypeLibraryRequestWrapper dtlrw) {
		log.info("Fetching the " + dtlrw.getScope() + " datatype library...");
		DatatypeLibrary.SCOPE scope = DatatypeLibrary.SCOPE.valueOf(dtlrw.getScope());
		DatatypeLibrary result = datatypeLibraryService.findByScope(scope, dtlrw.getAccountId(), dtlrw.getDtLib());
		return result;
	}

	@RequestMapping(value = "/{accountId}", method = RequestMethod.POST)
	public List<DatatypeLibrary> datatypeLibraryByAccountId(@PathVariable("accountId") Long accountId)
			throws DatatypeLibraryNotFoundException, UserAccountNotFoundException, DatatypeLibraryException {
		log.info("Fetching the USER datatype library...");
		List<DatatypeLibrary> result = datatypeLibraryService.findByAccountId(accountId);
		return result;
	}

	@RequestMapping(value = "/save/{accountId}", method = RequestMethod.POST)
	public DatatypeLibrarySaveResponse save(@RequestBody DatatypeLibrary library) throws DatatypeLibrarySaveException {
		log.info("Saving the USER datatype library...");
		DatatypeLibrary saved = datatypeLibraryService.apply(library);
		return new DatatypeLibrarySaveResponse(saved.getDate(), saved.getScope().name());
	}
}
