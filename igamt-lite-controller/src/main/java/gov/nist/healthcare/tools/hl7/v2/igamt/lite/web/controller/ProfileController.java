package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileSummary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.ComponentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.FieldService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.GroupService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.MessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.SegmentRefService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.ChangesNotDoneException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.ProfileNotFoundException;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;



@RestController
@RequestMapping("/profiles")
public class ProfileController extends CommonController {

	Logger logger = LoggerFactory.getLogger(ProfileController.class);

	@Autowired
	private ProfileService profileService;

	@Autowired
	private MessageService messageService;

	@Autowired
	private SegmentRefService segmentRefService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private ComponentService componentService;

	@Autowired
	private FieldService fieldService;

	/*@
	 Autowired
	private CodeService codeService;
	 */

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
		logger.info("Clone pofile with id=" + targetId);
		Profile p = profileService.findOne(targetId);
		if (p == null) {
			throw new ProfileNotFoundException(targetId);
		}
		Profile profile = profileService.clone(p);
		profileService.save(profile);
		return profile;
	}

	@RequestMapping(value = "/apply", method = RequestMethod.POST)
	public @ResponseBody String[] apply(@RequestBody String jsonChanges)
			throws ChangesNotDoneException, IOException, ProfileNotFoundException {
		logger.info("Applying changes");

		String[] errorList = new String[]{};

		try {
			Long id;
			Iterator<Entry<String, JsonNode>> nodes;
			Entry<String, JsonNode> node;
			JsonNode individualChanges;
			Entry<String, JsonNode> newValue;


			JsonFactory f = new JsonFactory();
			JsonParser jp = f.createJsonParser(jsonChanges);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(jp);

			//profile
			nodes = rootNode.get("profile").getFields();

			while (nodes.hasNext()){
				node = nodes.next();
				id = Long.valueOf(node.getKey());
				individualChanges = node.getValue();
				Profile p = profileService.findOne(id);
				if (p == null) {
					throw new ProfileNotFoundException(id);
				}
				BeanWrapper metadata = new BeanWrapperImpl(p.getMetaData());

				Iterator<Entry<String, JsonNode>> newValues = individualChanges.getFields();
				while (newValues.hasNext()){
					newValue = newValues.next();
					metadata.setPropertyValue(newValue.getKey(), newValue.getValue());
				}
			}

			//message
			nodes = rootNode.get("message").getFields();
			while (nodes.hasNext()){
				node = nodes.next();
				id = Long.valueOf(node.getKey());
				individualChanges = node.getValue();

				Message m = messageService.findOne(id);
				BeanWrapper message = new BeanWrapperImpl(m);
				Iterator<Entry<String, JsonNode>> newValues = individualChanges.getFields();
				while (newValues.hasNext()){
					newValue = newValues.next();
					message.setPropertyValue(newValue.getKey(), newValue.getValue());
				}
			}

			//segmentRef
			nodes = rootNode.get("segmentRef").getFields();

			while (nodes.hasNext()){
				node = nodes.next();
				id = Long.valueOf(node.getKey());
				individualChanges = node.getValue();

				SegmentRef s = segmentRefService.findOne(id);
				BeanWrapper segmentRef = new BeanWrapperImpl(s);

				Iterator<Entry<String, JsonNode>> newValues = individualChanges.getFields();
				while (newValues.hasNext()){
					newValue = newValues.next();
					if (newValue.getKey() == "usage"){
						((SegmentRefOrGroup) segmentRef).setUsage(Usage.fromValue(newValue.getValue().asText()));
					} else {
						segmentRef.setPropertyValue(newValue.getKey(), newValue.getValue());
					}
				}
			}

			//group
			nodes = rootNode.get("group").getFields();
			while (nodes.hasNext()){
				node = nodes.next();
				//Group has a String id; node.getKey() is used directly
				individualChanges = node.getValue();

				Group g = groupService.findOne(node.getKey()); 
				BeanWrapper group = new BeanWrapperImpl(g);

				Iterator<Entry<String, JsonNode>> newValues = individualChanges.getFields();
				while (newValues.hasNext()){
					newValue = newValues.next();
					if (newValue.getKey() == "usage"){
						((SegmentRefOrGroup) group).setUsage(Usage.fromValue(newValue.getValue().asText()));
					} else {
						group.setPropertyValue(newValue.getKey(), newValue.getValue());
					}
				}
			}

			//component
			nodes = rootNode.get("component").getFields();
			while (nodes.hasNext()){
				node = nodes.next();
				id = Long.valueOf(node.getKey());
				individualChanges = node.getValue();

				Component c = componentService.findOne(id); 
				BeanWrapper component = new BeanWrapperImpl(c);

				Iterator<Entry<String, JsonNode>> newValues = individualChanges.getFields();
				while (newValues.hasNext()){
					newValue = newValues.next();
					if (newValue.getKey() == "usage"){
						((Component) component).setUsage(Usage.fromValue(newValue.getValue().asText()));
					} else {
						component.setPropertyValue(newValue.getKey(), newValue.getValue());
					}
				}
			}

			//field
			nodes = rootNode.get("field").getFields();
			while (nodes.hasNext()){
				node = nodes.next();
				id = Long.valueOf(node.getKey());
				individualChanges = node.getValue();

				Field f1 = fieldService.findOne(id); 
				BeanWrapper field = new BeanWrapperImpl(f1);

				Iterator<Entry<String, JsonNode>> newValues = individualChanges.getFields();
				while (newValues.hasNext()){
					newValue = newValues.next();
					if (newValue.getKey() == "usage"){
						((Field) field).setUsage(Usage.fromValue(newValue.getValue().asText()));
					} else {
						field.setPropertyValue(newValue.getKey(), newValue.getValue());
					}
				}
			}

			/*
		//code
		nodes = rootNode.get("code").getFields();
		while (nodes.hasNext()){
			node = nodes.next();
			id = Long.valueOf(node.getKey());
			individualChanges = node.getValue();

			Code c1 = codeService.findOne(id);
			BeanWrapper code = new BeanWrapperImpl(c1);
			Iterator<Entry<String, JsonNode>> newValues = individualChanges.getFields();
			while (newValues.hasNext()){
				newValue = newValues.next();
				code.setPropertyValue(newValue.getKey(), newValue.getValue());
			}
		}
			 */
		}
		catch (JsonParseException e)
		{

		}


		//profileService.save(profile);
		return errorList;
	}

}
