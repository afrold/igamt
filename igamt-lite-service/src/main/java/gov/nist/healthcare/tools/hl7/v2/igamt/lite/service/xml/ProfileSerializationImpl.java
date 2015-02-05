package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.xml;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatypes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Encodings;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segments;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables.TableLibrary;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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

	@Override
	public Profile deserializeXMLToProfile(String xmlContentsProfile, String xmlValueSet, String xmlPredicates, String xmlConformanceStatements) {
		Document profileDoc = this.stringToDom(xmlContentsProfile);
		Profile profile = new Profile();
		
		Element elmConformanceProfile = (Element)profileDoc.getElementsByTagName("ConformanceProfile").item(0);
		profile.setId(elmConformanceProfile.getAttribute("ID"));
		profile.setType(elmConformanceProfile.getAttribute("Type"));
		profile.setHl7Version(elmConformanceProfile.getAttribute("HL7Version"));
		profile.setSchemaVersion(elmConformanceProfile.getAttribute("SchemaVersion"));
		profile.setSegments(new Segments());
		profile.setDatatypes(new Datatypes());
		
		profile.setTableLibrary(new TableSerializationImpl().deserializeXMLToTableLibrary(xmlValueSet));
		profile.setConformanceStatementsLibrary(new ConstraintsSerializationImpl().deserializeXMLToConformanceContext(xmlConformanceStatements));
		profile.setPredicatesLibrary(new ConstraintsSerializationImpl().deserializeXMLToConformanceContext(xmlPredicates));
		
		this.deserializeMetaData(profile, elmConformanceProfile);
		this.deserializeEncodings(profile, elmConformanceProfile);
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
		//FIXME: The ID need to be fixed in IGAMT, it has to be something 
		// predictable since it will be used while writing constrains
		e.addAttribute(new Attribute("ID", UUID.randomUUID().toString()));
		e.addAttribute(new Attribute("Type", profile.getType()));
		e.addAttribute(new Attribute("HL7Version", profile.getHl7Version()));
		e.addAttribute(new Attribute("SchemaVersion", profile.getSchemaVersion()));
		
		if(profile.getMetaData() != null){
			nu.xom.Element elmMetaData = new nu.xom.Element("MetaData");
			ProfileMetaData metaDataObj = profile.getMetaData();
			elmMetaData.addAttribute(new Attribute("Name", metaDataObj.getName()));
			elmMetaData.addAttribute(new Attribute("OrgName", metaDataObj.getOrgName()));
			if(metaDataObj.getVersion() != null) elmMetaData.addAttribute(new Attribute("Version", metaDataObj.getVersion()));
			if(metaDataObj.getStatus() != null) elmMetaData.addAttribute(new Attribute("Status", metaDataObj.getStatus()));
			if(metaDataObj.getTopics() != null) elmMetaData.addAttribute(new Attribute("Topics", metaDataObj.getTopics()));
			
			e.appendChild(elmMetaData);
		}
		
		if(profile.getEncodings() != null && profile.getEncodings().getEncoding().size() > 0){
			nu.xom.Element elmEncodings = new nu.xom.Element("Encodings");
			for(String s:profile.getEncodings().getEncoding()){
				nu.xom.Element elmEncoding = new nu.xom.Element("Encoding");
				elmEncoding.appendChild(s);
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
	
	private nu.xom.Element serializeMessage(Message m) {
		nu.xom.Element elmMessage = new nu.xom.Element("Message");
		elmMessage.addAttribute(new Attribute("ID", m.getUuid()));
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
		elmSegment.addAttribute(new Attribute("Ref", segmentRef.getSegment().getUuid()));
		elmSegment.addAttribute(new Attribute("Usage", segmentRef.getUsage().value()));
		elmSegment.addAttribute(new Attribute("Min", segmentRef.getMin() + ""));
		elmSegment.addAttribute(new Attribute("Max", segmentRef.getMax()));
		return elmSegment;
	}

	private nu.xom.Element serializeSegment(Segment s) {
		nu.xom.Element elmSegment = new nu.xom.Element("Segment");
		elmSegment.addAttribute(new Attribute("ID", s.getUuid()));
		elmSegment.addAttribute(new Attribute("Name", s.getName()));
		elmSegment.addAttribute(new Attribute("Description", s.getDescription()));		
		for(Field f:s.getFields()){
			nu.xom.Element elmField = new nu.xom.Element("Field");
			elmField.addAttribute(new Attribute("Name", f.getName()));
			elmField.addAttribute(new Attribute("Usage", f.getUsage().toString()));
			elmField.addAttribute(new Attribute("Datatype", f.getDatatype().getUuid()));
			elmField.addAttribute(new Attribute("MinLength", "" + f.getMinLength()));
			elmField.addAttribute(new Attribute("Min", "" + f.getMin()));
			elmField.addAttribute(new Attribute("Max", "" + f.getMax()));
			if(f.getMaxLength() != null && !f.getMaxLength().equals("")) elmField.addAttribute(new Attribute("MaxLength", f.getMaxLength()));
			if(f.getConfLength() != null && !f.getConfLength().equals("")) elmField.addAttribute(new Attribute("ConfLength", f.getConfLength()));
			if(f.getTable() != null && !f.getTable().equals("")) elmField.addAttribute(new Attribute("Table", f.getTable()));
			if(f.getItemNo() != null && !f.getItemNo().equals("")) elmField.addAttribute(new Attribute("ItemNo", f.getItemNo()));
			elmSegment.appendChild(elmField);
		}
		return elmSegment;
	}

	private nu.xom.Element serializeDatatype(Datatype d) {
		nu.xom.Element elmDatatype = new nu.xom.Element("Datatype");
		elmDatatype.addAttribute(new Attribute("ID", d.getUuid()));
		elmDatatype.addAttribute(new Attribute("Name", d.getName()));
		elmDatatype.addAttribute(new Attribute("Description", d.getDescription()));	
		
		for(Component c:d.getComponents()){
			nu.xom.Element elmComponent = new nu.xom.Element("Component");
			elmComponent.addAttribute(new Attribute("Name", c.getName()));
			elmComponent.addAttribute(new Attribute("Usage", c.getUsage().toString()));
			elmComponent.addAttribute(new Attribute("Datatype", c.getDatatype().getUuid()));
			elmComponent.addAttribute(new Attribute("MinLength", "" + c.getMinLength()));
			if(c.getMaxLength() != null && !c.getMaxLength().equals("")) elmComponent.addAttribute(new Attribute("MaxLength", c.getMaxLength()));
			if(c.getConfLength() != null && !c.getConfLength().equals("")) elmComponent.addAttribute(new Attribute("ConfLength", c.getConfLength()));
			if(c.getTable() != null && !c.getTable().equals("")) elmComponent.addAttribute(new Attribute("Table", c.getTable()));
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
			metaData.setVersion(elmMetaData.getAttribute("Version"));
			metaData.setStatus(elmMetaData.getAttribute("Status"));
			metaData.setTopics(elmMetaData.getAttribute("Topics"));
			profile.setMetaData(metaData);
		}
	}
	
	private void deserializeEncodings(Profile profile, Element elmConformanceProfile){
		NodeList nodes = elmConformanceProfile.getElementsByTagName("Encoding");
		if(nodes != null && nodes.getLength() != 0){
			Encodings encodings = new Encodings();
			Set<String> encoding = new HashSet<String>();
			for(int i=0; i<nodes.getLength(); i++){
				encoding.add(nodes.item(i).getTextContent());
			}
			
			encodings.setEncoding(encoding);
			profile.setEncodings(encodings);
		}
	}
	
	private void deserializeMessages(Profile profile, Element elmConformanceProfile){
		NodeList nodes = elmConformanceProfile.getElementsByTagName("Message");
		if(nodes != null && nodes.getLength() != 0){
			Messages messagesObj = new Messages();
			Set<Message> messages = new HashSet<Message>();
			for(int i=0; i<nodes.getLength(); i++){
				Message messageObj = new Message();
				Element elmMessage = (Element)nodes.item(i);
				messageObj.setDescription(elmMessage.getAttribute("Description"));
				messageObj.setEvent(elmMessage.getAttribute("Event"));
				messageObj.setUuid(elmMessage.getAttribute("ID"));
				messageObj.setMessages(messagesObj);
				messageObj.setStructID(elmMessage.getAttribute("StructID"));
				messageObj.setType(elmMessage.getAttribute("Type"));
				
				this.deserializeSegmentRefOrGroups(elmConformanceProfile, messageObj, elmMessage, profile.getSegments(), profile.getDatatypes());
				
				messages.add(messageObj);
			}
			messagesObj.setProfile(profile);
			messagesObj.setMessages(messages);
			profile.setMessages(messagesObj);
		}
	}
	
	private void deserializeSegmentRefOrGroups(Element elmConformanceProfile, Message messageObj , Element elmMessage, Segments segments, Datatypes datatypes){
		List<SegmentRefOrGroup> segmentRefOrGroups = new ArrayList<SegmentRefOrGroup>();
		NodeList nodes = elmMessage.getChildNodes();
		
		for(int i=0;i<nodes.getLength(); i++){	
			if(nodes.item(i).getNodeName().equals("Segment")){
				this.deserializeSegmentRef(elmConformanceProfile, segmentRefOrGroups, (Element)nodes.item(i), segments, datatypes);
			}else if(nodes.item(i).getNodeName().equals("Group")){
				this.deserializeGroup(elmConformanceProfile, segmentRefOrGroups, (Element)nodes.item(i), segments, datatypes);
			}
		}
		
		messageObj.setSegmentRefOrGroups(segmentRefOrGroups);
	}
	
	private void deserializeSegmentRef(Element elmConformanceProfile, List<SegmentRefOrGroup> segmentRefOrGroups, Element segmentElm, Segments segments, Datatypes datatypes){
		SegmentRef segmentRefObj = new SegmentRef();
		segmentRefObj.setMax(segmentElm.getAttribute("Max"));
		segmentRefObj.setMin(new BigInteger(segmentElm.getAttribute("Min")));
		segmentRefObj.setUsage(Usage.fromValue(segmentElm.getAttribute("Usage")));
		
		this.deserializeSegment(elmConformanceProfile, segmentRefObj, segmentElm.getAttribute("Ref"), segments, datatypes);
		
		segmentRefOrGroups.add(segmentRefObj);
	}
	
	private void deserializeSegment(Element elmConformanceProfile, SegmentRef segmentRefObj, String ref, Segments segments, Datatypes datatypes){
		Element segmentElm = this.findSegmentElm(elmConformanceProfile, ref);
		
		
		if(segmentElm == null){
			segmentRefObj.setSegment(null);
		}else {
			Segment segmentObj = new Segment();
			
			segmentObj.setDescription(segmentElm.getAttribute("Description"));
			//FIXME: Need displayName on XML
			segmentObj.setDisplayName(segmentElm.getAttribute("Name"));
			segmentObj.setName(segmentElm.getAttribute("Name"));
			segmentObj.setUuid(ref);
			
			this.deserializeField(elmConformanceProfile, segmentElm, segmentObj, datatypes);
			
			segmentRefObj.setSegment(segmentObj);
			segments.addSegment(segmentObj);
		}
	}
	
	private void deserializeField(Profile profile, Element elmConformanceProfile, Element segmentElm, Segment segmentObj, Datatypes datatypes){
		NodeList nodes = segmentElm.getChildNodes();
		
		for(int i=0; i<nodes.getLength(); i++){
			if(nodes.item(i).getNodeName().equals("Field")){
				Element elmField = (Element)nodes.item(i);
				Field fieldObj = new Field();
				
				fieldObj.setConfLength(elmField.getAttribute("ConfLength"));
				fieldObj.setItemNo(elmField.getAttribute("ItemNo"));
				fieldObj.setMax(elmField.getAttribute("Max"));
				fieldObj.setMaxLength(elmField.getAttribute("MaxLength"));
				fieldObj.setMin(new BigInteger(elmField.getAttribute("Min")));
				fieldObj.setMinLength(new BigInteger(elmField.getAttribute("MinLength")));
				fieldObj.setName(elmField.getAttribute("Name"));
				fieldObj.setSegment(segmentObj);
				fieldObj.setTable(this.findTable(elmField.getAttribute("Table"), profile.getTableLibrary()));
				fieldObj.setUsage(Usage.fromValue(elmField.getAttribute("Usage")));
				fieldObj.setUuid(null);
				
				this.deserializeDTForField(profile, elmConformanceProfile, fieldObj, elmField.getAttribute("Datatype"), datatypes);
				
				segmentObj.getFields().add(fieldObj);
			}
		}
	}
	
	private void deserializeDTForField(Profile profile, Element elmConformanceProfile, Field fieldObj, String ref, Datatypes datatypes){
		Element datatypeElm = this.findDataTypeElm(elmConformanceProfile, ref);
		
		if(datatypeElm == null){
			fieldObj.setDatatype(null);
		}else {
			Datatype datatypeObj = new Datatype();
			datatypeObj.setDescription(datatypeElm.getAttribute("Description"));
			//FIXME NAME and DISPLAYNAME problems
			datatypeObj.setDisplayName(datatypeElm.getAttribute("Name"));
			datatypeObj.setName(datatypeElm.getAttribute("Name"));
			datatypeObj.setUuid(datatypeElm.getAttribute("ID"));
				
			
			NodeList nodes = datatypeElm.getChildNodes();
			for(int i=0; i < nodes.getLength(); i++){
				if(nodes.item(i).getNodeName().equals("Component")){
					Element elmComponent = (Element)nodes.item(i);
					Component componentObj = new Component();
					
					componentObj.setConfLength(elmComponent.getAttribute("ConfLength"));
					componentObj.setMaxLength(elmComponent.getAttribute("MaxLength"));
					componentObj.setMinLength(new BigInteger(elmComponent.getAttribute("MinLength")));
					componentObj.setName(elmComponent.getAttribute("Name"));
					componentObj.setTable(this.findTable(elmComponent.getAttribute("Table"), profile.getTableLibrary()));
					componentObj.setUsage(Usage.fromValue(elmComponent.getAttribute("Usage")));
					componentObj.setUuid(elmComponent.getAttribute("ID"));
					
					this.deserializeDTForComponent(elmConformanceProfile, componentObj, elmComponent.getAttribute("Datatype"), datatypes);
					
					datatypeObj.getComponents().add(componentObj);
				}
			}
			
			
			fieldObj.setDatatype(datatypeObj);
			datatypes.addDatatype(datatypeObj);
		}
	}
	
	private void deserializeDTForComponent(Profile profile, Element elmConformanceProfile, Component parentComponentObj, String ref, Datatypes datatypes){
		Element datatypeElm = this.findDataTypeElm(elmConformanceProfile, ref);
		
		if(datatypeElm == null){
			parentComponentObj.setDatatype(null);
		}else {
			Datatype datatypeObj = new Datatype();
			datatypeObj.setDescription(datatypeElm.getAttribute("Description"));
			//FIXME NAME and DISPLAYNAME problems
			datatypeObj.setDisplayName(datatypeElm.getAttribute("Name"));
			datatypeObj.setName(datatypeElm.getAttribute("Name"));
			datatypeObj.setUuid(datatypeElm.getAttribute("ID"));
			
			
			
			NodeList nodes = datatypeElm.getChildNodes();
			for(int i=0; i < nodes.getLength(); i++){
				if(nodes.item(i).getNodeName().equals("Component")){
					Element elmComponent = (Element)nodes.item(i);
					Component componentObj = new Component();
					
					componentObj.setConfLength(elmComponent.getAttribute("ConfLength"));
					componentObj.setMaxLength(elmComponent.getAttribute("MaxLength"));
					componentObj.setMinLength(new BigInteger(elmComponent.getAttribute("MinLength")));
					componentObj.setName(elmComponent.getAttribute("Name"));
					componentObj.setTable(this.findTable(elmComponent.getAttribute("Table"), profile.getTableLibrary()));
					componentObj.setUsage(Usage.fromValue(elmComponent.getAttribute("Usage")));
					componentObj.setUuid(elmComponent.getAttribute("ID"));
					
					this.deserializeDTForComponent(profile, elmConformanceProfile, componentObj, elmComponent.getAttribute("Datatype"), datatypes);
					
					datatypeObj.getComponents().add(componentObj);
				}
			}

			parentComponentObj.setDatatype(datatypeObj);
			datatypes.addDatatype(datatypeObj);
		}
	}
	
	private Element findDataTypeElm(Element elmConformanceProfile, String ref) {
		NodeList nodes = elmConformanceProfile.getElementsByTagName("Datatypes").item(0).getChildNodes();
		
		for(int i=0; i < nodes.getLength(); i++){
			if(nodes.item(i).getNodeName().equals("Datatype")){
				Element elm = (Element)nodes.item(i);
				if(elm.getAttribute("ID").equals(ref)){
					return elm;
				}
			}
		}
		
		return null;
	}

	private Element findSegmentElm(Element elmConformanceProfile, String ref){
		NodeList nodes = elmConformanceProfile.getElementsByTagName("Segments").item(0).getChildNodes();
		
		for(int i=0; i < nodes.getLength(); i++){
			if(nodes.item(i).getNodeName().equals("Segment")){
				Element elm = (Element)nodes.item(i);
				if(elm.getAttribute("ID").equals(ref)){
					return elm;
				}
			}
		}
		
		return null;
	}
	
	private void deserializeGroup(Element elmConformanceProfile, List<SegmentRefOrGroup> segmentRefOrGroups, Element groupElm, Segments segments, Datatypes datatypes){
		Group groupObj = new Group();
		groupObj.setMax(groupElm.getAttribute("Max"));
		groupObj.setMin(new BigInteger(groupElm.getAttribute("Min")));
		groupObj.setName(groupElm.getAttribute("Name"));
		groupObj.setUsage(Usage.fromValue(groupElm.getAttribute("Usage")));
		groupObj.setUuid(null);
		
		List<SegmentRefOrGroup> childSegmentRefOrGroups = new ArrayList<SegmentRefOrGroup>();
		
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
	
	public static void main(String[] args) throws IOException {
		ProfileSerializationImpl test = new ProfileSerializationImpl();
		Profile profile = test.deserializeXMLToProfile(new String(Files.readAllBytes(Paths.get("src//main//resources//vxu//Profile.xml"))),
				new String(Files.readAllBytes(Paths.get("src//main//resources//vxu//ValueSets.xml"))),
				new String(Files.readAllBytes(Paths.get("src//main//resources//vxu//PredicateConstraints.xml"))),
				new String(Files.readAllBytes(Paths.get("src//main//resources//vxu//ConformanceStatementConstraints.xml"))));
		System.out.println(test.serializeProfileToXML(profile));		
	}
}
