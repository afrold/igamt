package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileComponentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller.wrappers.IntegrationIGDocumentRequestWrapper;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DataNotFoundException;


@RestController
@RequestMapping("/profile-components")
public class ProfileComponentController extends CommonController {
	Logger log = LoggerFactory.getLogger(ProfileComponentController.class);
	
	@Autowired
	  private ProfileComponentService profileComponentService;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
	  public ProfileComponent getProfileComponentById(@PathVariable("id") String id) throws DataNotFoundException {
	    log.info("Fetching ProfileComponentById..." + id);
	    return profileComponentService.findById(id);
	  }
	@RequestMapping(value = "/save", method = RequestMethod.POST,
		      consumes = "application/json", produces = "application/json")
		  public ProfileComponent createPC(@RequestBody ProfileComponent pc){
		    log.info("Creation of Profile Component.");
		    log.debug("pc.getName()=" + pc.getName());
		    log.debug("pc.getChildren()=" + pc.getChildren());
		    
		    



		    
		    return pc;
		  }
}
