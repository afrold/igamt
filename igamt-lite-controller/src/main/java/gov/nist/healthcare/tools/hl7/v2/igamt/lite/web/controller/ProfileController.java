package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import gov.nist.healthcare.nht.acmgt.dto.ResponseMessage;
import gov.nist.healthcare.nht.acmgt.dto.domain.Account;
import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.config.ChangeCommand;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.OperationNotAllowException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.UserAccountNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
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

	@Autowired
	UserService userService;

	@Autowired
	AccountRepository accountRepository;

	public ProfileService getProfileService() {
		return profileService;
	}

	public void setProfileService(ProfileService profileService) {
		this.profileService = profileService;
	}

	@ExceptionHandler(UserAccountNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseMessage profileNotFound(UserAccountNotFoundException ex) {
		logger.debug(ex.getMessage());
		return new ResponseMessage(ResponseMessage.Type.error,
				"accountNotFound", null);
	}

	@ExceptionHandler(ProfileNotFoundException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseMessage profileNotFound(ProfileException ex) {
		logger.debug(ex.getMessage());
		return new ResponseMessage(ResponseMessage.Type.error,
				"profileNotFound", null);
	}

	@ExceptionHandler(OperationNotAllowException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseMessage OperationNotAllowException(ProfileException ex) {
		logger.debug(ex.getMessage());
		return new ResponseMessage(ResponseMessage.Type.error,
				"operationNotAllow", ex.getMessage());
	}

	/**
	 * Return the list of pre-loaded profiles
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public List<Profile> preloaded() {
		logger.info("Fetching all preloaded profiles...");
		List<Profile> result = profileService.findAllPreloaded();
		return result;
	}

	@RequestMapping(value = "/cuser", method = RequestMethod.GET)
	public List<Profile> userProfiles() throws UserAccountNotFoundException {
		logger.info("Fetching all custom profiles...");
		User u = userService.getCurrentUser();
		Account account = accountRepository.findByTheAccountsUsername(u
				.getUsername());
		if (account != null) {
			return profileService.findByAccountId(account.getId());
		}
		throw new UserAccountNotFoundException();
	}

	// /**
	// * Return a profile by its id
	// *
	// * @return
	// * @throws ProfileNotFoundException
	// */
	// @RequestMapping(value = "/{id}", method = RequestMethod.GET)
	// public Profile profile(@PathVariable("id") String id)
	// throws ProfileNotFoundException {
	// logger.info("GET pofile with id=" + id);
	// Profile p = profileService.findOne(id);
	// if (p == null) {
	// throw new ProfileNotFoundException(id);
	// }
	// return p;
	// }

	@RequestMapping(value = "/{id}/clone", method = RequestMethod.POST)
	public Profile clone(@PathVariable("id") String id)
			throws ProfileNotFoundException, UserAccountNotFoundException,
			ProfileException {
		logger.info("Clone profile with id=" + id);
		User u = userService.getCurrentUser();
		Account account = accountRepository.findByTheAccountsUsername(u
				.getUsername());
		if (account == null)
			throw new UserAccountNotFoundException();
		Profile p = profileService.findOne(id);
		if (p == null) {
			throw new ProfileNotFoundException(id);
		}
		Profile profile = profileService.clone(p);
		profile.setScope(ProfileScope.USER);
		profile.setAccountId(account.getId());
		profileService.save(profile);
		return profile;

	}

	@RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
	public ResponseMessage delete(@PathVariable("id") String id)
			throws ProfileNotFoundException, UserAccountNotFoundException,
			OperationNotAllowException {
		User u = userService.getCurrentUser();
		Account account = accountRepository.findByTheAccountsUsername(u
				.getUsername());
		if (account == null)
			throw new UserAccountNotFoundException();
		logger.info("Delete profile with id=" + id);
		Profile p = profileService.findOne(id);
		if (p == null) {
			throw new ProfileNotFoundException(id);
		}
		if (p.getAccountId() == account.getId()) {
			profileService.delete(id);
			return new ResponseMessage(ResponseMessage.Type.success,
					"profileDeletedSuccess", null);
		} else {
			throw new OperationNotAllowException("delete");
		}

	}

	@RequestMapping(value = "/{id}/save", method = RequestMethod.POST)
	public List<String> save(@RequestBody ChangeCommand jsonChanges,
			@PathVariable("id") String id) throws ProfileNotFoundException,
			UserAccountNotFoundException {
		User u = userService.getCurrentUser();
		Account account = accountRepository.findByTheAccountsUsername(u
				.getUsername());
		if (account == null)
			throw new UserAccountNotFoundException();
		logger.info("Applying changes = " + jsonChanges);
		Profile p = profileService.findOne(id);
		if (p == null) {
			throw new ProfileNotFoundException(id);
		}
		return profileService.apply(jsonChanges.getValue(), p);
	}

	@RequestMapping(value = "/{id}/export/XML", method = RequestMethod.POST, produces = "text/xml")
	public void export(@PathVariable("id") String id,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		// logger.info(log.toString());
		InputStream content = null;
		content = profileService.exportAsXml(id);
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
