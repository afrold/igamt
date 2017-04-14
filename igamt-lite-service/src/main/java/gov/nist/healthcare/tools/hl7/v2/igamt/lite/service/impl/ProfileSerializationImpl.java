/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Case;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfileStructure;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DocumentMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DynamicMapping;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DynamicMappingItem;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Mapping;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetOrSingleCodeBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByID;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByName;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByNameOrByID;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintColumnDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintIFColumnData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintTHENColumnData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraints;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Context;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.CompositeProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ConstraintsSerialization;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileSerialization;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableSerialization;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.SerializationUtil;
import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.NodeFactory;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

@Service
public class ProfileSerializationImpl implements ProfileSerialization {
	private Logger log = LoggerFactory.getLogger(ProfileSerializationImpl.class);

	@Autowired
	private DatatypeService datatypeService;

	@Autowired
	private SegmentService segmentService;

	@Autowired
	private TableService tableService;

	@Autowired
	private TableSerialization tableSerializationService;
	
	@Autowired
	private CompositeProfileService compositeProfileService;

	@Autowired
	private ConstraintsSerialization constraintsSerializationService;

	@Autowired
	private SerializationUtil serializationUtil;

	private HashMap<String, Datatype> datatypesMap;
	private HashMap<String, Segment> segmentsMap;
	private Constraints conformanceStatement;
	private Constraints predicates;

	@Override
	public Profile deserializeXMLToProfile(String xmlContentsProfile, String xmlValueSet, String xmlConstraints) {
		Document profileDoc = this.stringToDom(xmlContentsProfile);
		Element elmConformanceProfile = (Element) profileDoc.getElementsByTagName("ConformanceProfile").item(0);

		// Read Profile Meta
		Profile profile = new Profile();
		profile.setMetaData(new ProfileMetaData());
		this.deserializeMetaData(profile, elmConformanceProfile);
		this.deserializeEncodings(profile, elmConformanceProfile);

		// Read Profile Libs
		profile.setSegmentLibrary(new SegmentLibrary());
		profile.setDatatypeLibrary(new DatatypeLibrary());

		profile.setTableLibrary(tableSerializationService.deserializeXMLToTableLibrary(xmlValueSet));

		this.conformanceStatement = constraintsSerializationService
				.deserializeXMLToConformanceStatements(xmlConstraints);
		this.predicates = constraintsSerializationService.deserializeXMLToPredicates(xmlConstraints);
		profile.setConstraintId(this.releaseConstraintId(xmlConstraints));

		this.constructDatatypesMap((Element) elmConformanceProfile.getElementsByTagName("Datatypes").item(0), profile);

		DatatypeLibrary datatypes = new DatatypeLibrary();
		for (String key : datatypesMap.keySet()) {
			Datatype d = datatypeService.save(datatypesMap.get(key));
			DatatypeLink link = new DatatypeLink();
			link.setExt(key.replace(d.getName(), ""));
			link.setId(d.getId());
			link.setName(d.getName());

			datatypes.addDatatype(link);
		}
		profile.setDatatypeLibrary(datatypes);

		this.segmentsMap = this.constructSegmentsMap(
				(Element) elmConformanceProfile.getElementsByTagName("Segments").item(0), profile);

		SegmentLibrary segments = new SegmentLibrary();
		for (String key : segmentsMap.keySet()) {
			Segment s = segmentService.save(segmentsMap.get(key));
			SegmentLink link = new SegmentLink();
			link.setId(s.getId());
			link.setExt(key.replace(s.getName(), ""));
			link.setName(s.getName());
			segments.addSegment(link);
		}
		profile.setSegmentLibrary(segments);

		// Read Profile Messages
		this.deserializeMessages(profile, elmConformanceProfile);

		return profile;
	}

	@Override
	public Profile deserializeXMLToProfile(nu.xom.Document docProfile, nu.xom.Document docValueSet,
			nu.xom.Document docConstraints) {
		return this.deserializeXMLToProfile(docProfile.toXML(), docValueSet.toXML(), docConstraints.toXML());
	}

	private String serializeProfileDisplayToXML(Profile profile, DocumentMetaData metadata, Date dateUpdated) {
		return this.serializeProfileDisplayToDoc(profile, metadata).toXML();
	}

	private String serializeProfileGazelleToXML(Profile profile) {
		return this.serializeProfileGazelleToDoc(profile).toXML();
	}

	@Override
	public String serializeDatatypeLibraryToXML(DatatypeLibrary datatypeLibrary) {
		return this.serializeDatatypeLibraryToDoc(datatypeLibrary).toXML();
	}

	@Override
	public nu.xom.Document serializeDatatypeLibraryToDoc(DatatypeLibrary datatypeLibrary) {
		nu.xom.Element e = new nu.xom.Element("DatatypeLibrary");
		e.addAttribute(new Attribute("ID", datatypeLibrary.getMetaData().getDatatypeLibId()));
		nu.xom.Element elmMetaData = new nu.xom.Element("MetaData");
		elmMetaData.addAttribute(new Attribute("Name", datatypeLibrary.getMetaData().getName()));
		elmMetaData.addAttribute(new Attribute("OrgName", datatypeLibrary.getMetaData().getOrgName()));
		elmMetaData.addAttribute(new Attribute("Version", datatypeLibrary.getMetaData().getVersion()));
		elmMetaData.addAttribute(new Attribute("Date", datatypeLibrary.getMetaData().getDate()));
		e.appendChild(elmMetaData);

		TableLibrary tables = new TableLibrary();
		// tables.setChildren(datatypeLibrary.get);

		nu.xom.Element ds = new nu.xom.Element("Datatypes");
		for (DatatypeLink link : datatypeLibrary.getChildren()) {
			Datatype d = datatypeService.findById(link.getId());
			ds.appendChild(this.serializeDatatypeWithConstraints(link, d, tables, datatypeLibrary));
		}
		e.appendChild(ds);

		nu.xom.Document doc = new nu.xom.Document(e);

		return doc;
	}

	private nu.xom.Document serializeProfileDisplayToDoc(Profile profile, DocumentMetaData metadata) {
		nu.xom.Element e = new nu.xom.Element("ConformanceProfile");
		this.serializeProfileMetaData(e, profile, metadata);

		for (Message m : profile.getMessages().getChildren()) {
			e.appendChild(this.serializeDisplayMessage(m, profile));
		}
		e.appendChild(
				tableSerializationService.serializeTableLibraryToElement(profile, metadata, profile.getDateUpdated()));
		nu.xom.Document doc = new nu.xom.Document(e);
		return doc;
	}

	private nu.xom.Document serializeProfileGazelleToDoc(Profile profile) {
		nu.xom.Element e = new nu.xom.Element("HL7v2xConformanceProfile");
		e.addAttribute(new Attribute("HL7Version",
				serializationUtil.str(profile.getMetaData().getHl7Version().replaceAll("\\.", "-"))));
		e.addAttribute(new Attribute("ProfileType", serializationUtil.str(profile.getMetaData().getType())));
		// e.addAttribute(new Attribute("Identifier",
		// serializationUtil.str(profile.getMetaData().getProfileID())));

		nu.xom.Element metadataElm = new nu.xom.Element("MetaData");
		metadataElm.addAttribute(new Attribute("Name", serializationUtil.str(profile.getMetaData().getName())));
		metadataElm.addAttribute(new Attribute("OrgName", serializationUtil.str(profile.getMetaData().getOrgName())));
		metadataElm.addAttribute(new Attribute("Version", serializationUtil.str(profile.getMetaData().getVersion())));
		// metadataElm.addAttribute(new Attribute("Status",
		// serializationUtil.str(profile.getMetaData().getStatus())));
		// metadataElm.addAttribute(new Attribute("Topics",
		// serializationUtil.str(profile.getMetaData().getTopics())));
		e.appendChild(metadataElm);

		nu.xom.Element impNoteElm = new nu.xom.Element("ImpNote");
		impNoteElm.appendChild(serializationUtil.str(profile.getMetaData().getName()));
		e.appendChild(impNoteElm);

		nu.xom.Element useCaseElm = new nu.xom.Element("UseCase");
		e.appendChild(useCaseElm);

		nu.xom.Element encodingsElm = new nu.xom.Element("Encodings");
		nu.xom.Element encodingElm = new nu.xom.Element("Encoding");
		encodingElm.appendChild("ER7");
		encodingsElm.appendChild(encodingElm);
		e.appendChild(encodingsElm);

		nu.xom.Element dynamicDefElm = new nu.xom.Element("DynamicDef");
		dynamicDefElm.addAttribute(new Attribute("AccAck", "NE"));
		dynamicDefElm.addAttribute(new Attribute("AppAck", "AL"));
		dynamicDefElm.addAttribute(new Attribute("MsgAckMode", "Deferred"));
		e.appendChild(dynamicDefElm);

		for (Message message : profile.getMessages().getChildren()) {
			nu.xom.Element hL7v2xStaticDefElm = new nu.xom.Element("HL7v2xStaticDef");
			hL7v2xStaticDefElm.addAttribute(new Attribute("MsgType", serializationUtil.str(message.getMessageType())));
			hL7v2xStaticDefElm.addAttribute(new Attribute("EventType", serializationUtil.str(message.getEvent())));
			hL7v2xStaticDefElm.addAttribute(new Attribute("MsgStructID", serializationUtil.str(message.getStructID())));
			hL7v2xStaticDefElm
					.addAttribute(new Attribute("EventDesc", serializationUtil.str(message.getDescription())));
			// hL7v2xStaticDefElm.addAttribute(new Attribute("Identifier",
			// serializationUtil.str(message.getMessageID())));

			nu.xom.Element metadataMessageElm = new nu.xom.Element("MetaData");
			metadataMessageElm.addAttribute(new Attribute("Name", serializationUtil.str(message.getName())));
			metadataMessageElm
					.addAttribute(new Attribute("OrgName", serializationUtil.str(profile.getMetaData().getOrgName())));
			hL7v2xStaticDefElm.appendChild(metadataMessageElm);

			Map<Integer, SegmentRefOrGroup> segmentRefOrGroups = new HashMap<Integer, SegmentRefOrGroup>();
			for (SegmentRefOrGroup segmentRefOrGroup : message.getChildren()) {
				segmentRefOrGroups.put(segmentRefOrGroup.getPosition(), segmentRefOrGroup);
			}
			for (int i = 1; i < segmentRefOrGroups.size() + 1; i++) {
				String path = i + "[1]";
				SegmentRefOrGroup segmentRefOrGroup = segmentRefOrGroups.get(i);
				if (segmentRefOrGroup instanceof SegmentRef) {
					hL7v2xStaticDefElm.appendChild(
							serializeGazelleSegment((SegmentRef) segmentRefOrGroup, profile, message, path));
				} else if (segmentRefOrGroup instanceof Group) {
					hL7v2xStaticDefElm
							.appendChild(serializeGazelleGroup((Group) segmentRefOrGroup, profile, message, path));
				}
			}
			e.appendChild(hL7v2xStaticDefElm);
		}

		nu.xom.Document doc = new nu.xom.Document(e);
		return doc;
	}

	private nu.xom.Document serializeProfileToDoc(Profile profile, DocumentMetaData metadata, Date dateUpdated,
			Map<String, Segment> segmentsMap, 
			Map<String, Datatype> datatypesMap,
			Map<String, Table> tablesMap) {
		
		nu.xom.Element e = new nu.xom.Element("ConformanceProfile");
		this.serializeProfileMetaData(e, profile, metadata);

		nu.xom.Element ms = new nu.xom.Element("Messages");
		for (Message m : profile.getMessages().getChildren()) {
			ms.appendChild(this.serializeMessage(m, segmentsMap));
		}
		e.appendChild(ms);

		nu.xom.Element ss = new nu.xom.Element("Segments");
		for (String key : segmentsMap.keySet()) {
			Segment s = segmentsMap.get(key);
			System.out.println(key);
			System.out.println(s.getId());
			ss.appendChild(this.serializeSegment(s, tablesMap, datatypesMap));
		}
		e.appendChild(ss);

		nu.xom.Element ds = new nu.xom.Element("Datatypes");
		for (String key : datatypesMap.keySet()) {
			Datatype d = datatypesMap.get(key);
			ds.appendChild(this.serializeDatatypeForValidation(d, tablesMap, datatypesMap));
		}
		e.appendChild(ds);

		nu.xom.Document doc = new nu.xom.Document(e);

		return doc;
	}

	private nu.xom.Document serializeProfileToDoc(Profile profile, DocumentMetaData metadata, Date dateUpdated) {
		HashMap<String, Segment> segmentsMap = new HashMap<String, Segment>();
		HashMap<String, Datatype> datatypesMap = new HashMap<String, Datatype>();
		HashMap<String, Table> tablesMap = new HashMap<String, Table>();

		for (SegmentLink sl : profile.getSegmentLibrary().getChildren()) {
			Segment s = segmentService.findById(sl.getId());
			if (s != null)
				segmentsMap.put(s.getId(), s);
		}

		for (DatatypeLink dl : profile.getDatatypeLibrary().getChildren()) {
			Datatype d = datatypeService.findById(dl.getId());
			if (d != null)
				datatypesMap.put(d.getId(), d);
		}

		for (TableLink tl : profile.getTableLibrary().getChildren()) {
			Table t = tableService.findById(tl.getId());
			if (t != null)
				tablesMap.put(t.getId(), t);
		}

		return this.serializeProfileToDoc(profile, metadata, dateUpdated, segmentsMap, datatypesMap, tablesMap);
	}

	private void serializeProfileMetaData(nu.xom.Element e, Profile profile, DocumentMetaData igMetaData) {
		Attribute schemaDecl = new Attribute("noNamespaceSchemaLocation", "https://raw.githubusercontent.com/Jungyubw/NIST_healthcare_hl7_v2_profile_schema/master/Schema/NIST%20Validation%20Schema/Profile.xsd");
		schemaDecl.setNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		e.addAttribute(schemaDecl);
		
		e.addAttribute(new Attribute("ID", profile.getId()));
		ProfileMetaData metaData = profile.getMetaData();
		if (metaData.getType() != null && !metaData.getType().equals(""))
			e.addAttribute(new Attribute("Type", serializationUtil.str(metaData.getType())));
		if (metaData.getHl7Version() != null && !metaData.getHl7Version().equals(""))
			e.addAttribute(new Attribute("HL7Version", serializationUtil.str(metaData.getHl7Version())));
		if (metaData.getSchemaVersion() != null && !metaData.getSchemaVersion().equals(""))
			e.addAttribute(new Attribute("SchemaVersion", serializationUtil.str(metaData.getSchemaVersion())));

		nu.xom.Element elmMetaData = new nu.xom.Element("MetaData");
		elmMetaData.addAttribute(new Attribute("Name", !serializationUtil.str(igMetaData.getTitle()).equals("")
				? serializationUtil.str(igMetaData.getTitle()) : "No Title Info"));
		elmMetaData.addAttribute(new Attribute("OrgName", !serializationUtil.str(igMetaData.getOrgName()).equals("")
				? serializationUtil.str(igMetaData.getOrgName()) : "No Org Info"));
		elmMetaData.addAttribute(new Attribute("Version", !serializationUtil.str(igMetaData.getVersion()).equals("")
				? serializationUtil.str(igMetaData.getVersion()) : "No Version Info"));
		elmMetaData.addAttribute(new Attribute("Date", !serializationUtil.str(igMetaData.getDate()).equals("")
				? serializationUtil.str(igMetaData.getDate()) : "No Date Info"));

		if (metaData.getSpecificationName() != null && !metaData.getSpecificationName().equals(""))
			elmMetaData.addAttribute(
					new Attribute("SpecificationName", serializationUtil.str(metaData.getSpecificationName())));
		if (metaData.getStatus() != null && !metaData.getStatus().equals(""))
			elmMetaData.addAttribute(new Attribute("Status", serializationUtil.str(metaData.getStatus())));
		if (metaData.getTopics() != null && !metaData.getTopics().equals(""))
			elmMetaData.addAttribute(new Attribute("Topics", serializationUtil.str(metaData.getTopics())));

		e.appendChild(elmMetaData);
	}

	private void constructDatatypesMap(Element elmDatatypes, Profile profile) {
		this.datatypesMap = new HashMap<String, Datatype>();
		NodeList datatypeNodeList = elmDatatypes.getElementsByTagName("Datatype");

		for (int i = 0; i < datatypeNodeList.getLength(); i++) {
			Element elmDatatype = (Element) datatypeNodeList.item(i);
			if (!datatypesMap.keySet().contains(elmDatatype.getAttribute("ID"))) {
				datatypesMap.put(elmDatatype.getAttribute("ID"),
						this.deserializeDatatype(elmDatatype, profile, elmDatatypes));
			}
		}
	}

	// private Element getDatatypeElement(Element elmDatatypes, String id) {
	// NodeList datatypeNodeList = elmDatatypes
	// .getElementsByTagName("Datatype");
	// for (int i = 0; i < datatypeNodeList.getLength(); i++) {
	// Element elmDatatype = (Element) datatypeNodeList.item(i);
	// if (id.equals(elmDatatype.getAttribute("ID"))) {
	// return elmDatatype;
	// }
	// }
	// return null;
	// }

	private Datatype deserializeDatatype(Element elmDatatype, Profile profile, Element elmDatatypes) {
		String ID = elmDatatype.getAttribute("ID");
		if (!datatypesMap.keySet().contains(ID)) {
			Datatype datatypeObj = new Datatype();
			datatypeObj.setDescription(elmDatatype.getAttribute("Description"));
			if (elmDatatype.getAttribute("Label") != null && !elmDatatype.getAttribute("Label").equals("")) {
				datatypeObj.setLabel(elmDatatype.getAttribute("Label"));
			} else {
				datatypeObj.setLabel(elmDatatype.getAttribute("Name"));
			}
			datatypeObj.setName(elmDatatype.getAttribute("Name"));
			datatypeObj.setPredicates(
					this.findPredicates(this.predicates.getDatatypes(), ID, elmDatatype.getAttribute("Name")));
			datatypeObj.setConformanceStatements(this.findConformanceStatement(this.conformanceStatement.getDatatypes(),
					ID, elmDatatype.getAttribute("Name")));

			NodeList nodes = elmDatatype.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				if (nodes.item(i).getNodeName().equals("Component")) {
					Element elmComponent = (Element) nodes.item(i);
					Component componentObj = new Component();
					componentObj.setName(elmComponent.getAttribute("Name"));
					componentObj.setUsage(Usage.fromValue(elmComponent.getAttribute("Usage")));
					componentObj.setPosition(new Integer(elmComponent.getAttribute("Position")));
					// Element elmDt = getDatatypeElement(elmDatatypes,
					// elmComponent.getAttribute("Datatype"));
					// Datatype datatype = this.deserializeDatatype(elmDt,
					// profile, elmDatatypes);
					// TODO
					// componentObj.setDatatype(datatype.getId());
					componentObj.setMinLength(new Integer(elmComponent.getAttribute("MinLength")));
					if (elmComponent.getAttribute("MaxLength") != null) {
						componentObj.setMaxLength(elmComponent.getAttribute("MaxLength"));
					}
					if (elmComponent.getAttribute("ConfLength") != null) {
						componentObj.setConfLength(elmComponent.getAttribute("ConfLength"));
					}
					// TODO
					if (elmComponent.getAttribute("Binding") != null) {
						// componentObj.setTable(findTableIdByMappingId(elmComponent.getAttribute("Binding"),
						// profile.getTableLibrary()));
					}

					if (elmComponent.getAttribute("Hide") != null && elmComponent.getAttribute("Hide").equals("true")) {
						componentObj.setHide(true);
					} else {
						componentObj.setHide(false);
					}

					datatypeObj.addComponent(componentObj);
				}
			}

			// datatypeObj = this.deserializeDatatype(elmDatatype, profile,
			// elmDatatypes);
			datatypesMap.put(ID, datatypeObj);

			return datatypeObj;

		} else {
			return datatypesMap.get(ID);
		}
	}

	private List<ConformanceStatement> findConformanceStatement(Context context, String id, String name) {
		Set<ByNameOrByID> byNameOrByIDs = context.getByNameOrByIDs();
		List<ConformanceStatement> result = new ArrayList<ConformanceStatement>();
		for (ByNameOrByID byNameOrByID : byNameOrByIDs) {
			if (byNameOrByID instanceof ByID) {
				ByID byID = (ByID) byNameOrByID;
				if (byID.getByID().equals(id)) {
					for (ConformanceStatement c : byID.getConformanceStatements()) {
						result.add(c);
					}
				} else if (byNameOrByID instanceof ByName) {
					ByName byName = (ByName) byNameOrByID;
					if (byName.getByName().equals(name)) {
						for (ConformanceStatement c : byName.getConformanceStatements()) {
							result.add(c);
						}
					}
				}
			}
		}
		return result;
	}

	private List<Predicate> findPredicates(Context context, String id, String name) {
		Set<ByNameOrByID> byNameOrByIDs = context.getByNameOrByIDs();
		List<Predicate> result = new ArrayList<Predicate>();
		for (ByNameOrByID byNameOrByID : byNameOrByIDs) {
			if (byNameOrByID instanceof ByID) {
				ByID byID = (ByID) byNameOrByID;
				if (byID.getByID().equals(id)) {
					for (Predicate p : byID.getPredicates()) {
						result.add(p);
					}
				}
			} else if (byNameOrByID instanceof ByName) {
				ByName byName = (ByName) byNameOrByID;
				if (byName.getByName().equals(name)) {
					for (Predicate p : byName.getPredicates()) {
						result.add(p);
					}
				}
			}
		}
		return result;
	}

	private Datatype findDatatype(String key, Profile profile) {
		if (datatypesMap.get(key) != null)
			return datatypesMap.get(key);
		throw new IllegalArgumentException("Datatype " + key + " not found");
	}

	private HashMap<String, Segment> constructSegmentsMap(Element elmSegments, Profile profile) {
		HashMap<String, Segment> segmentsMap = new HashMap<String, Segment>();
		NodeList segmentNodeList = elmSegments.getElementsByTagName("Segment");

		for (int i = 0; i < segmentNodeList.getLength(); i++) {
			Element elmSegment = (Element) segmentNodeList.item(i);
			segmentsMap.put(elmSegment.getAttribute("ID"), this.deserializeSegment(elmSegment, profile));
		}

		return segmentsMap;
	}

	private nu.xom.Element serializeMessage(Message m, Map<String, Segment> segmentsMap) {
		nu.xom.Element elmMessage = new nu.xom.Element("Message");
		elmMessage.addAttribute(new Attribute("ID", m.getId()));
		if (m.getIdentifier() != null && !m.getIdentifier().equals(""))
			elmMessage.addAttribute(new Attribute("Identifier", serializationUtil.str(m.getIdentifier())));
		if (m.getName() != null && !m.getName().equals(""))
			elmMessage.addAttribute(new Attribute("Name", serializationUtil.str(m.getName())));
		elmMessage.addAttribute(new Attribute("Type", serializationUtil.str(m.getMessageType())));
		elmMessage.addAttribute(new Attribute("Event", serializationUtil.str(m.getEvent())));
		elmMessage.addAttribute(new Attribute("StructID", serializationUtil.str(m.getStructID())));
		if (m.getDescription() != null && !m.getDescription().equals(""))
			elmMessage.addAttribute(new Attribute("Description", serializationUtil.str(m.getDescription())));

		Map<Integer, SegmentRefOrGroup> segmentRefOrGroups = new HashMap<Integer, SegmentRefOrGroup>();

		for (SegmentRefOrGroup segmentRefOrGroup : m.getChildren()) {
			segmentRefOrGroups.put(segmentRefOrGroup.getPosition(), segmentRefOrGroup);
		}

		for (int i = 1; i < segmentRefOrGroups.size() + 1; i++) {
			SegmentRefOrGroup segmentRefOrGroup = segmentRefOrGroups.get(i);
			if (segmentRefOrGroup instanceof SegmentRef) {
				elmMessage.appendChild(serializeSegmentRef((SegmentRef) segmentRefOrGroup, segmentsMap));
			} else if (segmentRefOrGroup instanceof Group) {
				elmMessage.appendChild(serializeGroup((Group) segmentRefOrGroup, segmentsMap));
			}
		}

		return elmMessage;
	}

	private nu.xom.Element serializeDisplayMessage(Message m, Profile profile) {
		nu.xom.Element elmMessage = new nu.xom.Element("Message");
		if (m.getName() != null && !m.getName().equals(""))
			elmMessage.addAttribute(new Attribute("Name", serializationUtil.str(m.getName())));
		elmMessage.addAttribute(new Attribute("Type", serializationUtil.str(m.getMessageType())));
		elmMessage.addAttribute(new Attribute("Event", serializationUtil.str(m.getEvent())));
		elmMessage.addAttribute(new Attribute("StructID", serializationUtil.str(m.getStructID())));
		if (m.getDescription() != null && !m.getDescription().equals(""))
			elmMessage.addAttribute(new Attribute("Description", serializationUtil.str(m.getDescription())));

		Map<Integer, SegmentRefOrGroup> segmentRefOrGroups = new HashMap<Integer, SegmentRefOrGroup>();
		for (SegmentRefOrGroup segmentRefOrGroup : m.getChildren()) {
			segmentRefOrGroups.put(segmentRefOrGroup.getPosition(), segmentRefOrGroup);
		}

		for (int i = 1; i < segmentRefOrGroups.size() + 1; i++) {

			String path = i + "[1]";

			SegmentRefOrGroup segmentRefOrGroup = segmentRefOrGroups.get(i);
			if (segmentRefOrGroup instanceof SegmentRef) {
				elmMessage.appendChild(serializeDisplaySegment((SegmentRef) segmentRefOrGroup, profile, m, path));
			} else if (segmentRefOrGroup instanceof Group) {
				elmMessage.appendChild(serializeDisplayGroup((Group) segmentRefOrGroup, profile, m, path));
			}
		}
		return elmMessage;
	}

	private nu.xom.Element serializeGroup(Group group, Map<String, Segment> segmentsMap) {
		nu.xom.Element elmGroup = new nu.xom.Element("Group");
		elmGroup.addAttribute(new Attribute("ID", serializationUtil.str(group.getName())));
		elmGroup.addAttribute(new Attribute("Name", serializationUtil.str(group.getName())));
		elmGroup.addAttribute(new Attribute("Usage", serializationUtil.str(group.getUsage().value())));
		elmGroup.addAttribute(new Attribute("Min", serializationUtil.str(group.getMin() + "")));
		elmGroup.addAttribute(new Attribute("Max", serializationUtil.str(group.getMax())));

		Map<Integer, SegmentRefOrGroup> segmentRefOrGroups = new HashMap<Integer, SegmentRefOrGroup>();

		for (SegmentRefOrGroup segmentRefOrGroup : group.getChildren()) {
			segmentRefOrGroups.put(segmentRefOrGroup.getPosition(), segmentRefOrGroup);
		}

		for (int i = 1; i < segmentRefOrGroups.size() + 1; i++) {
			SegmentRefOrGroup segmentRefOrGroup = segmentRefOrGroups.get(i);
			if (segmentRefOrGroup instanceof SegmentRef) {
				elmGroup.appendChild(serializeSegmentRef((SegmentRef) segmentRefOrGroup, segmentsMap));
			} else if (segmentRefOrGroup instanceof Group) {
				elmGroup.appendChild(serializeGroup((Group) segmentRefOrGroup, segmentsMap));
			}
		}

		return elmGroup;
	}

	private nu.xom.Element serializeGazelleGroup(Group group, Profile profile, Message message, String path) {
		nu.xom.Element elmSegGroup = new nu.xom.Element("SegGroup");
		if (group.getName().contains(".")) {
			elmSegGroup.addAttribute(new Attribute("Name",
					serializationUtil.str(group.getName().substring(group.getName().lastIndexOf(".") + 1))));
		} else {
			elmSegGroup.addAttribute(new Attribute("Name", serializationUtil.str(group.getName())));
		}

		elmSegGroup.addAttribute(new Attribute("LongName", serializationUtil.str(group.getName())));
		if (group.getUsage().value().equals("B")) {
			elmSegGroup.addAttribute(new Attribute("Usage", "X"));
		} else {
			elmSegGroup.addAttribute(new Attribute("Usage", serializationUtil.str(group.getUsage().value())));
		}
		elmSegGroup.addAttribute(new Attribute("Min", serializationUtil.str(group.getMin() + "")));
		if (group.getMax().equals("0")) {
			elmSegGroup.addAttribute(new Attribute("Max", "" + 1));
		} else {
			elmSegGroup.addAttribute(new Attribute("Max", serializationUtil.str(group.getMax())));
		}
		List<ConformanceStatement> groupConformanceStatements = this.findConformanceStatements(null, null,
				message.getConformanceStatements(), path);

		if (groupConformanceStatements.size() > 0) {
			nu.xom.Element elmImpNote = new nu.xom.Element("ImpNote");
			String note = "";
			for (ConformanceStatement c : groupConformanceStatements) {
				note = note + "\n" + "[" + c.getConstraintId() + "]" + c.getDescription();
			}
			elmImpNote.appendChild(note);
			elmSegGroup.appendChild(elmImpNote);
		}

		Predicate groupPredicate = this.findPredicate(null, null, message.getPredicates(), path);
		if (groupPredicate != null) {
			nu.xom.Element elmPredicate = new nu.xom.Element("Predicate");
			String note = "[C(" + groupPredicate.getTrueUsage() + "/" + groupPredicate.getFalseUsage() + ")]"
					+ groupPredicate.getDescription();
			elmPredicate.appendChild(note);
			elmSegGroup.appendChild(elmPredicate);
		}

		Map<Integer, SegmentRefOrGroup> segmentRefOrGroups = new HashMap<Integer, SegmentRefOrGroup>();

		for (SegmentRefOrGroup segmentRefOrGroup : group.getChildren()) {
			segmentRefOrGroups.put(segmentRefOrGroup.getPosition(), segmentRefOrGroup);
		}

		for (int i = 1; i < segmentRefOrGroups.size() + 1; i++) {
			String childPath = path + "." + i + "[1]";
			SegmentRefOrGroup segmentRefOrGroup = segmentRefOrGroups.get(i);
			if (segmentRefOrGroup instanceof SegmentRef) {
				elmSegGroup.appendChild(
						serializeGazelleSegment((SegmentRef) segmentRefOrGroup, profile, message, childPath));
			} else if (segmentRefOrGroup instanceof Group) {
				elmSegGroup.appendChild(serializeGazelleGroup((Group) segmentRefOrGroup, profile, message, childPath));
			}
		}

		return elmSegGroup;
	}

	private nu.xom.Element serializeDisplayGroup(Group group, Profile profile, Message message, String path) {
		nu.xom.Element elmGroup = new nu.xom.Element("Group");
		elmGroup.addAttribute(new Attribute("ID", serializationUtil.str(group.getName())));
		elmGroup.addAttribute(new Attribute("Name", serializationUtil.str(group.getName())));
		elmGroup.addAttribute(new Attribute("Usage", serializationUtil.str(group.getUsage().value())));
		elmGroup.addAttribute(new Attribute("Min", serializationUtil.str(group.getMin() + "")));
		elmGroup.addAttribute(new Attribute("Max", serializationUtil.str(group.getMax())));

		Predicate groupPredicate = this.findPredicate(null, null, message.getPredicates(), path);
		if (groupPredicate != null) {
			nu.xom.Element elmPredicate = new nu.xom.Element("Predicate");
			elmPredicate.addAttribute(new Attribute("TrueUsage", "" + groupPredicate.getTrueUsage()));
			elmPredicate.addAttribute(new Attribute("FalseUsage", "" + groupPredicate.getFalseUsage()));

			nu.xom.Element elmDescription = new nu.xom.Element("Description");
			elmDescription.appendChild(groupPredicate.getDescription());
			elmPredicate.appendChild(elmDescription);

			nu.xom.Node n = this.innerXMLHandler(groupPredicate.getAssertion());
			if (n != null)
				elmPredicate.appendChild(n);

			elmGroup.appendChild(elmPredicate);
		}

		List<ConformanceStatement> groupConformanceStatements = this.findConformanceStatements(null, null,
				message.getConformanceStatements(), path);

		if (groupConformanceStatements.size() > 0) {
			nu.xom.Element elmConformanceStatements = new nu.xom.Element("ConformanceStatements");

			for (ConformanceStatement c : groupConformanceStatements) {
				nu.xom.Element elmConformanceStatement = new nu.xom.Element("ConformanceStatement");
				elmConformanceStatement.addAttribute(new Attribute("ID", "" + c.getConstraintId()));
				nu.xom.Element elmDescription = new nu.xom.Element("Description");
				elmDescription.appendChild(c.getDescription());
				elmConformanceStatement.appendChild(elmDescription);

				nu.xom.Node n = this.innerXMLHandler(c.getAssertion());
				if (n != null)
					elmConformanceStatement.appendChild(n);

				elmConformanceStatements.appendChild(elmConformanceStatement);
			}

			elmGroup.appendChild(elmConformanceStatements);
		}

		Map<Integer, SegmentRefOrGroup> segmentRefOrGroups = new HashMap<Integer, SegmentRefOrGroup>();

		for (SegmentRefOrGroup segmentRefOrGroup : group.getChildren()) {
			segmentRefOrGroups.put(segmentRefOrGroup.getPosition(), segmentRefOrGroup);
		}

		nu.xom.Element elmStructure = new nu.xom.Element("Structure");

		for (int i = 1; i < segmentRefOrGroups.size() + 1; i++) {
			String childPath = path + "." + i + "[1]";
			SegmentRefOrGroup segmentRefOrGroup = segmentRefOrGroups.get(i);
			if (segmentRefOrGroup instanceof SegmentRef) {
				elmStructure.appendChild(
						serializeDisplaySegment((SegmentRef) segmentRefOrGroup, profile, message, childPath));
			} else if (segmentRefOrGroup instanceof Group) {
				elmStructure.appendChild(serializeDisplayGroup((Group) segmentRefOrGroup, profile, message, childPath));
			}
		}

		elmGroup.appendChild(elmStructure);

		return elmGroup;
	}

	private nu.xom.Element serializeSegmentRef(SegmentRef segmentRef, Map<String, Segment> segmentsMap) {
		Segment s = segmentsMap.get(segmentRef.getRef().getId());
		nu.xom.Element elmSegment = new nu.xom.Element("Segment");
		elmSegment.addAttribute(new Attribute("Ref",
				serializationUtil.str(s.getLabel() + "_" + s.getHl7Version().replaceAll("\\.", "-"))));
		elmSegment.addAttribute(new Attribute("Usage", serializationUtil.str(segmentRef.getUsage().value())));
		elmSegment.addAttribute(new Attribute("Min", serializationUtil.str(segmentRef.getMin() + "")));
		elmSegment.addAttribute(new Attribute("Max", serializationUtil.str(segmentRef.getMax())));
		return elmSegment;
	}

	private Predicate findPredicate(List<Predicate> predicates, String path, List<Predicate> messagePredicate,
			String messagePath) {
		if (predicates != null && path != null) {
			for (Predicate p : predicates) {
				if (p.getConstraintTarget().equals(path)) {
					return p;
				}
			}
		}

		if (messagePredicate != null && messagePath != null) {
			for (Predicate p : messagePredicate) {
				if (p.getConstraintTarget().equals(messagePath)) {
					return p;
				}
			}
		}
		return null;
	}

	private List<ConformanceStatement> findConformanceStatements(List<ConformanceStatement> conformanceStatements,
			String path, List<ConformanceStatement> messageConformanceStatements, String messagePath) {
		List<ConformanceStatement> result = new ArrayList<ConformanceStatement>();

		if (conformanceStatements != null && path != null) {
			for (ConformanceStatement c : conformanceStatements) {
				if (c.getConstraintTarget().equals(path)) {
					result.add(c);
				}
			}
		}
		if (messageConformanceStatements != null && messagePath != null) {
			for (ConformanceStatement c : messageConformanceStatements) {
				if (c.getConstraintTarget().equals(messagePath)) {
					result.add(c);
				}
			}
		}
		return result;
	}

	private nu.xom.Element serializeGazelleSegment(SegmentRef segmentRef, Profile profile, Message message,
			String path) {
		nu.xom.Element elmSegment = new nu.xom.Element("Segment");

		Segment segment = segmentService.findById(segmentRef.getRef().getId());
		elmSegment.addAttribute(new Attribute("Name", serializationUtil.str(segment.getName())));
		elmSegment.addAttribute(new Attribute("LongName", serializationUtil.str(segment.getDescription())));
		if (segmentRef.getUsage().value().equals("B")) {
			elmSegment.addAttribute(new Attribute("Usage", "X"));
		} else {
			elmSegment.addAttribute(new Attribute("Usage", serializationUtil.str(segmentRef.getUsage().value())));
		}
		elmSegment.addAttribute(new Attribute("Min", serializationUtil.str(segmentRef.getMin() + "")));

		if (segmentRef.getMax().equals("0")) {
			elmSegment.addAttribute(new Attribute("Max", "" + 1));
		} else {
			elmSegment.addAttribute(new Attribute("Max", serializationUtil.str(segmentRef.getMax())));
		}

		List<ConformanceStatement> segmentConformanceStatements = this.findConformanceStatements(null, null,
				message.getConformanceStatements(), path);
		if (segmentConformanceStatements.size() > 0) {
			nu.xom.Element elmImpNote = new nu.xom.Element("ImpNote");
			String note = "";
			for (ConformanceStatement c : segmentConformanceStatements) {
				note = note + "\n" + "[" + c.getConstraintId() + "]" + c.getDescription();
			}
			elmImpNote.appendChild(note);
			elmSegment.appendChild(elmImpNote);
		}

		Predicate segmentPredicate = this.findPredicate(null, null, message.getPredicates(), path);
		if (segmentPredicate != null) {
			nu.xom.Element elmPredicate = new nu.xom.Element("Predicate");
			String note = "[C(" + segmentPredicate.getTrueUsage() + "/" + segmentPredicate.getFalseUsage() + ")]"
					+ segmentPredicate.getDescription();
			elmPredicate.appendChild(note);
			elmSegment.appendChild(elmPredicate);
		}

		Map<Integer, Field> fields = new HashMap<Integer, Field>();
		for (Field f : segment.getFields()) {
			fields.put(f.getPosition(), f);
		}

		for (int i = 1; i < fields.size() + 1; i++) {
			String fieldPath = path + "." + i + "[1]";
			Field f = fields.get(i);
			this.serializeGazelleField(f, datatypeService.findById(f.getDatatype().getId()), elmSegment, profile,
					message, segment, fieldPath);

		}
		return elmSegment;
	}

	private nu.xom.Element serializeDisplaySegment(SegmentRef segmentRef, Profile profile, Message message,
			String path) {
		nu.xom.Element elmSegment = new nu.xom.Element("Segment");

		Segment segment = segmentService.findById(segmentRef.getRef().getId());

		elmSegment.addAttribute(new Attribute("ID", serializationUtil.str(segment.getLabel())));
		elmSegment.addAttribute(new Attribute("Usage", serializationUtil.str(segmentRef.getUsage().value())));
		elmSegment.addAttribute(new Attribute("Min", serializationUtil.str(segmentRef.getMin() + "")));
		elmSegment.addAttribute(new Attribute("Max", serializationUtil.str(segmentRef.getMax())));
		elmSegment.addAttribute(new Attribute("Name", serializationUtil.str(segment.getName())));
		elmSegment.addAttribute(new Attribute("Description", serializationUtil.str(segment.getDescription())));

		Predicate segmentPredicate = this.findPredicate(null, null, message.getPredicates(), path);
		if (segmentPredicate != null) {
			nu.xom.Element elmPredicate = new nu.xom.Element("Predicate");
			elmPredicate.addAttribute(new Attribute("TrueUsage", "" + segmentPredicate.getTrueUsage()));
			elmPredicate.addAttribute(new Attribute("FalseUsage", "" + segmentPredicate.getFalseUsage()));

			nu.xom.Element elmDescription = new nu.xom.Element("Description");
			elmDescription.appendChild(segmentPredicate.getDescription());
			elmPredicate.appendChild(elmDescription);

			nu.xom.Node n = this.innerXMLHandler(segmentPredicate.getAssertion());
			if (n != null)
				elmPredicate.appendChild(n);

			elmSegment.appendChild(elmPredicate);
		}

		List<ConformanceStatement> segmentConformanceStatements = this.findConformanceStatements(null, null,
				message.getConformanceStatements(), path);

		if (segmentConformanceStatements.size() > 0) {
			nu.xom.Element elmConformanceStatements = new nu.xom.Element("ConformanceStatements");

			for (ConformanceStatement c : segmentConformanceStatements) {
				nu.xom.Element elmConformanceStatement = new nu.xom.Element("ConformanceStatement");
				elmConformanceStatement.addAttribute(new Attribute("ID", "" + c.getConstraintId()));
				nu.xom.Element elmDescription = new nu.xom.Element("Description");
				elmDescription.appendChild(c.getDescription());
				elmConformanceStatement.appendChild(elmDescription);

				nu.xom.Node n = this.innerXMLHandler(c.getAssertion());
				if (n != null)
					elmConformanceStatement.appendChild(n);

				elmConformanceStatements.appendChild(elmConformanceStatement);
			}
			elmSegment.appendChild(elmConformanceStatements);
		}

		nu.xom.Element elmSegmentStructure = new nu.xom.Element("Structure");

		Map<Integer, Field> fields = new HashMap<Integer, Field>();
		for (Field f : segment.getFields()) {
			fields.put(f.getPosition(), f);
		}

		if (fields.size() > 0) {
			elmSegment.appendChild(elmSegmentStructure);
		}

		for (int i = 1; i < fields.size() + 1; i++) {
			String fieldPath = path + "." + i + "[1]";
			Field f = fields.get(i);
			Mapping mapping = this.findMapping(segment.getDynamicMapping().getMappings(), i);
			if (mapping == null) {
				this.serializeDisplayField(f, datatypeService.findById(f.getDatatype().getId()), elmSegmentStructure,
						profile, message, segment, fieldPath);
			} else {
				nu.xom.Element elmDynamicField = new nu.xom.Element("DynamicField");

				elmDynamicField.addAttribute(new Attribute("Name", serializationUtil.str(f.getName())));
				elmDynamicField.addAttribute(new Attribute("Reference", mapping.getReference() + ""));

				for (Case ca : mapping.getCases()) {
					nu.xom.Element elmCase = new nu.xom.Element("Case");
					elmCase.addAttribute(new Attribute("Value", serializationUtil.str(ca.getValue())));
					this.serializeDisplayField(f, datatypeService.findById(ca.getDatatype()), elmCase, profile, message,
							segment, fieldPath);
					elmDynamicField.appendChild(elmCase);
				}
				elmSegmentStructure.appendChild(elmDynamicField);
			}
		}
		return elmSegment;
	}

	private void serializeGazelleField(Field f, Datatype fieldDatatype, nu.xom.Element elmParent, Profile profile,
			Message message, Segment segment, String fieldPath) {
		nu.xom.Element elmField = new nu.xom.Element("Field");
		elmParent.appendChild(elmField);

		elmField.addAttribute(new Attribute("Name", serializationUtil.str(f.getName())));
		if (f.getUsage().value().equals("B")) {
			elmField.addAttribute(new Attribute("Usage", "X"));
		} else {
			elmField.addAttribute(new Attribute("Usage", serializationUtil.str(f.getUsage().value())));
		}
		elmField.addAttribute(new Attribute("Min", "" + f.getMin()));
		if (f.getMax().equals("0")) {
			elmField.addAttribute(new Attribute("Max", "" + 1));
		} else {
			elmField.addAttribute(new Attribute("Max", serializationUtil.str(f.getMax())));
		}

		if (f.getMaxLength() != null && !f.getMaxLength().equals("")) {
			if (f.getMaxLength().equals("*")) {
				elmField.addAttribute(new Attribute("Length", "" + 225));
			} else if (f.getMaxLength().equals("0")) {
				elmField.addAttribute(new Attribute("Length", "" + 1));
			} else {
				elmField.addAttribute(new Attribute("Length", serializationUtil.str(f.getMaxLength())));
			}
		}
		elmField.addAttribute(new Attribute("Datatype", serializationUtil.str(fieldDatatype.getName())));
		if (f.getTables() != null) {
			if (f.getTables().size() > 0) {
				String bindingString = "";
				for (TableLink tl : f.getTables()) {
					Table table = tableService.findById(tl.getId());
					if (table != null && table.getBindingIdentifier() != null
							&& !table.getBindingIdentifier().equals(""))
						bindingString = bindingString + table.getBindingIdentifier() + ":";
				}

				if (!bindingString.equals(""))
					elmField.addAttribute(
							new Attribute("Table", bindingString.substring(0, bindingString.length() - 1)));

			}
		}

		if (f.getItemNo() != null && !f.getItemNo().equals(""))
			elmField.addAttribute(new Attribute("ItemNo", serializationUtil.str(f.getItemNo())));

		List<ConformanceStatement> fieldConformanceStatements = this.findConformanceStatements(
				segment.getConformanceStatements(), f.getPosition() + "[1]", message.getConformanceStatements(),
				fieldPath);
		if (fieldConformanceStatements.size() > 0) {
			nu.xom.Element elmImpNote = new nu.xom.Element("ImpNote");
			String note = "";
			for (ConformanceStatement c : fieldConformanceStatements) {
				note = note + "\n" + "[" + c.getConstraintId() + "]" + c.getDescription();
			}
			elmImpNote.appendChild(note);
			elmField.appendChild(elmImpNote);
		}

		Predicate fieldPredicate = this.findPredicate(segment.getPredicates(), f.getPosition() + "[1]",
				message.getPredicates(), fieldPath);
		if (fieldPredicate != null) {
			nu.xom.Element elmPredicate = new nu.xom.Element("Predicate");
			String note = "[C(" + fieldPredicate.getTrueUsage() + "/" + fieldPredicate.getFalseUsage() + ")]"
					+ fieldPredicate.getDescription();
			elmPredicate.appendChild(note);
			elmField.appendChild(elmPredicate);
		}

		Map<Integer, Component> components = new HashMap<Integer, Component>();

		for (Component c : fieldDatatype.getComponents()) {
			components.put(c.getPosition(), c);
		}

		for (int j = 1; j < components.size() + 1; j++) {
			String componentPath = fieldPath + "." + j + "[1]";
			Component c = components.get(j);
			this.serializeGazelleComponent(c, datatypeService.findById(c.getDatatype().getId()), elmField, profile,
					message, fieldDatatype, componentPath);
		}
	}

	private void serializeDisplayField(Field f, Datatype fieldDatatype, nu.xom.Element elmParent, Profile profile,
			Message message, Segment segment, String fieldPath) {
		nu.xom.Element elmField = new nu.xom.Element("Field");
		elmParent.appendChild(elmField);

		elmField.addAttribute(new Attribute("Name", serializationUtil.str(f.getName())));
		elmField.addAttribute(new Attribute("Usage", serializationUtil.str(f.getUsage().toString())));
		elmField.addAttribute(new Attribute("Datatype", serializationUtil.str(fieldDatatype.getName())));
		elmField.addAttribute(new Attribute("Flavor", serializationUtil.str(fieldDatatype.getLabel())));
		elmField.addAttribute(new Attribute("MinLength", "" + f.getMinLength()));
		if (f.getMaxLength() != null && !f.getMaxLength().equals(""))
			elmField.addAttribute(new Attribute("MaxLength", serializationUtil.str(f.getMaxLength())));
		if (f.getConfLength() != null && !f.getConfLength().equals(""))
			elmField.addAttribute(new Attribute("ConfLength", serializationUtil.str(f.getConfLength())));

		if (f.getTables() != null) {
			if (f.getTables().size() > 0) {
				String bindingString = "";
				String bindingStrength = "";
				String bindingLocation = "";
				for (TableLink tl : f.getTables()) {
					Table table = tableService.findById(tl.getId());
					if (table != null && table.getBindingIdentifier() != null
							&& !table.getBindingIdentifier().equals(""))
						bindingString = bindingString + table.getBindingIdentifier() + ":";
				}

				if (!bindingString.equals(""))
					elmField.addAttribute(
							new Attribute("Binding", bindingString.substring(0, bindingString.length() - 1)));
				if (!bindingStrength.equals("")) {
					elmField.addAttribute(new Attribute("BindingStrength", bindingStrength));
				}

				if (!bindingLocation.equals("")) {
					elmField.addAttribute(new Attribute("BindingLocation", bindingLocation));
				} else {
					Datatype d = datatypeService.findById(f.getDatatype().getId());
					if (d != null && d.getComponents() != null && d.getComponents().size() > 0) {
						elmField.addAttribute(new Attribute("BindingLocation", "1"));
					}
				}

			}
		}

		elmField.addAttribute(new Attribute("Min", "" + f.getMin()));
		elmField.addAttribute(new Attribute("Max", "" + f.getMax()));
		if (f.getItemNo() != null && !f.getItemNo().equals(""))
			elmField.addAttribute(new Attribute("ItemNo", serializationUtil.str(f.getItemNo())));

		Predicate fieldPredicate = this.findPredicate(segment.getPredicates(), f.getPosition() + "[1]",
				message.getPredicates(), fieldPath);
		if (fieldPredicate != null) {
			nu.xom.Element elmPredicate = new nu.xom.Element("Predicate");
			elmPredicate.addAttribute(new Attribute("TrueUsage", "" + fieldPredicate.getTrueUsage()));
			elmPredicate.addAttribute(new Attribute("FalseUsage", "" + fieldPredicate.getFalseUsage()));

			nu.xom.Element elmDescription = new nu.xom.Element("Description");
			elmDescription.appendChild(fieldPredicate.getDescription());
			elmPredicate.appendChild(elmDescription);

			nu.xom.Node n = this.innerXMLHandler(fieldPredicate.getAssertion());
			if (n != null)
				elmPredicate.appendChild(n);

			elmField.appendChild(elmPredicate);
		}

		List<ConformanceStatement> fieldConformanceStatements = this.findConformanceStatements(
				segment.getConformanceStatements(), f.getPosition() + "[1]", message.getConformanceStatements(),
				fieldPath);

		if (fieldConformanceStatements.size() > 0) {
			nu.xom.Element elmConformanceStatements = new nu.xom.Element("ConformanceStatements");

			for (ConformanceStatement c : fieldConformanceStatements) {
				nu.xom.Element elmConformanceStatement = new nu.xom.Element("ConformanceStatement");
				elmConformanceStatement.addAttribute(new Attribute("ID", "" + c.getConstraintId()));
				nu.xom.Element elmDescription = new nu.xom.Element("Description");
				elmDescription.appendChild(c.getDescription());
				elmConformanceStatement.appendChild(elmDescription);

				nu.xom.Node n = this.innerXMLHandler(c.getAssertion());
				if (n != null)
					elmConformanceStatement.appendChild(n);

				elmConformanceStatements.appendChild(elmConformanceStatement);
			}

			elmField.appendChild(elmConformanceStatements);
		}

		nu.xom.Element elmFieldStructure = new nu.xom.Element("Structure");
		Map<Integer, Component> components = new HashMap<Integer, Component>();

		for (Component c : fieldDatatype.getComponents()) {
			components.put(c.getPosition(), c);
		}

		if (components.size() > 0) {
			elmField.appendChild(elmFieldStructure);
		}

		for (int j = 1; j < components.size() + 1; j++) {
			String componentPath = fieldPath + "." + j + "[1]";
			Component c = components.get(j);
			this.serializeDisplayComponent(c, datatypeService.findById(c.getDatatype().getId()), elmFieldStructure,
					profile, message, fieldDatatype, componentPath);
		}
	}

	private void serializeGazelleComponent(Component c, Datatype componentDatatype, nu.xom.Element elmParent,
			Profile profile, Message message, Datatype fieldDatatype, String componentPath) {
		nu.xom.Element elmComponent = new nu.xom.Element("Component");
		elmComponent.addAttribute(new Attribute("Name", serializationUtil.str(c.getName())));
		if (c.getUsage().value().equals("B")) {
			elmComponent.addAttribute(new Attribute("Usage", "X"));
		} else {
			elmComponent.addAttribute(new Attribute("Usage", serializationUtil.str(c.getUsage().value())));
		}
		elmComponent.addAttribute(new Attribute("Datatype", serializationUtil.str(componentDatatype.getName())));
		if (c.getMaxLength() != null && !c.getMaxLength().equals("")) {
			if (c.getMaxLength().equals("*")) {
				elmComponent.addAttribute(new Attribute("Length", "" + 225));
			} else if (c.getMaxLength().equals("0")) {
				elmComponent.addAttribute(new Attribute("Length", "" + 1));
			} else {
				elmComponent.addAttribute(new Attribute("Length", serializationUtil.str(c.getMaxLength())));
			}
		}

		if (c.getTables() != null) {
			if (c.getTables().size() > 0) {
				String bindingString = "";
				for (TableLink tl : c.getTables()) {
					Table table = tableService.findById(tl.getId());
					if (table != null && table.getBindingIdentifier() != null
							&& !table.getBindingIdentifier().equals(""))
						bindingString = bindingString + table.getBindingIdentifier() + ":";
				}

				if (!bindingString.equals(""))
					elmComponent.addAttribute(
							new Attribute("Table", bindingString.substring(0, bindingString.length() - 1)));
			}
		}

		List<ConformanceStatement> componentConformanceStatements = this.findConformanceStatements(
				fieldDatatype.getConformanceStatements(), c.getPosition() + "[1]", message.getConformanceStatements(),
				componentPath);
		if (componentConformanceStatements.size() > 0) {
			nu.xom.Element elmImpNote = new nu.xom.Element("ImpNote");
			String note = "";
			for (ConformanceStatement cs : componentConformanceStatements) {
				note = note + "\n" + "[" + cs.getConstraintId() + "]" + cs.getDescription();
			}
			elmImpNote.appendChild(note);
			elmComponent.appendChild(elmImpNote);
		}

		Predicate componentPredicate = this.findPredicate(fieldDatatype.getPredicates(), c.getPosition() + "[1]",
				message.getPredicates(), componentPath);
		if (componentPredicate != null) {
			nu.xom.Element elmPredicate = new nu.xom.Element("Predicate");
			String note = "[C(" + componentPredicate.getTrueUsage() + "/" + componentPredicate.getFalseUsage() + ")]"
					+ componentPredicate.getDescription();
			elmPredicate.appendChild(note);
			elmComponent.appendChild(elmPredicate);
		}

		Map<Integer, Component> subComponents = new HashMap<Integer, Component>();

		for (Component sc : componentDatatype.getComponents()) {
			subComponents.put(sc.getPosition(), sc);
		}

		for (int k = 1; k < subComponents.size() + 1; k++) {
			String subComponentPath = componentPath + "." + k + "[1]";
			Component sc = subComponents.get(k);
			this.serializeGazelleSubComponent(sc, datatypeService.findById(sc.getDatatype().getId()), elmComponent,
					profile, message, componentDatatype, subComponentPath);
		}
		elmParent.appendChild(elmComponent);
	}

	private void serializeDisplayComponent(Component c, Datatype componentDatatype, nu.xom.Element elmParent,
			Profile profile, Message message, Datatype fieldDatatype, String componentPath) {
		nu.xom.Element elmComponent = new nu.xom.Element("Component");
		elmComponent.addAttribute(new Attribute("Name", serializationUtil.str(c.getName())));
		elmComponent.addAttribute(new Attribute("Usage", serializationUtil.str(c.getUsage().toString())));
		elmComponent.addAttribute(new Attribute("Datatype", serializationUtil.str(componentDatatype.getName())));
		elmComponent.addAttribute(new Attribute("Flavor", serializationUtil.str(componentDatatype.getLabel())));
		elmComponent.addAttribute(new Attribute("MinLength", "" + c.getMinLength()));
		if (c.getMaxLength() != null && !c.getMaxLength().equals(""))
			elmComponent.addAttribute(new Attribute("MaxLength", serializationUtil.str(c.getMaxLength())));
		if (c.getConfLength() != null && !c.getConfLength().equals(""))
			elmComponent.addAttribute(new Attribute("ConfLength", serializationUtil.str(c.getConfLength())));
		if (c.getTables() != null) {
			if (c.getTables().size() > 0) {
				String bindingString = "";
				String bindingStrength = "";
				String bindingLocation = "";
				for (TableLink tl : c.getTables()) {
					Table table = tableService.findById(tl.getId());
					if (table != null && table.getBindingIdentifier() != null
							&& !table.getBindingIdentifier().equals(""))
						bindingString = bindingString + table.getBindingIdentifier() + ":";
				}

				if (!bindingString.equals(""))
					elmComponent.addAttribute(
							new Attribute("Binding", bindingString.substring(0, bindingString.length() - 1)));
				if (!bindingStrength.equals("")) {
					elmComponent.addAttribute(new Attribute("BindingStrength", bindingStrength));
				}

				if (!bindingLocation.equals("")) {
					elmComponent.addAttribute(new Attribute("BindingLocation", bindingLocation));
				} else {
					Datatype d = datatypeService.findById(c.getDatatype().getId());
					if (d != null && d.getComponents() != null && d.getComponents().size() > 0) {
						elmComponent.addAttribute(new Attribute("BindingLocation", "1"));
					}
				}

			}
		}
		Predicate componentPredicate = this.findPredicate(fieldDatatype.getPredicates(), c.getPosition() + "[1]",
				message.getPredicates(), componentPath);
		if (componentPredicate != null) {
			nu.xom.Element elmPredicate = new nu.xom.Element("Predicate");
			elmPredicate.addAttribute(new Attribute("TrueUsage", "" + componentPredicate.getTrueUsage()));
			elmPredicate.addAttribute(new Attribute("FalseUsage", "" + componentPredicate.getFalseUsage()));

			nu.xom.Element elmDescription = new nu.xom.Element("Description");
			elmDescription.appendChild(componentPredicate.getDescription());
			elmPredicate.appendChild(elmDescription);

			nu.xom.Node n = this.innerXMLHandler(componentPredicate.getAssertion());
			if (n != null)
				elmPredicate.appendChild(n);

			elmComponent.appendChild(elmPredicate);
		}

		List<ConformanceStatement> componentConformanceStatements = this.findConformanceStatements(
				fieldDatatype.getConformanceStatements(), c.getPosition() + "[1]", message.getConformanceStatements(),
				componentPath);

		if (componentConformanceStatements.size() > 0) {
			nu.xom.Element elmConformanceStatements = new nu.xom.Element("ConformanceStatements");

			for (ConformanceStatement cs : componentConformanceStatements) {
				nu.xom.Element elmConformanceStatement = new nu.xom.Element("ConformanceStatement");
				elmConformanceStatement.addAttribute(new Attribute("ID", "" + cs.getConstraintId()));
				nu.xom.Element elmDescription = new nu.xom.Element("Description");
				elmDescription.appendChild(cs.getDescription());
				elmConformanceStatement.appendChild(elmDescription);

				nu.xom.Node n = this.innerXMLHandler(cs.getAssertion());
				if (n != null)
					elmConformanceStatement.appendChild(n);

				elmConformanceStatements.appendChild(elmConformanceStatement);
			}

			elmComponent.appendChild(elmConformanceStatements);
		}

		nu.xom.Element elmComponentStructure = new nu.xom.Element("Structure");
		Map<Integer, Component> subComponents = new HashMap<Integer, Component>();

		for (Component sc : componentDatatype.getComponents()) {
			subComponents.put(sc.getPosition(), sc);
		}

		if (subComponents.size() > 0) {
			elmComponent.appendChild(elmComponentStructure);
		}

		for (int k = 1; k < subComponents.size() + 1; k++) {
			String subComponentPath = componentPath + "." + k + "[1]";
			Component sc = subComponents.get(k);
			this.serializeDisplaySubComponent(sc, datatypeService.findById(sc.getDatatype().getId()),
					elmComponentStructure, profile, message, componentDatatype, subComponentPath);
		}
		elmParent.appendChild(elmComponent);
	}

	private void serializeGazelleSubComponent(Component sc, Datatype subComponentDatatype, nu.xom.Element elmParent,
			Profile profile, Message message, Datatype componentDatatype, String subComponentPath) {
		nu.xom.Element elmSubComponent = new nu.xom.Element("SubComponent");
		elmSubComponent.addAttribute(new Attribute("Name", serializationUtil.str(sc.getName())));
		if (sc.getUsage().value().equals("B")) {
			elmSubComponent.addAttribute(new Attribute("Usage", "X"));
		} else {
			elmSubComponent.addAttribute(new Attribute("Usage", serializationUtil.str(sc.getUsage().value())));
		}
		elmSubComponent.addAttribute(new Attribute("Datatype", serializationUtil.str(subComponentDatatype.getName())));
		if (sc.getMaxLength() != null && !sc.getMaxLength().equals("")) {
			if (sc.getMaxLength().equals("*")) {
				elmSubComponent.addAttribute(new Attribute("Length", "" + 225));
			} else if (sc.getMaxLength().equals("0")) {
				elmSubComponent.addAttribute(new Attribute("Length", "" + 1));
			} else {
				elmSubComponent.addAttribute(new Attribute("Length", serializationUtil.str(sc.getMaxLength())));
			}
		}

		if (sc.getTables() != null) {
			if (sc.getTables().size() > 0) {
				String bindingString = "";
				for (TableLink tl : sc.getTables()) {
					Table table = tableService.findById(tl.getId());
					if (table != null && table.getBindingIdentifier() != null
							&& !table.getBindingIdentifier().equals(""))
						bindingString = bindingString + table.getBindingIdentifier() + ":";
				}

				if (!bindingString.equals(""))
					elmSubComponent.addAttribute(
							new Attribute("Table", bindingString.substring(0, bindingString.length() - 1)));
			}
		}

		List<ConformanceStatement> subComponentConformanceStatements = this.findConformanceStatements(
				componentDatatype.getConformanceStatements(), sc.getPosition() + "[1]",
				message.getConformanceStatements(), subComponentPath);
		if (subComponentConformanceStatements.size() > 0) {
			nu.xom.Element elmImpNote = new nu.xom.Element("ImpNote");
			String note = "";
			for (ConformanceStatement cs : subComponentConformanceStatements) {
				note = note + "\n" + "[" + cs.getConstraintId() + "]" + cs.getDescription();
			}
			elmImpNote.appendChild(note);
			elmSubComponent.appendChild(elmImpNote);
		}

		Predicate subComponentPredicate = this.findPredicate(componentDatatype.getPredicates(),
				sc.getPosition() + "[1]", message.getPredicates(), subComponentPath);
		if (subComponentPredicate != null) {
			nu.xom.Element elmPredicate = new nu.xom.Element("Predicate");
			String note = "[C(" + subComponentPredicate.getTrueUsage() + "/" + subComponentPredicate.getFalseUsage()
					+ ")]" + subComponentPredicate.getDescription();
			elmPredicate.appendChild(note);
			elmSubComponent.appendChild(elmPredicate);
		}

		elmParent.appendChild(elmSubComponent);
	}

	private void serializeDisplaySubComponent(Component sc, Datatype subComponentDatatype, nu.xom.Element elmParent,
			Profile profile, Message message, Datatype componentDatatype, String subComponentPath) {
		nu.xom.Element elmSubComponent = new nu.xom.Element("SubComponent");
		elmSubComponent.addAttribute(new Attribute("Name", serializationUtil.str(sc.getName())));
		elmSubComponent.addAttribute(new Attribute("Usage", serializationUtil.str(sc.getUsage().toString())));
		elmSubComponent.addAttribute(new Attribute("Datatype", serializationUtil.str(subComponentDatatype.getName())));
		elmSubComponent.addAttribute(new Attribute("Flavor", serializationUtil.str(subComponentDatatype.getLabel())));
		elmSubComponent.addAttribute(new Attribute("MinLength", "" + sc.getMinLength()));
		if (sc.getMaxLength() != null && !sc.getMaxLength().equals(""))
			elmSubComponent.addAttribute(new Attribute("MaxLength", serializationUtil.str(sc.getMaxLength())));
		if (sc.getConfLength() != null && !sc.getConfLength().equals(""))
			elmSubComponent.addAttribute(new Attribute("ConfLength", serializationUtil.str(sc.getConfLength())));

		if (sc.getTables() != null) {
			if (sc.getTables().size() > 0) {
				String bindingString = "";
				String bindingStrength = "";
				String bindingLocation = "";
				for (TableLink tl : sc.getTables()) {
					Table table = tableService.findById(tl.getId());
					if (table != null && table.getBindingIdentifier() != null
							&& !table.getBindingIdentifier().equals(""))
						bindingString = bindingString + table.getBindingIdentifier() + ":";
				}

				if (!bindingString.equals(""))
					elmSubComponent.addAttribute(
							new Attribute("Binding", bindingString.substring(0, bindingString.length() - 1)));
				if (!bindingStrength.equals("")) {
					elmSubComponent.addAttribute(new Attribute("BindingStrength", bindingStrength));
				}

				if (!bindingLocation.equals("")) {
					elmSubComponent.addAttribute(new Attribute("BindingLocation", bindingLocation));
				} else {
					Datatype d = datatypeService.findById(sc.getDatatype().getId());
					if (d != null && d.getComponents() != null && d.getComponents().size() > 0) {
						elmSubComponent.addAttribute(new Attribute("BindingLocation", "1"));
					}
				}

			}
		}

		Predicate subComponentPredicate = this.findPredicate(componentDatatype.getPredicates(),
				sc.getPosition() + "[1]", message.getPredicates(), subComponentPath);
		if (subComponentPredicate != null) {
			nu.xom.Element elmPredicate = new nu.xom.Element("Predicate");
			elmPredicate.addAttribute(new Attribute("TrueUsage", "" + subComponentPredicate.getTrueUsage()));
			elmPredicate.addAttribute(new Attribute("FalseUsage", "" + subComponentPredicate.getFalseUsage()));

			nu.xom.Element elmDescription = new nu.xom.Element("Description");
			elmDescription.appendChild(subComponentPredicate.getDescription());
			elmPredicate.appendChild(elmDescription);

			nu.xom.Node n = this.innerXMLHandler(subComponentPredicate.getAssertion());
			if (n != null)
				elmPredicate.appendChild(n);

			elmSubComponent.appendChild(elmPredicate);
		}

		List<ConformanceStatement> subComponentConformanceStatements = this.findConformanceStatements(
				componentDatatype.getConformanceStatements(), sc.getPosition() + "[1]",
				message.getConformanceStatements(), subComponentPath);

		if (subComponentConformanceStatements.size() > 0) {
			nu.xom.Element elmConformanceStatements = new nu.xom.Element("ConformanceStatements");

			for (ConformanceStatement cs : subComponentConformanceStatements) {
				nu.xom.Element elmConformanceStatement = new nu.xom.Element("ConformanceStatement");
				elmConformanceStatement.addAttribute(new Attribute("ID", "" + cs.getConstraintId()));
				nu.xom.Element elmDescription = new nu.xom.Element("Description");
				elmDescription.appendChild(cs.getDescription());
				elmConformanceStatement.appendChild(elmDescription);

				nu.xom.Node n = this.innerXMLHandler(cs.getAssertion());
				if (n != null)
					elmConformanceStatement.appendChild(n);

				elmConformanceStatements.appendChild(elmConformanceStatement);
			}

			elmSubComponent.appendChild(elmConformanceStatements);
		}
		elmParent.appendChild(elmSubComponent);
	}

	private Mapping findMapping(List<Mapping> mappings, int i) {
		for (Mapping m : mappings) {
			if (m.getPosition().equals(i))
				return m;
		}

		return null;
	}

	private nu.xom.Node innerXMLHandler(String xml) {
		Builder builder = new Builder(new NodeFactory());
		try {
			nu.xom.Document doc = builder.build(xml, null);
			return doc.getRootElement().copy();
		} catch (ValidityException e) {
			e.printStackTrace();
		} catch (ParsingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String findValueSetID(List<ValueSetOrSingleCodeBinding> valueSetBindings, String referenceLocation) {
		for (ValueSetOrSingleCodeBinding vsb : valueSetBindings) {
			if (vsb.getLocation().equals(referenceLocation))
				return vsb.getTableId();
		}
		return null;
	}

	private nu.xom.Element serializeSegment(Segment s, Map<String, Table> tablesMap, Map<String, Datatype> datatypesMap) {
		nu.xom.Element elmSegment = new nu.xom.Element("Segment");
		elmSegment.addAttribute(new Attribute("ID", s.getLabel() + "_" + s.getHl7Version().replaceAll("\\.", "-")));
		elmSegment.addAttribute(new Attribute("Name", serializationUtil.str(s.getName())));
		elmSegment.addAttribute(new Attribute("Label", serializationUtil.str(s.getLabel())));
		elmSegment.addAttribute(new Attribute("Description", serializationUtil.str(s.getDescription())));
		
		if(s.getName().equals("OBX") || s.getName().equals("MFA") || s.getName().equals("MFE")){
			String targetPosition = null;
			String reference = null;
			String secondReference = null;
			String referenceTableId = null;
			HashMap<String, Datatype> dm = new HashMap<String, Datatype>();
			HashMap<String, Datatype> dm2nd = new HashMap<String, Datatype>();
			
			if(s.getName().equals("OBX")){
				targetPosition = "5";
				reference = "2";
			}
			
			if(s.getName().equals("MFA")){
				targetPosition = "5";
				reference = "6";
			}
			
			if(s.getName().equals("MFE")){
				targetPosition = "4";
				reference = "5";
			}
			
			if(s.getCoConstraintsTable() != null && s.getCoConstraintsTable().getIfColumnDefinition() != null){
				if(s.getCoConstraintsTable().getIfColumnDefinition().isPrimitive()){
					secondReference = s.getCoConstraintsTable().getIfColumnDefinition().getPath();
				}else {
					secondReference = s.getCoConstraintsTable().getIfColumnDefinition().getPath() + ".1";
				}
			}
			
			referenceTableId = this.findValueSetID(s.getValueSetBindings(), reference);
			
			if(referenceTableId != null){
				Table table = tablesMap.get(referenceTableId);
				if(table != null){
					for(Code c: table.getCodes()){
						if(c.getValue() != null && table.getHl7Version() != null){
							Datatype d = datatypeService.findByNameAndVesionAndScope(c.getValue(), table.getHl7Version(), "HL7STANDARD");
							
							if(d != null){
								this.addDatatype(d, datatypesMap);
								dm.put(c.getValue(), d);
							}							
						}
					}
				}
				
				for(DynamicMappingItem item : s.getDynamicMappingDefinition().getDynamicMappingItems()){
					if(item.getFirstReferenceValue() != null && item.getDatatypeId() != null)
					dm.put(item.getFirstReferenceValue(), datatypesMap.get(item.getDatatypeId()));
				}
			}
			if(secondReference != null){
				for(CoConstraintColumnDefinition definition :s.getCoConstraintsTable().getThenColumnDefinitionList()){
					if(definition.isdMReference()){
						List<CoConstraintTHENColumnData> dataList = s.getCoConstraintsTable().getThenMapData().get(definition.getId());
						
						if(dataList != null && s.getCoConstraintsTable().getIfColumnData() != null){
							for(int i=0; i<dataList.size(); i++){
								CoConstraintIFColumnData ref = s.getCoConstraintsTable().getIfColumnData().get(i);
								CoConstraintTHENColumnData data = dataList.get(i);
								
								if(ref != null && data != null && ref.getValueData() != null && ref.getValueData().getValue() != null && data.getDatatypeId() != null && data.getValueData() != null && data.getValueData().getValue() != null){
									dm2nd.put(ref.getValueData().getValue(), datatypesMap.get(data.getDatatypeId()));
								}
							}
						}
					}
				}
			}
			
			
			if(dm.size() > 0 || dm2nd.size() > 0){
				nu.xom.Element elmDynamicMapping = new nu.xom.Element("DynamicMapping");
				nu.xom.Element elmMapping = new nu.xom.Element("Mapping");
				elmMapping.addAttribute(new Attribute("Position", targetPosition));
				elmMapping.addAttribute(new Attribute("Reference", reference));
				if(secondReference != null) elmMapping.addAttribute(new Attribute("SecondReference", secondReference));
				
				for(String key :dm.keySet()){
					nu.xom.Element elmCase = new nu.xom.Element("Case");
					Datatype d = dm.get(key);
					elmCase.addAttribute(new Attribute("Value", d.getName()));
					elmCase.addAttribute(new Attribute("Datatype", d.getLabel() + "_" + d.getHl7Version().replaceAll("\\.", "-")));
					elmMapping.appendChild(elmCase);
				}
				
				for(String key : dm2nd.keySet()){
					nu.xom.Element elmCase = new nu.xom.Element("Case");
					Datatype d = dm2nd.get(key);
					elmCase.addAttribute(new Attribute("Value", d.getName()));
					elmCase.addAttribute(new Attribute("SecondValue", key));
					elmCase.addAttribute(new Attribute("Datatype", d.getLabel() + "_" + d.getHl7Version().replaceAll("\\.", "-")));
					elmMapping.appendChild(elmCase);
				}
				elmDynamicMapping.appendChild(elmMapping);
				elmSegment.appendChild(elmDynamicMapping);
			}
		}

		Map<Integer, Field> fields = new HashMap<Integer, Field>();

		for (Field f : s.getFields()) {
			fields.put(f.getPosition(), f);
		}

		for (int i = 1; i < fields.size() + 1; i++) {
			
			Field f = fields.get(i);
			System.out.println(s.getLabel());
			System.out.println(f.getPosition());
			System.out.println(f.getDatatype().getId());
			Datatype d = datatypesMap.get(f.getDatatype().getId());
			System.out.println(f.getDatatype().getId());
			nu.xom.Element elmField = new nu.xom.Element("Field");
			elmField.addAttribute(new Attribute("Name", serializationUtil.str(f.getName())));
			elmField.addAttribute(new Attribute("Usage", serializationUtil.str(f.getUsage().toString())));
			elmField.addAttribute(new Attribute("Datatype", serializationUtil.str(d.getLabel() + "_" + d.getHl7Version().replaceAll("\\.", "-"))));
			elmField.addAttribute(new Attribute("MinLength", "" + f.getMinLength()));
			if (f.getMaxLength() != null && !f.getMaxLength().equals(""))
				elmField.addAttribute(new Attribute("MaxLength", serializationUtil.str(f.getMaxLength())));
			if (f.getConfLength() != null && !f.getConfLength().equals(""))
				elmField.addAttribute(new Attribute("ConfLength", serializationUtil.str(f.getConfLength())));
			
			List<ValueSetBinding> bindings = findBinding(s.getValueSetBindings(), f.getPosition());
			if(bindings.size() > 0){
				String bindingString = "";
				String bindingStrength = null;
				String bindingLocation = null;
				
				for(ValueSetBinding binding:bindings){
					Table table = tablesMap.get(binding.getTableId());
					bindingStrength = binding.getBindingStrength().toString();
					bindingLocation = binding.getBindingLocation();
					if (table != null && table.getBindingIdentifier() != null && !table.getBindingIdentifier().equals("")) {
						if (table.getHl7Version() != null && !table.getHl7Version().equals("")) {
							bindingString = bindingString + table.getBindingIdentifier() + "_" + table.getHl7Version().replaceAll("\\.", "-") + ":";
						} else {
							bindingString = bindingString + table.getBindingIdentifier() + ":";
						}
					}
				}
				
				if (!bindingString.equals(""))
					elmField.addAttribute(new Attribute("Binding", bindingString.substring(0, bindingString.length() - 1)));
				if (bindingStrength != null)
					elmField.addAttribute(new Attribute("BindingStrength", bindingStrength));

				if(d != null && d.getComponents() != null && d.getComponents().size() > 0){
					if(bindingLocation != null){
						bindingLocation = bindingLocation.replaceAll("\\s+","").replaceAll("or", ":");
					}else{
						elmField.addAttribute(new Attribute("BindingLocation", "1"));
					}
				}
				
			}

			if (f.isHide()) elmField.addAttribute(new Attribute("Hide", "true"));
			elmField.addAttribute(new Attribute("Min", "" + f.getMin()));
			elmField.addAttribute(new Attribute("Max", "" + f.getMax()));
			if (f.getItemNo() != null && !f.getItemNo().equals(""))
				elmField.addAttribute(new Attribute("ItemNo", serializationUtil.str(f.getItemNo())));
			elmSegment.appendChild(elmField);
		}

		return elmSegment;
	}

	private List<ValueSetBinding> findBinding(List<ValueSetOrSingleCodeBinding> valueSetBindings, Integer position) {
		List<ValueSetBinding> result = new ArrayList<ValueSetBinding>();
		if(valueSetBindings != null && position != null){
			for(ValueSetOrSingleCodeBinding binding:valueSetBindings){
				if(binding instanceof ValueSetBinding){
					ValueSetBinding valueSetBinding = (ValueSetBinding)binding;
					
					if(valueSetBinding.getLocation().equals("" + position)){
						result.add(valueSetBinding);
					}
				}
			}
		}
		return result;
	}

	private nu.xom.Element serializeDatatypeForValidation(Datatype d, Map<String, Table> tablesMap, Map<String, Datatype> datatypesMap) {
		nu.xom.Element elmDatatype = new nu.xom.Element("Datatype");
		elmDatatype.addAttribute(new Attribute("ID",serializationUtil.str(d.getLabel() + "_" + d.getHl7Version().replaceAll("\\.", "-"))));
		elmDatatype.addAttribute(new Attribute("Name", serializationUtil.str(d.getName())));
		elmDatatype.addAttribute(new Attribute("Label", serializationUtil.str(d.getLabel())));
		elmDatatype.addAttribute(new Attribute("Description", serializationUtil.str(d.getDescription())));

		if (d.getComponents() != null) {

			Map<Integer, Component> components = new HashMap<Integer, Component>();

			for (Component c : d.getComponents()) {
				components.put(c.getPosition(), c);
			}

			for (int i = 1; i < components.size() + 1; i++) {
				Component c = components.get(i);
				Datatype componentDatatype = datatypesMap.get(c.getDatatype().getId());
				nu.xom.Element elmComponent = new nu.xom.Element("Component");
				elmComponent.addAttribute(new Attribute("Name", serializationUtil.str(c.getName())));
				elmComponent.addAttribute(new Attribute("Usage", serializationUtil.str(c.getUsage().toString())));
				elmComponent.addAttribute(new Attribute("Datatype", serializationUtil.str(componentDatatype.getLabel()
						+ "_" + componentDatatype.getHl7Version().replaceAll("\\.", "-"))));
				elmComponent.addAttribute(new Attribute("MinLength", "" + c.getMinLength()));
				if (c.getMaxLength() != null && !c.getMaxLength().equals(""))
					elmComponent.addAttribute(new Attribute("MaxLength", serializationUtil.str(c.getMaxLength())));
				if (c.getConfLength() != null && !c.getConfLength().equals(""))
					elmComponent.addAttribute(new Attribute("ConfLength", serializationUtil.str(c.getConfLength())));
				
				List<ValueSetBinding> bindings = findBinding(d.getValueSetBindings(), c.getPosition());
				if(bindings.size() > 0){
					String bindingString = "";
					String bindingStrength = null;
					String bindingLocation = null;
					
					for(ValueSetBinding binding:bindings){
						Table table = tablesMap.get(binding.getTableId());
						bindingStrength = binding.getBindingStrength().toString();
						bindingLocation = binding.getBindingLocation();
						if (table != null && table.getBindingIdentifier() != null && !table.getBindingIdentifier().equals("")) {
							if (table.getHl7Version() != null && !table.getHl7Version().equals("")) {
								bindingString = bindingString + table.getBindingIdentifier() + "_" + table.getHl7Version().replaceAll("\\.", "-") + ":";
							} else {
								bindingString = bindingString + table.getBindingIdentifier() + ":";
							}
						}
					}
					
					if (!bindingString.equals(""))
						elmComponent.addAttribute(new Attribute("Binding", bindingString.substring(0, bindingString.length() - 1)));
					if (bindingStrength != null)
						elmComponent.addAttribute(new Attribute("BindingStrength", bindingStrength));

					if(componentDatatype != null && componentDatatype.getComponents() != null && componentDatatype.getComponents().size() > 0){
						if(bindingLocation != null){
							bindingLocation = bindingLocation.replaceAll("\\s+","").replaceAll("or", ":");
						}else{
							elmComponent.addAttribute(new Attribute("BindingLocation", "1"));
						}
					}
					
				}

				if (c.isHide())
					elmComponent.addAttribute(new Attribute("Hide", "true"));

				elmDatatype.appendChild(elmComponent);
			}
		}
		return elmDatatype;
	}

	private nu.xom.Element serializeDatatypeWithConstraints(DatatypeLink dl, Datatype d, TableLibrary tables,
			DatatypeLibrary datatypeLib) {
		nu.xom.Element elmDatatype = new nu.xom.Element("Datatype");
		elmDatatype.addAttribute(new Attribute("Name", serializationUtil.str(d.getName())));
		elmDatatype.addAttribute(new Attribute("Flavor", serializationUtil.str(d.getLabel())));
		elmDatatype.addAttribute(new Attribute("Description", serializationUtil.str(d.getDescription())));

		if (d.getComponents() != null) {

			Map<Integer, Component> components = new HashMap<Integer, Component>();

			for (Component c : d.getComponents()) {
				components.put(c.getPosition(), c);
			}

			for (int i = 1; i < components.size() + 1; i++) {
				Component c = components.get(i);
				Datatype componentDatatype = datatypeService.findById(c.getDatatype().getId());
				nu.xom.Element elmComponent = new nu.xom.Element("Component");
				elmComponent.addAttribute(new Attribute("Name", serializationUtil.str(c.getName())));
				elmComponent.addAttribute(new Attribute("Usage", serializationUtil.str(c.getUsage().toString())));
				elmComponent
						.addAttribute(new Attribute("Datatype", serializationUtil.str(componentDatatype.getName())));
				elmComponent.addAttribute(new Attribute("Flavor", serializationUtil.str(componentDatatype.getLabel())));
				elmComponent.addAttribute(new Attribute("MinLength", "" + c.getMinLength()));
				if (c.getMaxLength() != null && !c.getMaxLength().equals(""))
					elmComponent.addAttribute(new Attribute("MaxLength", serializationUtil.str(c.getMaxLength())));
				if (c.getConfLength() != null && !c.getConfLength().equals(""))
					elmComponent.addAttribute(new Attribute("ConfLength", serializationUtil.str(c.getConfLength())));

				if (c.getTables() != null) {
					if (c.getTables().size() > 0) {
						String bindingString = "";
						String bindingStrength = "";
						String bindingLocation = "";
						for (TableLink tl : c.getTables()) {
							Table table = tableService.findById(tl.getId());
							if (table != null && table.getBindingIdentifier() != null
									&& !table.getBindingIdentifier().equals(""))
								bindingString = bindingString + table.getBindingIdentifier() + ":";
						}

						if (!bindingString.equals(""))
							elmComponent.addAttribute(
									new Attribute("Binding", bindingString.substring(0, bindingString.length() - 1)));
						if (!bindingStrength.equals("")) {
							elmComponent.addAttribute(new Attribute("BindingStrength", bindingStrength));
						}

						if (!bindingLocation.equals("")) {
							elmComponent.addAttribute(new Attribute("BindingLocation", bindingLocation));
						} else {
							Datatype childD = datatypeService.findById(c.getDatatype().getId());
							if (childD != null && childD.getComponents() != null && childD.getComponents().size() > 0) {
								elmComponent.addAttribute(new Attribute("BindingLocation", "1"));
							}
						}

					}
				}

				Predicate componentPredicate = this.findPredicate(d.getPredicates(), c.getPosition() + "[1]", null,
						null);
				if (componentPredicate != null) {
					nu.xom.Element elmPredicate = new nu.xom.Element("Predicate");
					elmPredicate.addAttribute(new Attribute("TrueUsage", "" + componentPredicate.getTrueUsage()));
					elmPredicate.addAttribute(new Attribute("FalseUsage", "" + componentPredicate.getFalseUsage()));

					nu.xom.Element elmDescription = new nu.xom.Element("Description");
					elmDescription.appendChild(componentPredicate.getDescription());
					elmPredicate.appendChild(elmDescription);

					nu.xom.Node n = this.innerXMLHandler(componentPredicate.getAssertion());
					if (n != null)
						elmPredicate.appendChild(n);

					elmComponent.appendChild(elmPredicate);
				}

				List<ConformanceStatement> componentConformanceStatements = this
						.findConformanceStatements(d.getConformanceStatements(), c.getPosition() + "[1]", null, null);

				if (componentConformanceStatements.size() > 0) {
					nu.xom.Element elmConformanceStatements = new nu.xom.Element("ConformanceStatements");

					for (ConformanceStatement cs : componentConformanceStatements) {
						nu.xom.Element elmConformanceStatement = new nu.xom.Element("ConformanceStatement");
						elmConformanceStatement.addAttribute(new Attribute("ID", "" + cs.getConstraintId()));
						nu.xom.Element elmDescription = new nu.xom.Element("Description");
						elmDescription.appendChild(cs.getDescription());
						elmConformanceStatement.appendChild(elmDescription);

						nu.xom.Node n = this.innerXMLHandler(cs.getAssertion());
						if (n != null)
							elmConformanceStatement.appendChild(n);

						elmConformanceStatements.appendChild(elmConformanceStatement);
					}

					elmComponent.appendChild(elmConformanceStatements);
				}
				elmDatatype.appendChild(elmComponent);
			}
		}
		return elmDatatype;
	}

	private void deserializeMetaData(Profile profile, Element elmConformanceProfile) {
		profile.getMetaData().setProfileID(elmConformanceProfile.getAttribute("ID"));
		profile.getMetaData().setType(elmConformanceProfile.getAttribute("Type"));
		profile.getMetaData().setHl7Version(elmConformanceProfile.getAttribute("HL7Version"));
		profile.getMetaData().setSchemaVersion(elmConformanceProfile.getAttribute("SchemaVersion"));

		NodeList nodes = elmConformanceProfile.getElementsByTagName("MetaData");

		Element elmMetaData = (Element) nodes.item(0);
		profile.getMetaData().setName(elmMetaData.getAttribute("Name"));
		profile.getMetaData().setOrgName(elmMetaData.getAttribute("OrgName"));
		profile.getMetaData().setVersion(elmMetaData.getAttribute("Version"));
		profile.getMetaData().setDate(elmMetaData.getAttribute("Date"));
		profile.getMetaData().setSpecificationName(elmMetaData.getAttribute("SpecificationName"));
		profile.getMetaData().setStatus(elmMetaData.getAttribute("Status"));
		profile.getMetaData().setTopics(elmMetaData.getAttribute("Topics"));
	}

	private void deserializeEncodings(Profile profile, Element elmConformanceProfile) {
		NodeList nodes = elmConformanceProfile.getElementsByTagName("Encoding");
		if (nodes != null && nodes.getLength() != 0) {
			Set<String> encodingSet = new HashSet<String>();
			for (int i = 0; i < nodes.getLength(); i++) {
				encodingSet.add(nodes.item(i).getTextContent());
			}
			profile.getMetaData().setEncodings(encodingSet);
		}
	}

	private void deserializeMessages(Profile profile, Element elmConformanceProfile) {
		NodeList nodes = elmConformanceProfile.getElementsByTagName("Message");
		if (nodes != null && nodes.getLength() != 0) {
			Messages messagesObj = new Messages();
			for (int i = 0; i < nodes.getLength(); i++) {
				Message messageObj = new Message();
				Element elmMessage = (Element) nodes.item(i);
				messageObj.setMessageID(elmMessage.getAttribute("ID"));
				messageObj.setIdentifier(elmMessage.getAttribute("Identifier"));
				messageObj.setName(elmMessage.getAttribute("Name"));
				messageObj.setMessageType(elmMessage.getAttribute("Type"));
				messageObj.setEvent(elmMessage.getAttribute("Event"));
				messageObj.setStructID(elmMessage.getAttribute("StructID"));
				messageObj.setDescription(elmMessage.getAttribute("Description"));

				messageObj.setPredicates(this.findPredicates(this.predicates.getMessages(),
						elmMessage.getAttribute("ID"), elmMessage.getAttribute("StructID")));
				messageObj
						.setConformanceStatements(this.findConformanceStatement(this.conformanceStatement.getMessages(),
								elmMessage.getAttribute("ID"), elmMessage.getAttribute("StructID")));

				this.deserializeSegmentRefOrGroups(elmConformanceProfile, messageObj, elmMessage,
						profile.getSegmentLibrary(), profile.getDatatypeLibrary());
				messagesObj.addMessage(messageObj);
			}
			profile.setMessages(messagesObj);
		}
	}

	private void deserializeSegmentRefOrGroups(Element elmConformanceProfile, Message messageObj, Element elmMessage,
			SegmentLibrary segments, DatatypeLibrary datatypes) {
		List<SegmentRefOrGroup> segmentRefOrGroups = new ArrayList<SegmentRefOrGroup>();
		NodeList nodes = elmMessage.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getNodeName().equals("Segment")) {
				this.deserializeSegmentRef(elmConformanceProfile, segmentRefOrGroups, (Element) nodes.item(i), segments,
						datatypes);
			} else if (nodes.item(i).getNodeName().equals("Group")) {
				this.deserializeGroup(elmConformanceProfile, segmentRefOrGroups, (Element) nodes.item(i), segments,
						datatypes);
			}
		}

		messageObj.setChildren(segmentRefOrGroups);

	}

	private void deserializeSegmentRef(Element elmConformanceProfile, List<SegmentRefOrGroup> segmentRefOrGroups,
			Element segmentElm, SegmentLibrary segments, DatatypeLibrary datatypes) {
		SegmentRef segmentRefObj = new SegmentRef();
		segmentRefObj.setMax(segmentElm.getAttribute("Max"));
		segmentRefObj.setMin(new Integer(segmentElm.getAttribute("Min")));
		segmentRefObj.setUsage(Usage.fromValue(segmentElm.getAttribute("Usage")));
		// segmentRefObj.setRef(this.segmentsMap.get(segmentElm.getAttribute("Ref")).getId());
		segmentRefOrGroups.add(segmentRefObj);
	}

	private Segment deserializeSegment(Element segmentElm, Profile profile) {
		Segment segmentObj = new Segment();
		segmentObj.setDescription(segmentElm.getAttribute("Description"));
		if (segmentElm.getAttribute("Label") != null && !segmentElm.getAttribute("Label").equals("")) {
			segmentObj.setLabel(segmentElm.getAttribute("Label"));
		} else {
			segmentObj.setLabel(segmentElm.getAttribute("Name"));
		}
		segmentObj.setName(segmentElm.getAttribute("Name"));
		segmentObj.setPredicates(this.findPredicates(this.predicates.getSegments(), segmentElm.getAttribute("ID"),
				segmentElm.getAttribute("Name")));
		segmentObj.setConformanceStatements(this.findConformanceStatement(this.conformanceStatement.getSegments(),
				segmentElm.getAttribute("ID"), segmentElm.getAttribute("Name")));

		NodeList dynamicMapping = segmentElm.getElementsByTagName("Mapping");
		DynamicMapping dynamicMappingObj = null;
		if (dynamicMapping.getLength() > 0) {
			dynamicMappingObj = new DynamicMapping();
		}

		for (int i = 0; i < dynamicMapping.getLength(); i++) {
			Element mappingElm = (Element) dynamicMapping.item(i);
			Mapping mappingObj = new Mapping();
			mappingObj.setPosition(Integer.parseInt(mappingElm.getAttribute("Position")));
			mappingObj.setReference(Integer.parseInt(mappingElm.getAttribute("Reference")));
			NodeList cases = mappingElm.getElementsByTagName("Case");

			for (int j = 0; j < cases.getLength(); j++) {
				Element caseElm = (Element) cases.item(j);
				Case caseObj = new Case();
				caseObj.setValue(caseElm.getAttribute("Value"));
				caseObj.setDatatype(this.findDatatype(caseElm.getAttribute("Datatype"), profile).getId());

				mappingObj.addCase(caseObj);

			}

			dynamicMappingObj.addMapping(mappingObj);

		}

		if (dynamicMappingObj != null)
			segmentObj.setDynamicMapping(dynamicMappingObj);

		NodeList fields = segmentElm.getElementsByTagName("Field");
		for (int i = 0; i < fields.getLength(); i++) {
			Element fieldElm = (Element) fields.item(i);
			segmentObj.addField(this.deserializeField(fieldElm, segmentObj, profile, segmentElm.getAttribute("ID"), i));
		}
		return segmentObj;
	}

	private Field deserializeField(Element fieldElm, Segment segment, Profile profile, String segmentId, int position) {
		Field fieldObj = new Field();

		fieldObj.setName(fieldElm.getAttribute("Name"));
		fieldObj.setUsage(Usage.fromValue(fieldElm.getAttribute("Usage")));
		// fieldObj.setDatatype(this.findDatatype(fieldElm.getAttribute("Datatype"),
		// profile).getId());
		fieldObj.setMinLength(new Integer(fieldElm.getAttribute("MinLength")));
		fieldObj.setPosition(new Integer(fieldElm.getAttribute("Position")));
		if (fieldElm.getAttribute("MaxLength") != null) {
			fieldObj.setMaxLength(fieldElm.getAttribute("MaxLength"));
		}
		if (fieldElm.getAttribute("ConfLength") != null) {
			fieldObj.setConfLength(fieldElm.getAttribute("ConfLength"));
		}
		// if (fieldElm.getAttribute("Binding") != null) {
		// fieldObj.setTable(findTableIdByMappingId(fieldElm.getAttribute("Binding"),
		// profile.getTableLibrary()));
		// }
		// if (fieldElm.getAttribute("BindingStrength") != null) {
		// fieldObj.setBindingStrength(fieldElm.getAttribute("BindingStrength"));
		// }
		//
		// if (fieldElm.getAttribute("BindingLocation") != null) {
		// fieldObj.setBindingLocation(fieldElm.getAttribute("BindingLocation"));
		// }
		if (fieldElm.getAttribute("Hide") != null && fieldElm.getAttribute("Hide").equals("true")) {
			fieldObj.setHide(true);
		} else {
			fieldObj.setHide(false);
		}
		fieldObj.setMin(new Integer(fieldElm.getAttribute("Min")));
		fieldObj.setMax(fieldElm.getAttribute("Max"));
		if (fieldElm.getAttribute("ItemNo") != null) {
			fieldObj.setItemNo(fieldElm.getAttribute("ItemNo"));
		}
		return fieldObj;
	}

	private void deserializeGroup(Element elmConformanceProfile, List<SegmentRefOrGroup> segmentRefOrGroups,
			Element groupElm, SegmentLibrary segments, DatatypeLibrary datatypes) {
		Group groupObj = new Group();
		groupObj.setMax(groupElm.getAttribute("Max"));
		groupObj.setMin(new Integer(groupElm.getAttribute("Min")));
		groupObj.setName(groupElm.getAttribute("Name"));
		groupObj.setUsage(Usage.fromValue(groupElm.getAttribute("Usage")));

		List<SegmentRefOrGroup> childSegmentRefOrGroups = new ArrayList<SegmentRefOrGroup>();

		NodeList nodes = groupElm.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getNodeName().equals("Segment")) {
				this.deserializeSegmentRef(elmConformanceProfile, childSegmentRefOrGroups, (Element) nodes.item(i),
						segments, datatypes);
			} else if (nodes.item(i).getNodeName().equals("Group")) {
				this.deserializeGroup(elmConformanceProfile, childSegmentRefOrGroups, (Element) nodes.item(i), segments,
						datatypes);
			}
		}

		groupObj.setChildren(childSegmentRefOrGroups);

		segmentRefOrGroups.add(groupObj);
	}

	private Document stringToDom(String xmlSource) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setIgnoringComments(false);
		factory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			return builder.parse(new InputSource(new StringReader(xmlSource)));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public HashMap<String, Datatype> getDatatypesMap() {
		return datatypesMap;
	}

	public void setDatatypesMap(HashMap<String, Datatype> datatypesMap) {
		this.datatypesMap = datatypesMap;
	}

	public HashMap<String, Segment> getSegmentsMap() {
		return segmentsMap;
	}

	public void setSegmentsMap(HashMap<String, Segment> segmentsMap) {
		this.segmentsMap = segmentsMap;
	}

	private InputStream serializeProfileToZip(Profile profile, DocumentMetaData metadata, java.util.Date dateUpdated,
			Map<String, Segment> segmentsMap, 
			Map<String, Datatype> datatypesMap,
			Map<String, Table> tablesMap) throws IOException, CloneNotSupportedException {
		
		
		this.normalizeProfile(profile, segmentsMap, datatypesMap);
		
		
		ByteArrayOutputStream outputStream = null;
		byte[] bytes;
		outputStream = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(outputStream);

		String profileXMLStr = this.serializeProfileToDoc(profile, metadata, dateUpdated, segmentsMap, datatypesMap, tablesMap).toXML();
		String valueSetXMLStr = tableSerializationService.serializeTableLibraryUsingMapToXML(profile, metadata, tablesMap, dateUpdated);
		String constraintXMLStr = constraintsSerializationService.serializeConstraintsUsingMapToXML(profile, metadata, segmentsMap, datatypesMap, tablesMap, dateUpdated);

//		Exception profileError = null;
//		try {
//			new ProfileValidationServiceImpl().validate(profileXMLStr, "validation/profilesSchema/Profile.xsd");
//		} catch (Exception e) {
//			profileError = e;
//		}
//
//		Exception valueSetEreor = null;
//		try {
//			new ProfileValidationServiceImpl().validate(valueSetXMLStr, "validation/profilesSchema/ValueSets.xsd");
//		} catch (Exception e) {
//			valueSetEreor = e;
//		}
//
//		Exception constraintsError = null;
//		try {
//			new ProfileValidationServiceImpl().validate(constraintXMLStr,
//					"validation/profilesSchema/ConformanceContext.xsd");
//		} catch (Exception e) {
//			constraintsError = e;
//		}

		this.generateProfileIS(out, profileXMLStr);
		this.generateValueSetIS(out, valueSetXMLStr);
		this.generateConstraintsIS(out, constraintXMLStr);
//		this.generateProfileError(out, profileError);
//		this.generateValueSetError(out, valueSetEreor);
//		this.generateConstraintsError(out, constraintsError);

		out.close();
		bytes = outputStream.toByteArray();
		return new ByteArrayInputStream(bytes);
	}

	private void normalizeProfile(Profile profile, Map<String, Segment> segmentsMap, Map<String, Datatype> datatypesMap) throws CloneNotSupportedException {
		Map<String, Datatype> toBeAddedDTs = new HashMap<String, Datatype>();
		Map<String, Segment> toBeAddedSegs = new HashMap<String, Segment>();
		
		for(String key:datatypesMap.keySet()){
			Datatype d = datatypesMap.get(key);
			for(ValueSetOrSingleCodeBinding binding:d.getValueSetBindings()){
				if(binding instanceof ValueSetBinding){
					ValueSetBinding valueSetBinding = (ValueSetBinding)binding;
					List<ValueSetBinding> valueSetBindings = findvalueSetBinding(d.getValueSetBindings(), valueSetBinding.getLocation());
					List<String> pathList = new LinkedList<String> (Arrays.asList(valueSetBinding.getLocation().split("\\.")));


					if(pathList.size() > 1){
						Component c = d.findComponentByPosition(Integer.parseInt(pathList.remove(0)));
						
						Datatype childD = datatypesMap.get(c.getDatatype().getId());
						if(childD == null) childD = toBeAddedDTs.get(c.getDatatype().getId());
						Datatype copyD = childD.clone();
						
						int randumNum = new SecureRandom().nextInt(100000);
						copyD.setId(d.getId() + "_A" +  randumNum);
						String ext = d.getExt();
						if(ext == null) ext = "";
						copyD.setExt(ext + "_A" + randumNum);
						toBeAddedDTs.put(copyD.getId(), copyD);
						c.getDatatype().setId(copyD.getId());
						
						visitDatatype(pathList, copyD, datatypesMap, valueSetBindings, toBeAddedDTs);		
					}
				}
			}
		}
		
		for(String key:segmentsMap.keySet()){
			Segment s = segmentsMap.get(key);
			for(ValueSetOrSingleCodeBinding binding:s.getValueSetBindings()){
				if(binding instanceof ValueSetBinding){
					ValueSetBinding valueSetBinding = (ValueSetBinding)binding;
					List<ValueSetBinding> valueSetBindings = findvalueSetBinding(s.getValueSetBindings(), valueSetBinding.getLocation());
					List<String> pathList = new LinkedList<String> (Arrays.asList(valueSetBinding.getLocation().split("\\.")));

					if(pathList.size() > 1){
						Field f = s.findFieldByPosition(Integer.parseInt(pathList.remove(0)));
						
						Datatype d = datatypesMap.get(f.getDatatype().getId());
						if(d == null) d = toBeAddedDTs.get(f.getDatatype().getId());
						Datatype copyD = d.clone();
						
						int randumNum = new SecureRandom().nextInt(100000);
						copyD.setId(d.getId() + "_A" +  randumNum);
						String ext = d.getExt();
						if(ext == null) ext = "";
						copyD.setExt(ext + "_A" + randumNum);
						toBeAddedDTs.put(copyD.getId(), copyD);
						f.getDatatype().setId(copyD.getId());
			
						visitDatatype(pathList, copyD, datatypesMap, valueSetBindings, toBeAddedDTs);		
					}
					
				}
			}
			
		}
		
		for(Message m:profile.getMessages().getChildren()){
			for(ValueSetOrSingleCodeBinding binding:m.getValueSetBindings()){
				if(binding instanceof ValueSetBinding){
					ValueSetBinding valueSetBinding = (ValueSetBinding)binding;
					List<ValueSetBinding> valueSetBindings = findvalueSetBinding(m.getValueSetBindings(), valueSetBinding.getLocation());
					List<String> pathList = new LinkedList<String> ( Arrays.asList(valueSetBinding.getLocation().split("\\.")));
					SegmentRefOrGroup child = m.findChildByPosition(Integer.parseInt(pathList.remove(0)));
					visitGroupOrSegmentRef(pathList, child, segmentsMap, datatypesMap, valueSetBindings, toBeAddedDTs, toBeAddedSegs);					
				}
			}
		}
		System.out.println("TOBEADDED DT");
		for(String key:toBeAddedDTs.keySet()){
			System.out.println(key);
			datatypesMap.put(key, toBeAddedDTs.get(key));
		}
		System.out.println("TOBEADDED SEG");
		for(String key:toBeAddedSegs.keySet()){
			System.out.println(key);
			System.out.println(toBeAddedSegs.get(key).getLabel());
			segmentsMap.put(key, toBeAddedSegs.get(key));
		}		
	}

	private List<ValueSetBinding> findvalueSetBinding(List<ValueSetOrSingleCodeBinding> valueSetBindings, String location) {
		List<ValueSetBinding> resutls = new ArrayList<ValueSetBinding>();
		for(ValueSetOrSingleCodeBinding binding:valueSetBindings){
			if(binding instanceof ValueSetBinding){
				ValueSetBinding valueSetBinding = (ValueSetBinding)binding;
				if(valueSetBinding.getLocation().equals(location)) resutls.add(valueSetBinding);
			}
		}
		return resutls;
	}

	private void visitGroupOrSegmentRef(List<String> pathList, SegmentRefOrGroup srog, Map<String, Segment> segmentsMap, Map<String, Datatype> datatypesMap, List<ValueSetBinding> valueSetBindings, Map<String, Datatype> toBeAddedDTs, Map<String, Segment> toBeAddedSegs) throws CloneNotSupportedException {
		if(srog instanceof Group){
			Group g = (Group)srog;
			SegmentRefOrGroup child = g.findChildByPosition(Integer.parseInt(pathList.remove(0)));
			visitGroupOrSegmentRef(pathList, child, segmentsMap, datatypesMap, valueSetBindings, toBeAddedDTs, toBeAddedSegs);
		}else{
			SegmentRef sr = (SegmentRef)srog;
			System.out.println("REF id:::" + sr.getRef().getId());
			Segment s = segmentsMap.get(sr.getRef().getId());
			if(s == null) s = toBeAddedSegs.get(sr.getRef().getId());
			Segment copyS = s.clone();
			int randumNum = new SecureRandom().nextInt(100000);
			copyS.setId(s.getId() + "_A" +  randumNum);
			String ext = s.getExt();
			if(ext == null) ext = "";
			copyS.setExt(ext + "_A" + randumNum);

			if(pathList.size() == 1){
				List<ValueSetBinding> newValueSetBindings = new ArrayList<ValueSetBinding>();
				for(ValueSetBinding binding:valueSetBindings){
					ValueSetBinding newValueSetBinding = binding.clone();
					newValueSetBinding.setLocation(pathList.get(0));
					newValueSetBindings.add(newValueSetBinding);
				}
				List<ValueSetOrSingleCodeBinding> toBeDeleted = this.findToBeDeletedValueSetBindinigsByLocation(copyS.getValueSetBindings(), pathList.get(0));
				for(ValueSetOrSingleCodeBinding binding:toBeDeleted){
					copyS.getValueSetBindings().remove(binding);
				}
				copyS.getValueSetBindings().addAll(newValueSetBindings);
				
			}else if(pathList.size() > 1){
				Field f = copyS.findFieldByPosition(Integer.parseInt(pathList.remove(0)));
				Datatype d = datatypesMap.get(f.getDatatype().getId());
				if(d == null) d = toBeAddedDTs.get(f.getDatatype().getId());
				Datatype copyD = d.clone();
				
				randumNum = new SecureRandom().nextInt(100000);
				copyD.setId(d.getId() + "_A" +  randumNum);
				String ext2 = d.getExt();
				if(ext2 == null) ext2 = "";
				copyD.setExt(ext2 + "_A" + randumNum);
				toBeAddedDTs.put(copyD.getId(), copyD);
				System.out.println("------WOOOOO");
				System.out.println(copyD.getId());
				f.getDatatype().setId(copyD.getId());
				visitDatatype(pathList, copyD, datatypesMap, valueSetBindings, toBeAddedDTs);				
			}
			sr.getRef().setId(copyS.getId());
			toBeAddedSegs.put(copyS.getId(), copyS);
		}
	}

	private List<ValueSetOrSingleCodeBinding> findToBeDeletedValueSetBindinigsByLocation(List<ValueSetOrSingleCodeBinding> valueSetBindings, String location) {
		
		List<ValueSetOrSingleCodeBinding> toBeDeleted = new ArrayList<ValueSetOrSingleCodeBinding>();
		
		for(ValueSetOrSingleCodeBinding binding : valueSetBindings){
			if(binding.getLocation().equals(location)){
				toBeDeleted.add(binding);
			}
		}
		
		return toBeDeleted;
	}

	private void visitDatatype(List<String> pathList, Datatype datatype, Map<String, Datatype> datatypesMap, List<ValueSetBinding> valueSetBindings, Map<String, Datatype> toBeAddedDTs) throws CloneNotSupportedException {
		if(pathList.size() == 1){
			List<ValueSetBinding> newValueSetBindings = new ArrayList<ValueSetBinding>();
			for(ValueSetBinding binding:valueSetBindings){
				ValueSetBinding newValueSetBinding = binding.clone();
				newValueSetBinding.setLocation(pathList.get(0));
				newValueSetBindings.add(newValueSetBinding);
			}
			List<ValueSetOrSingleCodeBinding> toBeDeleted = this.findToBeDeletedValueSetBindinigsByLocation(datatype.getValueSetBindings(), pathList.get(0));
			
			for(ValueSetOrSingleCodeBinding binding:toBeDeleted){
				datatype.getValueSetBindings().remove(binding);
			}

			datatype.getValueSetBindings().addAll(newValueSetBindings);
		
		}else if(pathList.size() > 1){
			Component c = datatype.findComponentByPosition(Integer.parseInt(pathList.remove(0)));
			
			Datatype d = datatypesMap.get(c.getDatatype().getId());
			if(d == null) d = toBeAddedDTs.get(c.getDatatype().getId());
			Datatype copyD = d.clone();
			
			int randumNum = new SecureRandom().nextInt(100000);
			copyD.setId(d.getId() + "_A" +  randumNum);
			String ext = d.getExt();
			if(ext == null) ext = "";
			copyD.setExt(ext + "_A" + randumNum);
			toBeAddedDTs.put(copyD.getId(), copyD);
			c.getDatatype().setId(copyD.getId());
			visitDatatype(pathList, copyD, datatypesMap, valueSetBindings, toBeAddedDTs);	
		}
		
	}

	private InputStream serializeProfileGazelleToZip(Profile profile) throws IOException {
		ByteArrayOutputStream outputStream = null;
		byte[] bytes;
		outputStream = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(outputStream);

		this.generateGazelleProfileIS(out, this.serializeProfileGazelleToXML(profile));
		this.generateValueSetIS(out, tableSerializationService.serializeTableLibraryToGazelleXML(profile));

		out.close();
		bytes = outputStream.toByteArray();
		return new ByteArrayInputStream(bytes);
	}

	private InputStream serializeProfileDisplayToZip(List<Profile> profiles, DocumentMetaData metadata,
			Date dateUpdated) throws IOException {
		ByteArrayOutputStream outputStream = null;
		byte[] bytes;
		outputStream = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(outputStream);

		for (Profile p : profiles) {
			Message m = p.getMessages().getChildren().iterator().next();
			String folderName = m.getIdentifier() + "(" + m.getName() + ")";
			this.generateDisplayProfileIS(out, this.serializeProfileDisplayToXML(p, metadata, dateUpdated), folderName);
		}

		out.close();
		bytes = outputStream.toByteArray();
		return new ByteArrayInputStream(bytes);
	}

	@Override
	public InputStream serializeDatatypeToZip(DatatypeLibrary datatypeLibrary) throws IOException {
		ByteArrayOutputStream outputStream = null;
		byte[] bytes;
		outputStream = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(outputStream);

		this.generateDatatypeLibraryIS(out, this.serializeDatatypeLibraryToXML(datatypeLibrary));
		this.generateValueSetIS(out, tableSerializationService.serializeTableLibraryToXML(datatypeLibrary));

		out.close();
		bytes = outputStream.toByteArray();
		return new ByteArrayInputStream(bytes);
	}

	private void generateDatatypeLibraryIS(ZipOutputStream out, String dtLibXML) throws IOException {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry("Datatypes.xml"));
		InputStream inProfile = IOUtils.toInputStream(dtLibXML);
		int lenTP;
		while ((lenTP = inProfile.read(buf)) > 0) {
			out.write(buf, 0, lenTP);
		}
		out.closeEntry();
		inProfile.close();
	}

	private void generateProfileIS(ZipOutputStream out, String profileXML) throws IOException {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry("Profile.xml"));
		InputStream inProfile = IOUtils.toInputStream(profileXML);
		int lenTP;
		while ((lenTP = inProfile.read(buf)) > 0) {
			out.write(buf, 0, lenTP);
		}
		out.closeEntry();
		inProfile.close();
	}

	private void generateProfileError(ZipOutputStream out, Exception e) throws IOException {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry("ProfileValidation.txt"));
		InputStream inProfile = null;
		if (e == null) {
			inProfile = IOUtils.toInputStream("No error found!");
		} else {
			inProfile = IOUtils.toInputStream(
					e.getMessage() + System.getProperty("line.separator") + ProfileSerializationImpl.getStackTrace(e));
		}
		int lenTP;
		while ((lenTP = inProfile.read(buf)) > 0) {
			out.write(buf, 0, lenTP);
		}
		out.closeEntry();
		inProfile.close();
	}

	private void generateValueSetError(ZipOutputStream out, Exception e) throws IOException {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry("ValueSetValidation.txt"));
		InputStream inProfile = null;
		if (e == null) {
			inProfile = IOUtils.toInputStream("No error found!");
		} else {
			inProfile = IOUtils.toInputStream(
					e.getMessage() + System.getProperty("line.separator") + ProfileSerializationImpl.getStackTrace(e));
		}
		int lenTP;
		while ((lenTP = inProfile.read(buf)) > 0) {
			out.write(buf, 0, lenTP);
		}
		out.closeEntry();
		inProfile.close();
	}

	private void generateConstraintsError(ZipOutputStream out, Exception e) throws IOException {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry("ConstraintsValidation.txt"));
		InputStream inProfile = null;
		if (e == null) {
			inProfile = IOUtils.toInputStream("No error found!");
		} else {
			inProfile = IOUtils.toInputStream(
					e.getMessage() + System.getProperty("line.separator") + ProfileSerializationImpl.getStackTrace(e));
		}
		int lenTP;
		while ((lenTP = inProfile.read(buf)) > 0) {
			out.write(buf, 0, lenTP);
		}
		out.closeEntry();
		inProfile.close();
	}

	private static String getStackTrace(final Throwable throwable) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		throwable.printStackTrace(pw);
		return sw.getBuffer().toString();
	}

	private void generateGazelleProfileIS(ZipOutputStream out, String profileXML) throws IOException {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry("Gazelle_Profile.xml"));
		InputStream inProfile = IOUtils.toInputStream(profileXML);
		int lenTP;
		while ((lenTP = inProfile.read(buf)) > 0) {
			out.write(buf, 0, lenTP);
		}
		out.closeEntry();
		inProfile.close();
	}

	private void generateDisplayProfileIS(ZipOutputStream out, String profileXML, String name) throws IOException {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry(name + File.separator + "NIST_DisplayProfile.xml"));
		InputStream inProfile = IOUtils.toInputStream(profileXML);
		int lenTP;
		while ((lenTP = inProfile.read(buf)) > 0) {
			out.write(buf, 0, lenTP);
		}
		out.closeEntry();
		inProfile.close();
	}

	private void generateValueSetIS(ZipOutputStream out, String valueSetXML) throws IOException {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry("ValueSets.xml"));
		InputStream inValueSet = IOUtils.toInputStream(valueSetXML);
		int lenTP;
		while ((lenTP = inValueSet.read(buf)) > 0) {
			out.write(buf, 0, lenTP);
		}
		out.closeEntry();
		inValueSet.close();
	}

	private void generateConstraintsIS(ZipOutputStream out, String constraintsXML) throws IOException {
		byte[] buf = new byte[1024];
		out.putNextEntry(new ZipEntry("Constraints.xml"));
		InputStream inConstraints = IOUtils.toInputStream(constraintsXML);
		int lenTP;
		while ((lenTP = inConstraints.read(buf)) > 0) {
			out.write(buf, 0, lenTP);
		}
		out.closeEntry();
		inConstraints.close();
	}

	@Override
	public InputStream serializeProfileGazelleToZip(Profile original, String[] ids)
			throws IOException, CloneNotSupportedException {
		Profile filteredProfile = new Profile();

		HashMap<String, Segment> segmentsMap = new HashMap<String, Segment>();
		HashMap<String, Datatype> datatypesMap = new HashMap<String, Datatype>();

		filteredProfile.setBaseId(original.getBaseId());
		filteredProfile.setChanges(original.getChanges());
		filteredProfile.setComment(original.getComment());
		filteredProfile.setConstraintId(original.getConstraintId());
		filteredProfile.setScope(original.getScope());
		filteredProfile.setSectionContents(original.getSectionContents());
		filteredProfile.setSectionDescription(original.getSectionDescription());
		filteredProfile.setSectionPosition(original.getSectionPosition());
		filteredProfile.setSectionTitle(original.getSectionTitle());
		filteredProfile.setSourceId(original.getSourceId());
		filteredProfile.setType(original.getType());
		filteredProfile.setUsageNote(original.getUsageNote());
		filteredProfile.setMetaData(original.getMetaData());

		Messages messages = new Messages();
		for (Message m : original.getMessages().getChildren()) {
			if (Arrays.asList(ids).contains(m.getId())) {
				messages.addMessage(m);
				for (SegmentRefOrGroup seog : m.getChildren()) {
					this.visit(seog, segmentsMap, datatypesMap, original);
				}

			}
		}

		SegmentLibrary segments = new SegmentLibrary();
		for (String key : segmentsMap.keySet()) {

			segments.addSegment(segmentsMap.get(key));
		}

		DatatypeLibrary datatypes = new DatatypeLibrary();
		for (String key : datatypesMap.keySet()) {
			datatypes.addDatatype(datatypesMap.get(key));
		}

		filteredProfile.setDatatypeLibrary(datatypes);
		filteredProfile.setSegmentLibrary(segments);
		filteredProfile.setMessages(messages);
		filteredProfile.setTableLibrary(original.getTableLibrary());

		return this.serializeProfileGazelleToZip(filteredProfile);
	}
	
	
	//TODO
	@Override
	public InputStream serializeCompositeProfileToZip(IGDocument doc, String[] ids) throws IOException, CloneNotSupportedException {
		Map<String, Segment> segmentsMap = new HashMap<String, Segment>();
		Map<String, Datatype> datatypesMap = new HashMap<String, Datatype>();
		Map<String, Table> tablesMap = new HashMap<String, Table>();
		
		for(SegmentLink sl: doc.getProfile().getSegmentLibrary().getChildren()){
			if (sl != null) {
				Segment s = segmentService.findById(sl.getId());
				if (s != null) {
					segmentsMap.put(s.getId(), s);
				}
			}
		}
		
		for(DatatypeLink dl: doc.getProfile().getDatatypeLibrary().getChildren()){
			if (dl != null) {
				Datatype d = datatypeService.findById(dl.getId());
				if (d != null) {
					datatypesMap.put(d.getId(), d);
				}
			}
		}
		
		for (TableLink tl : doc.getProfile().getTableLibrary().getChildren()) {
			if (tl != null) {
				Table t = tableService.findById(tl.getId());
				if (t != null) {
					tablesMap.put(t.getId(), t);
				}
			}
		}
		
		
		
		Profile filteredProfile = new Profile();
		filteredProfile.setBaseId(doc.getProfile().getBaseId());
		filteredProfile.setChanges(doc.getProfile().getChanges());
		filteredProfile.setComment(doc.getProfile().getComment());
		filteredProfile.setConstraintId(doc.getProfile().getConstraintId());
		filteredProfile.setScope(doc.getProfile().getScope());
		filteredProfile.setSectionContents(doc.getProfile().getSectionContents());
		filteredProfile.setSectionDescription(doc.getProfile().getSectionDescription());
		filteredProfile.setSectionPosition(doc.getProfile().getSectionPosition());
		filteredProfile.setSectionTitle(doc.getProfile().getSectionTitle());
		filteredProfile.setSourceId(doc.getProfile().getSourceId());
		filteredProfile.setType(doc.getProfile().getType());
		filteredProfile.setUsageNote(doc.getProfile().getUsageNote());
		filteredProfile.setMetaData(doc.getProfile().getMetaData());
		
		Messages messages = new Messages();
		for(CompositeProfileStructure cps:doc.getProfile().getCompositeProfiles().getChildren()){
			if (Arrays.asList(ids).contains(cps.getId())){
				CompositeProfile cp = compositeProfileService.buildCompositeProfile(cps);
				segmentsMap.putAll(cp.getSegmentsMap());
				datatypesMap.putAll(cp.getDatatypesMap());
				messages.addMessage(cp.convertMessage());
			}
		}
	
		SegmentLibrary segments = new SegmentLibrary();
		for (String key : segmentsMap.keySet()) {
			segments.addSegment(segmentsMap.get(key));
		}

		DatatypeLibrary datatypes = new DatatypeLibrary();
		for (String key : datatypesMap.keySet()) {
			datatypes.addDatatype(datatypesMap.get(key));
		}

		TableLibrary tables = new TableLibrary();
		for (String key : tablesMap.keySet()) {
			tables.addTable(tablesMap.get(key));
		}

		filteredProfile.setDatatypeLibrary(datatypes);
		filteredProfile.setSegmentLibrary(segments);
		filteredProfile.setMessages(messages);
		filteredProfile.setTableLibrary(tables);

		return this.serializeProfileToZip(filteredProfile, doc.getMetaData(), doc.getDateUpdated(), segmentsMap, datatypesMap, tablesMap);
	}

	@Override
	public InputStream serializeProfileToZip(Profile original, String[] ids, DocumentMetaData metadata,
			Date dateUpdated) throws IOException, CloneNotSupportedException {
		Profile filteredProfile = new Profile();

		HashMap<String, Segment> segmentsMap = new HashMap<String, Segment>();
		HashMap<String, Datatype> datatypesMap = new HashMap<String, Datatype>();
		HashMap<String, Table> tablesMap = new HashMap<String, Table>();

		filteredProfile.setBaseId(original.getBaseId());
		filteredProfile.setChanges(original.getChanges());
		filteredProfile.setComment(original.getComment());
		filteredProfile.setConstraintId(original.getConstraintId());
		filteredProfile.setScope(original.getScope());
		filteredProfile.setSectionContents(original.getSectionContents());
		filteredProfile.setSectionDescription(original.getSectionDescription());
		filteredProfile.setSectionPosition(original.getSectionPosition());
		filteredProfile.setSectionTitle(original.getSectionTitle());
		filteredProfile.setSourceId(original.getSourceId());
		filteredProfile.setType(original.getType());
		filteredProfile.setUsageNote(original.getUsageNote());
		filteredProfile.setMetaData(original.getMetaData());

		Messages messages = new Messages();
		for (Message m : original.getMessages().getChildren()) {
			if (Arrays.asList(ids).contains(m.getId())) {
				if (m.getMessageID() == null)
					m.setMessageID(UUID.randomUUID().toString());
				messages.addMessage(m);
				for (SegmentRefOrGroup seog : m.getChildren()) {
					this.visit(seog, segmentsMap, datatypesMap, original);
				}

			}
		}

		for (TableLink tl : original.getTableLibrary().getChildren()) {
			if (tl != null) {
				Table t = tableService.findById(tl.getId());
				if (t != null) {
					tablesMap.put(t.getId(), t);
				}
			}
		}

		SegmentLibrary segments = new SegmentLibrary();
		for (String key : segmentsMap.keySet()) {
			segments.addSegment(segmentsMap.get(key));
		}

		DatatypeLibrary datatypes = new DatatypeLibrary();
		for (String key : datatypesMap.keySet()) {
			datatypes.addDatatype(datatypesMap.get(key));
		}

		TableLibrary tables = new TableLibrary();
		for (String key : tablesMap.keySet()) {
			tables.addTable(tablesMap.get(key));
		}

		filteredProfile.setDatatypeLibrary(datatypes);
		filteredProfile.setSegmentLibrary(segments);
		filteredProfile.setMessages(messages);
		filteredProfile.setTableLibrary(tables);

		return this.serializeProfileToZip(filteredProfile, metadata, dateUpdated, segmentsMap, datatypesMap, tablesMap);
	}

	@Override
	public InputStream serializeProfileDisplayToZip(Profile original, String[] ids, DocumentMetaData metadata,
			Date dateUpdated) throws IOException, CloneNotSupportedException {

		List<Profile> filteredProfiles = new ArrayList<Profile>();

		for (String id : ids) {
			Profile filteredProfile = new Profile();

			HashMap<String, Segment> segmentsMap = new HashMap<String, Segment>();
			HashMap<String, Datatype> datatypesMap = new HashMap<String, Datatype>();

			filteredProfile.setBaseId(original.getBaseId());
			filteredProfile.setChanges(original.getChanges());
			filteredProfile.setComment(original.getComment());
			filteredProfile.setConstraintId(original.getConstraintId());
			filteredProfile.setScope(original.getScope());
			filteredProfile.setSectionContents(original.getSectionContents());
			filteredProfile.setSectionDescription(original.getSectionDescription());
			filteredProfile.setSectionPosition(original.getSectionPosition());
			filteredProfile.setSectionTitle(original.getSectionTitle());
			filteredProfile.setSourceId(original.getSourceId());
			filteredProfile.setType(original.getType());
			filteredProfile.setUsageNote(original.getUsageNote());
			filteredProfile.setMetaData(original.getMetaData().clone());

			Messages messages = new Messages();
			for (Message m : original.getMessages().getChildren()) {
				if (id.equals(m.getId())) {
					filteredProfile.getMetaData().setProfileID(m.getMessageID());
					messages.addMessage(m);
					for (SegmentRefOrGroup seog : m.getChildren()) {
						this.visit(seog, segmentsMap, datatypesMap, original);
					}
				}
			}

			SegmentLibrary segments = new SegmentLibrary();
			for (String key : segmentsMap.keySet()) {
				segments.addSegment(segmentsMap.get(key));
			}

			DatatypeLibrary datatypes = new DatatypeLibrary();
			for (String key : datatypesMap.keySet()) {
				datatypes.addDatatype(datatypesMap.get(key));
			}

			filteredProfile.setDatatypeLibrary(datatypes);
			filteredProfile.setSegmentLibrary(segments);
			filteredProfile.setMessages(messages);
			filteredProfile.setTableLibrary(original.getTableLibrary());

			filteredProfiles.add(filteredProfile);
		}

		return this.serializeProfileDisplayToZip(filteredProfiles, metadata, dateUpdated);
	}

	private void visit(SegmentRefOrGroup seog, HashMap<String, Segment> segmentsMap,
			HashMap<String, Datatype> datatypesMap, Profile original) {
		if (seog instanceof SegmentRef) {
			SegmentRef sr = (SegmentRef) seog;

			Segment s = segmentService.findById(sr.getRef().getId());
			segmentsMap.put(s.getId(), s);

			for (Field f : s.getFields()) {
				this.addDatatype(datatypeService.findById(f.getDatatype().getId()), datatypesMap);
			}
			
			if(s.getDynamicMappingDefinition() != null){
				for (DynamicMappingItem item : s.getDynamicMappingDefinition().getDynamicMappingItems()) {
					if(item != null && item.getDatatypeId() != null){
						Datatype dt = datatypeService.findById(item.getDatatypeId());
						if (dt != null) {
							this.addDatatype(dt, datatypesMap);
						}	
					}
				}	
			}
			
			if(s.getCoConstraintsTable() != null && s.getCoConstraintsTable().getThenMapData() != null){
				for (String key : s.getCoConstraintsTable().getThenMapData().keySet()) {
					for (CoConstraintTHENColumnData data : s.getCoConstraintsTable().getThenMapData().get(key)) {
						if (data.getDatatypeId() != null) {
							Datatype dt = datatypeService.findById(data.getDatatypeId());
							if (dt != null) {
								this.addDatatype(dt, datatypesMap);
							}
						}
					}
				}				
			}

		} else {
			Group g = (Group) seog;
			for (SegmentRefOrGroup child : g.getChildren()) {
				this.visit(child, segmentsMap, datatypesMap, original);
			}
		}

	}

	private void addDatatype(Datatype d, Map<String, Datatype> datatypesMap) {
		if (d != null) {
			datatypesMap.put(d.getId(), d);
			for (Component c : d.getComponents()) {
				this.addDatatype(datatypeService.findById(c.getDatatype().getId()), datatypesMap);
			}
		}else {
			log.error("datatypelink is missing!");
		}
	}

	private String releaseConstraintId(String xmlConstraints) {
		if (xmlConstraints != null) {
			Document conformanceContextDoc = this.stringToDom(xmlConstraints);
			Element elmConformanceContext = (Element) conformanceContextDoc.getElementsByTagName("ConformanceContext")
					.item(0);
			return elmConformanceContext.getAttribute("UUID");
		}
		return null;
	}

	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}

	public String serializeProfileToXML(Profile p, DocumentMetaData documentMetaData, Date dateUpdated) {
		// TODO Auto-generated method stub
		return serializeProfileToDoc(p, documentMetaData, dateUpdated).toXML();
	}

}
