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

/**
 * 
 * @author Olivier MARIE-ROSE
 * 
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatypes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.clone.ProfileClone;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.CodeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.ComponentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.FieldService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.GroupService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.MessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.SegmentRefService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.TableService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileServiceImpl implements ProfileService {

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private MessageService messageService;

	@Autowired
	private SegmentService segmentService;

	@Autowired
	private SegmentRefService segmentRefService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private ComponentService componentService;

	@Autowired
	private FieldService fieldService;

	@Autowired
	private TableService tableService;

	@Autowired
	private CodeService codeService;

	@Override
	@Transactional
	public Profile save(Profile p) {
		return profileRepository.save(p);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		profileRepository.delete(id);
	}

	@Override
	public Profile findOne(Long id) {
		Profile profile = profileRepository.findOne(id);
		setDatatypeReferences(profile);
		return profile;
	}

	public Profile setDatatypeReferences(Profile profile) {
		for (Segment s : profile.getSegments().getChildren()) {
			setDatatypeReferences(s, profile.getDatatypes());
		}
		for (Datatype d : profile.getDatatypes().getChildren()) {
			setDatatypeReferences(d, profile.getDatatypes());
		}
		return profile;
	}

	private void setDatatypeReferences(Segment segment, Datatypes datatypes) {
		for (Field f : segment.getFields()) {
			f.setDatatype(datatypes.find(f.getDatatypeLabel()));
		}
	}

	private void setDatatypeReferences(Datatype datatype, Datatypes datatypes) {
		if (datatype != null && datatype.getComponents() != null) {
			for (Component c : datatype.getComponents()) {
				c.setDatatype(datatypes.find(c.getDatatypeLabel()));
			}
		}
	}

	@Override
	public List<Profile> findAllPreloaded() {
		List<Profile> profiles = profileRepository.findAllPreloaded();
		for (Profile profile : profiles) {
			setDatatypeReferences(profile);
		}
		return profiles;

	}

	@Override
	public List<Profile> findAllCustom() {
		List<Profile> profiles = profileRepository.findAllCustom();
		for (Profile profile : profiles) {
			setDatatypeReferences(profile);
		}
		return profiles;
	}

	@Override
	public Profile clone(Profile p) {
		return new ProfileClone().clone(p);
	}

	/*
	 * { "component": { "59": { "usage": "C" }, "303": { "maxLength": "27" } } }
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public List<String> apply(String jsonChanges) throws ProfileNotFoundException {
		List<String> errorList = new ArrayList<String>();

		try {
			Long id;
			Iterator<Entry<String, JsonNode>> nodes;
			Entry<String, JsonNode> node;
			JsonNode individualChanges;
			Entry<String, JsonNode> newValue;
			Iterator<Entry<String, JsonNode>> newValues;

			JsonFactory f = new JsonFactory();
			JsonParser jp = f.createJsonParser(jsonChanges);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(jp);

			// profile
			nodes = rootNode.path("profile").getFields();
			while (nodes.hasNext()) {
				node = nodes.next();
				id = Long.valueOf(node.getKey());
				individualChanges = node.getValue();
				Profile p = profileRepository.findOne(id);
				if (p == null) {
					errorList.add("profile ID not found: " + node.getKey());
				} else {
					
					//FIXME
					//Now: all changes are saved; 
					//Todo: "unsave" changes that could not be saved
					//FIXME 2
					//changes is initialized with a dummy value {"0":0}
					
					if (p.getChanges() == null){
						p.setChanges(new String("{\"0\":0}"));
					}
					JsonNode currentNode = mapper.readTree(f.createJsonParser(p.getChanges()));
					JsonNode newNode = mapper.readTree(f.createJsonParser(jsonChanges));
					JsonNode updated = merge(currentNode, newNode);
					p.setChanges(updated.toString());
					
					
					
					BeanWrapper metadata = new BeanWrapperImpl(p.getMetaData());
					newValues = individualChanges
							.getFields();
					while (newValues.hasNext()) {
						newValue = newValues.next();
						try{
							metadata.setPropertyValue(newValue.getKey(), newValue
									.getValue().getTextValue());
						}
						catch (NotWritablePropertyException e) {
							errorList.add(new String("profile property not set: " 
									+ newValue.getKey() + newValue.getValue().getTextValue()));
						}
					}
					profileRepository.save(p);
				}
			}

			// message
			nodes = rootNode.path("message").getFields();
			while (nodes.hasNext()) {
				node = nodes.next();
				id = Long.valueOf(node.getKey());
				individualChanges = node.getValue();

				Message m = messageService.findOne(id);
				if (m == null) {
					errorList.add("message ID not found: " + node.getKey());
				} else {
					BeanWrapper message = new BeanWrapperImpl(m);
					newValues = individualChanges
							.getFields();
					while (newValues.hasNext()) {
						newValue = newValues.next();
						try {
							message.setPropertyValue(newValue.getKey(), newValue
									.getValue().getTextValue());
						}
						catch (NotWritablePropertyException e) {
							errorList.add(new String("message property not set: " 
									+ newValue.getKey() + newValue.getValue().getTextValue()));
						}
					}
					messageService.save(m);
				}
			}

			// segment
			nodes = rootNode.path("segment").getFields();

			while (nodes.hasNext()) {
				node = nodes.next();
				id = Long.valueOf(node.getKey());
				individualChanges = node.getValue();

				Segment s = segmentService.findOne(id);

				if (s == null) {
					errorList.add("segment ID not found: " + node.getKey());
				} else {
					BeanWrapper segment = new BeanWrapperImpl(s);

					newValues = individualChanges
							.getFields();
					while (newValues.hasNext()) {
						newValue = newValues.next();
						try {
							segment.setPropertyValue(newValue.getKey(), newValue
									.getValue().getTextValue());
						}
						catch (NotWritablePropertyException e) {
							errorList.add(new String("Segment property not set: " 
									+ newValue.getKey() + newValue.getValue().getTextValue()));
						}
					}
					segmentService.save(s);
				}
			}

			// segmentRef
			nodes = rootNode.path("segmentRef").getFields();

			while (nodes.hasNext()) {
				node = nodes.next();
				id = Long.valueOf(node.getKey());
				individualChanges = node.getValue();

				SegmentRef sr = segmentRefService.findOne(id);

				if (sr == null) {
					errorList.add("SegmentRef ID not found: " + node.getKey());
				} else {
					BeanWrapper segmentRef = new BeanWrapperImpl(sr);

					newValues = individualChanges
							.getFields();
					while (newValues.hasNext()) {
						newValue = newValues.next();
						try {
							segmentRef.setPropertyValue(newValue.getKey(), newValue
									.getValue().getTextValue());
						}
						catch (NotWritablePropertyException e) {
							errorList.add(new String("SegmentRef property not set: " 
									+ newValue.getKey() + newValue.getValue().getTextValue()));
						}
					}
					segmentRefService.save(sr);
				}
			}


			// group
			nodes = rootNode.path("group").getFields();
			while (nodes.hasNext()) {
				node = nodes.next();
				// Group has a String id; node.getKey() is used directly
				individualChanges = node.getValue();
				id = Long.valueOf(node.getKey());
				Group g = groupService.findOne(id);
				if (g == null) {
					errorList.add("Group ID not found: " + node.getKey());
				} else {
					BeanWrapper group = new BeanWrapperImpl(g);
					newValues = individualChanges
							.getFields();
					while (newValues.hasNext()) {
						newValue = newValues.next();
						try {
							group.setPropertyValue(newValue.getKey(), newValue
									.getValue().getTextValue());
						}
						catch (NotWritablePropertyException e) {
							errorList.add(new String("group property not set: " 
									+ newValue.getKey() + newValue.getValue().getTextValue()));
						}
					}
					groupService.save(g);
				}
			}

			// component
			nodes = rootNode.path("component").getFields();
			while (nodes.hasNext()) {
				node = nodes.next();
				id = Long.valueOf(node.getKey());
				individualChanges = node.getValue();

				Component c = componentService.findOne(id);
				if (c == null) {
					errorList.add("Component ID not found: " + node.getKey());
				} else {
					BeanWrapper component = new BeanWrapperImpl(c);
					newValues = individualChanges.getFields();
					while (newValues.hasNext()) {
						newValue = newValues.next();
						try {
							component.setPropertyValue(newValue.getKey(), newValue
									.getValue().getTextValue());
						}
						catch (NotWritablePropertyException e) {
							errorList.add(new String("Component property not set: " 
									+ newValue.getKey() + newValue.getValue().getTextValue()));
						}
					}
					componentService.save(c);
				}
			}

			// field
			nodes = rootNode.path("field").getFields();
			while (nodes.hasNext()) {
				node = nodes.next();
				id = Long.valueOf(node.getKey());
				individualChanges = node.getValue();

				Field f1 = fieldService.findOne(id);
				BeanWrapper field = new BeanWrapperImpl(f1);

				newValues = individualChanges.getFields();
				while (newValues.hasNext()) {
					newValue = newValues.next();
					try {
						field.setPropertyValue(newValue.getKey(), newValue
								.getValue().getTextValue());
					} catch (NotWritablePropertyException e) {
						errorList.add(new String("profile property not set: " 
								+ newValue.getKey() + newValue.getValue().getTextValue()));
					}
				}
				fieldService.save(f1);
			}

			// table
			nodes = rootNode.path("table").getFields();
			while (nodes.hasNext()) {
				node = nodes.next();
				id = Long.valueOf(node.getKey());
				individualChanges = node.getValue();

				Table t = tableService.findOne(id);
				BeanWrapper code = new BeanWrapperImpl(t);
				newValues = individualChanges.getFields();
				while (newValues.hasNext()) {
					newValue = newValues.next();
					try {
						code.setPropertyValue(newValue.getKey(), newValue
								.getValue().getTextValue());
					} catch (NotWritablePropertyException e) {
						errorList.add(new String("table property not set: " 
								+ newValue.getKey() + newValue.getValue().getTextValue()));
					}
				}
				tableService.save(t);
			}

			// code
			nodes = rootNode.path("code").getFields();
			while (nodes.hasNext()) {
				node = nodes.next();
				id = Long.valueOf(node.getKey());
				individualChanges = node.getValue();

				Code c1 = codeService.findOne(id);
				if (c1 == null) {
					errorList.add("Code ID not found: " + node.getKey());
				} else {
					BeanWrapper code = new BeanWrapperImpl(c1);
					newValues = individualChanges
							.getFields();
					while (newValues.hasNext()) {
						newValue = newValues.next();
						try {
							code.setPropertyValue(newValue.getKey(), newValue
									.getValue().getTextValue());
						}
						catch (NotWritablePropertyException e) {
							errorList.add(new String("code property not set: " 
									+ newValue.getKey() + newValue.getValue().getTextValue()));
						}
					}
					codeService.save(c1);
				}
			}
		} catch (IOException e) {

		}
		return errorList;
	}
	
	public JsonNode merge(JsonNode mainNode, JsonNode updateNode) {
	    Iterator<String> fieldNames = updateNode.getFieldNames();
	    while (fieldNames.hasNext()) {
	        String fieldName = fieldNames.next();
	        JsonNode jsonNode = mainNode.get(fieldName);
	        // if field exists and is an embedded object
	        if (jsonNode != null && jsonNode.isObject()) {
	            merge(jsonNode, updateNode.get(fieldName));
	        }
	        else {
	            if (mainNode instanceof ObjectNode) {
	                // Overwrite field
	                JsonNode value = updateNode.get(fieldName);
	                ((ObjectNode) mainNode).put(fieldName, value);
	            }
	        }
	    }
	    return mainNode;
	}
	
	public Byte[] exportAsPdf(Long targetId){
		return new Byte[]{};
	}

	public Byte[] exportAsXml(Long targetId){
		return new Byte[]{};
	}


}