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

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Case;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segments;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Tables;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByID;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByName;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByNameOrByID;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraints;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Context;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.ExportUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nu.xom.Attribute;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ProfileSerializationImpl implements ProfileSerialization {
	Logger logger = LoggerFactory.getLogger( ProfileSerializationImpl.class );

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
		profile.setSegments(new Segments());
		profile.setDatatypes(new Datatypes());


		profile.setTables(new TableSerializationImpl().deserializeXMLToTableLibrary(xmlValueSet));
		
		this.conformanceStatement = new ConstraintsSerializationImpl().deserializeXMLToConformanceStatements(xmlConstraints);
		this.predicates = new ConstraintsSerializationImpl().deserializeXMLToPredicates(xmlConstraints);
		profile.setConstraintId(new ConstraintsSerializationImpl().releaseConstraintId(xmlConstraints));

		this.constructDatatypesMap((Element) elmConformanceProfile.getElementsByTagName("Datatypes").item(0), profile);

		Datatypes datatypes = new Datatypes();
		for (String key : datatypesMap.keySet()) {
			datatypes.addDatatype(datatypesMap.get(key));
		}
		profile.setDatatypes(datatypes);

		this.segmentsMap = this.constructSegmentsMap((Element) elmConformanceProfile.getElementsByTagName("Segments").item(0), profile);

		Segments segments = new Segments();
		for (String key : segmentsMap.keySet()) {
			segments.addSegment(segmentsMap.get(key));
		}
		profile.setSegments(segments);

		// Read Profile Messages
		this.deserializeMessages(profile, elmConformanceProfile);



		return profile;
	}

	@Override
	public Profile deserializeXMLToProfile(nu.xom.Document docProfile, nu.xom.Document docValueSet, nu.xom.Document docConstraints) {
		return this.deserializeXMLToProfile(docProfile.toXML(), docValueSet.toXML(), docConstraints.toXML());
	}

	@Override
	public String serializeProfileToXML(Profile profile) {
		return this.serializeProfileToDoc(profile).toXML();
	}

	@Override
	public nu.xom.Document serializeProfileToDoc(Profile profile) {
		nu.xom.Element e = new nu.xom.Element("ConformanceProfile");
		ProfileMetaData metaData = profile.getMetaData();
		
		if(profile.getMetaData().getProfileID() == null || profile.getMetaData().getProfileID().equals("")){
			e.addAttribute(new Attribute("ID", UUID.randomUUID().toString()));
		}else {
			e.addAttribute(new Attribute("ID", profile.getMetaData().getProfileID()));
		}
		
		if(metaData.getType() != null && !metaData.getType().equals("")) e.addAttribute(new Attribute("Type", ExportUtil.str(metaData.getType())));
		if(metaData.getHl7Version() != null && !metaData.getHl7Version().equals("")) e.addAttribute(new Attribute("HL7Version", ExportUtil.str(metaData.getHl7Version())));
		if(metaData.getSchemaVersion() != null && !metaData.getSchemaVersion().equals("")) e.addAttribute(new Attribute("SchemaVersion", ExportUtil.str(metaData.getSchemaVersion())));

		nu.xom.Element elmMetaData = new nu.xom.Element("MetaData");
		elmMetaData.addAttribute(new Attribute("Name", ExportUtil.str(metaData.getName())));
		elmMetaData.addAttribute(new Attribute("OrgName", ExportUtil.str(metaData.getOrgName())));
		elmMetaData.addAttribute(new Attribute("Version", ExportUtil.str(metaData.getVersion())));
		elmMetaData.addAttribute(new Attribute("Date", ExportUtil.str(metaData.getDate())));
		
		if (metaData.getSpecificationName() != null && !metaData.getSpecificationName().equals("")) 
			elmMetaData.addAttribute(new Attribute("SpecificationName",ExportUtil.str(metaData.getSpecificationName())));
		if (metaData.getStatus() != null && !metaData.getStatus().equals("")) 
			elmMetaData.addAttribute(new Attribute("Status", ExportUtil.str(metaData.getStatus())));
		if (metaData.getTopics() != null && !metaData.getTopics().equals("")) 
			elmMetaData.addAttribute(new Attribute("Topics", ExportUtil.str(metaData.getTopics())));

		e.appendChild(elmMetaData);

		if (profile.getMetaData().getEncodings() != null && profile.getMetaData().getEncodings().size() > 0) {
			nu.xom.Element elmEncodings = new nu.xom.Element("Encodings");
			for (String encoding : profile.getMetaData().getEncodings()) {
				nu.xom.Element elmEncoding = new nu.xom.Element("Encoding");
				elmEncoding.appendChild(encoding);
				elmEncodings.appendChild(elmEncoding);
			}
			e.appendChild(elmEncodings);
		}


		nu.xom.Element ms = new nu.xom.Element("Messages");
		for (Message m : profile.getMessages().getChildren()) {
			ms.appendChild(this.serializeMessage(m, profile.getSegments()));
		}
		e.appendChild(ms);

		nu.xom.Element ss = new nu.xom.Element("Segments");
		for (Segment s : profile.getSegments().getChildren()) {
			ss.appendChild(this.serializeSegment(s, profile.getTables(), profile.getDatatypes()));
		}
		e.appendChild(ss);

		nu.xom.Element ds = new nu.xom.Element("Datatypes");
		for (Datatype d : profile.getDatatypes().getChildren()) {
			ds.appendChild(this.serializeDatatype(d, profile.getTables(), profile.getDatatypes()));
		}
		e.appendChild(ds);

		nu.xom.Document doc = new nu.xom.Document(e);

		return doc;
	}

	private void constructDatatypesMap(Element elmDatatypes, Profile profile) {
		this.datatypesMap = new HashMap<String, Datatype>();
		NodeList datatypeNodeList = elmDatatypes.getElementsByTagName("Datatype");

		for (int i = 0; i < datatypeNodeList.getLength(); i++) {
			Element elmDatatype = (Element) datatypeNodeList.item(i);
			if (!datatypesMap.keySet().contains(elmDatatype.getAttribute("ID"))) {
				datatypesMap.put(elmDatatype.getAttribute("ID"), this.deserializeDatatype(elmDatatype, profile,elmDatatypes));
			}
		}
	}

	private Element getDatatypeElement(Element elmDatatypes, String id) {
		NodeList datatypeNodeList = elmDatatypes
				.getElementsByTagName("Datatype");
		for (int i = 0; i < datatypeNodeList.getLength(); i++) {
			Element elmDatatype = (Element) datatypeNodeList.item(i);
			if (id.equals(elmDatatype.getAttribute("ID"))) {
				return elmDatatype;
			}
		}
		return null;
	}

	private Datatype deserializeDatatype(Element elmDatatype, Profile profile, Element elmDatatypes) {
		String ID = elmDatatype.getAttribute("ID");
		if (!datatypesMap.keySet().contains(ID)) {
			Datatype datatypeObj = new Datatype();
			datatypeObj.setDescription(elmDatatype.getAttribute("Description"));
			if(elmDatatype.getAttribute("Label") != null &&  !elmDatatype.getAttribute("Label").equals("")){
				datatypeObj.setLabel(elmDatatype.getAttribute("Label"));
			}else{
				datatypeObj.setLabel(elmDatatype.getAttribute("Name"));
			}
			datatypeObj.setName(elmDatatype.getAttribute("Name"));
			datatypeObj.setPredicates(this.findPredicates(this.predicates.getDatatypes(), ID, elmDatatype.getAttribute("Name")));
			datatypeObj.setConformanceStatements(this.findConformanceStatement(this.conformanceStatement.getDatatypes(), ID, elmDatatype.getAttribute("Name")));

			NodeList nodes = elmDatatype.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				if (nodes.item(i).getNodeName().equals("Component")) {
					Element elmComponent = (Element) nodes.item(i);
					Component componentObj = new Component();
					componentObj.setName(elmComponent.getAttribute("Name"));
					componentObj.setUsage(Usage.fromValue(elmComponent.getAttribute("Usage")));
					Element elmDt = getDatatypeElement(elmDatatypes, elmComponent.getAttribute("Datatype"));
					Datatype datatype = this.deserializeDatatype(elmDt, profile, elmDatatypes);
					componentObj.setDatatype(datatype.getId());
					componentObj.setMinLength(new Integer(elmComponent.getAttribute("MinLength")));
					if (elmComponent.getAttribute("MaxLength") != null) {
						componentObj.setMaxLength(elmComponent.getAttribute("MaxLength"));
					}
					if (elmComponent.getAttribute("ConfLength") != null) {
						componentObj.setConfLength(elmComponent.getAttribute("ConfLength"));
					}

					if (elmComponent.getAttribute("Binding") != null) {
						componentObj.setTable(findTableIdByMappingId(elmComponent.getAttribute("Binding"), profile.getTables()));
					}

					if (elmComponent.getAttribute("BindingStrength") != null) {
						componentObj.setBindingStrength(elmComponent.getAttribute("BindingStrength"));
					}

					if (elmComponent.getAttribute("BindingLocation") != null) {
						componentObj.setBindingLocation(elmComponent.getAttribute("BindingLocation"));
					}

					if(elmComponent.getAttribute("Hide") != null && elmComponent.getAttribute("Hide").equals("true") ){
						componentObj.setHide(true);
					}else{
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
				}else if (byNameOrByID instanceof ByName) {
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

	// private Datatype findDatatype(String key, Profile profile,
	// Element elmDatatypes) {
	// if (datatypesMap.containsKey(key)) {
	// return datatypesMap.get(key);
	// }
	// NodeList datatypes = elmDatatypes.getElementsByTagName("Datatype");
	// for (int i = 0; i < datatypes.getLength(); i++) {
	// Element elmDatatype = (Element) datatypes.item(i);
	// if (elmDatatype.getAttribute("ID").equals(key)) {
	// Datatype dt = this.deserializeDatatype(elmDatatype, profile,
	// elmDatatypes);
	// if (datatypesMap.containsKey(key)) {
	// return datatypesMap.get(key);
	// } else {
	// datatypesMap.put(key, dt);
	// return dt;
	// }
	// }
	// }
	// throw new IllegalArgumentException("Datatype " + key + " not found");
	// }

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
			segmentsMap.put(elmSegment.getAttribute("ID"),
					this.deserializeSegment(elmSegment, profile));
		}

		return segmentsMap;
	}

	private nu.xom.Element serializeMessage(Message m, Segments segments) {
		nu.xom.Element elmMessage = new nu.xom.Element("Message");
		elmMessage.addAttribute(new Attribute("ID", m.getMessageID()));
		if(m.getIdentifier() != null && !m.getIdentifier().equals("")) elmMessage.addAttribute(new Attribute("Identifier", ExportUtil.str(m.getIdentifier())));
		if(m.getName() != null && !m.getName().equals("")) elmMessage.addAttribute(new Attribute("Name", ExportUtil.str(m.getName())));
		elmMessage.addAttribute(new Attribute("Type",ExportUtil.str( m.getMessageType())));
		elmMessage.addAttribute(new Attribute("Event", ExportUtil.str(m.getEvent())));
		elmMessage.addAttribute(new Attribute("StructID", ExportUtil.str(m.getStructID())));
		if (m.getDescription() != null && !m.getDescription().equals("")) elmMessage.addAttribute(new Attribute("Description", ExportUtil.str(m.getDescription())));

		Map<Integer, SegmentRefOrGroup> segmentRefOrGroups = new HashMap<Integer, SegmentRefOrGroup>();

		for (SegmentRefOrGroup segmentRefOrGroup : m.getChildren()) {
			segmentRefOrGroups.put(segmentRefOrGroup.getPosition(), segmentRefOrGroup);
		}

		for (int i = 1; i < segmentRefOrGroups.size() + 1; i++) {
			SegmentRefOrGroup segmentRefOrGroup = segmentRefOrGroups.get(i);
			if (segmentRefOrGroup instanceof SegmentRef) {
				elmMessage.appendChild(serializeSegmentRef((SegmentRef) segmentRefOrGroup, segments));
			} else if (segmentRefOrGroup instanceof Group) {
				elmMessage.appendChild(serializeGroup((Group) segmentRefOrGroup, segments));
			}
		}

		return elmMessage;
	}

	private nu.xom.Element serializeGroup(Group group, Segments segments) {
		nu.xom.Element elmGroup = new nu.xom.Element("Group");
		elmGroup.addAttribute(new Attribute("ID", ExportUtil.str(group.getName())));
		elmGroup.addAttribute(new Attribute("Name", ExportUtil.str(group.getName())));
		elmGroup.addAttribute(new Attribute("Usage", ExportUtil.str(group.getUsage().value())));
		elmGroup.addAttribute(new Attribute("Min", ExportUtil.str(group.getMin() + "")));
		elmGroup.addAttribute(new Attribute("Max", ExportUtil.str(group.getMax())));

		Map<Integer, SegmentRefOrGroup> segmentRefOrGroups = new HashMap<Integer, SegmentRefOrGroup>();
		
		for (SegmentRefOrGroup segmentRefOrGroup : group.getChildren()) {
			segmentRefOrGroups.put(segmentRefOrGroup.getPosition(), segmentRefOrGroup);
		}
		
		for (int i = 1; i < segmentRefOrGroups.size() + 1; i++) {
			SegmentRefOrGroup segmentRefOrGroup = segmentRefOrGroups.get(i);
			if (segmentRefOrGroup instanceof SegmentRef) {
				elmGroup.appendChild(serializeSegmentRef((SegmentRef) segmentRefOrGroup, segments));
			} else if (segmentRefOrGroup instanceof Group) {
				elmGroup.appendChild(serializeGroup((Group) segmentRefOrGroup, segments));
			}
		}

		return elmGroup;
	}

	private nu.xom.Element serializeSegmentRef(SegmentRef segmentRef, Segments segments) {
		nu.xom.Element elmSegment = new nu.xom.Element("Segment");
		elmSegment.addAttribute(new Attribute("Ref", ExportUtil.str(segments.findOneSegmentById(segmentRef.getRef()).getLabel())));
		elmSegment.addAttribute(new Attribute("Usage", ExportUtil.str(segmentRef.getUsage().value())));
		elmSegment.addAttribute(new Attribute("Min", ExportUtil.str(segmentRef.getMin() + "")));
		elmSegment.addAttribute(new Attribute("Max", ExportUtil.str(segmentRef.getMax())));
		return elmSegment;
	}

	private nu.xom.Element serializeSegment(Segment s, Tables tables, Datatypes datatypes) {
		nu.xom.Element elmSegment = new nu.xom.Element("Segment");
		elmSegment.addAttribute(new Attribute("ID", s.getLabel()));
		elmSegment.addAttribute(new Attribute("Name", ExportUtil.str(s.getName())));
		elmSegment.addAttribute(new Attribute("Label", ExportUtil.str(s.getLabel())));
		elmSegment.addAttribute(new Attribute("Description", ExportUtil.str(s.getDescription())));
		
		if (s.getDynamicMapping() != null && s.getDynamicMapping().getMappings().size() > 0){
			nu.xom.Element elmDynamicMapping = new nu.xom.Element("DynamicMapping");
			
			for (Mapping m: s.getDynamicMapping().getMappings()){
				nu.xom.Element elmMapping = new nu.xom.Element("Mapping");
				elmMapping.addAttribute(new Attribute("Position", String.valueOf(m.getPosition())));
				elmMapping.addAttribute(new Attribute("Reference", String.valueOf(m.getReference())));
				
				for (Case c : m.getCases()){
					nu.xom.Element elmCase = new nu.xom.Element("Case");
					elmCase.addAttribute(new Attribute("Value", c.getValue()));
					elmCase.addAttribute(new Attribute("Datatype", datatypes.findOne(c.getDatatype()).getLabel())); 
					elmMapping.appendChild(elmCase);
				}
				
				elmDynamicMapping.appendChild(elmMapping);
			}
			elmSegment.appendChild(elmDynamicMapping);
		}

		Map<Integer, Field> fields = new HashMap<Integer, Field>();

		for (Field f : s.getFields()) {
			fields.put(f.getPosition(), f);
		}

		for (int i = 1; i < fields.size() + 1; i++) {
			Field f = fields.get(i);
			nu.xom.Element elmField = new nu.xom.Element("Field");
			elmField.addAttribute(new Attribute("Name", ExportUtil.str(f.getName())));
			elmField.addAttribute(new Attribute("Usage", ExportUtil.str(f.getUsage().toString())));
			elmField.addAttribute(new Attribute("Datatype", ExportUtil.str(datatypes.findOne(f.getDatatype()).getLabel())));
			elmField.addAttribute(new Attribute("MinLength", "" + f.getMinLength()));
			if (f.getMaxLength() != null && !f.getMaxLength().equals("")) elmField.addAttribute(new Attribute("MaxLength", ExportUtil.str(f.getMaxLength())));
			if (f.getConfLength() != null && !f.getConfLength().equals("")) elmField.addAttribute(new Attribute("ConfLength", ExportUtil.str(f.getConfLength())));
			if (f.getTable() != null && !f.getTable().equals("")) elmField.addAttribute(new Attribute("Binding", tables.findOneTableById(f.getTable()).getBindingIdentifier()));
			if (f.getBindingStrength() != null && !f.getBindingStrength().equals("")) elmField.addAttribute(new Attribute("BindingStrength", ExportUtil.str(f.getBindingStrength())));
			if (f.getBindingLocation() != null && !f.getBindingLocation().equals("")) elmField.addAttribute(new Attribute("BindingLocation", ExportUtil.str(f.getBindingLocation())));
			if (f.isHide()) elmField.addAttribute(new Attribute("Hide", "true"));
			elmField.addAttribute(new Attribute("Min", "" + f.getMin()));
			elmField.addAttribute(new Attribute("Max", "" + f.getMax()));
			if (f.getItemNo() != null && !f.getItemNo().equals("")) elmField.addAttribute(new Attribute("ItemNo",ExportUtil.str( f.getItemNo())));
			elmSegment.appendChild(elmField);
		}

		return elmSegment;
	}

	private nu.xom.Element serializeDatatype(Datatype d, Tables tables, Datatypes datatypes) {
		nu.xom.Element elmDatatype = new nu.xom.Element("Datatype");
		elmDatatype.addAttribute(new Attribute("ID", ExportUtil.str(d.getLabel())));
		elmDatatype.addAttribute(new Attribute("Name", ExportUtil.str(d.getName())));
		elmDatatype.addAttribute(new Attribute("Label", ExportUtil.str(d.getLabel())));
		elmDatatype.addAttribute(new Attribute("Description", ExportUtil.str(d.getDescription())));

		if (d.getComponents() != null) {

			Map<Integer, Component> components = new HashMap<Integer, Component>();

			for (Component c : d.getComponents()) {
				components.put(c.getPosition(), c);
			}

			for (int i = 1; i < components.size() + 1; i++) {
				Component c = components.get(i);
				nu.xom.Element elmComponent = new nu.xom.Element("Component");
				elmComponent.addAttribute(new Attribute("Name", ExportUtil.str(c.getName())));
				elmComponent.addAttribute(new Attribute("Usage", ExportUtil.str(c.getUsage().toString())));
				elmComponent.addAttribute(new Attribute("Datatype", ExportUtil.str(datatypes.findOne(c.getDatatype()).getLabel())));
				elmComponent.addAttribute(new Attribute("MinLength", "" + c.getMinLength()));
				if (c.getMaxLength() != null && !c.getMaxLength().equals("")) elmComponent.addAttribute(new Attribute("MaxLength", ExportUtil.str(c.getMaxLength())));
				if (c.getConfLength() != null && !c.getConfLength().equals("")) elmComponent.addAttribute(new Attribute("ConfLength", ExportUtil.str(c.getConfLength())));
				if (c.getTable() != null && !c.getTable().equals("")){
					if (tables.findOneTableById(c.getTable()) != null){
						elmComponent.addAttribute(new Attribute("Binding", tables.findOneTableById(c.getTable()).getBindingIdentifier() + ""));
					} else {
						logger.warn("Value set "+c.getTable()+" not found in library");
						elmComponent.addAttribute(new Attribute("Binding", c.getTable()));
					}
				}
				if (c.getBindingStrength() != null && !c.getBindingStrength().equals("")) elmComponent.addAttribute(new Attribute("BindingStrength", ExportUtil.str(c.getBindingStrength())));
				if (c.getBindingLocation() != null && !c.getBindingLocation().equals("")) elmComponent.addAttribute(new Attribute("BindingLocation", ExportUtil.str(c.getBindingLocation())));
				if (c.isHide()) elmComponent.addAttribute(new Attribute("Hide", "true"));
				
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

	private void deserializeEncodings(Profile profile,
			Element elmConformanceProfile) {
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
				
				messageObj.setPredicates(this.findPredicates(this.predicates.getMessages(), elmMessage.getAttribute("ID"), elmMessage.getAttribute("StructID")));
				messageObj.setConformanceStatements(this.findConformanceStatement(this.conformanceStatement.getMessages(), elmMessage.getAttribute("ID"), elmMessage.getAttribute("StructID")));

				this.deserializeSegmentRefOrGroups(elmConformanceProfile, messageObj, elmMessage, profile.getSegments(), profile.getDatatypes());
				messagesObj.addMessage(messageObj);
			}
			profile.setMessages(messagesObj);
		}
	}

	private void deserializeSegmentRefOrGroups(Element elmConformanceProfile,
			Message messageObj, Element elmMessage, Segments segments,
			Datatypes datatypes) {
		List<SegmentRefOrGroup> segmentRefOrGroups = new ArrayList<SegmentRefOrGroup>();
		NodeList nodes = elmMessage.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getNodeName().equals("Segment")) {
				this.deserializeSegmentRef(elmConformanceProfile,
						segmentRefOrGroups, (Element) nodes.item(i), segments,
						datatypes);
			} else if (nodes.item(i).getNodeName().equals("Group")) {
				this.deserializeGroup(elmConformanceProfile,
						segmentRefOrGroups, (Element) nodes.item(i), segments,
						datatypes);
			}
		}

		messageObj.setChildren(segmentRefOrGroups);

	}

	private void deserializeSegmentRef(Element elmConformanceProfile,
			List<SegmentRefOrGroup> segmentRefOrGroups, Element segmentElm,
			Segments segments, Datatypes datatypes) {
		SegmentRef segmentRefObj = new SegmentRef();
		segmentRefObj.setMax(segmentElm.getAttribute("Max"));
		segmentRefObj.setMin(new Integer(segmentElm.getAttribute("Min")));
		segmentRefObj.setUsage(Usage.fromValue(segmentElm.getAttribute("Usage")));
		segmentRefObj.setRef(this.segmentsMap.get(segmentElm.getAttribute("Ref")).getId());
		segmentRefOrGroups.add(segmentRefObj);
	}

	private Segment deserializeSegment(Element segmentElm, Profile profile) {
		Segment segmentObj = new Segment();
		segmentObj.setDescription(segmentElm.getAttribute("Description"));
		if(segmentElm.getAttribute("Label") != null && !segmentElm.getAttribute("Label").equals("")){
			segmentObj.setLabel(segmentElm.getAttribute("Label"));
		}else{
			segmentObj.setLabel(segmentElm.getAttribute("Name"));
		}
		segmentObj.setName(segmentElm.getAttribute("Name"));
		segmentObj.setPredicates(this.findPredicates(this.predicates.getSegments(), segmentElm.getAttribute("ID"), segmentElm.getAttribute("Name")));
		segmentObj.setConformanceStatements(this.findConformanceStatement(this.conformanceStatement.getSegments(), segmentElm.getAttribute("ID"), segmentElm.getAttribute("Name")));

		
		NodeList dynamicMapping = segmentElm.getElementsByTagName("Mapping");
		DynamicMapping dynamicMappingObj = null;
		if(dynamicMapping.getLength() > 0){
			dynamicMappingObj = new DynamicMapping();
		}
		
		for (int i = 0; i < dynamicMapping.getLength(); i++) {
			Element mappingElm = (Element)dynamicMapping.item(i);
			Mapping mappingObj = new Mapping();
			mappingObj.setPosition(Integer.parseInt(mappingElm.getAttribute("Position")));
			mappingObj.setReference(Integer.parseInt(mappingElm.getAttribute("Reference")));
			NodeList cases = mappingElm.getElementsByTagName("Case");
			
			for(int j = 0; j < cases.getLength(); j++) {
				Element caseElm = (Element)cases.item(j);
				Case caseObj = new Case();
				caseObj.setValue(caseElm.getAttribute("Value"));
				caseObj.setDatatype(this.findDatatype(caseElm.getAttribute("Datatype"), profile).getId());
				
				mappingObj.addCase(caseObj);
				
			}
			
			
			dynamicMappingObj.addMapping(mappingObj);
			
		}
		
		if(dynamicMappingObj != null) segmentObj.setDynamicMapping(dynamicMappingObj);
		
		NodeList fields = segmentElm.getElementsByTagName("Field");
		for (int i = 0; i < fields.getLength(); i++) {
			Element fieldElm = (Element) fields.item(i);
			segmentObj.addField(this.deserializeField(fieldElm, segmentObj, profile, segmentElm.getAttribute("ID"), i));
		}
		return segmentObj;
	}

	private Field deserializeField(Element fieldElm, Segment segment,
			Profile profile, String segmentId, int position) {
		Field fieldObj = new Field();

		fieldObj.setName(fieldElm.getAttribute("Name"));
		fieldObj.setUsage(Usage.fromValue(fieldElm.getAttribute("Usage")));
		fieldObj.setDatatype(this.findDatatype(fieldElm.getAttribute("Datatype"), profile).getId());
		fieldObj.setMinLength(new Integer(fieldElm.getAttribute("MinLength")));
		if(fieldElm.getAttribute("MaxLength") != null){
			fieldObj.setMaxLength(fieldElm.getAttribute("MaxLength"));
		}
		if(fieldElm.getAttribute("ConfLength") != null){
			fieldObj.setConfLength(fieldElm.getAttribute("ConfLength"));
		}
		if (fieldElm.getAttribute("Binding") != null) {
			fieldObj.setTable(findTableIdByMappingId(fieldElm.getAttribute("Binding"), profile.getTables()));
		}
		if (fieldElm.getAttribute("BindingStrength") != null) {
			fieldObj.setBindingStrength(fieldElm.getAttribute("BindingStrength"));
		}

		if (fieldElm.getAttribute("BindingLocation") != null) {
			fieldObj.setBindingLocation(fieldElm.getAttribute("BindingLocation"));
		}
		if(fieldElm.getAttribute("Hide") != null && fieldElm.getAttribute("Hide").equals("true") ){
			fieldObj.setHide(true);
		}else{
			fieldObj.setHide(false);
		}
		fieldObj.setMin(new Integer(fieldElm.getAttribute("Min")));
		fieldObj.setMax(fieldElm.getAttribute("Max"));
		if(fieldElm.getAttribute("ItemNo") != null){
			fieldObj.setItemNo(fieldElm.getAttribute("ItemNo"));
		}
		return fieldObj;
	}

	private String findTableIdByMappingId(String bindingIdentifier, Tables tables) {
		for (Table table : tables.getChildren()) {
			if (table.getBindingIdentifier().equals(bindingIdentifier)) {
				return table.getId();
			}
		}
		return null;
	}

	private void deserializeGroup(Element elmConformanceProfile, List<SegmentRefOrGroup> segmentRefOrGroups, Element groupElm, Segments segments, Datatypes datatypes) {
		Group groupObj = new Group();
		groupObj.setMax(groupElm.getAttribute("Max"));
		groupObj.setMin(new Integer(groupElm.getAttribute("Min")));
		groupObj.setName(groupElm.getAttribute("Name"));
		groupObj.setUsage(Usage.fromValue(groupElm.getAttribute("Usage")));
		groupObj.setPredicates(this.findPredicates(this.predicates.getGroups(), groupElm.getAttribute("ID"), groupElm.getAttribute("Name")));
		groupObj.setConformanceStatements(this.findConformanceStatement(this.conformanceStatement.getGroups(), groupElm.getAttribute("ID"), groupElm.getAttribute("Name")));

		List<SegmentRefOrGroup> childSegmentRefOrGroups = new ArrayList<SegmentRefOrGroup>();

		NodeList nodes = groupElm.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getNodeName().equals("Segment")) {
				this.deserializeSegmentRef(elmConformanceProfile,
						childSegmentRefOrGroups, (Element) nodes.item(i),
						segments, datatypes);
			} else if (nodes.item(i).getNodeName().equals("Group")) {
				this.deserializeGroup(elmConformanceProfile,
						childSegmentRefOrGroups, (Element) nodes.item(i),
						segments, datatypes);
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

	@Override
	public InputStream serializeProfileToZip(Profile profile) throws IOException{
		ByteArrayOutputStream outputStream = null;
		byte[] bytes;
		outputStream = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(outputStream);

		this.generateProfileIS(out, this.serializeProfileToXML(profile));
		this.generateValueSetIS(out, new TableSerializationImpl().serializeTableLibraryToXML(profile));
		this.generateConstraintsIS(out, new ConstraintsSerializationImpl().serializeConstraintsToXML(profile));

		out.close();
		bytes = outputStream.toByteArray();
		return new ByteArrayInputStream(bytes);
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
	public InputStream serializeProfileToZip(Profile original, String[] ids) throws IOException, CloneNotSupportedException {
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
		for(Message m:original.getMessages().getChildren()){
			if(Arrays.asList(ids).contains(m.getId())){
				messages.addMessage(m);
				
				for(SegmentRefOrGroup seog :m.getChildren()){
					this.visit(seog, segmentsMap, datatypesMap, tablesMap, original);
				}
				
			}
		}
		
		Segments segments = new Segments();
		for (String key : segmentsMap.keySet()) {
			segments.addSegment(segmentsMap.get(key));
		}
		
		Datatypes datatypes = new Datatypes();
		for (String key : datatypesMap.keySet()) {
			datatypes.addDatatype(datatypesMap.get(key));
		}
		
		
		Tables tables = new Tables();
		for (String key : tablesMap.keySet()) {
			tables.addTable(tablesMap.get(key));
		}
		
		filteredProfile.setDatatypes(datatypes);
		filteredProfile.setSegments(segments);
		filteredProfile.setMessages(messages);
		filteredProfile.setTables(tables);
		
		return this.serializeProfileToZip(filteredProfile);
	}
	
	

	private void visit(SegmentRefOrGroup seog, HashMap<String, Segment> segmentsMap, HashMap<String, Datatype> datatypesMap, HashMap<String, Table> tablesMap, Profile original) {
		if(seog instanceof SegmentRef){
			SegmentRef sr = (SegmentRef)seog;
		
			Segment s = original.getSegments().findOneSegmentById(sr.getRef());
			segmentsMap.put(sr.getRef(), s);
			
			for(Field f:s.getFields()){
				this.addDatatype(f.getDatatype(), original, datatypesMap, tablesMap);
				if(f.getTable() != null && !f.getTable().equals("")){
					tablesMap.put(f.getTable(), original.getTables().findOneTableById(f.getTable()));
				}
			}
			
			
		}else {
			Group g = (Group)seog;
			for(SegmentRefOrGroup child :g.getChildren()){
				this.visit(child, segmentsMap, datatypesMap, tablesMap, original);
			}
		}
		
	}

	private void addDatatype(String key, Profile original, HashMap<String, Datatype> datatypesMap, HashMap<String, Table> tablesMap) {
		Datatype d = original.getDatatypes().findOne(key);
		
		datatypesMap.put(key, d);
		
		for(Component c:d.getComponents()){
			this.addDatatype(c.getDatatype(), original, datatypesMap, tablesMap);
			if(c.getTable() != null && !c.getTable().equals("")){
				tablesMap.put(c.getTable(), original.getTables().findOneTableById(c.getTable()));
			}
		}
	}

	public static void main(String[] args) throws IOException, CloneNotSupportedException {
		ProfileSerializationImpl test1 = new ProfileSerializationImpl();
		Profile profile = test1.deserializeXMLToProfile(
				new String(Files.readAllBytes(Paths.get("src//main//resources//IZ_XML_Profiles//IZ Profiles//IZ_Profile.xml"))),
				new String(Files.readAllBytes(Paths.get("src//main//resources//IZ_XML_Profiles//IZ ValueSets//IZ_ValueSetLibrary.xml"))),
				new String(Files.readAllBytes(Paths.get("src//main//resources//IZ_XML_Profiles//IZ Constraints//IZ_Constraints.xml")))
		);
		Set<String> idSet = new HashSet<String>();
	
		for(Message m:profile.getMessages().getChildren()){
			idSet.add(m.getId());
		}
		
		InputStream is = test1.serializeProfileToZip(profile, idSet.toArray(new String[0]));
		OutputStream outputStream =  new FileOutputStream(new File("src//main//resources//IZ_XML_Profiles//out.zip"));
		int read = 0;
		byte[] bytes = new byte[1024];

		while ((read = is.read(bytes)) != -1) {
			outputStream.write(bytes, 0, read);
		}
		
		outputStream.close();
		
	}
}