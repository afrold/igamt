package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileSummary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.ProfileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/profiles")
public class ProfileController extends CommonController {

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
		return "ERROR:" + ex.getMessage();
	}

	/**
	 * Return the list of pre-loaded profiles
	 * 
	 * @return
	 */
	@RequestMapping(value = "/preloaded", method = RequestMethod.GET)
	public Iterable<ProfileSummary> profileSummaries() {
		logger.info("Fetching all preloaded profiles...");
		return profileService.findAllPreloadedSummaries();
	}

	/**
	 * Return a profile by its id
	 * 
	 * @return
	 * @throws ProfileNotFoundException
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Profile profile(@PathVariable("id") Long id)
			throws ProfileNotFoundException {
		logger.info("GET pofile with id=" + id);
		Profile p = profileService.findOne(id);
		if (p == null) {
			throw new ProfileNotFoundException(id);
		}
		return p;
	}

	@RequestMapping(value = "/{targetId}", method = RequestMethod.POST)
	public Profile clone(@PathVariable("targetId") Long targetId)
			throws ProfileNotFoundException {
		logger.info("Clone profile with id=" + targetId);
		Profile p = profileService.findOne(targetId);
		if (p == null) {
			throw new ProfileNotFoundException(targetId);
		}
		Profile profile = profileService.clone(p);
		profileService.save(profile);
		return profile;
	}

	@RequestMapping(value = "/apply", method = RequestMethod.POST)
	public String[] save(@RequestBody String jsonChanges) {
		logger.info("Applying changes = " + jsonChanges);
		String[] failures = profileService.apply(jsonChanges);
		return failures;
	}

}
