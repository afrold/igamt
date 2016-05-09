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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.DatatypeSaveResponse;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DatatypeSaveException;

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

import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.DatatypeSaveResponse;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DatatypeSaveException;

/**
 * @author Harold Affo (harold.affo@nist.gov) Mar 17, 2015
 */

@RestController
@RequestMapping("/datatypes")
public class DatatypeController extends CommonController {

	Logger log = LoggerFactory.getLogger(DatatypeController.class);

	@Autowired
	private DatatypeService datatypeService;

	@Autowired
	UserService userService;

	@Autowired
	AccountRepository accountRepository;

	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public List<Datatype> datatypes() {
		log.info("Fetching all Datatypes...");
		List<Datatype> result = datatypeService.findAll();
		return result;
	}

	@RequestMapping(value = "/findByIds", method = RequestMethod.POST, produces = "application/json")
	public List<Datatype> findByIds(@RequestBody List<String> ids) {
		log.info("Fetching datatypeByIds..." + ids);
		List<Datatype> result = datatypeService.findByIds(ids);
		return result;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
	public Datatype getDatatypeById(@PathVariable("id") String id) {
		log.info("Fetching datatypeById..." + id);
		Datatype result = datatypeService.findById(id);
		return result;
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public DatatypeSaveResponse save(@RequestBody Datatype datatype)
			throws DatatypeSaveException {
		log.debug("datatypeLibrary=" + datatype);
		log.debug("datatypeLibrary.getId()=" + datatype.getId());
		log.info("Saving the " + datatype.getScope() + " datatype library.");
		// User u = userService.getCurrentUser();
		// Account account =
		// accountRepository.findByTheAccountsUsername(u.getUsername());
		// datatype.setAccountId(account.getId());
		Datatype saved = datatypeService.save(datatype);
		log.debug("saved.getId()=" + saved.getId());
		log.debug("saved.getScope()=" + saved.getScope());
		return new DatatypeSaveResponse(saved.getName(), saved.getScope()
				.name());
	}

	@RequestMapping(value = "/findFlavors", method = RequestMethod.GET, produces = "application/json")
	public List<Datatype> findFlavors(@RequestParam("name") String name,
			@RequestParam("hl7Version") String hl7Version,
			@RequestParam("scope") String scope) {
		log.info("Finding flavors of datatype, name=" + name + ", hl7Version="
				+ hl7Version + ", scope=" + scope + "...");
		List<Datatype> datatypes = 
		
		Datatype result = datatypeService.findById(id);
		return result;
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public void delete(@PathVariable("id") String id) throws DatatypeSaveException {
		log.info("Deleting " + id);
		datatypeService.delete(id);
	}
}
