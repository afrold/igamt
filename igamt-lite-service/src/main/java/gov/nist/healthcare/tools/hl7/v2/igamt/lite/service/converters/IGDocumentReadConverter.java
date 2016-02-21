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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ContentDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatypes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DocumentMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DynamicMapping;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Extensibility;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Mapping;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segments;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Stability;
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
public class IGDocumentReadConverter implements Converter<DBObject, IGDocument> {

	public IGDocumentReadConverter() {
		System.out.println("IGDocument Read Converter Created");
	}

	@Override
	public IGDocument convert(DBObject source) {
		IGDocument igd = new IGDocument();
		igd.setAccountId(readLong(source, "accountId"));
		igd.setChildSections(sections((DBObject) source.get("childSections")));
		igd.setComment(readString(source, "comment"));
		igd.setId(readMongoId(source));
		igd.setMetaData(documentMetaData((DBObject) source.get("metaData")));
		igd.setProfile(profile((DBObject) source.get("profile")));
		igd.setScope(IGDocumentScope.valueOf(((String) source.get("scope"))));
		igd.setType(((String) source.get("type")));
		igd.setUsageNote(readString(source, "usageNote"));
		return igd;
	}
	
	private Profile profile(DBObject source) {
		Profile profile = new Profile();
		profile.setId(readMongoId(source));
		profile.setComment(readString(source, "comment"));
		profile.setType(((String) source.get("type")));
		profile.setUsageNote(readString(source, "usageNote"));
		profile.setScope(IGDocumentScope.valueOf(((String) source.get("scope"))));
		profile.setChanges(((String) source.get("changes")));
		profile.setAccountId(readLong(source, "accountId"));
		profile.setMetaData(profileMetaData((DBObject) source.get("metaData")));
		profile.setTables(tables((DBObject) source.get("tables")));
		profile.setDatatypes(datatypes((DBObject) source.get("datatypes"), profile));
		profile.setSegments(segments((DBObject) source.get("segments"), profile));
		profile.setMessages(messages((DBObject) source.get("messages"), profile));
		
		profile.setSectionContents((String) source.get("sectionContents"));
		profile.setSectionDescription((String) source.get("sectionDescription"));
		profile.setSectionPosition((Integer) source.get("sectionPosition"));
		profile.setSectionTitle((String) source.get("sectionTitle"));
		
		profile.setConstraintId((String) source.get("constraintId"));

		Object baseId = source.get("baseId");
		profile.setBaseId(baseId != null ? (String) baseId : null);

		Object sourceId = source.get("sourceId");
		profile.setSourceId(sourceId != null ? (String) sourceId : null);

		return profile;
	}

	private DocumentMetaData documentMetaData(DBObject source) {
		DocumentMetaData metaData = new DocumentMetaData();
		metaData.setDate(source.get("date") != null ? ((String) source.get("date")) : null);
		metaData.setExt(source.get("ext") != null ? ((String) source.get("ext")) : null);
		metaData.setIdentifier(source.get("identifier") != null ? ((String) source.get("identifier")) : null);
		metaData.setOrgName(source.get("orgName") != null ? ((String) source.get("orgName")) : null);
		metaData.setSpecificationName(source.get("specificationName") != null ? ((String) source.get("specificationName")) : null);
		metaData.setStatus(source.get("status") != null ? ((String) source.get("status")) : null);
		metaData.setSubTitle(source.get("subTitle") != null ? ((String) source.get("subTitle")) : null);
		metaData.setTitle(source.get("title") != null ? ((String) source.get("title")) : null);
		metaData.setTopics(source.get("topics") != null ? ((String) source.get("topics")) : null);
		metaData.setType(source.get("type") != null ? ((String) source.get("type")) : null);
		metaData.setVersion(source.get("version") != null ? ((String) source.get("version")) : null);
		return metaData;
	}
	
	private Set<Section> sections(DBObject source) {
		Set<Section> sections = new HashSet<Section>();
		BasicDBList sectionsDBObjects = (BasicDBList) source;
		Iterator<Object> it = sectionsDBObjects.iterator();
		while (it.hasNext()) {
			DBObject childSource = (DBObject) it.next();
			Section targetSection = new Section();
			targetSection.setChildSections(sections((DBObject) childSource.get("childSections")));
			targetSection.setId(readMongoId(childSource));
			targetSection.setSectionContents((String) childSource.get("sectionContents"));
			targetSection.setSectionDescription((String) childSource.get("sectionDescription"));
			targetSection.setSectionPosition((Integer) childSource.get("sectionPosition"));
			targetSection.setSectionTitle((String) childSource.get("sectionTitle"));
			targetSection.setType((String) childSource.get("type"));
			sections.add(targetSection);
		}
		return sections;
	}
	
	private ProfileMetaData profileMetaData(DBObject source) {
		ProfileMetaData metaData = new ProfileMetaData();
		metaData.setName(((String) source.get("name")));
		metaData.setProfileID(((String) source.get("profileID")));
		metaData.setOrgName(((String) source.get("orgName")));
		metaData.setStatus(((String) source.get("status")));
		metaData.setTopics(((String) source.get("topics")));
		metaData.setType(((String) source.get("type")));
		metaData.setSpecificationName(source.get("specificationName") != null ? ((String) source.get("specificationName")) : null);
		metaData.setHl7Version(((String) source.get("hl7Version")));
		metaData.setSchemaVersion(((String) source.get("schemaVersion")));
		metaData.setSubTitle(((String) source.get("subTitle")));
		metaData.setVersion(((String) source.get("version")));
		metaData.setDate(((String) source.get("date")));
		metaData.setExt(source.get("ext") != null ? ((String) source.get("ext")): null);
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
		segments.setId(readMongoId(source));
		segments.setType("segments");
		segments.setSectionContents((String) source.get("sectionContents"));
		segments.setSectionDescription((String) source.get("sectionDescription"));
		segments.setSectionPosition((Integer) source.get("sectionPosition"));
		segments.setSectionTitle((String) source.get("sectionTitle"));
		
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
		seg.setId(readMongoId(source)); 
		seg.setType(((String) source.get("type")));
		seg.setLabel((String) source.get("label"));
		seg.setName(((String) source.get("name")));
		seg.setDescription((String) source.get("description"));
		seg.setComment(readString(source, "comment"));
		seg.setText1(readString(source, "text1"));
		seg.setText2(readString(source, "text2"));
		seg.setSectionPosition((Integer) source.get("sectionPosition"));

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

		DBObject dynamicMappingDBObject = (DBObject) source.get("dynamicMapping");
		if (dynamicMappingDBObject != null) {
			DynamicMapping dyn = dynamicMapping(dynamicMappingDBObject, datatypes);
			seg.setDynamicMapping(dyn);
		}

		return seg;
	}

	private Datatypes datatypes(DBObject source, Profile profile) {
		Datatypes datatypes = new Datatypes();
		datatypes.setId(readMongoId(source));
		datatypes.setType("datatypes");
		
		datatypes.setSectionContents((String) source.get("sectionContents"));
		datatypes.setSectionDescription((String) source.get("sectionDescription"));
		datatypes.setSectionPosition((Integer) source.get("sectionPosition"));
		datatypes.setSectionTitle((String) source.get("sectionTitle"));
		
		BasicDBList datatypesDBObjects = (BasicDBList) source.get("children");
		datatypes.setChildren(new HashSet<Datatype>());
		
		
		if (datatypesDBObjects != null) {
			for (Object childObj : datatypesDBObjects) {
				DBObject child = (DBObject) childObj;
				if (datatypes.findOne(readMongoId(child)) == null) {
					datatypes.addDatatype(datatype(child, datatypes,
							profile.getTables(), datatypesDBObjects));
				}
			}
		}

		return datatypes;
	}

	private Datatype datatype(DBObject source, Datatypes datatypes,
			Tables tables, BasicDBList datatypesDBObjects)
					throws ProfileConversionException {
		Datatype dt = new Datatype();
		dt.setId(readMongoId(source));
		dt.setType(((String) source.get("type")));
		dt.setLabel((String) source.get("label"));
		dt.setName(((String) source.get("name")));
		dt.setHl7Version(((String) source.get("hl7Version")));
		dt.setDescription((String) source.get("description"));
		dt.setComment(readString(source, "comment"));
		dt.setUsageNote(readString(source, "usageNote"));
		dt.setComponents(new ArrayList<Component>());
		dt.setSectionPosition((Integer) source.get("sectionPosition"));
		
		BasicDBList componentObjects = (BasicDBList) source.get("components");
		if (componentObjects != null) {
			List<Component> components = new ArrayList<Component>();
			for (Object compObj : componentObjects) {
				DBObject compObject = (DBObject) compObj;
				Component c = component(compObject, datatypes, tables,
						datatypesDBObjects);
				components.add(c);
			}
			dt.setComponents(components);
		}

		BasicDBList confStsObjects = (BasicDBList) source.get("conformanceStatements");
		if (confStsObjects != null) {
			List<ConformanceStatement> confStatements = new ArrayList<ConformanceStatement>();
			for (Object confStObj : confStsObjects) {
				DBObject confStObject = (DBObject) confStObj;
				ConformanceStatement cs = conformanceStatement(confStObject);
				confStatements.add(cs);
			}
			dt.setConformanceStatements(confStatements);
		}

		BasicDBList predDBObjects = (BasicDBList) source.get("predicates");
		if (predDBObjects != null) {
			List<Predicate> predicates = new ArrayList<Predicate>();
			for (Object predObj : predDBObjects) {
				DBObject predObject = (DBObject) predObj;
				Predicate pred = predicate(predObject);
				predicates.add(pred);
			}
			dt.setPredicates(predicates);
		}

		return dt;
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
		cs.setId(readMongoId(source));
		cs.setConstraintId(((String) source.get("constraintId")));
		cs.setConstraintTarget(((String) source.get("constraintTarget")));
		cs.setDescription((String) source.get("description"));
		cs.setAssertion(((String) source.get("assertion")));
		cs.setReference(reference(((DBObject) source.get("reference"))));
		return cs;
	}

	private Predicate predicate(DBObject source) {
		Predicate p = new Predicate();
		p.setId(readMongoId(source));
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
		p.setId(readMongoId(source));
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
		p.setId(readMongoId(source));
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
		p.setId(readMongoId(source));
		p.setValue(((String) source.get("value")));
		Datatype d = findDatatypeById(((String) source.get("datatype")),
				datatypes);
		if (d == null) {
			throw new ProfileConversionException("Datatype "
					+ ((String) source.get("datatype")) + " not found");
		}
		p.setDatatype(d.getId());
		return p;
	}

	private Field field(DBObject source, Datatypes datatypes, Tables tables) {
		Field f = new Field();
		f.setId(readMongoId(source));
		f.setType(((String) source.get("type")));
		f.setName(((String) source.get("name")));
		f.setComment(readString(source, "comment"));
		f.setMinLength(getMinLength(source));
		f.setMaxLength((String) source.get("maxLength"));
		f.setConfLength(getConfLength(source));
		f.setPosition((Integer) source.get("position"));
		f.setTable(((String) source.get("table")));
		f.setUsage(Usage.valueOf((String) source.get("usage")));
		f.setBindingLocation((String) source.get("bindingLocation"));
		f.setBindingStrength((String) source.get("bindingStrength"));
		f.setItemNo((String) source.get("itemNo"));
		f.setMin((Integer) source.get("min"));
		f.setMax((String) source.get("max"));
		f.setText(readString(source, "text"));
		f.setDatatype(((String) source.get("datatype")));
		return f;
	}

	private Component component(DBObject source, Datatypes datatypes,
			Tables tables, BasicDBList datatypesDBObjects)
					throws ProfileConversionException {
		Component c = new Component();
		c.setId(readMongoId(source));
		c.setType(((String) source.get("type")));
		c.setName(((String) source.get("name")));
		c.setComment(readString(source, "comment"));
		c.setMinLength(getMinLength(source));
		c.setMaxLength((String) source.get("maxLength"));
		c.setConfLength(getConfLength(source));
		c.setPosition((Integer) source.get("position"));
		c.setTable(((String) source.get("table")));
		c.setUsage(Usage.valueOf((String) source.get("usage")));
		c.setBindingLocation((String) source.get("bindingLocation"));
		c.setBindingStrength((String) source.get("bindingStrength"));
		c.setDatatype(((String) source.get("datatype")));
		return c;
	}

	private Tables tables(DBObject source) {
		Tables tables = new Tables();
		tables.setId(readMongoId(source));
		tables.setType("tables");
		tables.setValueSetLibraryIdentifier(((String) source.get("valueSetLibraryIdentifier")));
		tables.setStatus(((String) source.get("status")));
		tables.setValueSetLibraryVersion(((String) source.get("valueSetLibraryVersion")));
		tables.setOrganizationName(((String) source.get("organizationName")));
		tables.setName(((String) source.get("name")));
		tables.setDescription(((String) source.get("description")));
		tables.setDateCreated(((String) source.get("dateCreated")));
		
		tables.setSectionContents((String) source.get("sectionContents"));
		tables.setSectionDescription((String) source.get("sectionDescription"));
		tables.setSectionPosition((Integer) source.get("sectionPosition"));
		tables.setSectionTitle((String) source.get("sectionTitle"));

		tables.setChildren(new HashSet<Table>());

		BasicDBList childrenDBObjects = (BasicDBList) source.get("children");
		if (childrenDBObjects != null)
			for (Object tableObj : childrenDBObjects) {
				DBObject tableObject = (DBObject) tableObj;
				Table table = new Table();
				table.setType("table");
				table.setSectionPosition((Integer) tableObject.get("sectionPosition"));
				
				table.setCodes(new ArrayList<Code>());
				table.setId(readMongoId(tableObject));
				table.setBindingIdentifier(((String) tableObject.get("bindingIdentifier")));
				//Nullity tests added for retro compatibility
				if (tableObject.get("bindingIdentifier") == null){
					table.setBindingIdentifier(((String) tableObject.get("mappingId")));
				}
				table.setName(((String) tableObject.get("name")));
				table.setDescription(((String) tableObject.get("description")));
				//Nullity tests added for retro compatibility
				if (tableObject.get("description") == null){
					table.setDescription((String) tableObject.get("name"));
				}
				table.setOrder(tableObject.get("order") != null ? ((Integer) tableObject.get("order")): 0);
				table.setGroup(((String) tableObject.get("group")));
				table.setVersion(((String) tableObject.get("version")));
				table.setOid(((String) tableObject.get("oid")));
				//Nullity tests added for retro compatibility
				table.setStability(tableObject.get("stability") == null ? Stability.Dynamic : Stability.fromValue((String) tableObject.get("stability")));
				table.setExtensibility(tableObject.get("extensibility") == null ? Extensibility.Open : Extensibility.fromValue((String) tableObject.get("extensibility")));
				table.setContentDefinition(tableObject.get("contentDefinition") == null ? ContentDefinition.Intensional : ContentDefinition.fromValue((String) tableObject.get("contentDefinition")));
				BasicDBList codesDBObjects = (BasicDBList) tableObject
						.get("codes");
				if (codesDBObjects != null)
					for (Object codeObj : codesDBObjects) {
						DBObject codeObject = (DBObject) codeObj;
						Code code = new Code();
						code.setId(readMongoId(codeObject));
						code.setValue(((String) codeObject.get("value")));
						code.setCodeSystem(((String) codeObject.get("codeSystem")));
						code.setCodeSystemVersion(((String) codeObject.get("codeSystemVersion")));
						code.setCodeUsage(((String) codeObject.get("codeUsage")));
						code.setType(((String) codeObject.get("type")));
						code.setLabel(((String) codeObject.get("label")));
						code.setComments(readString(codeObject, "comments"));
						//Added for retro compatibility
						if (codeObject.get("value") == null){
							code.setValue(((String) codeObject.get("code")));
						}
						if (codeObject.get("codeSystem") == null){
							code.setCodeSystem((String) codeObject.get("codesys"));
						}
						table.addCode(code);
					}

				tables.addTable(table);

			}

		return tables;

	}

	private Messages messages(DBObject source, Profile profile) {
		Messages messages = new Messages();
		messages.setId(readMongoId(source));
		messages.setType("messages");
		messages.setSectionContents((String) source.get("sectionContents"));
		messages.setSectionDescription((String) source.get("sectionDescription"));
		messages.setSectionPosition((Integer) source.get("sectionPosition"));
		messages.setSectionTitle((String) source.get("sectionTitle"));
		
		
		BasicDBList messagesDBObjects = (BasicDBList) source.get("children");
		messages.setChildren(new HashSet<Message>());
		for (Object childObj : messagesDBObjects) {
			Message message = new Message();
			DBObject child = (DBObject) childObj;
			message.setId(readMongoId(child));
			message.setName((String) child.get("name"));
			if (child.get("name") == null){
				message.setName((String) child.get("messageType") + "_" + (String) child.get("event"));
			}
			message.setMessageType((String) child.get("messageType"));
			message.setComment(readString(child, "comment"));
			message.setDescription((String) child.get("description"));
			message.setEvent((String) child.get("event"));
			message.setIdentifier((String) child.get("identifier"));
			message.setPosition((Integer) child.get("position"));
			message.setStructID((String) child.get("structID"));
			message.setType((String) child.get("type"));
			message.setSectionPosition((Integer) child.get("sectionPosition"));
			message.setMessageID((String) child.get("messageID"));
			
			
			BasicDBList confStsObjects = (BasicDBList) child.get("conformanceStatements");
			if (confStsObjects != null) {
				List<ConformanceStatement> confStatements = new ArrayList<ConformanceStatement>();
				for (Object confStObj : confStsObjects) {
					DBObject confStObject = (DBObject) confStObj;
					ConformanceStatement cs = conformanceStatement(confStObject);
					confStatements.add(cs);
				}
				message.setConformanceStatements(confStatements);
			}

			BasicDBList predDBObjects = (BasicDBList) child.get("predicates");
			if (predDBObjects != null) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				for (Object predObj : predDBObjects) {
					DBObject predObject = (DBObject) predObj;
					Predicate pred = predicate(predObject);
					predicates.add(pred);
				}
				message.setPredicates(predicates);
			}

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
		segRef.setId(readMongoId(source));
		segRef.setType(((String) source.get("type")));
		segRef.setUsage(Usage.valueOf(((String) source.get("usage"))));
		segRef.setComment(readString(source, "comment"));
		segRef.setPosition((Integer) source.get("position"));
		segRef.setMin((Integer) source.get("min"));
		segRef.setMax((String) source.get("max"));
		segRef.setRef((String) source.get("ref"));
		return segRef;
	}

	private Group group(DBObject source, Segments segments) {
		Group group = new Group();
		group.setId(readMongoId(source));
		group.setType(((String) source.get("type")));
		group.setUsage(Usage.valueOf(((String) source.get("usage"))));
		group.setComment(readString(source, "comment"));
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
		
		
		BasicDBList confStsObjects = (BasicDBList) source.get("conformanceStatements");
		if (confStsObjects != null) {
			List<ConformanceStatement> confStatements = new ArrayList<ConformanceStatement>();
			for (Object confStObj : confStsObjects) {
				DBObject confStObject = (DBObject) confStObj;
				ConformanceStatement cs = conformanceStatement(confStObject);
				confStatements.add(cs);
			}
			group.setConformanceStatements(confStatements);
		}

		BasicDBList predDBObjects = (BasicDBList) source.get("predicates");
		if (predDBObjects != null) {
			List<Predicate> predicates = new ArrayList<Predicate>();
			for (Object predObj : predDBObjects) {
				DBObject predObject = (DBObject) predObj;
				Predicate pred = predicate(predObject);
				predicates.add(pred);
			}
			group.setPredicates(predicates);
		}
		
		
		
		
		return group;
	}

	@SuppressWarnings("unused")
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
			Datatype d = datatypes.findOne(id);
			if (d != null) {
				return d;
			}
		}

		return null;
	}

	private String readMongoId(DBObject source){
		if ( source.get("_id") != null){
			if (source.get("_id") instanceof ObjectId){
				return ((ObjectId) source.get("_id")).toString();
			} else {
				return (String) source.get("_id");
			}
		} else if ( source.get("id") != null){
			if (source.get("id") instanceof ObjectId){
				return ((ObjectId) source.get("id")).toString();
			} else {
				return (String) source.get("id");
			}
		}
		return null;
	}

	private Long readLong(DBObject source, String tag){
		if ( source.get(tag) != null){
			if (source.get(tag) instanceof Integer){
				return Long.valueOf((Integer) source.get(tag));
			} else if (source.get(tag) instanceof String) {
				return Long.valueOf((String)source.get(tag));
			} else if (source.get(tag) instanceof Long) {
				return Long.valueOf((Long)source.get(tag));
			}
		}
		return Long.valueOf(0);
	}

	private String readString(DBObject source, String tag){
		if ( source.get(tag) != null){
				return String.valueOf((String) source.get(tag));
			}
		return "";
	} 
	
	
	private Integer getMinLength(DBObject source){
		return ((Integer) source.get("minLength") == -1 ? 0:((Integer) source.get("minLength")));
	} 
	
	private String getConfLength(DBObject source){
		return "-1".equals((String) source.get("confLength")) ? "":(String) source.get("confLength");
	} 
	

	// private DBObject findDatatypeById(String id, BasicDBList datatypes)
	// throws ProfileConversionException {
	// if (datatypes != null) {
	// for (Object d : datatypes) {
	// DBObject dbObj = (DBObject) d;
	// String di = ((ObjectId) dbObj.get("_id")).toString();
	// if (id.equals(di)) {
	// return dbObj;
	// }
	// }
	// }
	// throw new ProfileConversionException("Datatype DBObject " + id
	// + " not found");
	// }
	//
	// private Table findTableById(String id, Tables tables) {
	// if (id == null) {
	// return null;
	// }
	// if (tables != null) {
	// for (Table t : tables.getChildren()) {
	// if (t.getId().equals(id)) {
	// return t;
	// }
	// }
	// }
	// throw new IllegalArgumentException("Table " + id
	// + " not found in the profile");
	// }

}
