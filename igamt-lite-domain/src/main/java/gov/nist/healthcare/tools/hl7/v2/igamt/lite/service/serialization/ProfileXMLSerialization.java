package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatypes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Encodings;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.MetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segments;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ProfileXMLSerialization {

	public Profile deserializeXMLToProfile(String xmlContents) {
		Document profileDoc = this.stringToDom(xmlContents);
		Profile profile = new Profile();
		
		Element elmConformanceProfile = (Element)profileDoc.getElementsByTagName("ConformanceProfile").item(0);
		profile.setId(elmConformanceProfile.getAttribute("ID"));
		profile.setType(elmConformanceProfile.getAttribute("Type"));
		profile.setHl7Version(elmConformanceProfile.getAttribute("HL7Version"));
		profile.setSchemaVersion(elmConformanceProfile.getAttribute("SchemaVersion"));
		profile.setSegments(new Segments());
		profile.setDatatypes(new Datatypes());
		
		this.deserializeMetaData(profile, elmConformanceProfile);
		this.deserializeEncodings(profile, elmConformanceProfile);
		
		
		
		this.deserializeMessages(profile, elmConformanceProfile);
		
		
		System.out.println(profile.getSegments().getSegments().size());
		System.out.println(profile.getDatatypes().getDatatypes().size());
		
		
		return profile;
	}

	public void serializeProfileToXML(Profile profile) {

	}
	
	private void deserializeMetaData(Profile profile, Element elmConformanceProfile){
		NodeList nodes = elmConformanceProfile.getElementsByTagName("MetaData");
		if(nodes != null && nodes.getLength() != 0){
			MetaData metaData = new MetaData();
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
	
	private void deserializeField(Element elmConformanceProfile, Element segmentElm, Segment segmentObj, Datatypes datatypes){
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
				fieldObj.setTable(elmField.getAttribute("Table"));
				fieldObj.setUsage(Usage.fromValue(elmField.getAttribute("Usage")));
				fieldObj.setUuid(null);
				
				this.deserializeDatatypeForField(elmConformanceProfile, fieldObj, elmField.getAttribute("Datatype"), datatypes);
				
				segmentObj.getFields().add(fieldObj);
			}
		}
	}
	
	private void deserializeDatatypeForField(Element elmConformanceProfile, Field fieldObj, String ref, Datatypes datatypes){
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
			
			this.deserializeDComponents(elmConformanceProfile, datatypeElm, datatypeObj, datatypes);
			
			fieldObj.setDatatype(datatypeObj);
			datatypes.addDatatype(datatypeObj);
		}
	}
	
	private void deserializeDatatypeForComponent(Element elmConformanceProfile, Component componentObj, String ref, Datatypes datatypes){
		Element datatypeElm = this.findDataTypeElm(elmConformanceProfile, ref);
		
		if(datatypeElm == null){
			componentObj.setDatatype(null);
		}else {
			Datatype datatypeObj = new Datatype();
			datatypeObj.setDescription(datatypeElm.getAttribute("Description"));
			//FIXME NAME and DISPLAYNAME problems
			datatypeObj.setDisplayName(datatypeElm.getAttribute("Name"));
			datatypeObj.setName(datatypeElm.getAttribute("Name"));
			datatypeObj.setUuid(datatypeElm.getAttribute("ID"));

			componentObj.setDatatype(datatypeObj);
			datatypes.addDatatype(datatypeObj);
		}
	}
	
	private void deserializeDComponents(Element elmConformanceProfile, Element datatypeElm, Datatype datatypeObj, Datatypes datatypes){
		NodeList nodes = datatypeElm.getChildNodes();
		
		for(int i=0; i < nodes.getLength(); i++){
			if(nodes.item(i).getNodeName().equals("Component")){
				Element elmComponent = (Element)nodes.item(i);
				Component componentObj = new Component();
				
				componentObj.setConfLength(elmComponent.getAttribute("ConfLength"));
				componentObj.setMaxLength(elmComponent.getAttribute("MaxLength"));
				componentObj.setMinLength(new BigInteger(elmComponent.getAttribute("MinLength")));
				componentObj.setName(elmComponent.getAttribute("Name"));
				componentObj.setTable(elmComponent.getAttribute("Table"));
				componentObj.setUsage(Usage.fromValue(elmComponent.getAttribute("Usage")));
				componentObj.setUuid(null);
				
				this.deserializeDatatypeForComponent(elmConformanceProfile, componentObj, elmComponent.getAttribute("Datatype"), datatypes);
				
				datatypeObj.getComponents().add(componentObj);
			}
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
	
	public static void main(String[] args) throws IOException {
		ProfileXMLSerialization test = new ProfileXMLSerialization();
		Profile profile = test.deserializeXMLToProfile(new String(Files.readAllBytes(Paths.get("C://Users//jungyubw//Desktop//VXU new Profile//Profile.xml"))));
//		System.out.println(profile.toString());
		
		System.out.println("----------------------------------------------------------------------------------------------------------");
		
		profile = test.deserializeXMLToProfile(new String(Files.readAllBytes(Paths.get("C://Users//jungyubw//Desktop//VXU new Profile//Profile (2).xml"))));
//		System.out.println(profile.toString());
		
		System.out.println("----------------------------------------------------------------------------------------------------------");
		
		profile = test.deserializeXMLToProfile(new String(Files.readAllBytes(Paths.get("C://Users//jungyubw//Desktop//VXU new Profile//Profile (3).xml"))));
//		System.out.println(profile.toString());
		
	}
}
