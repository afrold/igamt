package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Changes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.FileCopyUtils;
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

	@ExceptionHandler(ProfileException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String profileNotFound(ProfileException ex) {
		logger.debug(ex.getMessage());
		return "ERROR:" + ex.getMessage();
	}

	/**
	 * Return the list of pre-loaded profiles
	 * 
	 * @return
	 */
	@RequestMapping(value = "/preloaded", method = RequestMethod.GET, produces = "application/json")
	public List<Profile> preloaded() {
		logger.info("Fetching all preloaded profiles...");
		List<Profile> result = profileService.findAllPreloaded();
		return result;
	}

	// TODO: temporary call before integration of registration
	@RequestMapping(value = "/custom", method = RequestMethod.GET)
	public List<Profile> customs() {
		logger.info("Fetching all custom profiles...");
		List<Profile> result = profileService.findAllCustom();
		return result;
	}

	/**
	 * Return a profile by its id
	 * 
	 * @return
	 * @throws ProfileNotFoundException
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Profile profile(@PathVariable("id") String id)
			throws ProfileNotFoundException {
		logger.info("GET pofile with id=" + id);
		Profile p = profileService.findOne(id);
		if (p == null) {
			throw new ProfileNotFoundException(id);
		}
		return p;
	}

	@RequestMapping(value = "/{targetId}/clone", method = RequestMethod.POST)
	public Profile clone(@PathVariable("targetId") String targetId)
			throws ProfileNotFoundException, ProfileException {
		logger.info("Clone profile with id=" + targetId);
		Profile p = profileService.findOne(targetId);
		if (p == null) {
			throw new ProfileNotFoundException(targetId);
		}
		Profile profile = profileService.clone(p);
		profile.setPreloaded(false);
		profileService.save(profile);

		return profile;
	}

	@RequestMapping(value = "/{targetId}/delete", method = RequestMethod.POST)
	public void delete(@PathVariable("targetId") String targetId)
			throws ProfileNotFoundException {
		logger.info("Delete profile with id=" + targetId);
		profileService.delete(targetId);
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public List<String> save(@RequestBody Changes jsonChanges) {
		logger.info("Applying changes = " + jsonChanges);
		return profileService.apply(jsonChanges.getValue());
	}

	@RequestMapping(value = "/{targetId}/export/XML", method = RequestMethod.POST, produces = "text/xml")
	public void export(@PathVariable("targetId") String targetId,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		// logger.info(log.toString());
		InputStream content = null;
		content = profileService.exportAsXml(targetId);
		response.setContentType("text/xml");
		response.setHeader("Content-disposition",
				"attachment;filename=Profile.xml");
		FileCopyUtils.copy(content, response.getOutputStream());

		// if ("pdf".equalsIgnoreCase(format)) {
		// content = profileService.exportA sPdf(targetId);
		// response.setContentType("application/pdf");
		// response.setHeader("Content-disposition",
		// "attachment;filename=IG.pdf");
		// FileCopyUtils.copy(content, response.getOutputStream());
		// } else if ("xml".equalsIgnoreCase(format)) {
		//
		// }
	}

}
