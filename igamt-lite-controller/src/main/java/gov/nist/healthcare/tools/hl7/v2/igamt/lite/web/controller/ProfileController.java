package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.View;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.ProfileNotFoundException;

 import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/profiles")
public class ProfileController {

	Logger logger = LoggerFactory.getLogger(ProfileController.class);

	@Autowired
	private ProfileService profileService;

	public ProfileService getProfileService() {
		return profileService;
	}

	public void setProfileService(ProfileService profileService) {
		this.profileService = profileService;
	}

	@ExceptionHandler(ProfileNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String profileNotFound(ProfileNotFoundException ex) {
		logger.debug(ex.getMessage());
		return "Cannot find the profile." + ex.getMessage();
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String exception(Exception ex) {
		logger.debug(ex.getMessage());
		ex.printStackTrace();
		return "ERROR:" + ex.getMessage();
	}
	

	/**
	 * Return the list of pre-loaded profiles in a Summary View mode
	 * 
	 * @return
	 */
	@JsonView(View.Summary.class)
	@RequestMapping(value = "/preloaded", method = RequestMethod.GET)
	public Iterable<Profile> profiles() {
		logger.info("Fetching all testPlans...");
		return profileService.findAllPreloaded();
	}
	
	/**
	 * Return the list of pre-loaded profiles in a Summary View mode
	 * 
	 * @return
	 */
 	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Profile profile(@PathVariable("id") Long id) {
		logger.info("Fetching all testPlans...");
		Profile p = profileService.findOne(id);
		return p;
	}
	

}
