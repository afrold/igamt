package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.xml;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatypes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Encoding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Encodings;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segments;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByID;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByNameOrByID;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Context;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables.TableLibrary;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javassist.bytecode.Descriptor.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nu.xom.Attribute;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ProfileSerializationImpl implements ProfileSerialization{

	private HashMap<String,Datatype> datatypesMap;
	private HashMap<String,Segment> segmentsMap;
	
	@Override
	public Profile deserializeXMLToProfile(String xmlContentsProfile, String xmlValueSet, String xmlPredicates, String xmlConformanceStatements) {
		Document profileDoc = this.stringToDom(xmlContentsProfile);
		Profile profile = new Profile();
		Element elmConformanceProfile = (Element)profileDoc.getElementsByTagName("ConformanceProfile").item(0);
		
		//Read Profile Meta
		profile.setType(elmConformanceProfile.getAttribute("Type"));
		profile.setHl7Version(elmConformanceProfile.getAttribute("HL7Version"));
		profile.setSchemaVersion(elmConformanceProfile.getAttribute("SchemaVersion"));
		profile.setSegments(new Segments());
		profile.setDatatypes(new Datatypes());
		this.deserializeMetaData(profile, elmConformanceProfile);
		this.deserializeEncodings(profile, elmConformanceProfile);
		
		
		//Read Profile Libs
		profile.setTableLibrary(new TableSerializationImpl().deserializeXMLToTableLibrary(xmlValueSet));
//		profile.setConformanceStatements(new ConstraintsSerializationImpl().deserializeXMLToConformanceContext(xmlConformanceStatements));
//		profile.setPredicates(new ConstraintsSerializationImpl().deserializeXMLToConformanceContext(xmlPredicates));
		
		this.datatypesMap = this.constructDatatypesMap((Element)elmConformanceProfile.getElementsByTagName("Datatypes").item(0), profile);
		Datatypes datatypes = new Datatypes();
		for(String key:datatypesMap.keySet()){
			datatypes.addDatatype(datatypesMap.get(key));
		}
		profile.setDatatypes(datatypes);
		
		this.segmentsMap = this.constructSegmentsMap((Element)elmConformanceProfile.getElementsByTagName("Segments").item(0), profile);
		Segments segments = new Segments();
		for(String key:segmentsMap.keySet()){
			segments.addSegment(segmentsMap.get(key));
		}
		profile.setSegments(segments);
		
		//Read Profile Messages
		this.deserializeMessages(profile, elmConformanceProfile);
		
		return profile;
	}

	@Override
	public Profile deserializeXMLToProfile(nu.xom.Document docProfile, nu.xom.Document docValueSet, nu.xom.Document docPredicates, nu.xom.Document docConformanceStatements) {
		return this.deserializeXMLToProfile(docProfile.toXML(), docValueSet.toXML(), docPredicates.toXML(), docConformanceStatements.toXML());
	}
	

	@Override
	public String serializeProfileToXML(Profile profile) {
		return this.serializeProfileToDoc(profile).toXML();
	}
	
	@Override
	public nu.xom.Document serializeProfileToDoc(Profile profile) {
		nu.xom.Element e = new nu.xom.Element("ConformanceProfile");
		e.addAttribute(new Attribute("ID", profile.getId() + ""));
		if(profile.getType() != null && !profile.getType().equals("")) e.addAttribute(new Attribute("Type", profile.getType()));
		if(profile.getHl7Version() != null && !profile.getHl7Version().equals("")) e.addAttribute(new Attribute("HL7Version", profile.getHl7Version()));
		if(profile.getSchemaVersion() != null && !profile.getSchemaVersion().equals("")) e.addAttribute(new Attribute("SchemaVersion", profile.getSchemaVersion()));
		
		if(profile.getMetaData() != null){
			nu.xom.Element elmMetaData = new nu.xom.Element("MetaData");
			ProfileMetaData metaDataObj = profile.getMetaData();
			elmMetaData.addAttribute(new Attribute("Name", metaDataObj.getName()));
			elmMetaData.addAttribute(new Attribute("OrgName", metaDataObj.getOrgName()));
//			if(metaDataObj.getVersion() != null) elmMetaData.addAttribute(new Attribute("Version", metaDataObj.getVersion()));
			if(metaDataObj.getStatus() != null) elmMetaData.addAttribute(new Attribute("Status", metaDataObj.getStatus()));
			if(metaDataObj.getTopics() != null) elmMetaData.addAttribute(new Attribute("Topics", metaDataObj.getTopics()));
			
			e.appendChild(elmMetaData);
		}
		
		if(profile.getEncodings() != null && profile.getEncodings().getEncodings().size() > 0){
			nu.xom.Element elmEncodings = new nu.xom.Element("Encodings");
			for(Encoding encoding:profile.getEncodings().getEncodings()){
				nu.xom.Element elmEncoding = new nu.xom.Element("Encoding");
				elmEncoding.appendChild(encoding.getValue());
				elmEncodings.appendChild(elmEncoding);
			}
			e.appendChild(elmEncodings);
		}
		
		nu.xom.Element ms = new nu.xom.Element("Messages");
		for(Message m: profile.getMessages().getMessages()){
			ms.appendChild(this.serializeMessage(m));
		}
		e.appendChild(ms);
		
		nu.xom.Element ss = new nu.xom.Element("Segments");
		for(Segment s: profile.getSegments().getSegments()){
			ss.appendChild(this.serializeSegment(s));
		}
		e.appendChild(ss);
		
		nu.xom.Element ds = new nu.xom.Element("Datatypes");
		for(Datatype d:profile.getDatatypes().getDatatypes()){
			ds.appendChild(this.serializeDatatype(d));
		}
		e.appendChild(ds);
		
		
		nu.xom.Document doc = new nu.xom.Document(e);
		
		return doc;
	}
	
	private HashMap<String, Datatype> constructDatatypesMap(Element elmDatatypes, Profile profile) {
		this.datatypesMap = new HashMap<String,Datatype>();
		NodeList datatypeNodeList = elmDatatypes.getElementsByTagName("Datatype");
		
		for(int i=0; i < datatypeNodeList.getLength(); i++){
			Element elmDatatype = (Element)datatypeNodeList.item(i);
			datatypesMap.put(elmDatatype.getAttribute("ID"), this.deserializeDatatype(elmDatatype, profile, elmDatatypes));
		}
		
		return datatypesMap;
	}

	private Datatype deserializeDatatype(Element elmDatatype, Profile profile, Element elmDatatypes) {
		Datatype datatypeObj = new Datatype();
		datatypeObj.setDescription(elmDatatype.getAttribute("Description"));
		//[Woo] I assumed the default name could be base name.
		datatypeObj.setLabel(elmDatatype.getAttribute("ID"));
		datatypeObj.setName(elmDatatype.getAttribute("Name"));
//		datatypeObj.setPredicates(this.findConstraints(profile.getPredicates().getDatatypes(), elmDatatype.getAttribute("ID")));
//		datatypeObj.setConformanceStatements(this.findConstraints(profile.getConformanceStatements().getDatatypes(), elmDatatype.getAttribute("ID")));
		
		NodeList nodes = elmDatatype.getChildNodes();
		for(int i=0; i < nodes.getLength(); i++){
			if(nodes.item(i).getNodeName().equals("Component")){
				Element elmComponent = (Element)nodes.item(i);
				Component componentObj = new Component();
				componentObj.setConfLength(elmComponent.getAttribute("ConfLength"));
				componentObj.setMaxLength(elmComponent.getAttribute("MaxLength"));
				componentObj.setMinLength(new Integer(elmComponent.getAttribute("MinLength")));
				componentObj.setName(elmComponent.getAttribute("Name"));
				componentObj.setTable(this.findTable(elmComponent.getAttribute("Table"), profile.getTableLibrary()));
				componentObj.setUsage(Usage.fromValue(elmComponent.getAttribute("Usage")));
//				componentObj.setBelongTo(datatypeObj);
				componentObj.setBindingLocation(elmComponent.getAttribute("BindingLocation"));
				componentObj.setBindingStrength(elmComponent.getAttribute("BindingStrength"));
 				componentObj.setDatatype(this.findDatatype(elmComponent.getAttribute("Datatype"), profile, elmDatatypes));
				datatypeObj.addComponent(componentObj);
			}
		}
		return datatypeObj;
	}

	private Set<Constraint> findConstraints(Context context, String key) {
		Set<ByNameOrByID> byNameOrByIDs = context.getByNameOrByIDs();
		Set<Constraint> result= new HashSet<Constraint>();
		for(ByNameOrByID byNameOrByID:byNameOrByIDs){
			if(byNameOrByID instanceof ByID){
				ByID byID = (ByID)byNameOrByID;
				
				if(byID.getByID().equals(key)){
					for(Constraint c:byID.getConstraints()){
						result.add(c);
					}
				}
				
			}
		}
		
		return result;
	}

	private Datatype findDatatype(String key, Profile profile, Element elmDatatypes) {
		if(datatypesMap.get(key) != null) return datatypesMap.get(key);
		NodeList datatypes = elmDatatypes.getElementsByTagName("Datatype");
		for(int i=0; i<datatypes.getLength(); i++){
			Element elmDatatype = (Element)datatypes.item(i);
			if(elmDatatype.getAttribute("ID").equals(key)) return this.deserializeDatatype(elmDatatype, profile, elmDatatypes);
		}
		System.out.println("NULL DT found");
		return null;
	}

	private HashMap<String, Segment> constructSegmentsMap(Element elmSegments, Profile profile) {
		HashMap<String,Segment> segmentsMap = new HashMap<String,Segment>();
		NodeList segmentNodeList = elmSegments.getElementsByTagName("Segment");
		
		for(int i=0; i < segmentNodeList.getLength(); i++){
			Element elmSegment = (Element)segmentNodeList.item(i);
			segmentsMap.put(elmSegment.getAttribute("ID"), this.deserializeSegment(elmSegment, profile));
		}
		
		return segmentsMap;
	}
	
	private nu.xom.Element serializeMessage(Message m) {
		nu.xom.Element elmMessage = new nu.xom.Element("Message");
		elmMessage.addAttribute(new Attribute("ID", m.getId()+""));
		elmMessage.addAttribute(new Attribute("Type", m.getType()));
		elmMessage.addAttribute(new Attribute("Event", m.getEvent()));
		elmMessage.addAttribute(new Attribute("StructID", m.getStructID()));
		if(m.getDescription() != null && !m.getDescription().equals(""))  elmMessage.addAttribute(new Attribute("Description", m.getDescription()));
		
		
		for(SegmentRefOrGroup segmentRefOrGroup:m.getSegmentRefOrGroups()){
			if(segmentRefOrGroup instanceof SegmentRef){
				elmMessage.appendChild(serializeSegmentRef((SegmentRef)segmentRefOrGroup));
			}else if(segmentRefOrGroup instanceof Group){
				elmMessage.appendChild(serializeGroup((Group)segmentRefOrGroup));
			}
		}
		
		return elmMessage;
	}

	private nu.xom.Element serializeGroup(Group group) {
		nu.xom.Element elmGroup = new nu.xom.Element("Group");
		elmGroup.addAttribute(new Attribute("Name", group.getName()));
		elmGroup.addAttribute(new Attribute("Usage", group.getUsage().value()));
		elmGroup.addAttribute(new Attribute("Min", group.getMin() + ""));
		elmGroup.addAttribute(new Attribute("Max", group.getMax()));
		
		for(SegmentRefOrGroup segmentRefOrGroup:group.getSegmentsOrGroups()){
			if(segmentRefOrGroup instanceof SegmentRef){
				elmGroup.appendChild(serializeSegmentRef((SegmentRef)segmentRefOrGroup));
			}else if(segmentRefOrGroup instanceof Group){
				elmGroup.appendChild(serializeGroup((Group)segmentRefOrGroup));
			}
		}
		
		
		return elmGroup;
	}

	private nu.xom.Element serializeSegmentRef(SegmentRef segmentRef) {
		nu.xom.Element elmSegment = new nu.xom.Element("Segment");
		elmSegment.addAttribute(new Attribute("Ref", segmentRef.getSegment().getLabel()+ ""));
		elmSegment.addAttribute(new Attribute("Usage", segmentRef.getUsage().value()));
		elmSegment.addAttribute(new Attribute("Min", segmentRef.getMin() + ""));
		elmSegment.addAttribute(new Attribute("Max", segmentRef.getMax()));
		return elmSegment;
	}

	private nu.xom.Element serializeSegment(Segment s) {
		nu.xom.Element elmSegment = new nu.xom.Element("Segment");
		elmSegment.addAttribute(new Attribute("ID", s.getLabel()+""));
		elmSegment.addAttribute(new Attribute("Name", s.getName()));
		elmSegment.addAttribute(new Attribute("Description", s.getDescription()));		
		for(Field f:s.getFields()){
			nu.xom.Element elmField = new nu.xom.Element("Field");
			elmField.addAttribute(new Attribute("Name", f.getName()));
			elmField.addAttribute(new Attribute("Usage", f.getUsage().toString()));
			elmField.addAttribute(new Attribute("Datatype", f.getDatatype().getLabel()+ ""));
			elmField.addAttribute(new Attribute("MinLength", "" + f.getMinLength()));
			elmField.addAttribute(new Attribute("Min", "" + f.getMin()));
			elmField.addAttribute(new Attribute("Max", "" + f.getMax()));
			if(f.getMaxLength() != null && !f.getMaxLength().equals("")) elmField.addAttribute(new Attribute("MaxLength", f.getMaxLength()));
			if(f.getConfLength() != null && !f.getConfLength().equals("")) elmField.addAttribute(new Attribute("ConfLength", f.getConfLength()));
			if(f.getTable() != null && !f.getTable().equals("")) elmField.addAttribute(new Attribute("Table", f.getTable().getMappingId()));
			if(f.getItemNo() != null && !f.getItemNo().equals("")) elmField.addAttribute(new Attribute("ItemNo", f.getItemNo()));
			elmSegment.appendChild(elmField);
		}
		return elmSegment;
	}

	private nu.xom.Element serializeDatatype(Datatype d) {
		nu.xom.Element elmDatatype = new nu.xom.Element("Datatype");
		elmDatatype.addAttribute(new Attribute("ID", d.getLabel() + ""));
		elmDatatype.addAttribute(new Attribute("Name", d.getName()));
		elmDatatype.addAttribute(new Attribute("Description", d.getDescription()));	
		
		for(Component c:d.getComponents()){
			nu.xom.Element elmComponent = new nu.xom.Element("Component");
			elmComponent.addAttribute(new Attribute("Name", c.getName()));
			elmComponent.addAttribute(new Attribute("Usage", c.getUsage().toString()));
			elmComponent.addAttribute(new Attribute("Datatype", c.getDatatype().getLabel() + ""));
			elmComponent.addAttribute(new Attribute("MinLength", "" + c.getMinLength()));
			if(c.getMaxLength() != null && !c.getMaxLength().equals("")) elmComponent.addAttribute(new Attribute("MaxLength", c.getMaxLength()));
			if(c.getConfLength() != null && !c.getConfLength().equals("")) elmComponent.addAttribute(new Attribute("ConfLength", c.getConfLength()));
			if(c.getTable() != null && !c.getTable().equals("")) elmComponent.addAttribute(new Attribute("Table", c.getTable().getMappingId() +""));
			elmDatatype.appendChild(elmComponent);
		}
		return elmDatatype;
	}
	
	private void deserializeMetaData(Profile profile, Element elmConformanceProfile){
		NodeList nodes = elmConformanceProfile.getElementsByTagName("MetaData");
		if(nodes != null && nodes.getLength() != 0){
			ProfileMetaData metaData = new ProfileMetaData();
			Element elmMetaData = (Element)nodes.item(0);
			metaData.setName(elmMetaData.getAttribute("Name"));
			metaData.setOrgName(elmMetaData.getAttribute("OrgName"));
//			metaData.setVersion(elmMetaData.getAttribute("Version"));
			metaData.setStatus(elmMetaData.getAttribute("Status"));
			metaData.setTopics(elmMetaData.getAttribute("Topics"));
			profile.setMetaData(metaData);
		}
	}
	
	private void deserializeEncodings(Profile profile, Element elmConformanceProfile){
		NodeList nodes = elmConformanceProfile.getElementsByTagName("Encoding");
		if(nodes != null && nodes.getLength() != 0){
			Encodings encodings = new Encodings();
			Set<Encoding> encodingSet = new HashSet<Encoding>();
			for(int i=0; i<nodes.getLength(); i++){
				encodingSet.add(new Encoding(nodes.item(i).getTextContent()));
			}
			
			encodings.setEncodings(encodingSet);
			profile.setEncodings(encodings);
		}
	}
	
	private void deserializeMessages(Profile profile, Element elmConformanceProfile){
		NodeList nodes = elmConformanceProfile.getElementsByTagName("Message");
		if(nodes != null && nodes.getLength() != 0){
			Messages messagesObj = new Messages();
 			for(int i=0; i<nodes.getLength(); i++){
				Message messageObj1 = new Message();
				Element elmMessage = (Element)nodes.item(i);
				messageObj1.setDescription(elmMessage.getAttribute("Description"));
				messageObj1.setEvent(elmMessage.getAttribute("Event"));
				messageObj1.setStructID(elmMessage.getAttribute("StructID"));
				messageObj1.setType(elmMessage.getAttribute("Type"));
				
				this.deserializeSegmentRefOrGroups(elmConformanceProfile, messageObj1, elmMessage, profile.getSegments(), profile.getDatatypes());				
 				messagesObj.addMessage(messageObj1);
			}
 			profile.setMessages(messagesObj);
		}
	}
	
	private void deserializeSegmentRefOrGroups(Element elmConformanceProfile, Message messageObj , Element elmMessage, Segments segments, Datatypes datatypes){
		Set<SegmentRefOrGroup> segmentRefOrGroups = new LinkedHashSet<SegmentRefOrGroup>();
		NodeList nodes = elmMessage.getChildNodes();
		
		for(int i=0;i<nodes.getLength(); i++){	
			if(nodes.item(i).getNodeName().equals("Segment")){
				this.deserializeSegmentRef(elmConformanceProfile, segmentRefOrGroups, (Element)nodes.item(i), segments, datatypes);
			}else if(nodes.item(i).getNodeName().equals("Group")){
				this.deserializeGroup(elmConformanceProfile, segmentRefOrGroups, (Element)nodes.item(i), segments, datatypes);
			}
		}
		
		java.util.Iterator<SegmentRefOrGroup> it = segmentRefOrGroups.iterator();
		while(it.hasNext()){
			messageObj.addChild(it.next());
		}
		
		
	}
	
	private void deserializeSegmentRef(Element elmConformanceProfile, Set<SegmentRefOrGroup> segmentRefOrGroups, Element segmentElm, Segments segments, Datatypes datatypes){
		SegmentRef segmentRefObj = new SegmentRef();
		segmentRefObj.setMax(segmentElm.getAttribute("Max"));
		segmentRefObj.setMin(new Integer(segmentElm.getAttribute("Min")));
		segmentRefObj.setUsage(Usage.fromValue(segmentElm.getAttribute("Usage")));
		segmentRefObj.setSegment(this.segmentsMap.get(segmentElm.getAttribute("Ref")));
		segmentRefOrGroups.add(segmentRefObj);
	}
	
	private Segment deserializeSegment(Element segmentElm, Profile profile){
		Segment segmentObj = new Segment();
		segmentObj.setDescription(segmentElm.getAttribute("Description"));
		//[Woo] I assumed the default name could be base name.
		segmentObj.setLabel(segmentElm.getAttribute("ID"));
		segmentObj.setName(segmentElm.getAttribute("Name"));
//		segmentObj.setPredicates(this.findConstraints(profile.getPredicates().getSegments(), segmentElm.getAttribute("ID")));
//		segmentObj.setConformanceStatements(this.findConstraints(profile.getConformanceStatements().getSegments(), segmentElm.getAttribute("ID")));
		
		NodeList fields = segmentElm.getElementsByTagName("Field");
		for(int i=0; i<fields.getLength(); i++){
			Element fieldElm = (Element)fields.item(i);	
			segmentObj.addField(this.deserializeField(fieldElm, segmentObj, profile, segmentElm.getAttribute("ID"), i));
		}
		return segmentObj;
	}
	
	private Field deserializeField(Element fieldElm, Segment segment, Profile profile, String segmentId, int position){
		Field fieldObj = new Field();
		
		fieldObj.setConfLength(fieldElm.getAttribute("ConfLength"));
		fieldObj.setItemNo(fieldElm.getAttribute("ItemNo"));
		fieldObj.setMax(fieldElm.getAttribute("Max"));
		fieldObj.setMaxLength(fieldElm.getAttribute("MaxLength"));
		fieldObj.setMin(new BigInteger(fieldElm.getAttribute("Min")));
		fieldObj.setMinLength(new Integer(fieldElm.getAttribute("MinLength")));
		fieldObj.setName(fieldElm.getAttribute("Name"));
		fieldObj.setUsage(Usage.fromValue(fieldElm.getAttribute("Usage")));
//		fieldObj.setSegment(segment);
		fieldObj.setTable(this.findTable(fieldElm.getAttribute("Table"), profile.getTableLibrary()));
		fieldObj.setBindingStrength(fieldElm.getAttribute("BindingStrength"));
		fieldObj.setBindingLocation(fieldElm.getAttribute("BindingLocation"));	
		fieldObj.setDatatype(this.datatypesMap.get(fieldElm.getAttribute("Datatype")));

		return fieldObj;
	}
	
	private void deserializeGroup(Element elmConformanceProfile, Set<SegmentRefOrGroup> segmentRefOrGroups, Element groupElm, Segments segments, Datatypes datatypes){
		Group groupObj = new Group();
		groupObj.setMax(groupElm.getAttribute("Max"));
		groupObj.setMin(new Integer(groupElm.getAttribute("Min")));
		groupObj.setName(groupElm.getAttribute("Name"));
		groupObj.setUsage(Usage.fromValue(groupElm.getAttribute("Usage")));
		
		Set<SegmentRefOrGroup> childSegmentRefOrGroups = new LinkedHashSet<SegmentRefOrGroup>();
		
		NodeList nodes = groupElm.getChildNodes();
		for(int i=0;i<nodes.getLength(); i++){	
			if(nodes.item(i).getNodeName().equals("Segment")){
				this.deserializeSegmentRef(elmConformanceProfile, childSegmentRefOrGroups, (Element)nodes.item(i), segments, datatypes);
			}else if(nodes.item(i).getNodeName().equals("Group")){
				this.deserializeGroup(elmConformanceProfile, childSegmentRefOrGroups, (Element)nodes.item(i), segments, datatypes);
			}
		}
		
		groupObj.setSegmentsOrGroups(childSegmentRefOrGroups);
		
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
	
	private Table findTable(String mappingId, TableLibrary tableLibrary){
		for(Table t:tableLibrary.getTables().getTables()){
			if(t.getMappingId().equals(mappingId)) return t;
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

	public static void main(String[] args) throws IOException {
		ProfileSerializationImpl test1 = new ProfileSerializationImpl();
		TableSerializationImpl test2 = new TableSerializationImpl();
		ConstraintsSerializationImpl test3 = new ConstraintsSerializationImpl();
		ConstraintsSerializationImpl test4 = new ConstraintsSerializationImpl();
		
//		Profile profile = test1.deserializeXMLToProfile(new String(Files.readAllBytes(Paths.get("src//main//resources//vxu//Profile.xml"))),
//				new String(Files.readAllBytes(Paths.get("src//main//resources//vxu//ValueSets_all.xml"))),
//				new String(Files.readAllBytes(Paths.get("src//main//resources//vxu//PredicateConstraints.xml"))),
//				new String(Files.readAllBytes(Paths.get("src//main//resources//vxu//ConformanceStatementConstraints.xml"))));
		Profile profile = test1.deserializeXMLToProfile(new String(Files.readAllBytes(Paths.get("src//main//resources//vxu//Profile.xml"))),
				new String(Files.readAllBytes(Paths.get("src//main//resources//vxu//ValueSets_all.xml"))),
				null,
				null);
		System.out.println(test1.serializeProfileToXML(profile));
		System.out.println(test2.serializeTableLibraryToXML(profile.getTableLibrary()));
//		System.out.println(test3.serializeConformanceContextToXML(profile.getConformanceStatements()));
//		System.out.println(test4.serializeConformanceContextToXML(profile.getPredicates()));
	}
}
