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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Harold Affo (harold.affo@nist.gov) Mar 17, 2015
 */

@RestController
@RequestMapping("/datatypes")
public class DatatypeController extends CommonController {

	Logger logger = LoggerFactory.getLogger(ProfileController.class);

	// @Autowired
	// private DatatypeService datatypeService;
	//
	// @RequestMapping(value = "/{targetId}/clone", method = RequestMethod.POST)
	// public Datatype clone(@PathVariable("targetId") String targetId,
	// @PathVariable("profileId") String profileId)
	// throws DatatypeNotFoundException, CloneNotSupportedException {
	// logger.info("Clone datatype with id=" + targetId);
	// Datatype d = datatypeService.findOne(targetId);
	// if (d == null) {
	// throw new DatatypeNotFoundException(targetId);
	// }
	// Datatype clone = datatypeService.clone(d); // FIXME: clone datatype
	// datatypeService.save(clone);
	// return clone;
	// }

}
