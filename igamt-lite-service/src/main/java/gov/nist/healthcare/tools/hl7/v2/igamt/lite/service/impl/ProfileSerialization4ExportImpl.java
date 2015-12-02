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

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatypes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.HL7Version;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SchemaVersion;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segments;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Tables;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByID;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByNameOrByID;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraints;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Context;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ProfileSerialization4ExportImpl implements ProfileSerialization {

	Logger logger = LoggerFactory.getLogger( ProfileSerialization4ExportImpl.class );

	private HashMap<String, Datatype> datatypesMap;
	private HashMap<String, Segment> segmentsMap;
	private Constraints conformanceStatement;
	private Constraints predicates;




	public File serializeProfileToFile(Profile profile) throws UnsupportedEncodingException {
		File out;
		try {
			out = File.createTempFile("ProfileTemp", ".xml");
			FileOutputStream outputStream = (FileOutputStream) Files.newOutputStream(out.toPath());
			Serializer ser;
			ser = new Serializer(outputStream, "UTF-8");
			ser.setIndent(4);
			ser.write(this.serializeProfileToDoc(profile));
			return out;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}

	}

	@Override
	public String serializeProfileToXML(Profile profile) {
		return this.serializeProfileToDoc(profile).toXML();
	}

	@Override
	public nu.xom.Document serializeProfileToDoc(Profile profile) {
		nu.xom.Element e = new nu.xom.Element("ConformanceProfile");
		e.addAttribute(new Attribute("ID", profile.getId() + ""));
		ProfileMetaData metaData = profile.getMetaData();
		if (metaData.getType() != null && !metaData.getType().equals(""))
			e.addAttribute(new Attribute("Type", metaData.getType()));
		if (metaData.getHl7Version() != null
				&& !metaData.getHl7Version().equals(""))
			e.addAttribute(new Attribute("HL7Version", metaData.getHl7Version()));
		if (metaData.getSchemaVersion() != null
				&& !metaData.getSchemaVersion().equals(""))
			e.addAttribute(new Attribute("SchemaVersion", metaData
					.getSchemaVersion()));

		if (profile.getMetaData() != null) {
			nu.xom.Element elmMetaData = new nu.xom.Element("MetaData");
			ProfileMetaData metaDataObj = profile.getMetaData();
			elmMetaData.addAttribute(new Attribute("Name", metaDataObj
					.getName()+""));
			elmMetaData.addAttribute(new Attribute("OrgName", metaDataObj
					.getOrgName()));
			if (metaDataObj.getStatus() != null)
				elmMetaData.addAttribute(new Attribute("Status", metaDataObj
						.getStatus()));
			if (metaDataObj.getTopics() != null)
				elmMetaData.addAttribute(new Attribute("Topics", metaDataObj
						.getTopics()));
			if (metaDataObj.getSubTitle() != null)
				elmMetaData.addAttribute(new Attribute("Subtitle", metaDataObj
						.getSubTitle()));
			if (metaDataObj.getVersion() != null)
				elmMetaData.addAttribute(new Attribute("Version", metaDataObj
						.getVersion()));
			if (metaDataObj.getDate() != null)
				elmMetaData.addAttribute(new Attribute("Date", metaDataObj
						.getDate()));
			if (metaDataObj.getExt() != null)
				elmMetaData.addAttribute(new Attribute("Ext", metaDataObj
						.getExt()));
			if (profile.getComment() != null && !profile.getComment().equals("")) {
				elmMetaData.addAttribute(new Attribute("Comment", profile.getComment()));
			}

			e.appendChild(elmMetaData);

			if (profile.getMetaData().getEncodings() != null
					&& profile.getMetaData().getEncodings().size() > 0) {
				nu.xom.Element elmEncodings = new nu.xom.Element("Encodings");
				for (String encoding : profile.getMetaData().getEncodings()) {
					nu.xom.Element elmEncoding = new nu.xom.Element("Encoding");
					elmEncoding.appendChild(encoding);
					elmEncodings.appendChild(elmEncoding);
				}
				e.appendChild(elmEncodings);
			}

		}

		if (profile.getUsageNote() != null) {
			nu.xom.Element ts = new nu.xom.Element("Text");
			if (profile.getUsageNote() != null && !profile.getUsageNote().equals("")) {
				nu.xom.Element elmUsageNote = new nu.xom.Element("UsageNote");
				elmUsageNote.appendChild(profile.getUsageNote());
				ts.appendChild(elmUsageNote);
			}
			e.appendChild(ts);
		}

		nu.xom.Element msd = new nu.xom.Element("MessagesDisplay");
		for (Message m : profile.getMessages().getChildren()) {
			msd.appendChild(this.serializeMessageDisplay(m, profile.getSegments()));
		}
		e.appendChild(msd);

		nu.xom.Element ms = new nu.xom.Element("Messages");
		for (Message m : profile.getMessages().getChildren()) {
			ms.appendChild(this.serializeMessage(m, profile.getSegments()));
		}
		e.appendChild(ms);

		nu.xom.Element ss = new nu.xom.Element("Segments");
		//              Not iterating with getSegments in order to retrieve positions; using getMessages().getChildren instead
		//              for (Segment s : profile.getSegments().getChildren()) {
		//                      ss.appendChild(this.serializeSegment(s, profile.getTables(), profile.getDatatypes()));
		//              }
		for (Message m : profile.getMessages().getChildren()) {
			for (SegmentRefOrGroup srog: m.getChildren()){
				if (srog instanceof SegmentRef) {
					this.serializeSegment(ss, (SegmentRef) srog, profile.getSegments(), profile.getTables(), profile.getDatatypes());
				} else if (srog instanceof Group) {
					this.serializeGroup(ss, (Group) srog, profile.getSegments(), profile.getTables(), profile.getDatatypes());

				}
			}
		}
		e.appendChild(ss);

		nu.xom.Element ds = new nu.xom.Element("Datatypes");
		for (Datatype d : profile.getDatatypes().getChildren()) {
			if (d.getLabel().contains("_")) {
				ds.appendChild(this.serializeDatatype(d, profile.getTables(), profile.getDatatypes()));
			}
		}
		e.appendChild(ds);

		nu.xom.Element ts = new nu.xom.Element("ValueSets");
		List<Table> tables = new ArrayList<Table>(profile.getTables()
				.getChildren());
		Collections.sort(tables);
		for (Table t : tables) {
			ts.appendChild(this.serializeTable(t));
		}
		e.appendChild(ts);


		nu.xom.Document doc = new nu.xom.Document(e);

		return doc;
	}

	private nu.xom.Element serializeTable(Table t){
		nu.xom.Element elmTableDefinition = new nu.xom.Element("ValueSetDefinition");
		elmTableDefinition.addAttribute(new Attribute("Id", (t.getBindingIdentifier() == null) ? "" : t.getBindingIdentifier()));
		elmTableDefinition.addAttribute(new Attribute("BindingIdentifier", (t.getBindingIdentifier() == null) ? "" : t.getBindingIdentifier()));
		elmTableDefinition.addAttribute(new Attribute("Name",(t.getName() == null) ? "" : t.getName()));
		elmTableDefinition.addAttribute(new Attribute("Description",(t.getDescription() == null) ? "" : t.getDescription()));
		elmTableDefinition.addAttribute(new Attribute("Version", (t.getVersion() == null) ? "" : "" + t.getVersion()));
		elmTableDefinition.addAttribute(new Attribute("Oid",(t.getOid() == null) ? "" : t.getOid()));
		elmTableDefinition.addAttribute(new Attribute("Stability", (t.getStability() == null) ? "" : t.getStability().value()));
		elmTableDefinition.addAttribute(new Attribute("Extensibility", (t.getExtensibility() == null) ? "" : t.getExtensibility().value()));
		elmTableDefinition.addAttribute(new Attribute("ContentDefinition", (t.getContentDefinition() == null) ? "" : t.getContentDefinition().value()));
		
		
		
		

		if (t.getCodes() != null) {
			for (Code c : t.getCodes()) {
				nu.xom.Element elmTableElement = new nu.xom.Element("ValueElement");
				elmTableElement.addAttribute(new Attribute("Value", (c.getValue() == null) ? "" : c.getValue()));
				elmTableElement.addAttribute(new Attribute("Label",(c.getLabel() == null) ? "" : c.getLabel()));
				elmTableElement.addAttribute(new Attribute("CodeSystem", (c.getCodeSystem() == null) ? "" : c.getCodeSystem()));
				elmTableElement.addAttribute(new Attribute("CodeSystemVersion", (c.getCodeSystemVersion() == null) ? "" : c.getCodeSystemVersion()));
				elmTableElement.addAttribute(new Attribute("Usage", (c.getCodeUsage() == null) ? "" : c.getCodeUsage()));
				elmTableElement.addAttribute(new Attribute("Comments",(c.getComments() == null) ? "" : c.getComments()));
				elmTableDefinition.appendChild(elmTableElement);
			}
		}
		return elmTableDefinition;
	}

	private void serializeGroup(nu.xom.Element ss, Group g, Segments segments, Tables tables, Datatypes datatypes
			) {

		List<SegmentRefOrGroup> segsOrGroups = g.getChildren();
		Collections.sort(segsOrGroups);
		for (SegmentRefOrGroup srog : segsOrGroups) {
			if (srog instanceof SegmentRef) {
				this.serializeSegment(ss, (SegmentRef) srog, segments, tables, datatypes);
			} else if (srog instanceof Group) {
				this.serializeGroup(ss, (Group) srog, segments, tables, datatypes);

			}
		}
	}


	private void constructDatatypesMap(Element elmDatatypes, Profile profile) {
		this.datatypesMap = new HashMap<String, Datatype>();
		NodeList datatypeNodeList = elmDatatypes
				.getElementsByTagName("Datatype");

		for (int i = 0; i < datatypeNodeList.getLength(); i++) {
			Element elmDatatype = (Element) datatypeNodeList.item(i);
			// helps get rid of duplicates
			if (!datatypesMap.keySet().contains(elmDatatype.getAttribute("ID"))) {
				datatypesMap.put(elmDatatype.getAttribute("ID"),
						this.deserializeDatatype(elmDatatype, profile,
								elmDatatypes));
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

	private Datatype deserializeDatatype(Element elmDatatype, Profile profile,
			Element elmDatatypes) {
		String ID = elmDatatype.getAttribute("ID");
		if (!datatypesMap.keySet().contains(ID)) {
			Datatype datatypeObj = new Datatype();
			datatypeObj.setDescription(elmDatatype.getAttribute("Description"));
			// [Woo] I assumed the default name could be base name.
			datatypeObj.setLabel(elmDatatype.getAttribute("ID"));
			datatypeObj.setName(elmDatatype.getAttribute("Name"));
			datatypeObj.setPredicates(this.findPredicates(
					this.predicates.getDatatypes(),
					elmDatatype.getAttribute("ID")));
			datatypeObj.setConformanceStatements(this.findConformanceStatement(
					this.conformanceStatement.getDatatypes(),
					elmDatatype.getAttribute("ID")));

			NodeList nodes = elmDatatype.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				if (nodes.item(i).getNodeName().equals("Component")) {
					Element elmComponent = (Element) nodes.item(i);
					Component componentObj = new Component();
					componentObj.setConfLength(elmComponent
							.getAttribute("ConfLength"));
					componentObj.setMaxLength(elmComponent
							.getAttribute("MaxLength"));
					componentObj.setMinLength(new Integer(elmComponent
							.getAttribute("MinLength")));
					componentObj.setName(elmComponent.getAttribute("Name"));

					componentObj.setUsage(Usage.fromValue(elmComponent
							.getAttribute("Usage")));

					if (elmComponent.getAttribute("Table") != null) {
						String tableScript = elmComponent.getAttribute("Table");
						String[] tableTags = tableScript.split("#");
						// System.out.println(tableScript);
						if (tableTags.length == 1) {
							componentObj.setTable(findTableIdByMappingId(
									tableTags[0], profile.getTables()));
						} else if (tableTags.length == 2) {
							componentObj.setTable(findTableIdByMappingId(
									tableTags[0], profile.getTables()));
							componentObj.setBindingStrength(tableTags[1]);
						} else if (tableTags.length == 3) {
							componentObj.setTable(findTableIdByMappingId(
									tableTags[0], profile.getTables()));
							componentObj.setBindingStrength(tableTags[1]);
							componentObj.setBindingLocation(tableTags[2]);
						}
					}
					componentObj.setUsage(Usage.fromValue(elmComponent
							.getAttribute("Usage")));
					componentObj.setBindingLocation(elmComponent
							.getAttribute("BindingLocation"));
					componentObj.setBindingStrength(elmComponent
							.getAttribute("BindingStrength"));
					// componentObj.setDatatype(elmComponent.getAttribute("Datatype"));

					// Datatype datatype = null;
					// String ID = elmDatatype.getAttribute("ID");
					// if (!datatypesMap.keySet().contains(ID)) {
					// datatype = this.deserializeDatatype(elmDatatype,
					// profile, elmDatatypes);
					// datatypesMap.put(ID, datatype);
					// } else {
					// datatype = datatypesMap.get(ID);
					// }
					Element elmDt = getDatatypeElement(elmDatatypes,
							elmComponent.getAttribute("Datatype"));
					Datatype datatype = this.deserializeDatatype(elmDt,
							profile, elmDatatypes);
					componentObj.setDatatype(datatype.getId());
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

	private List<ConformanceStatement> findConformanceStatement(
			Context context, String key) {
		Set<ByNameOrByID> byNameOrByIDs = context.getByNameOrByIDs();
		List<ConformanceStatement> result = new ArrayList<ConformanceStatement>();
		for (ByNameOrByID byNameOrByID : byNameOrByIDs) {
			if (byNameOrByID instanceof ByID) {
				ByID byID = (ByID) byNameOrByID;
				if (byID.getByID().equals(key)) {
					for (ConformanceStatement c : byID
							.getConformanceStatements()) {
						result.add(c);
					}
				}
			}
		}
		return result;
	}

	private List<Predicate> findPredicates(Context context, String key) {
		Set<ByNameOrByID> byNameOrByIDs = context.getByNameOrByIDs();
		List<Predicate> result = new ArrayList<Predicate>();
		for (ByNameOrByID byNameOrByID : byNameOrByIDs) {
			if (byNameOrByID instanceof ByID) {
				ByID byID = (ByID) byNameOrByID;
				if (byID.getByID().equals(key)) {
					for (Predicate c : byID.getPredicates()) {
						result.add(c);
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

	private HashMap<String, Segment> constructSegmentsMap(Element elmSegments,
			Profile profile) {
		HashMap<String, Segment> segmentsMap = new HashMap<String, Segment>();
		NodeList segmentNodeList = elmSegments.getElementsByTagName("Segment");

		for (int i = 0; i < segmentNodeList.getLength(); i++) {
			Element elmSegment = (Element) segmentNodeList.item(i);
			segmentsMap.put(elmSegment.getAttribute("ID"),
					this.deserializeSegment(elmSegment, profile));
		}

		return segmentsMap;
	}

	private nu.xom.Element serializeMessageDisplay(Message m, Segments segments) {
		nu.xom.Element elmMessage = new nu.xom.Element("MessageDisplay");
		elmMessage.addAttribute(new Attribute("ID", m.getId() + ""));
		elmMessage.addAttribute(new Attribute("Type", m.getMessageType()));
		elmMessage.addAttribute(new Attribute("Event", m.getEvent()));
		elmMessage.addAttribute(new Attribute("StructID", m.getStructID()));
		if (m.getDescription() != null && !m.getDescription().equals(""))
			elmMessage.addAttribute(new Attribute("Description", m
					.getDescription()));
		if (m.getComment() != null && !m.getComment().equals("")) {
			elmMessage.addAttribute(new Attribute("Comment", m.getComment()));
		}
		elmMessage.addAttribute(new Attribute("Position", m.getPosition().toString()));
		if (m.getUsageNote() != null && !m.getUsageNote().equals("")) {
			elmMessage.appendChild(this.serializeRichtext("UsageNote", m.getUsageNote()));
		}


		Map<Integer, SegmentRefOrGroup> segmentRefOrGroups = new HashMap<Integer, SegmentRefOrGroup>();

		for (SegmentRefOrGroup segmentRefOrGroup : m.getChildren()) {
			segmentRefOrGroups.put(segmentRefOrGroup.getPosition(),
					segmentRefOrGroup);
		}

		for (int i = 1; i < segmentRefOrGroups.size() + 1; i++) {
			SegmentRefOrGroup segmentRefOrGroup = segmentRefOrGroups.get(i);
			if (segmentRefOrGroup instanceof SegmentRef) {
				this.serializeSegmentRefDisplay(elmMessage, (SegmentRef) segmentRefOrGroup, segments, 0);
			} else if (segmentRefOrGroup instanceof Group) {
				this.serializeGroupDisplay(elmMessage, (Group) segmentRefOrGroup, segments, 0);
			}
		}

		return elmMessage;
	}

	private void serializeGroupDisplay(nu.xom.Element elmDisplay, Group group, Segments segments, Integer depth) {
		nu.xom.Element elmGroup = new nu.xom.Element("Elt");
		elmGroup.addAttribute(new Attribute("IdGpe", group.getId()));
		elmGroup.addAttribute(new Attribute("Name", group.getName()));
		elmGroup.addAttribute(new Attribute("Usage", group.getUsage().value()));
		elmGroup.addAttribute(new Attribute("Min", group.getMin() + ""));
		elmGroup.addAttribute(new Attribute("Max", group.getMax()));
		elmGroup.addAttribute(new Attribute("Ref", StringUtils.repeat(".", 4*depth) + "["));
		elmGroup.addAttribute(new Attribute("Comment", "BEGIN " + group.getName() + " GROUP"));
		elmGroup.addAttribute(new Attribute("Position", group.getPosition().toString()));
		elmDisplay.appendChild(elmGroup);

		for (SegmentRefOrGroup segmentRefOrGroup : group.getChildren()) {
			if (segmentRefOrGroup instanceof SegmentRef) {
				this.serializeSegmentRefDisplay(elmDisplay, (SegmentRef) segmentRefOrGroup, segments, depth + 1);
			} else if (segmentRefOrGroup instanceof Group) {
				this.serializeGroupDisplay(elmDisplay, (Group) segmentRefOrGroup, segments, depth + 1);
			}
		}
		nu.xom.Element elmGroup2 = new nu.xom.Element("Elt");
		elmGroup2.addAttribute(new Attribute("IdGpe", group.getId()));
		elmGroup2.addAttribute(new Attribute("Name", group.getName()));
		elmGroup2.addAttribute(new Attribute("Usage", group.getUsage().value()));
		elmGroup2.addAttribute(new Attribute("Min", group.getMin() + ""));
		elmGroup2.addAttribute(new Attribute("Max", group.getMax()));
		elmGroup2.addAttribute(new Attribute("Ref", StringUtils.repeat(".", 4*depth) + "]"));
		elmGroup2.addAttribute(new Attribute("Depth", String.valueOf(depth)));
		elmGroup2.addAttribute(new Attribute("Comment", "END " + group.getName() + " GROUP"));
		elmGroup2.addAttribute(new Attribute("Position", group.getPosition().toString()));
		elmDisplay.appendChild(elmGroup2);

	}

	private void serializeSegmentRefDisplay(nu.xom.Element elmDisplay, SegmentRef segmentRef, Segments segments, Integer depth) {
		nu.xom.Element elmSegment = new nu.xom.Element("Elt");
		elmSegment.addAttribute(new Attribute("IDRef", segmentRef.getId()));
		elmSegment.addAttribute(new Attribute("IDSeg", segmentRef.getRef()));
		elmSegment.addAttribute(new Attribute("Ref", StringUtils.repeat(".", 4*depth) + ((Segment)segments.findOneSegmentById(segmentRef.getRef())).getName()));
		elmSegment.addAttribute(new Attribute("Depth", String.valueOf(depth)));
		elmSegment.addAttribute(new Attribute("Usage", segmentRef.getUsage()
				.value()));
		elmSegment.addAttribute(new Attribute("Min", segmentRef.getMin() + ""));
		elmSegment.addAttribute(new Attribute("Max", segmentRef.getMax()));
		if (segmentRef.getComment() != null)
			elmSegment.addAttribute(new Attribute("Comment", segmentRef.getComment()));
		elmSegment.addAttribute(new Attribute("Position", segmentRef.getPosition().toString()));
		elmDisplay.appendChild(elmSegment);
	}


	private nu.xom.Element serializeMessage(Message m, Segments segments) {
		nu.xom.Element elmMessage = new nu.xom.Element("Message");
		elmMessage.addAttribute(new Attribute("ID", m.getId() + ""));
		elmMessage.addAttribute(new Attribute("Type", m.getMessageType()));
		elmMessage.addAttribute(new Attribute("Event", m.getEvent()));
		elmMessage.addAttribute(new Attribute("StructID", m.getStructID()));
		if (m.getDescription() != null && !m.getDescription().equals(""))
			elmMessage.addAttribute(new Attribute("Description", m
					.getDescription()));
		if (m.getComment() != null && !m.getComment().equals("")) {
			elmMessage.addAttribute(new Attribute("Comment", m.getComment()));
		}
		elmMessage.addAttribute(new Attribute("Position", m.getPosition().toString()));
		//              if (m.getUsageNote() != null && !m.getUsageNote().equals("")) {
		if (m.getUsageNote() != null) {
			elmMessage.appendChild(this.serializeRichtext("UsageNote",m.getUsageNote()));
		}


		Map<Integer, SegmentRefOrGroup> segmentRefOrGroups = new HashMap<Integer, SegmentRefOrGroup>();

		for (SegmentRefOrGroup segmentRefOrGroup : m.getChildren()) {
			segmentRefOrGroups.put(segmentRefOrGroup.getPosition(),
					segmentRefOrGroup);
		}

		for (int i = 1; i < segmentRefOrGroups.size() + 1; i++) {
			SegmentRefOrGroup segmentRefOrGroup = segmentRefOrGroups.get(i);
			if (segmentRefOrGroup instanceof SegmentRef) {
				elmMessage
				.appendChild(serializeSegmentRef((SegmentRef) segmentRefOrGroup, segments));
			} else if (segmentRefOrGroup instanceof Group) {
				elmMessage
				.appendChild(serializeGroup((Group) segmentRefOrGroup, segments));
			}
		}

		return elmMessage;
	}

	private nu.xom.Element serializeGroup(Group group, Segments segments) {
		nu.xom.Element elmGroup = new nu.xom.Element("Group");
		elmGroup.addAttribute(new Attribute("IdGpe", group.getId()));
		elmGroup.addAttribute(new Attribute("Name", group.getName()));
		elmGroup.addAttribute(new Attribute("Usage", group.getUsage().value()));
		elmGroup.addAttribute(new Attribute("Min", group.getMin() + ""));
		elmGroup.addAttribute(new Attribute("Max", group.getMax()));
		if (group.getComment() != null)
			elmGroup.addAttribute(new Attribute("Comment", group.getComment()));
		elmGroup.addAttribute(new Attribute("Position", group.getPosition().toString()));

		for (SegmentRefOrGroup segmentRefOrGroup : group.getChildren()) {
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
		elmSegment.addAttribute(new Attribute("IDRef", segmentRef.getId()));
		elmSegment.addAttribute(new Attribute("IDSeg", segmentRef.getRef()));
		elmSegment.addAttribute(new Attribute("Ref", ((Segment)segments.findOneSegmentById(segmentRef.getRef())).getName()));
		elmSegment.addAttribute(new Attribute("Usage", segmentRef.getUsage()
				.value()));
		elmSegment.addAttribute(new Attribute("Min", segmentRef.getMin() + ""));
		elmSegment.addAttribute(new Attribute("Max", segmentRef.getMax()));
		if (segmentRef.getComment() != null)
			elmSegment.addAttribute(new Attribute("Comment", segmentRef.getComment()));
		elmSegment.addAttribute(new Attribute("Position", segmentRef.getPosition().toString()));
		return elmSegment;
	}

	private nu.xom.Element serializeRichtext(String attribute, String richtext){
		nu.xom.Element elmText1 = new nu.xom.Element("Text");
		try {
			elmText1.addAttribute(new Attribute("Type", attribute));
			Builder parser = new Builder();

			nu.xom.Document document = parser.build(new StringReader(
					"<p>" + richtext + "</p>"));
			nu.xom.Element child = document
					.getRootElement();

			elmText1.appendChild(child.copy());
		} catch (ParsingException | IOException e) {
			e.printStackTrace();
		}
		return elmText1;
	}

	private void serializeSegment(nu.xom.Element ss, SegmentRef sr, Segments segments, Tables tables, Datatypes datatypes) {
		Segment s = segments.findOneSegmentById(((SegmentRef) sr).getRef());
		nu.xom.Element elmSegment = new nu.xom.Element("Segment");
		elmSegment.addAttribute(new Attribute("ID", s.getId() + ""));
		elmSegment.addAttribute(new Attribute("Name", s.getName()));
		elmSegment
		.addAttribute(new Attribute("Description", s.getDescription()));
		if (s.getComment() != null){
			elmSegment.addAttribute(new Attribute("Comment", s.getComment()));
		}

		//TODO if ( !s.getText1().equals("") | !s.getText2().equals("")){
		if (!s.getText1().equals("")){
			elmSegment.appendChild(this.serializeRichtext("Text1", s.getText1()));
		}
		if (!s.getText2().equals("")){
			elmSegment.appendChild(this.serializeRichtext("Text2", s.getText2()));
		}
		//              }



		Map<Integer, Field> fields = new HashMap<Integer, Field>();

		for (Field f : s.getFields()) {
			fields.put(f.getPosition(), f);
		}

		for (int i = 1; i < fields.size() + 1; i++) {
			Field f = fields.get(i);
			nu.xom.Element elmField = new nu.xom.Element("Field");
			elmField.addAttribute(new Attribute("Name", f.getName()));
			elmField.addAttribute(new Attribute("Usage", f.getUsage()
					.toString()));
			//                        elmField.addAttribute(new Attribute("Datatype", datatypes.findOne(
			//                                f.getDatatype()).getLabel()));
			if (f.getDatatype() != null && datatypes.findOne(f.getDatatype()) != null){
				elmField.addAttribute(new Attribute("Datatype", datatypes.findOne(f.getDatatype()).getLabel()));
			}
			elmField.addAttribute(new Attribute("MinLength", ""
					+ f.getMinLength()));
			elmField.addAttribute(new Attribute("Min", "" + f.getMin()));
			elmField.addAttribute(new Attribute("Max", "" + f.getMax()));
			if (f.getMaxLength() != null && !f.getMaxLength().equals(""))
				elmField.addAttribute(new Attribute("MaxLength", f
						.getMaxLength()));
			if (f.getConfLength() != null && !f.getConfLength().equals(""))
				elmField.addAttribute(new Attribute("ConfLength", f
						.getConfLength()));
			if (f.getTable() != null && !f.getTable().equals(""))
				elmField.addAttribute(new Attribute("Binding", tables.findOneTableById(
						f.getTable()).getBindingIdentifier()));
			if (f.getItemNo() != null && !f.getItemNo().equals(""))
				elmField.addAttribute(new Attribute("ItemNo", f.getItemNo()));
			if (f.getComment() != null && !f.getText().equals(""))
				elmField.addAttribute(new Attribute("Comment", f.getComment()));
			elmField.addAttribute(new Attribute("Position", String.valueOf(f.getPosition())));


			if (f.getText() != null && !f.getText().equals("")){
				elmField.appendChild(this.serializeRichtext("Text", f.getText()));
			}

			List<Constraint> constraints = findConstraints( i, s.getPredicates(), s.getConformanceStatements());
			if (!constraints.isEmpty()) {
				for (Constraint constraint : constraints) {
					nu.xom.Element elmConstraint = new nu.xom.Element("Constraint");
					if (constraint instanceof Predicate) {
						elmConstraint.addAttribute(new Attribute("Type", "ConditionPredicate"));
					} else if (constraint instanceof ConformanceStatement) {
						elmConstraint.addAttribute(new Attribute("Type", "ConformanceStatement"));
					}
					elmConstraint.appendChild(constraint.getDescription());
					elmField.appendChild(elmConstraint);
				}
			}
			elmSegment.appendChild(elmField);
		}
		ss.appendChild(elmSegment);
	}


	private List<Constraint> findConstraints(Integer target,
			List<Predicate> predicates,
			List<ConformanceStatement> conformanceStatements) {
		List<Constraint> constraints = new ArrayList<>();
		for (Predicate pre : predicates) {
			if (target == Integer.parseInt(pre
					.getConstraintTarget().substring(
							0,
							pre.getConstraintTarget().indexOf(
									'[')))) {
				constraints.add(pre);
			}
		}
		for (ConformanceStatement conformanceStatement : conformanceStatements) {
			if (target == Integer.parseInt(conformanceStatement
					.getConstraintTarget().substring(
							0,
							conformanceStatement.getConstraintTarget().indexOf(
									'[')))) {
				constraints.add(conformanceStatement);
			}
		}
		return constraints;
	}

	private nu.xom.Element serializeDatatype(Datatype d, Tables tables, Datatypes datatypes) {
		nu.xom.Element elmDatatype = new nu.xom.Element("Datatype");
		elmDatatype.addAttribute(new Attribute("ID", d.getId() + ""));
		elmDatatype.addAttribute(new Attribute("Name", d.getName()));
		elmDatatype.addAttribute(new Attribute("Label", d.getLabel()));
		elmDatatype.addAttribute(new Attribute("Description", d
				.getDescription()));
		elmDatatype.addAttribute(new Attribute("Comment", d.getComment()));
		nu.xom.Element elmText = new nu.xom.Element("Texts");
		elmText.addAttribute(new Attribute("Type", "UsageNote"));
		elmText.appendChild(d.getUsageNote());
		elmDatatype.appendChild(elmText);

		if (d.getComponents() != null) {

			Map<Integer, Component> components = new HashMap<Integer, Component>();

			for (Component c : d.getComponents()) {
				components.put(c.getPosition(), c);
			}

			for (int i = 1; i < components.size() + 1; i++) {
				Component c = components.get(i);
				nu.xom.Element elmComponent = new nu.xom.Element("Component");
				elmComponent.addAttribute(new Attribute("Name", c.getName()));
				elmComponent.addAttribute(new Attribute("Usage", c.getUsage()
						.toString()));
				elmComponent.addAttribute(new Attribute("Datatype", datatypes.findOne(
						c.getDatatype()).getLabel()));
				elmComponent.addAttribute(new Attribute("MinLength", ""
						+ c.getMinLength()));
				if (c.getMaxLength() != null && !c.getMaxLength().equals(""))
					elmComponent.addAttribute(new Attribute("MaxLength", c
							.getMaxLength()));
				if (c.getConfLength() != null && !c.getConfLength().equals(""))

					elmComponent.addAttribute(new Attribute("ConfLength", c
							.getConfLength()));
				if (c.getComment() != null && !c.getComment().equals(""))
					elmComponent.addAttribute(new Attribute("Comment", c.getComment()));
				elmComponent.addAttribute(new Attribute("Position", c.getPosition().toString()));
				if (c.getText() != null) {
					elmComponent.appendChild(this.serializeRichtext("Text", c.getText()));
				}

				if (c.getTable() != null && !c.getTable().equals(""))
					if (tables.findOneTableById(c.getTable()) != null){
						elmComponent.addAttribute(new Attribute("Binding", tables
								.findOneTableById(c.getTable()).getBindingIdentifier() + ""));
					} else {
						logger.warn("Value set not found in library");
						elmComponent.addAttribute(new Attribute("Binding", c.getTable()));
					}

				List<Constraint> constraints = findConstraints( i, d.getPredicates(), d.getConformanceStatements());
				if (!constraints.isEmpty()) {
					for (Constraint constraint : constraints) {
						nu.xom.Element elmConstraint = new nu.xom.Element("Constraint");
						if (constraint instanceof Predicate) {
							elmConstraint.addAttribute(new Attribute("Type", "ConditionPredicate"));
						} else if (constraint instanceof ConformanceStatement) {
							elmConstraint.addAttribute(new Attribute("Type", "ConformanceStatement"));
						}
						elmConstraint.appendChild(constraint.getDescription());
						elmComponent.appendChild(elmConstraint);
					}
				}
				elmDatatype.appendChild(elmComponent);
			}
		}
		return elmDatatype;
	}

	@Override
	public Profile deserializeXMLToProfile(String xmlContentsProfile,
			String xmlValueSet, String xmlConstraints) {
		Document profileDoc = this.stringToDom(xmlContentsProfile);
		Profile profile = new Profile();
		profile.setMetaData(new ProfileMetaData());
		Element elmConformanceProfile = (Element) profileDoc
				.getElementsByTagName("ConformanceProfile").item(0);

		// Read Profile Meta
		profile.getMetaData().setType(
				elmConformanceProfile.getAttribute("Type"));
		profile.getMetaData().setHl7Version(
				elmConformanceProfile.getAttribute("HL7Version"));
		profile.getMetaData().setSchemaVersion(
				elmConformanceProfile.getAttribute("SchemaVersion"));
		profile.setSegments(new Segments());
		profile.setDatatypes(new Datatypes());
		this.deserializeMetaData(profile, elmConformanceProfile);
		this.deserializeEncodings(profile, elmConformanceProfile);

		// Read Profile Libs
		profile.setTables(new TableSerializationImpl()
		.deserializeXMLToTableLibrary(xmlValueSet));
		this.conformanceStatement = new ConstraintsSerializationImpl()
		.deserializeXMLToConformanceStatements(xmlConstraints);
		this.predicates = new ConstraintsSerializationImpl()
		.deserializeXMLToPredicates(xmlConstraints);

		this.constructDatatypesMap((Element) elmConformanceProfile
				.getElementsByTagName("Datatypes").item(0), profile);
		Datatypes datatypes = new Datatypes();
		for (String key : datatypesMap.keySet()) {
			datatypes.addDatatype(datatypesMap.get(key));
		}
		profile.setDatatypes(datatypes);

		this.segmentsMap = this.constructSegmentsMap(
				(Element) elmConformanceProfile
				.getElementsByTagName("Segments").item(0), profile);
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
	public Profile deserializeXMLToProfile(nu.xom.Document docProfile,
			nu.xom.Document docValueSet, nu.xom.Document docConstraints) {
		return this.deserializeXMLToProfile(docProfile.toXML(),
				docValueSet.toXML(), docConstraints.toXML());
	}
	private void deserializeMetaData(Profile profile,
			Element elmConformanceProfile) {
		NodeList nodes = elmConformanceProfile.getElementsByTagName("MetaData");
		if (nodes != null && nodes.getLength() != 0) {
			ProfileMetaData metaData = new ProfileMetaData();
			Element elmMetaData = (Element) nodes.item(0);
			metaData.setName(elmMetaData.getAttribute("Name"));
			metaData.setOrgName(elmMetaData.getAttribute("OrgName"));
			metaData.setStatus(elmMetaData.getAttribute("Status"));
			metaData.setTopics(elmMetaData.getAttribute("Topics"));
			profile.setMetaData(metaData);
		}
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

	private void deserializeMessages(Profile profile,
			Element elmConformanceProfile) {
		NodeList nodes = elmConformanceProfile.getElementsByTagName("Message");
		if (nodes != null && nodes.getLength() != 0) {
			Messages messagesObj = new Messages();
			for (int i = 0; i < nodes.getLength(); i++) {
				Message messageObj1 = new Message();
				Element elmMessage = (Element) nodes.item(i);
				messageObj1.setDescription(elmMessage
						.getAttribute("Description"));
				messageObj1.setEvent(elmMessage.getAttribute("Event"));
				messageObj1.setStructID(elmMessage.getAttribute("StructID"));
				messageObj1.setMessageType(elmMessage.getAttribute("Type"));

				this.deserializeSegmentRefOrGroups(elmConformanceProfile,
						messageObj1, elmMessage, profile.getSegments(),
						profile.getDatatypes());
				messagesObj.addMessage(messageObj1);
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
		segmentRefObj
		.setUsage(Usage.fromValue(segmentElm.getAttribute("Usage")));
		segmentRefObj.setRef(this.segmentsMap.get(
				segmentElm.getAttribute("Ref")).getId());
		segmentRefOrGroups.add(segmentRefObj);
	}

	private Segment deserializeSegment(Element segmentElm, Profile profile) {
		Segment segmentObj = new Segment();
		segmentObj.setDescription(segmentElm.getAttribute("Description"));
		// [Woo] I assumed the default name could be base name.
		segmentObj.setLabel(segmentElm.getAttribute("ID"));
		segmentObj.setName(segmentElm.getAttribute("Name"));
		segmentObj.setPredicates(this.findPredicates(
				this.predicates.getSegments(), segmentElm.getAttribute("ID")));
		segmentObj.setConformanceStatements(this.findConformanceStatement(
				this.conformanceStatement.getSegments(),
				segmentElm.getAttribute("ID")));

		NodeList fields = segmentElm.getElementsByTagName("Field");
		for (int i = 0; i < fields.getLength(); i++) {
			Element fieldElm = (Element) fields.item(i);
			segmentObj.addField(this.deserializeField(fieldElm, segmentObj,
					profile, segmentElm.getAttribute("ID"), i));
		}
		return segmentObj;
	}

	private Field deserializeField(Element fieldElm, Segment segment,
			Profile profile, String segmentId, int position) {
		Field fieldObj = new Field();

		fieldObj.setConfLength(fieldElm.getAttribute("ConfLength"));
		fieldObj.setItemNo(fieldElm.getAttribute("ItemNo"));
		fieldObj.setMax(fieldElm.getAttribute("Max"));
		fieldObj.setMaxLength(fieldElm.getAttribute("MaxLength"));
		fieldObj.setMin(new Integer(fieldElm.getAttribute("Min")));
		fieldObj.setMinLength(new Integer(fieldElm.getAttribute("MinLength")));
		fieldObj.setName(fieldElm.getAttribute("Name"));
		fieldObj.setUsage(Usage.fromValue(fieldElm.getAttribute("Usage")));
		if (fieldElm.getAttribute("Table") != null) {
			String tableScript = fieldElm.getAttribute("Table");
			String[] tableTags = tableScript.split("#");

			if (tableTags.length == 1) {
				fieldObj.setTable(findTableIdByMappingId(tableTags[0],
						profile.getTables()));
			} else if (tableTags.length == 2) {
				fieldObj.setTable(findTableIdByMappingId(tableTags[0],
						profile.getTables()));
				fieldObj.setBindingStrength(tableTags[1]);
			} else if (tableTags.length == 3) {
				fieldObj.setTable(findTableIdByMappingId(tableTags[0],
						profile.getTables()));
				fieldObj.setBindingStrength(tableTags[1]);
				fieldObj.setBindingLocation(tableTags[2]);
			}
		}
		fieldObj.setDatatype(this.findDatatype(
				fieldElm.getAttribute("Datatype"), profile).getId());
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

	private void deserializeGroup(Element elmConformanceProfile,
			List<SegmentRefOrGroup> segmentRefOrGroups, Element groupElm,
			Segments segments, Datatypes datatypes) {
		Group groupObj = new Group();
		groupObj.setMax(groupElm.getAttribute("Max"));
		groupObj.setMin(new Integer(groupElm.getAttribute("Min")));
		groupObj.setName(groupElm.getAttribute("Name"));
		groupObj.setUsage(Usage.fromValue(groupElm.getAttribute("Usage")));

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

	public static void main(String[] args) throws IOException, ValidityException, ParsingException, TransformerConfigurationException {
		ProfileSerialization4ExportImpl test1 = new ProfileSerialization4ExportImpl();

		Profile p1 = test1.deserializeXMLToProfile(
				new String(Files.readAllBytes(Paths
						.get("src//main//resources//vxu//Profile.xml"))),
						new String(Files.readAllBytes(Paths
								.get("src//main//resources//vxu//ValueSets_all.xml"))),
								new String(Files.readAllBytes(Paths
										.get("src//main//resources//vxu//Constraints.xml"))));

		System.out.println(StringUtils.repeat("& * ", 25));
		ProfileMetaData metaData = p1.getMetaData();

		DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
		Date date = new Date();
		metaData.setDate(dateFormat.format(date));
		metaData.setName("IZ_VXU_X");
		metaData.setOrgName("NIST");
		metaData.setSubTitle("Specifications");
		metaData.setVersion("1.0");

		metaData.setHl7Version(HL7Version.V2_7.value());
		metaData.setSchemaVersion(SchemaVersion.V1_0.value());
		metaData.setStatus("Draft");

		p1.setMetaData(metaData);

		Message message = p1.getMessages().getChildren()
				.toArray(new Message[] {})[0];
		SegmentRef segmentRef = (SegmentRef) message.getChildren().get(0);
		Group group = (Group) message.getChildren().get(5);
		Segment segment = p1.getSegments().findOneSegmentById(segmentRef.getRef());
		Field field = segment.getFields().get(0);
		Datatype datatype = p1.getDatatypes().getChildren()
				.toArray(new Datatype[] {})[0];

		segment.setText1("<h5>Text 1; added before segment.</h5><p>Generated from text editor</p>" +
				"<p><b>Features:</b></p><ol><li>Element 1</li><li style=\"color: green;\"><b>bolding</b> Options</li>" +
				"<li><i>Italic Creation</i></li></ol><p><b>Link test:</b><a href=\"https://www.nist.com\">Nist</a></p>");
		segment.setText2("Text2; added after segment:<li style=\"color: blue;\"><b>Theming</b> Options</li><li>some other options.</li>");
		segment.setComment("new segment comment");

		segmentRef.setMin(3);
		segmentRef.setMax("94969");

		field.setComment("wawa");
		field.setText("<li>field text 1</li><li>field text2</li>");
		field.setName("new field name");

		group.setMax("*");
		group.setComment("new group comment");

		p1.getMetaData().setName("IZ_VXU_X");
		datatype.setComment("new dt comment");
		datatype.setComment("new dt comment");

		message.setUsageNote("<b>message usage note</b>");
		message.setComment("Message comment");

		field.setComment("wawa");
		field.setText("This field is used to decide whether to process the message as defined in HL7 Application (level 7) Processing rules. <b>This is a required field.</b> Use 'P' for Production and 'T' for Testing, all other valuesText regarding a field");
		group.setMax("*");
		group.setComment("new group comment");



		System.out.println(test1.serializeProfileToXML(p1));
		//              System.out.println(child.toXML());
		//              System.out.println(message.getComment());
		//              System.out
		//              .println(test2.serializeTableLibraryToXML(profile.getTables()));
		//              System.out.println(test3.serializeConstraintsToXML(
		//              profile.getConformanceStatements(), profile.getPredicates()));




		//              // Apply XSL transformation on xml file to generate html
		//              Source text = new StreamSource(tmpXmlFile);
		//              TransformerFactory factory = TransformerFactory.newInstance();
		//              Source xslt = new StreamSource(new File(
		//                              "/Users/marieros/git/igl_new_dl2/igamt-lite-service/src/main/resources/rendering/profile2.xsl"));
		//              Transformer transformer;
		//              try {
		//                      transformer = factory.newTransformer(xslt);
		//                      transformer.setParameter("inlineConstraints", "false");
		//                      //File tmpHtmlFile = File.createTempFile("ProfileTemp", ".html");
		//                      File tmpHtmlFile = new File("/Users/marieros/Documents/testXslt/nvo/hh.html");
		//                      transformer.transform(text, new StreamResult(tmpHtmlFile));
		//              } catch (TransformerException e) {
		//                      e.printStackTrace();
		//              }
	}


	public File serializeProfileToZipFile(Profile profile) throws UnsupportedEncodingException {
		File out;
		try {
			out = File.createTempFile("Profile_" + profile.getId(), ".zip");
			FileOutputStream outputStream = (FileOutputStream) Files.newOutputStream(out.toPath());

			int read = 0;
			byte[] bytes = new byte[1024];

			InputStream is = this.serializeProfileToZip(profile);
			while ((read = is.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}

			is.close();
			outputStream.close();

			return out;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}

	}


	@Override
	public InputStream serializeProfileToZip(Profile profile) throws IOException {
		ByteArrayOutputStream outputStream = null;
		byte[] bytes;
		outputStream = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(outputStream);

		this.generateProfileIS(out, this.serializeProfileToXML(profile));
		this.generateValueSetIS(out, new TableSerializationImpl().serializeTableLibraryToXML(profile.getTables()));
		this.generateConstraintsIS(out, new ConstraintsSerializationImpl().serializeConstraintsToXML(profile.getConformanceStatements(), profile.getPredicates()));

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
		out.putNextEntry(new ZipEntry("ValueSet.xml"));
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
		out.putNextEntry(new ZipEntry("Profile.xml"));
		InputStream inConstraints = IOUtils.toInputStream(constraintsXML);
		int lenTP;
		while ((lenTP = inConstraints.read(buf)) > 0) {
			out.write(buf, 0, lenTP);
		}
		out.closeEntry();
		inConstraints.close();
	}
}