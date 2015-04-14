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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Case;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatypes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DynamicMapping;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Mapping;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segments;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Tables;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Reference;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileConversionException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

/**
 * @author Harold Affo (harold.affo@nist.gov) Mar 31, 2015
 */
@ReadingConverter
public class ProfileReadConverter implements Converter<DBObject, Profile> {

	@Override
	public Profile convert(DBObject source) {
		Profile profile = new Profile();
		profile.setId(((ObjectId) source.get("_id")).toString());
		profile.setComment(((String) source.get("comment")));
		profile.setType(((String) source.get("type")));
		profile.setUsageNote(((String) source.get("usageNote")));
		profile.setScope(ProfileScope.valueOf(((String) source.get("scope"))));
		profile.setVersion(((Integer) source.get("version")));
		profile.setChanges(((String) source.get("changes")));
		profile.setAccountId(((Long) source.get("accountId")));
		profile.setMetaData(metaData((DBObject) source.get("metaData")));
		profile.setTables(tables((DBObject) source.get("tables")));
		profile.setDatatypes(datatypes((DBObject) source.get("datatypes"),
				profile));
		profile.setSegments(segments((DBObject) source.get("segments"), profile));
		profile.setMessages(messages((DBObject) source.get("messages"), profile));
		return profile;

	}

	private ProfileMetaData metaData(DBObject source) {
		ProfileMetaData metaData = new ProfileMetaData();
		metaData.setName(((String) source.get("name")));
		metaData.setIdentifier(((String) source.get("identifier")));
		metaData.setOrgName(((String) source.get("orgName")));
		metaData.setStatus(((String) source.get("status")));
		metaData.setTopics(((String) source.get("topics")));
		metaData.setType(((String) source.get("type")));
		metaData.setHl7Version(((String) source.get("hl7Version")));
		metaData.setSchemaVersion(((String) source.get("schemaVersion")));
		metaData.setSubTitle(((String) source.get("subTitle")));
		metaData.setVersion(((String) source.get("version")));
		metaData.setDate(((String) source.get("datew")));

		Set<String> encodings = new HashSet<String>();
		Object encodingObj = source.get("encodings");
		BasicDBList encodingDBObjects = (BasicDBList) encodingObj;
		Iterator<Object> it = encodingDBObjects.iterator();
		while (it.hasNext()) {
			encodings.add((String) it.next());
		}
		metaData.setEncodings(encodings);
		return metaData;
	}

	private Segments segments(DBObject source, Profile profile) {
		Segments segments = new Segments();
		segments.setId(((ObjectId) source.get("_id")).toString());
		BasicDBList segmentsDBObjects = (BasicDBList) source.get("children");
		if (segmentsDBObjects != null) {
			Set<Segment> children = new HashSet<Segment>();
			for (Object child : segmentsDBObjects) {
				children.add(segment((DBObject) child, profile.getDatatypes(),
						profile.getTables()));
			}
			segments.setChildren(children);
		}
		return segments;
	}

	private Segment segment(DBObject source, Datatypes datatypes, Tables tables) {
		Segment seg = new Segment();
		seg.setId(((ObjectId) source.get("_id")).toString());
		seg.setType(((String) source.get("type")));
		seg.setLabel((String) source.get("label"));
		seg.setName(((String) source.get("name")));
		seg.setDescription((String) source.get("description"));
		seg.setComment((String) source.get("comment"));
		seg.setText1((String) source.get("text1"));
		seg.setText2((String) source.get("text2"));

		BasicDBList fieldObjects = (BasicDBList) source.get("fields");
		if (fieldObjects != null) {
			List<Field> fields = new ArrayList<Field>();
			for (Object fieldObject : fieldObjects) {
				Field f = field((DBObject) fieldObject, datatypes, tables);
				fields.add(f);
			}
			seg.setFields(fields);
		}

		BasicDBList confStsObjects = (BasicDBList) source
				.get("conformanceStatements");
		if (confStsObjects != null) {
			List<ConformanceStatement> confStatements = new ArrayList<ConformanceStatement>();
			for (Object confStObject : confStsObjects) {
				ConformanceStatement cs = conformanceStatement((DBObject) confStObject);
				confStatements.add(cs);
			}
			seg.setConformanceStatements(confStatements);
		}

		BasicDBList predDBObjects = (BasicDBList) source.get("predicates");
		if (predDBObjects != null) {
			List<Predicate> predicates = new ArrayList<Predicate>();
			for (Object predObj : predDBObjects) {
				DBObject predObject = (DBObject) predObj;
				Predicate pred = predicate(predObject);
				predicates.add(pred);
			}
			seg.setPredicates(predicates);
		}

		BasicDBList dynamicMappingsDBObjects = (BasicDBList) source
				.get("dynamicMappings");
		if (dynamicMappingsDBObjects != null) {
			List<DynamicMapping> dynamicMappings = new ArrayList<DynamicMapping>();
			for (Object dynObj : dynamicMappingsDBObjects) {
				DBObject dynObject = (DBObject) dynObj;
				DynamicMapping dyn = dynamicMapping(dynObject, datatypes);
				dynamicMappings.add(dyn);
			}
			seg.setDynamicMappings(dynamicMappings);
		}

		return seg;
	}

	private Datatypes datatypes(DBObject source, Profile profile) {
		Datatypes datatypes = new Datatypes();
		datatypes.setId(((ObjectId) source.get("_id")).toString());
		BasicDBList datatypesDBObjects = (BasicDBList) source.get("children");
		datatypes.setChildren(new HashSet<Datatype>());
		if (datatypesDBObjects != null) {
			for (Object childObj : datatypesDBObjects) {
				DBObject child = (DBObject) childObj;
				datatypes.addDatatype(datatype(child, datatypes,
						profile.getTables(), datatypesDBObjects));
			}
		}

		return datatypes;
	}

	private Datatype datatype(DBObject source, Datatypes datatypes,
			Tables tables, BasicDBList datatypesDBObjects)
			throws ProfileConversionException {
		Datatype segRef = new Datatype();
		segRef.setId(((ObjectId) source.get("_id")).toString());
		segRef.setType(((String) source.get("type")));
		segRef.setLabel((String) source.get("label"));
		segRef.setName(((String) source.get("name")));
		segRef.setDescription((String) source.get("description"));
		segRef.setComment((String) source.get("comment"));
		segRef.setUsageNote((String) source.get("usageNote"));
		segRef.setComponents(new ArrayList<Component>());
		BasicDBList componentObjects = (BasicDBList) source.get("components");
		if (componentObjects != null) {
			List<Component> components = new ArrayList<Component>();
			for (Object compObj : componentObjects) {
				DBObject compObject = (DBObject) compObj;
				Component c = component(compObject, datatypes, tables,
						datatypesDBObjects);
				components.add(c);
			}
			segRef.setComponents(components);
		}

		BasicDBList confStsObjects = (BasicDBList) source
				.get("conformanceStatements");
		if (confStsObjects != null) {
			List<ConformanceStatement> confStatements = new ArrayList<ConformanceStatement>();
			for (Object confStObj : confStsObjects) {
				DBObject confStObject = (DBObject) confStObj;
				ConformanceStatement cs = conformanceStatement(confStObject);
				confStatements.add(cs);
			}
			segRef.setConformanceStatements(confStatements);
		}

		BasicDBList predDBObjects = (BasicDBList) source.get("predicates");
		if (predDBObjects != null) {
			List<Predicate> predicates = new ArrayList<Predicate>();
			for (Object predObj : predDBObjects) {
				DBObject predObject = (DBObject) predObj;
				Predicate pred = predicate(predObject);
				predicates.add(pred);
			}
			segRef.setPredicates(predicates);
		}

		return segRef;
	}

	private Reference reference(DBObject source) {
		if (source != null) {
			Reference reference = new Reference();
			reference.setChapter(((String) source.get("chapter")));
			reference.setSection(((String) source.get("section")));
			reference.setPage((Integer) source.get("page"));
			reference.setUrl((String) source.get("url"));
			return reference;
		}
		return null;
	}

	private ConformanceStatement conformanceStatement(DBObject source) {
		ConformanceStatement cs = new ConformanceStatement();
		cs.setId(((ObjectId) source.get("_id")).toString());
		cs.setConstraintId(((String) source.get("constraintId")));
		cs.setConstraintTarget(((String) source.get("constraintTarget")));
		cs.setDescription((String) source.get("description"));
		cs.setAssertion(((String) source.get("assertion")));
		cs.setReference(reference(((DBObject) source.get("reference"))));
		return cs;
	}

	private Predicate predicate(DBObject source) {
		Predicate p = new Predicate();
		p.setId(((ObjectId) source.get("_id")).toString());
		p.setConstraintId(((String) source.get("constraintId")));
		p.setConstraintTarget(((String) source.get("constraintTarget")));
		p.setDescription((String) source.get("description"));
		p.setAssertion(((String) source.get("assertion")));
		p.setReference(reference(((DBObject) source.get("reference"))));
		p.setFalseUsage(Usage.valueOf(((String) source.get("falseUsage"))));
		p.setTrueUsage(Usage.valueOf(((String) source.get("trueUsage"))));
		return p;
	}

	private DynamicMapping dynamicMapping(DBObject source, Datatypes datatypes) {
		DynamicMapping p = new DynamicMapping();
		p.setId(((ObjectId) source.get("_id")).toString());
		p.setMin(((Integer) source.get("min")));
		p.setMax(((String) source.get("max")));
		p.setPosition(((Integer) source.get("position")));
		BasicDBList mappingsDBObjects = (BasicDBList) source.get("mappings");
		if (mappingsDBObjects != null) {
			List<Mapping> mappings = new ArrayList<Mapping>();
			for (Object compObj : mappingsDBObjects) {
				DBObject compObject = (DBObject) compObj;
				Mapping m = mapping(compObject, datatypes);
				mappings.add(m);
			}
			p.setMappings(mappings);
		}
		return p;
	}

	private Mapping mapping(DBObject source, Datatypes datatypes) {
		Mapping p = new Mapping();
		p.setId(((ObjectId) source.get("_id")).toString());
		p.setReference(((Integer) source.get("reference")));
		p.setPosition(((Integer) source.get("position")));
		BasicDBList mappingsDBObjects = (BasicDBList) source.get("cases");
		if (mappingsDBObjects != null) {
			List<Case> cases = new ArrayList<Case>();
			for (Object compObj : mappingsDBObjects) {
				DBObject compObject = (DBObject) compObj;
				Case m = toCase(compObject, datatypes);
				cases.add(m);
			}
			p.setCases(cases);
		}

		return p;
	}

	private Case toCase(DBObject source, Datatypes datatypes) {
		Case p = new Case();
		p.setId(((ObjectId) source.get("_id")).toString());
		p.setValue(((String) source.get("value")));
		Datatype d = findDatatypeById(((String) source.get("datatype")),
				datatypes);
		if (d == null) {
			throw new ProfileConversionException("Datatype "
					+ ((String) source.get("datatype")) + " not found");
		}
		p.setDatatype(d);
		return p;
	}

	private Field field(DBObject source, Datatypes datatypes, Tables tables) {
		Field f = new Field();
		f.setId(((String) source.get("_id")));
		f.setType(((String) source.get("type")));
		f.setName(((String) source.get("name")));
		f.setComment((String) source.get("comment"));
		f.setMinLength(((Integer) source.get("minLength")));
		f.setMaxLength((String) source.get("maxLength"));
		f.setConfLength((String) source.get("confLength"));
		f.setPosition((Integer) source.get("position"));
		f.setTable(source.get("table") != null ? findTableById(
				((String) source.get("table")), tables) : null);
		f.setUsage(Usage.valueOf((String) source.get("usage")));
		f.setBindingLocation((String) source.get("bindingLocation"));
		f.setBindingStrength((String) source.get("bindingStrength"));
		f.setItemNo((String) source.get("itemNo"));
		f.setMin((Integer) source.get("min"));
		f.setMax((String) source.get("max"));
		f.setText((String) source.get("text"));
		Datatype d = findDatatypeById(((String) source.get("datatype")),
				datatypes);
		if (d == null) {
			throw new ProfileConversionException("Datatype "
					+ (String) source.get("datatype") + " not found");
		}
		f.setDatatype(d);
		return f;
	}

	private Component component(DBObject source, Datatypes datatypes,
			Tables tables, BasicDBList datatypesDBObjects)
			throws ProfileConversionException {
		Component c = new Component();
		c.setId(((String) source.get("_id")));
		c.setType(((String) source.get("type")));
		c.setName(((String) source.get("name")));
		c.setComment((String) source.get("comment"));
		c.setMinLength(((Integer) source.get("minLength")));
		c.setMaxLength((String) source.get("maxLength"));
		c.setConfLength((String) source.get("confLength"));
		c.setPosition((Integer) source.get("position"));
		c.setTable(source.get("table") != null ? findTableById(
				((String) source.get("table")), tables) : null);
		c.setUsage(Usage.valueOf((String) source.get("usage")));
		c.setBindingLocation((String) source.get("bindingLocation"));
		c.setBindingStrength((String) source.get("bindingStrength"));
		String datatypeId = ((String) source.get("datatype"));
		Datatype d = findDatatypeById(datatypeId, datatypes);

		if (d == null) {
			DBObject dObject = findDatatypeById(datatypeId, datatypesDBObjects);
			d = datatype(dObject, datatypes, tables, datatypesDBObjects);
			datatypes.addDatatype(d);
		}
		c.setDatatype(d);
		return c;
	}

	private Tables tables(DBObject source) {
		Tables tables = new Tables();
		tables.setId(((ObjectId) source.get("_id")).toString());
		tables.setTableLibraryIdentifier(((String) source
				.get("tableLibraryIdentifier")));
		tables.setStatus(((String) source.get("status")));
		tables.setTableLibraryVersion(((String) source
				.get("tableLibraryVersion")));
		tables.setOrganizationName(((String) source.get("organizationName")));
		tables.setName(((String) source.get("name")));
		tables.setDescription(((String) source.get("description")));

		tables.setChildren(new HashSet<Table>());

		BasicDBList childrenDBObjects = (BasicDBList) source.get("children");
		if (childrenDBObjects != null)
			for (Object tableObj : childrenDBObjects) {
				DBObject tableObject = (DBObject) tableObj;
				Table table = new Table();
				table.setCodes(new ArrayList<Code>());
				table.setId(((ObjectId) tableObject.get("_id")).toString());
				table.setMappingAlternateId(((String) tableObject
						.get("mappingAlternateId")));
				table.setMappingId(((String) tableObject.get("mappingId")));
				table.setName(((String) tableObject.get("name")));
				table.setVersion(((String) tableObject.get("version")));
				table.setName(((String) tableObject.get("codesys")));
				table.setOid(((String) tableObject.get("oid")));
				table.setTableType(((String) tableObject.get("tableType")));
				table.setStability(((String) tableObject.get("stability")));
				table.setExtensibility(((String) tableObject
						.get("extensibility")));
				BasicDBList codesDBObjects = (BasicDBList) tableObject
						.get("codes");
				if (codesDBObjects != null)
					for (Object codeObj : codesDBObjects) {
						DBObject codeObject = (DBObject) codeObj;
						Code code = new Code();
						code.setId(((ObjectId) codeObject.get("_id"))
								.toString());
						code.setCode(((String) codeObject.get("code")));
						code.setCodesys((((String) codeObject.get("codeSys"))));
						code.setCodeUsage(((String) codeObject.get("codeUsage")));
						code.setLabel(((String) codeObject.get("label")));
						code.setSource(((String) codeObject.get("source")));
						code.setType(((String) codeObject.get("type")));
						table.addCode(code);
					}

				tables.addTable(table);

			}

		return tables;

	}

	private Messages messages(DBObject source, Profile profile) {
		Messages messages = new Messages();
		messages.setId(((ObjectId) source.get("_id")).toString());
		BasicDBList messagesDBObjects = (BasicDBList) source.get("children");
		messages.setChildren(new HashSet<Message>());
		for (Object childObj : messagesDBObjects) {
			Message message = new Message();
			DBObject child = (DBObject) childObj;
			message.setId(((ObjectId) child.get("_id")).toString());
			message.setMessageType((String) child.get("messageType"));
			message.setComment((String) child.get("comment"));
			message.setDescription((String) child.get("description"));
			message.setEvent((String) child.get("event"));
			message.setIdentifier((String) child.get("identifier"));
			message.setPosition((Integer) child.get("position"));
			message.setStructID((String) child.get("structID"));
			message.setType((String) child.get("type"));
			message.setVersion((String) child.get("version"));
			message.setDate((String) child.get("date"));
			message.setOid((String) child.get("oid"));

			BasicDBList segmentRefOrGroupDBObjects = (BasicDBList) child
					.get("children");
			for (Object segmentRefOrGroupObject : segmentRefOrGroupDBObjects) {
				DBObject segmentRefOrGroupDBObject = (DBObject) segmentRefOrGroupObject;
				String type = (String) segmentRefOrGroupDBObject.get("type");
				if (Constant.SEGMENTREF.equals(type)) {
					SegmentRef segRef = segmentRef(segmentRefOrGroupDBObject,
							profile.getSegments());
					message.addSegmentRefOrGroup(segRef);
				} else {
					Group group = group(segmentRefOrGroupDBObject,
							profile.getSegments());
					message.addSegmentRefOrGroup(group);
				}
			}
			messages.getChildren().add(message);

		}
		return messages;
	}

	private SegmentRef segmentRef(DBObject source, Segments segments) {
		SegmentRef segRef = new SegmentRef();
		segRef.setId(((String) source.get("_id")));
		segRef.setType(((String) source.get("type")));
		segRef.setUsage(Usage.valueOf(((String) source.get("usage"))));
		segRef.setComment(((String) source.get("comment")));
		segRef.setPosition((Integer) source.get("position"));
		segRef.setMin((Integer) source.get("min"));
		segRef.setMax((String) source.get("max"));
		segRef.setRef(findSegmentById(((String) source.get("ref")), segments));
		return segRef;
	}

	private Group group(DBObject source, Segments segments) {
		Group group = new Group();
		group.setId(((ObjectId) source.get("_id")).toString());
		group.setType(((String) source.get("type")));
		group.setUsage(Usage.valueOf(((String) source.get("usage"))));
		group.setComment(((String) source.get("comment")));
		group.setPosition((Integer) source.get("position"));
		group.setMin((Integer) source.get("min"));
		group.setMax((String) source.get("max"));
		group.setName(((String) source.get("name")));
		BasicDBList segmentRefOrGroupDBObjects = (BasicDBList) source
				.get("children");

		List<SegmentRefOrGroup> segOrGroups = new ArrayList<SegmentRefOrGroup>();

		for (Object segmentRefOrGroupObject : segmentRefOrGroupDBObjects) {
			DBObject segmentRefOrGroupDBObject = (DBObject) segmentRefOrGroupObject;
			String type = (String) segmentRefOrGroupDBObject.get("type");
			if (Constant.SEGMENTREF.equals(type)) {
				SegmentRef segRef = segmentRef(segmentRefOrGroupDBObject,
						segments);
				segOrGroups.add(segRef);
			} else {
				Group subGroup = group(segmentRefOrGroupDBObject, segments);
				segOrGroups.add(subGroup);
			}
		}
		group.setChildren(segOrGroups);
		return group;
	}

	private Segment findSegmentById(String id, Segments segments) {
		if (segments != null) {
			for (Segment s : segments.getChildren()) {
				if (s.getId().equals(id)) {
					return s;
				}
			}
		}
		throw new IllegalArgumentException("Segment " + id
				+ " not found in the profile");
	}

	private Datatype findDatatypeById(String id, Datatypes datatypes) {
		if (datatypes != null) {
			for (Datatype d : datatypes.getChildren()) {
				if (d.getId().equals(id)) {
					return d;
				}
			}
		}

		return null;
	}

	private DBObject findDatatypeById(String id, BasicDBList datatypes)
			throws ProfileConversionException {
		if (datatypes != null) {
			for (Object d : datatypes) {
				DBObject dbObj = (DBObject) d;
				String di = ((ObjectId) dbObj.get("_id")).toString();
				if (id.equals(di)) {
					return dbObj;
				}
			}
		}
		throw new ProfileConversionException("Datatype DBObject " + id
				+ " not found");
	}

	private Table findTableById(String id, Tables tables) {
		if (id == null) {
			return null;
		}
		if (tables != null) {
			for (Table t : tables.getChildren()) {
				if (t.getId().equals(id)) {
					return t;
				}
			}
		}
		throw new IllegalArgumentException("Table " + id
				+ " not found in the profile");
	}

}
