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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileSaveException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.NotWritablePropertyException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Harold Affo (harold.affo@nist.gov) Apr 16, 2015
 */
public class ProfileChangeService {

	private final static String EDIT_NODE = "edit";
	private final static String ADD_NODE = "add";
	private final static String DELETE_NODE = "delete";

	Map<String, Datatype> newDatatypesMap = new HashMap<String, Datatype>();
	Map<String, Table> newTablesMap = new HashMap<String, Table>();
	List<ProfilePropertySaveError> errors = new ArrayList<ProfilePropertySaveError>();
	Profile p = null;

	public List<ProfilePropertySaveError> apply(String jsonChanges, Profile p)
			throws ProfileSaveException {

		this.p = p;

		try {
			JsonFactory f = new JsonFactory();
			JsonParser jp = f.createParser(jsonChanges);
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode rootNode = mapper.readTree(jp);

			String nodeName = "datatype";
			if (rootNode.has(nodeName)) {
				setDatatypeValues(rootNode.path(nodeName).fields());
			}

			nodeName = "component";
			if (rootNode.has(nodeName)) {
				setComponentValues(rootNode.path(nodeName).fields());
			}

			nodeName = "field";
			if (rootNode.has(nodeName)) {
				setFieldValues(rootNode.path(nodeName).fields());
			}

			nodeName = "segment";
			if (rootNode.has(nodeName)) {
				setSegmentValues(rootNode.path(nodeName).fields());
			}

			nodeName = "segmentRef";
			if (rootNode.has(nodeName)) {
				setSegmentRefValues(rootNode.path(nodeName).fields());
			}

			nodeName = "group";
			if (rootNode.has(nodeName)) {
				setGroupValues(rootNode.path(nodeName).fields());
			}

			nodeName = "message";
			if (rootNode.has(nodeName)) {
				setMessageValues(rootNode.path(nodeName).fields());
			}

			nodeName = "profile";
			if (rootNode.has(nodeName)) {
				setProfileValues(rootNode.path(nodeName).fields());
			}

			nodeName = "table";
			if (rootNode.has(nodeName)) {
				setTableValues(rootNode.path(nodeName).fields());
			}

			nodeName = "predicate";
			if (rootNode.has(nodeName)) {
				setPredicateValues(rootNode.path(nodeName).fields());
			}

			nodeName = "conformanceStatement";
			if (rootNode.has(nodeName)) {
				setConformanceStatementValues(rootNode.path(nodeName).fields());
			}

			// profileRepository.save(p);

		} catch (Exception e) {
			throw new ProfileSaveException(e.getMessage());
		}
		return errors;

	}

	private Datatype toDatatype(JsonNode node) throws ProfileSaveException,
			JsonParseException, JsonMappingException, IOException {
		Datatype datatype = null;
		if (node != null) {
			ObjectMapper mapper = new ObjectMapper();
			datatype = mapper.readValue(node.asText(), Datatype.class);
		}
		return datatype;
	}

	private Table toTable(JsonNode node) throws ProfileSaveException,
			JsonParseException, JsonMappingException, IOException {
		Table table = null;
		if (node != null) {
			ObjectMapper mapper = new ObjectMapper();
			table = mapper.readValue(node.asText(), Table.class);
		}
		return table;
	}

	private void setProfileValues(Iterator<Entry<String, JsonNode>> nodes)
			throws ProfileSaveException, JsonParseException,
			JsonMappingException, IOException {
		while (nodes.hasNext()) {
			Entry<String, JsonNode> node = nodes.next();
			if (node.getKey().equals(EDIT_NODE)) {
				JsonNode individualChanges = node.getValue();
				ProfileMetaData target = p.getMetaData();
				Iterator<JsonNode> contentIt = individualChanges.iterator();
				while (contentIt.hasNext()) {
					JsonNode newValue = contentIt.next();
					Iterator<Entry<String, JsonNode>> fields = newValue
							.fields();
					setEditValues(fields, new BeanWrapperImpl(target));
				}
			}
		}
	}

	private void setTableValues(Iterator<Entry<String, JsonNode>> nodes)
			throws ProfileSaveException, JsonParseException,
			JsonMappingException, IOException {
		while (nodes.hasNext()) {
			Entry<String, JsonNode> node = nodes.next();
			if (node.getKey().equals(EDIT_NODE)) {
				JsonNode individualChanges = node.getValue();
				Iterator<JsonNode> contentIt = individualChanges.iterator();
				while (contentIt.hasNext()) {
					JsonNode newValue = contentIt.next();
					String id = newValue.findValue("id").asText();
					Iterator<Entry<String, JsonNode>> fields = newValue
							.fields();
					Table target = p.getTables().findOne(id);
					if (target != null) {
						setEditValues(fields, new BeanWrapperImpl(target));
					} else {
						errors.add(new ProfilePropertySaveError(id, "table",
								"Table with id=" + id + " not found"));
					}
				}

			} else if (node.getKey().equals(ADD_NODE)) {
				JsonNode individualChanges = node.getValue();
				Iterator<JsonNode> contentIt = individualChanges.iterator();
				while (contentIt.hasNext()) {
					JsonNode newValue = contentIt.next();
					String id = newValue.findValue("id").asText();
					Table t = toTable(newValue);
					newTablesMap.put(id, t);
					p.getTables().addTable(t);
				}
			} else if (node.getKey().equals(DELETE_NODE)) {
				JsonNode individualChanges = node.getValue();
				Iterator<JsonNode> contentIt = individualChanges.iterator();
				while (contentIt.hasNext()) {
					JsonNode newValue = contentIt.next();
					String id = newValue.findValue("id").asText();
					p.getTables().delete(id);
				}
			}
		}
	}

	private void setPredicateValues(Iterator<Entry<String, JsonNode>> nodes)
			throws ProfileSaveException, JsonParseException,
			JsonMappingException, IOException {
		while (nodes.hasNext()) {
			Entry<String, JsonNode> node = nodes.next();
			if (node.getKey().equals(EDIT_NODE)) {
				JsonNode individualChanges = node.getValue();
				Iterator<JsonNode> contentIt = individualChanges.iterator();
				while (contentIt.hasNext()) {
					JsonNode newValue = contentIt.next();
					String id = newValue.findValue("id").asText();
					Iterator<Entry<String, JsonNode>> fields = newValue
							.fields();
					Predicate target = p.findOnePredicate(id);
					if (target != null) {
						setEditValues(fields, new BeanWrapperImpl(target));
					} else {
						errors.add(new ProfilePropertySaveError(id,
								"predicate", "Predicate with id=" + id
										+ " not found"));
					}
				}

			}
		}
	}

	private void setConformanceStatementValues(
			Iterator<Entry<String, JsonNode>> nodes)
			throws ProfileSaveException, JsonParseException,
			JsonMappingException, IOException {
		while (nodes.hasNext()) {
			Entry<String, JsonNode> node = nodes.next();
			if (node.getKey().equals(EDIT_NODE)) {
				JsonNode individualChanges = node.getValue();
				Iterator<JsonNode> contentIt = individualChanges.iterator();
				while (contentIt.hasNext()) {
					JsonNode newValue = contentIt.next();
					String id = newValue.findValue("id").asText();
					Iterator<Entry<String, JsonNode>> fields = newValue
							.fields();
					ConformanceStatement target = p
							.findOneConformanceStatement(id);
					if (target != null) {
						setEditValues(fields, new BeanWrapperImpl(target));
					} else {
						errors.add(new ProfilePropertySaveError(id,
								"predicate", "Predicate with id=" + id
										+ " not found"));
					}
				}

			}
		}
	}

	private void setMessageValues(Iterator<Entry<String, JsonNode>> nodes)
			throws ProfileSaveException, JsonParseException,
			JsonMappingException, IOException {
		while (nodes.hasNext()) {
			Entry<String, JsonNode> node = nodes.next();
			if (node.getKey().equals(EDIT_NODE)) {
				JsonNode individualChanges = node.getValue();
				Iterator<JsonNode> contentIt = individualChanges.iterator();
				while (contentIt.hasNext()) {
					JsonNode newValue = contentIt.next();
					String id = newValue.findValue("id").asText();
					Iterator<Entry<String, JsonNode>> fields = newValue
							.fields();
					Message target = p.getMessages().findOne(id);
					if (target != null) {
						setEditValues(fields, new BeanWrapperImpl(target));
					} else {
						errors.add(new ProfilePropertySaveError(id, "message",
								"Message with id=" + id + " not found"));
					}
				}

			}
		}
	}

	private void setSegmentRefValues(Iterator<Entry<String, JsonNode>> nodes)
			throws ProfileSaveException, JsonParseException,
			JsonMappingException, IOException {
		while (nodes.hasNext()) {
			Entry<String, JsonNode> node = nodes.next();
			if (node.getKey().equals(EDIT_NODE)) {
				JsonNode individualChanges = node.getValue();
				Iterator<JsonNode> contentIt = individualChanges.iterator();
				while (contentIt.hasNext()) {
					JsonNode newValue = contentIt.next();
					String id = newValue.findValue("id").asText();
					Iterator<Entry<String, JsonNode>> fields = newValue
							.fields();
					SegmentRef target = (SegmentRef) p.getMessages()
							.findOneSegmentRefOrGroup(id);
					if (target != null) {
						setEditValues(fields, new BeanWrapperImpl(target));
					} else {
						errors.add(new ProfilePropertySaveError(id,
								"segmentRef", "SegmentRef with id=" + id
										+ " not found"));
					}
				}

			}
		}
	}

	private void setGroupValues(Iterator<Entry<String, JsonNode>> nodes)
			throws ProfileSaveException, JsonParseException,
			JsonMappingException, IOException {
		while (nodes.hasNext()) {
			Entry<String, JsonNode> node = nodes.next();
			if (node.getKey().equals(EDIT_NODE)) {
				JsonNode individualChanges = node.getValue();

				Iterator<JsonNode> contentIt = individualChanges.iterator();
				while (contentIt.hasNext()) {
					JsonNode newValue = contentIt.next();
					String id = newValue.findValue("id").asText();

					Iterator<Entry<String, JsonNode>> fields = newValue
							.fields();
					Group target = (Group) p.getMessages()
							.findOneSegmentRefOrGroup(id);
					if (target != null) {
						setEditValues(fields, new BeanWrapperImpl(target));
					} else {
						errors.add(new ProfilePropertySaveError(id, "group",
								"Group with id=" + id + " not found"));
					}
				}

			}
		}
	}

	private void setSegmentValues(Iterator<Entry<String, JsonNode>> nodes)
			throws ProfileSaveException, JsonParseException,
			JsonMappingException, IOException {
		while (nodes.hasNext()) {
			Entry<String, JsonNode> node = nodes.next();
			if (node.getKey().equals(EDIT_NODE)) {
				JsonNode individualChanges = node.getValue();
				Iterator<JsonNode> contentIt = individualChanges.iterator();
				while (contentIt.hasNext()) {
					JsonNode newValue = contentIt.next();
					String id = newValue.findValue("id").asText();
					Iterator<Entry<String, JsonNode>> fields = newValue
							.fields();
					Segment target = p.getSegments().findOne(id);
					if (target != null) {
						setEditValues(fields, new BeanWrapperImpl(target));
					} else {
						errors.add(new ProfilePropertySaveError(id, "segment",
								"Segment with id=" + id + " not found"));
					}
				}
			}
		}
	}

	private void setFieldValues(Iterator<Entry<String, JsonNode>> nodes)
			throws ProfileSaveException, JsonParseException,
			JsonMappingException, IOException {
		while (nodes.hasNext()) {
			Entry<String, JsonNode> node = nodes.next();
			if (node.getKey().equals(EDIT_NODE)) {
				JsonNode individualChanges = node.getValue();
				Iterator<JsonNode> contentIt = individualChanges.iterator();
				while (contentIt.hasNext()) {
					JsonNode newValue = contentIt.next();
					String id = newValue.findValue("id").asText();
					Iterator<Entry<String, JsonNode>> fields = newValue
							.fields();
					Field target = p.getSegments().findOneField(id);
					if (target != null) {
						setEditValues(fields, new BeanWrapperImpl(target));
					} else {
						errors.add(new ProfilePropertySaveError(id, "field",
								"Field with id=" + id + " not found"));
					}
				}
			}
		}
	}

	private void setEditValues(Iterator<Entry<String, JsonNode>> fields,
			BeanWrapper wrapper) {

		while (fields.hasNext()) {
			Entry<String, JsonNode> field = fields.next();
			try {
				if (!field.getKey().equals("id")) {
					String key = field.getKey();
					JsonNode value = field.getValue();
					if (key.equals("usage")
							&& StringUtils.isNotBlank(value.asText())) {
						wrapper.setPropertyValue(key,
								Usage.valueOf(value.asText()));
					} else if ((key.equals("min") || key.equals("minLength"))
							&& StringUtils.isNotBlank(value.asText())) {
						wrapper.setPropertyValue(key, value.asInt());
					} else if (key.equals("datatype")) {
						String datatypeId = value.findValue("id").asText();
						Datatype datatype = p.getDatatypes()
								.findOne(datatypeId);
						if (datatype == null) {
							datatype = new Datatype();
							datatype.setId(datatypeId);
						}
						wrapper.setPropertyValue(key, datatype);
					} else if (key.equals("table")) {
						String tableId = value.findValue("id").asText();
						Table table = p.getTables().findOne(tableId);
						if (tableId == null) {
							table = new Table();
							table.setId(tableId);
						}
						wrapper.setPropertyValue(key, tableId);
					} else {
						wrapper.setPropertyValue(key, value.asText());
					}
				}
			} catch (NotWritablePropertyException e) {
				errors.add(new ProfilePropertySaveError(wrapper
						.getPropertyValue("id").toString(), wrapper
						.getPropertyValue("type").toString(), e
						.getPropertyName(), "", EDIT_NODE));
			}
		}
	}

	private void setComponentValues(Iterator<Entry<String, JsonNode>> nodes)
			throws ProfileSaveException, JsonParseException,
			JsonMappingException, IOException {
		while (nodes.hasNext()) {
			Entry<String, JsonNode> node = nodes.next();
			if (node.getKey().equals(EDIT_NODE)) {
				JsonNode individualChanges = node.getValue();
				Iterator<JsonNode> contentIt = individualChanges.iterator();
				while (contentIt.hasNext()) {
					JsonNode newValue = contentIt.next();
					String id = newValue.findValue("id").asText();
					Iterator<Entry<String, JsonNode>> fields = newValue
							.fields();
					Component target = p.getSegments().findOneComponent(id);
					if (target != null) {
						setEditValues(fields, new BeanWrapperImpl(target));
					} else {
						errors.add(new ProfilePropertySaveError(id,
								"component", "Component with id=" + id
										+ " not found"));
					}
				}
			}
		}
	}

	private void setDatatypeValues(Iterator<Entry<String, JsonNode>> nodes)
			throws ProfileSaveException, JsonParseException,
			JsonMappingException, IOException {
		while (nodes.hasNext()) {
			Entry<String, JsonNode> node = nodes.next();
			if (node.getKey().equals(EDIT_NODE)) {
				JsonNode individualChanges = node.getValue();
				Iterator<JsonNode> contentIt = individualChanges.iterator();
				while (contentIt.hasNext()) {
					JsonNode newValue = contentIt.next();
					String id = newValue.findValue("id").asText();
					Iterator<Entry<String, JsonNode>> fields = newValue
							.fields();
					Datatype target = p.getDatatypes().findOne(id);
					if (target != null) {
						setEditValues(fields, new BeanWrapperImpl(target));
					} else {
						errors.add(new ProfilePropertySaveError(id, "datatype",
								"Datatype with id=" + id + " not found"));
					}
				}
			} else if (node.getKey().equals(ADD_NODE)) {
				JsonNode individualChanges = node.getValue();
				Iterator<JsonNode> contentIt = individualChanges.iterator();
				while (contentIt.hasNext()) {
					JsonNode newValue = contentIt.next();
					String id = newValue.findValue("id").asText();
					Datatype dt = toDatatype(newValue);
					newDatatypesMap.put(id, dt);
					p.getDatatypes().addDatatype(dt);
				}
			} else if (node.getKey().equals(DELETE_NODE)) {
				JsonNode individualChanges = node.getValue();
				Iterator<JsonNode> contentIt = individualChanges.iterator();
				while (contentIt.hasNext()) {
					JsonNode newValue = contentIt.next();
					String id = newValue.findValue("id").asText();
					p.getDatatypes().delete(id);
				}
			}
		}
	}

	public JsonNode merge(JsonNode mainNode, JsonNode updateNode) {
		Iterator<String> fieldNames = updateNode.fieldNames();
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

}
