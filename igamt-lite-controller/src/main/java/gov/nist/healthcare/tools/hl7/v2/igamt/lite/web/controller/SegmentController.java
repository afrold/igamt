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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ForbiddenOperationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.DateUtils;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.SegmentSaveResponse;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.SegmentSaveException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Harold Affo (harold.affo@nist.gov) Mar 17, 2015
 */

@RestController
@RequestMapping("/segments")
public class SegmentController extends CommonController {

	Logger log = LoggerFactory.getLogger(SegmentController.class);

	@Autowired
	private SegmentService segmentService;

	@Autowired
	UserService userService;

	@Autowired
	AccountRepository accountRepository;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
	public Segment getSegmentById(@PathVariable("id") String id) {
		log.info("Fetching segmentById..." + id);
		Segment result = segmentService.findById(id);
		return result;
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public SegmentSaveResponse save(@RequestBody Segment segment)
			throws SegmentSaveException, ForbiddenOperationException {
		log.debug("segment=" + segment);
		log.debug("segment.getId()=" + segment.getId());
		log.info("Saving the " + segment.getScope() + " segment.");
		User u = userService.getCurrentUser();
		Account account = accountRepository.findByTheAccountsUsername(u
				.getUsername());
		if (segment.getAccountId() == null)
			segment.setAccountId(account.getId());
		if (account.getId().equals(segment.getAccountId())
				|| segment.getParticipants().contains(account.getId())) {
			if (segment.getScope() == null)
				segment.setScope(SCOPE.USER);
			segment.setDate(DateUtils.getCurrentTime());
			Segment saved = segmentService.save(segment);
			log.debug("saved.getId()=" + saved.getId());
			log.debug("saved.getScope()=" + saved.getScope());
			return new SegmentSaveResponse(saved.getDate(), saved.getVersion());
		} else {
			throw new ForbiddenOperationException();
		}
	}
}
