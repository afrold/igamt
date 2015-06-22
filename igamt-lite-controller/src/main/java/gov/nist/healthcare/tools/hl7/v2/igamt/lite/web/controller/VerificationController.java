package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ElementVerification;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileSaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.ProfileSaveResponse;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.OperationNotAllowException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.UserAccountNotFoundException;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profiles")
public class VerificationController extends CommonController {

	Logger logger = LoggerFactory.getLogger(VerificationController.class);

	@Autowired
	private ProfileService profileService;

	public ProfileService getProfileService() {
		return profileService;
	}

	public void setProfileService(ProfileService profileService) {
		this.profileService = profileService;
	}


	private Profile findProfile(String profileId)
			throws ProfileNotFoundException {
		Profile p = profileService.findOne(profileId);
		if (p == null) {
			throw new ProfileNotFoundException(profileId);
		}
		return p;
	}

	@RequestMapping(value = "/{id}/verify/message/{mId}", method = RequestMethod.POST)
	public ElementVerification verifyMessage(@PathVariable("id") String id, @PathVariable("mId") String mId,
			HttpServletRequest request, HttpServletResponse response)
					throws IOException, ProfileNotFoundException {
		logger.info("Verifying segment " + mId + " from profile " + id);
		Profile p = findProfile(id);
		return profileService.verifyMessage(p, id, "segmentRef");		
	}
	@RequestMapping(value = "/{id}/verify/srog/{sId}", method = RequestMethod.POST)
	public ElementVerification verifySegmentRefOrGroup(@PathVariable("id") String id, @PathVariable("sId") String sId,
			HttpServletRequest request, HttpServletResponse response)
					throws IOException, ProfileNotFoundException {
		logger.info("Verifying segmentref or group " + sId + " from profile " + id);
		Profile p = findProfile(id);
		return profileService.verifySegmentRefOrGroup(p, id, "segmentRef");		
	}

	@RequestMapping(value = "/{id}/verify/segment/{sId}", method = RequestMethod.POST)
	public ElementVerification verifySegment(@PathVariable("id") String id, @PathVariable("sId") String sId,
			HttpServletRequest request, HttpServletResponse response)
					throws IOException, ProfileNotFoundException {
		logger.info("Verifying segment " + sId + " from profile " + id);
		Profile p = findProfile(id);
		return profileService.verifySegment(p, id, "segment");		
	}

	@RequestMapping(value = "/{id}/verify/field/{fId}", method = RequestMethod.POST)
	public ElementVerification verifyField(@PathVariable("id") String id, @PathVariable("fId") String fId,
			HttpServletRequest request, HttpServletResponse response)
					throws IOException, ProfileNotFoundException {
		logger.info("Verifying field " + fId + " from profile " + id);
		Profile p = findProfile(id);
		return profileService.verifyField(p, id, "field");		
	}

	@RequestMapping(value = "/{id}/verify/datatype/{dtId}", method = RequestMethod.POST)
	public ElementVerification verifyDatatype(@PathVariable("id") String id, @PathVariable("dtId") String dtId,
			HttpServletRequest request, HttpServletResponse response)
					throws IOException, ProfileNotFoundException {
		logger.info("Verifying datatype " + dtId + " from profile " + id);
		Profile p = findProfile(id);
		return profileService.verifyDatatype(p, id, "datatype");
	}

	@RequestMapping(value = "/{id}/verify/component/{cId}", method = RequestMethod.POST)
	public ElementVerification verifyComponent(@PathVariable("id") String id, @PathVariable("cId") String cId,
			HttpServletRequest request, HttpServletResponse response)
					throws IOException, ProfileNotFoundException {
		logger.info("Verifying datatype " + cId + " from profile " + id);
		Profile p = findProfile(id);
		return profileService.verifyComponent(p, id, "datatype");
	}

	@RequestMapping(value = "/{id}/verify/valueset/{vsId}", method = RequestMethod.POST)
	public ElementVerification verifyValueSet(@PathVariable("id") String id, @PathVariable("vsId") String vsId,
			HttpServletRequest request, HttpServletResponse response)
					throws IOException, ProfileNotFoundException {
		logger.info("Verifying segment " + vsId + " from profile " + id);
		Profile p = findProfile(id);
		return profileService.verifyValueSet(p, id, "valueset");
	}

	@RequestMapping(value = "/{id}/verify/length/{eltId}", method = RequestMethod.POST)
	public ElementVerification verifyLength(@PathVariable("id") String id, @PathVariable("eltId") String eltId,
			@RequestParam String type, @RequestParam String attName, @RequestParam String attValue,
			HttpServletRequest request, HttpServletResponse response)
					throws IOException, ProfileNotFoundException {
		logger.info("Verifying length of element " + eltId + " from profile " + id);
		Profile p = findProfile(id);
		return profileService.verifyLength(p, eltId, type, attName, attValue);
	}

	@RequestMapping(value = "/{id}/verify/cardinality/{eltId}", method = RequestMethod.POST)
	public ElementVerification verifyCardinality(@PathVariable("id") String id, @PathVariable("eltId") String eltId,
			@RequestParam String type, @RequestParam String attName, @RequestParam String attValue,
			HttpServletRequest request, HttpServletResponse response)
					throws IOException, ProfileNotFoundException {
		logger.info("Verifying length of element " + eltId + " from profile " + id);
		Profile p = findProfile(id);
		return profileService.verifyCardinality(p, eltId, type, attName, attValue);
	}

	@RequestMapping(value = "/{id}/verify/usage/{eltId}", method = RequestMethod.POST)
	public ElementVerification verifyUsage(@PathVariable("id") String id, @PathVariable("eltId") String eltId,
			@RequestParam String type, @RequestParam String attName, @RequestParam String attValue,
			HttpServletRequest request, HttpServletResponse response)
					throws IOException, ProfileNotFoundException {
		logger.info("Verifying length of element " + eltId + " from profile " + id);
		Profile p = findProfile(id);
		return profileService.verifyUsage(p, eltId, type, attName, attValue);
	}


}
