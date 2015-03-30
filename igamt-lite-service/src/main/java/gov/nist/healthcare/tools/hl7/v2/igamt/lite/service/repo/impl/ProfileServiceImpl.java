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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segments;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Tables;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.clone.ProfileClone;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.DatatypesService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.MessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.MessagesService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.SegmentsService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.TablesService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.xml.ProfileSerializationImpl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.NullInputStream;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.MongoException;

@Service
public class ProfileServiceImpl implements ProfileService {

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private MessageService messageService;

	@Autowired
	private MessagesService messagesService;

	@Autowired
	private SegmentService segmentService;

	@Autowired
	private SegmentsService segmentsService;

	@Autowired
	private DatatypeService datatypeService;

	@Autowired
	private DatatypesService datatypesService;

	@Autowired
	private TableService tableService;

	@Autowired
	private TablesService tableLibraryService;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Profile save(Profile p) throws ProfileException {
		try {
			Tables tables = p.getTables();
			if (tables != null) {
				tableLibraryService.save(tables);
				for (Table t : tables.getChildren()) {
					tableService.save(t);
				}

			}
			Datatypes datatypes = p.getDatatypes();
			if (datatypes != null) {
				datatypesService.save(datatypes);
				Set<Datatype> primitiveDatatypes = findPrimitiveDatatypes(datatypes);
				for (Datatype d : primitiveDatatypes) {
					datatypeService.save(d);
				}
				for (Datatype datatype : datatypes.getChildren()) {
					if (datatype.getId() == null) {
						datatypeService.save(datatype);
					}
				}
			}

			Segments segments = p.getSegments();
			if (segments != null) {
				segmentsService.save(segments);
				for (Segment s : segments.getChildren()) {
					segmentService.save(s);
				}
			}

			Messages messages = p.getMessages();
			if (messages != null) {
				messagesService.save(messages);
				for (Message m : messages.getChildren()) {

					// List<SegmentRefOrGroup> children = m.getChildren();

					messageService.save(m);
				}

			}
			return profileRepository.save(p);
		} catch (MongoException e) {
			throw new ProfileException(e);
		}
	}

	public Set<Datatype> findPrimitiveDatatypes(Datatypes datatypes) {
		Set<Datatype> primitives = new HashSet<Datatype>();
		for (Datatype datatype : datatypes.getChildren()) {
			findPrimitiveDatatypes(datatype, primitives);
		}
		return primitives;
	}

	public Set<Datatype> findPrimitiveDatatypes(Datatype datatype,
			Set<Datatype> result) {
		if (datatype.getComponents() == null
				|| datatype.getComponents().isEmpty()) {
			result.add(datatype);
		} else {
			for (Component component : datatype.getComponents()) {
				findPrimitiveDatatypes(component.getDatatype(), result);
			}
		}
		return result;
	}

	@Override
	@Transactional
	public void delete(String id) {
		profileRepository.delete(id);
	}

	@Override
	public Profile findOne(String id) {
		Profile profile = profileRepository.findOne(id);
		return profile;
	}

	// public Profile setDatatypeReferences(Profile profile) {
	// for (Segment s : profile.getSegments().getChildren()) {
	// setDatatypeReferences(s, profile.getDatatypes());
	// }
	// for (Datatype d : profile.getDatatypes().getChildren()) {
	// setDatatypeReferences(d, profile.getDatatypes());
	// }
	// return profile;
	// }
	//
	// private void setDatatypeReferences(Segment segment, Datatypes datatypes)
	// {
	// for (Field f : segment.getFields()) {
	// f.setDatatype(datatypes.find(f.getDatatypeLabel()));
	// }
	// }
	//
	// private void setDatatypeReferences(Datatype datatype, Datatypes
	// datatypes) {
	// if (datatype != null && datatype.getComponents() != null) {
	// for (Component c : datatype.getComponents()) {
	// c.setDatatype(datatypes.find(c.getDatatypeLabel()));
	// }
	// }
	// }

	@Override
	public List<Profile> findAllPreloaded() {
		List<Profile> profiles = profileRepository.findByPreloaded(true);
		if (profiles != null && !profiles.isEmpty()) {
			for (Profile profile : profiles) {
				processChildren(profile);
			}
		}
		return profiles;
	}

	private void processChildren(Profile profile) {
		List<Message> messages = messageService.findByMessagesId(profile
				.getMessages().getId());
		profile.getMessages().getChildren().addAll(messages);
	}

	@Override
	public List<Profile> findAllCustom() {
		List<Profile> profiles = profileRepository.findByPreloaded(false);
		if (profiles != null && !profiles.isEmpty()) {
			for (Profile profile : profiles) {
				processChildren(profile);
			}
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
	@Transactional(propagation = Propagation.REQUIRED)
	public List<String> apply(String jsonChanges) {
		List<String> errorList = new ArrayList<String>();
		//
		// try {
		// String id;
		// Iterator<Entry<String, JsonNode>> nodes;
		// Entry<String, JsonNode> node;
		// JsonNode individualChanges;
		// Entry<String, JsonNode> newValue;
		// Iterator<Entry<String, JsonNode>> newValues;
		//
		// JsonFactory f = new JsonFactory();
		// JsonParser jp = f.createJsonParser(jsonChanges);
		// ObjectMapper mapper = new ObjectMapper();
		// JsonNode rootNode = mapper.readTree(jp);
		//
		// // profile
		// nodes = rootNode.path("profile").getFields();
		// while (nodes.hasNext()) {
		// node = nodes.next();
		// id = Long.valueOf(node.getKey());
		// individualChanges = node.getValue();
		// Profile p = profileRepository.findOne(id);
		// if (p == null) {
		// errorList.add("profile ID not found: " + node.getKey());
		// } else {
		//
		// // FIXME
		// // Now: all changes are saved;
		// // Todo: "unsave" changes that could not be saved
		// // FIXME 2
		// // changes is initialized with a dummy value {"0":0}
		//
		// if (p.getChanges() == null) {
		// p.setChanges(new String("{\"0\":0}"));
		// }
		// JsonNode currentNode = mapper.readTree(f.createJsonParser(p
		// .getChanges()));
		// JsonNode newNode = mapper.readTree(f
		// .createJsonParser(jsonChanges));
		// JsonNode updated = merge(currentNode, newNode);
		// p.setChanges(updated.toString());
		//
		// BeanWrapper metadata = new BeanWrapperImpl(p.getMetaData());
		// newValues = individualChanges.getFields();
		// while (newValues.hasNext()) {
		// newValue = newValues.next();
		// try {
		// metadata.setPropertyValue(newValue.getKey(),
		// newValue.getValue().getTextValue());
		// } catch (NotWritablePropertyException e) {
		// errorList.add(new String(
		// "profile property not set: "
		// + newValue.getKey()
		// + newValue.getValue()
		// .getTextValue()));
		// }
		// }
		// profileRepository.save(p);
		// }
		// }
		//
		// // message
		// nodes = rootNode.path("message").getFields();
		// while (nodes.hasNext()) {
		// node = nodes.next();
		// id = Long.valueOf(node.getKey());
		// individualChanges = node.getValue();
		//
		// Message m = messageService.findOne(id);
		// if (m == null) {
		// errorList.add("message ID not found: " + node.getKey());
		// } else {
		// BeanWrapper message = new BeanWrapperImpl(m);
		// newValues = individualChanges.getFields();
		// while (newValues.hasNext()) {
		// newValue = newValues.next();
		// try {
		// message.setPropertyValue(newValue.getKey(),
		// newValue.getValue().getTextValue());
		// } catch (NotWritablePropertyException e) {
		// errorList.add(new String(
		// "message property not set: "
		// + newValue.getKey()
		// + newValue.getValue()
		// .getTextValue()));
		// }
		// }
		// messageService.save(m);
		// }
		// }
		//
		// // segment
		// nodes = rootNode.path("segment").getFields();
		//
		// while (nodes.hasNext()) {
		// node = nodes.next();
		// id = Long.valueOf(node.getKey());
		// individualChanges = node.getValue();
		//
		// Segment s = segmentService.findOne(id);
		//
		// if (s == null) {
		// errorList.add("segment ID not found: " + node.getKey());
		// } else {
		// BeanWrapper segment = new BeanWrapperImpl(s);
		//
		// newValues = individualChanges.getFields();
		// while (newValues.hasNext()) {
		// newValue = newValues.next();
		// try {
		// segment.setPropertyValue(newValue.getKey(),
		// newValue.getValue().getTextValue());
		// } catch (NotWritablePropertyException e) {
		// errorList.add(new String(
		// "Segment property not set: "
		// + newValue.getKey()
		// + newValue.getValue()
		// .getTextValue()));
		// }
		// }
		// segmentService.save(s);
		// }
		// }
		//
		// // segmentRef
		// nodes = rootNode.path("segmentRef").getFields();
		//
		// while (nodes.hasNext()) {
		// node = nodes.next();
		// id = Long.valueOf(node.getKey());
		// individualChanges = node.getValue();
		//
		// SegmentRef sr = segmentRefService.findOne(id);
		//
		// if (sr == null) {
		// errorList.add("SegmentRef ID not found: " + node.getKey());
		// } else {
		// BeanWrapper segmentRef = new BeanWrapperImpl(sr);
		//
		// newValues = individualChanges.getFields();
		// while (newValues.hasNext()) {
		// newValue = newValues.next();
		// try {
		// segmentRef.setPropertyValue(newValue.getKey(),
		// newValue.getValue().getTextValue());
		// } catch (NotWritablePropertyException e) {
		// errorList.add(new String(
		// "SegmentRef property not set: "
		// + newValue.getKey()
		// + newValue.getValue()
		// .getTextValue()));
		// }
		// }
		// segmentRefService.save(sr);
		// }
		// }
		//
		// // group
		// nodes = rootNode.path("group").getFields();
		// while (nodes.hasNext()) {
		// node = nodes.next();
		// // Group has a String id; node.getKey() is used directly
		// individualChanges = node.getValue();
		// id = Long.valueOf(node.getKey());
		// Group g = groupService.findOne(id);
		// if (g == null) {
		// errorList.add("Group ID not found: " + node.getKey());
		// } else {
		// BeanWrapper group = new BeanWrapperImpl(g);
		// newValues = individualChanges.getFields();
		// while (newValues.hasNext()) {
		// newValue = newValues.next();
		// try {
		// group.setPropertyValue(newValue.getKey(), newValue
		// .getValue().getTextValue());
		// } catch (NotWritablePropertyException e) {
		// errorList.add(new String("group property not set: "
		// + newValue.getKey()
		// + newValue.getValue().getTextValue()));
		// }
		// }
		// groupService.save(g);
		// }
		// }
		//
		// // component
		// nodes = rootNode.path("component").getFields();
		// while (nodes.hasNext()) {
		// node = nodes.next();
		// id = Long.valueOf(node.getKey());
		// individualChanges = node.getValue();
		//
		// Component c = componentService.findOne(id);
		// if (c == null) {
		// errorList.add("Component ID not found: " + node.getKey());
		// } else {
		// BeanWrapper component = new BeanWrapperImpl(c);
		// newValues = individualChanges.getFields();
		// while (newValues.hasNext()) {
		// newValue = newValues.next();
		// try {
		// component.setPropertyValue(newValue.getKey(),
		// newValue.getValue().getTextValue());
		// } catch (NotWritablePropertyException e) {
		// errorList.add(new String(
		// "Component property not set: "
		// + newValue.getKey()
		// + newValue.getValue()
		// .getTextValue()));
		// }
		// }
		// componentService.save(c);
		// }
		// }
		//
		// // field
		// nodes = rootNode.path("field").getFields();
		// while (nodes.hasNext()) {
		// node = nodes.next();
		// id = Long.valueOf(node.getKey());
		// individualChanges = node.getValue();
		//
		// Field f1 = fieldService.findOne(id);
		// BeanWrapper field = new BeanWrapperImpl(f1);
		//
		// newValues = individualChanges.getFields();
		// while (newValues.hasNext()) {
		// newValue = newValues.next();
		// try {
		// field.setPropertyValue(newValue.getKey(), newValue
		// .getValue().getTextValue());
		// } catch (NotWritablePropertyException e) {
		// errorList.add(new String("profile property not set: "
		// + newValue.getKey()
		// + newValue.getValue().getTextValue()));
		// }
		// }
		// fieldService.save(f1);
		// }
		//
		// // table
		// nodes = rootNode.path("table").getFields();
		// while (nodes.hasNext()) {
		// node = nodes.next();
		// id = Long.valueOf(node.getKey());
		// individualChanges = node.getValue();
		//
		// Table t = tableService.findOne(id);
		// BeanWrapper code = new BeanWrapperImpl(t);
		// newValues = individualChanges.getFields();
		// while (newValues.hasNext()) {
		// newValue = newValues.next();
		// try {
		// code.setPropertyValue(newValue.getKey(), newValue
		// .getValue().getTextValue());
		// } catch (NotWritablePropertyException e) {
		// errorList.add(new String("table property not set: "
		// + newValue.getKey()
		// + newValue.getValue().getTextValue()));
		// }
		// }
		// tableService.save(t);
		// }
		//
		// // code
		// nodes = rootNode.path("code").getFields();
		// while (nodes.hasNext()) {
		// node = nodes.next();
		// id = Long.valueOf(node.getKey());
		// individualChanges = node.getValue();
		//
		// Code c1 = codeService.findOne(id);
		// if (c1 == null) {
		// errorList.add("Code ID not found: " + node.getKey());
		// } else {
		// BeanWrapper code = new BeanWrapperImpl(c1);
		// newValues = individualChanges.getFields();
		// while (newValues.hasNext()) {
		// newValue = newValues.next();
		// try {
		// code.setPropertyValue(newValue.getKey(), newValue
		// .getValue().getTextValue());
		// } catch (NotWritablePropertyException e) {
		// errorList.add(new String("code property not set: "
		// + newValue.getKey()
		// + newValue.getValue().getTextValue()));
		// }
		// }
		// codeService.save(c1);
		// }
		// }

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
			} else {
				if (mainNode instanceof ObjectNode) {
					// Overwrite field
					JsonNode value = updateNode.get(fieldName);
					((ObjectNode) mainNode).put(fieldName, value);
				}
			}
		}
		return mainNode;
	}

	@Override
	public InputStream exportAsPdf(String targetId) {
		return new NullInputStream(1L);
	}

	@Override
	public InputStream exportAsXml(String targetId) {
		Profile p = clone(findOne(targetId));
		if (p != null) {
			return IOUtils.toInputStream(new ProfileSerializationImpl()
					.serializeProfileToXML(p));
		} else {
			return new NullInputStream(1L);
		}
	}

}