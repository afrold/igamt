package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Author;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByID;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByName;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByNameOrByID;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceContext;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Context;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.MetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Reference;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Standard;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.assertion.AndAssertion;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.assertion.ExistAssertion;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.assertion.ForAllAssertion;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.assertion.FormatAssertion;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.assertion.ImplyAssertion;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.assertion.NotAssertion;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.assertion.NumberListAssertion;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.assertion.OrAssertion;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.assertion.PathValueAssertion;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.assertion.PlainTextAssertion;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.assertion.PresenceAssertion;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.assertion.SimpleValueAssertion;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.assertion.StringListAssertion;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.assertion.XorAssertion;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables.TableLibrary;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ConstraintsXMLSerialization {

	public ConformanceContext deserializeXMLToConformanceContext(String xmlContents) {
		Document conformanceContextDoc = this.stringToDom(xmlContents);
		ConformanceContext conformanceContext = new ConformanceContext();
		
		
		Element elmConformanceContext = (Element)conformanceContextDoc.getElementsByTagName("ConformanceContext").item(0);
		conformanceContext.setUuid(elmConformanceContext.getAttribute("UUID"));
		
		this.deserializeXMLToMetaData((Element)elmConformanceContext.getElementsByTagName("MetaData").item(0), conformanceContext);
		
		this.deserializeXMLToContextType(elmConformanceContext, conformanceContext);
		
		return conformanceContext;
	}
	
	public void serializeTableLibraryToXML(TableLibrary tableLibrary) {

	}
	
	private void deserializeXMLToContextType(Element elmConformanceContext, ConformanceContext conformanceContext) {
		Context datatypeContextObj = new Context();
		Context segmentContextObj = new Context();
		Context groupContextObj = new Context();
		
		this.deserializeXMLToContext((Element)elmConformanceContext.getElementsByTagName("Datatype").item(0), datatypeContextObj);
		this.deserializeXMLToContext((Element)elmConformanceContext.getElementsByTagName("Segment").item(0), datatypeContextObj);
		this.deserializeXMLToContext((Element)elmConformanceContext.getElementsByTagName("Group").item(0), datatypeContextObj);
		
		conformanceContext.setDatatypeContext(datatypeContextObj);
		conformanceContext.setSegmentContext(segmentContextObj);
		conformanceContext.setGroupContext(groupContextObj);
		
	}

	private void deserializeXMLToContext(Element elmContext, Context contextObj) {
		NodeList nodes = elmContext.getChildNodes();
		
		for(int i=0; i<nodes.getLength(); i++){
			if(nodes.item(i).getNodeName().equals("ByName")){
				ByName byNameObj = new ByName();
				Element elmByName = (Element)nodes.item(i);
				byNameObj.setByName(elmByName.getAttribute("Name"));
				deserializeXMLToConstraints(elmByName, byNameObj);
				contextObj.getByNameOrByIDs().add(byNameObj);
			}else if(nodes.item(i).getNodeName().equals("ByID")){
				ByID byIDObj = new ByID();
				Element elmByID = (Element)nodes.item(i);
				byIDObj.setByID(elmByID.getAttribute("ID"));
				deserializeXMLToConstraints(elmByID, byIDObj);
				contextObj.getByNameOrByIDs().add(byIDObj);
			}
			
		}	
	}

	private void deserializeXMLToConstraints(Element elmByNameOrByID, ByNameOrByID byNameOrByIDObj) {
		NodeList nodes = elmByNameOrByID.getElementsByTagName("Constraint");
		
		for(int i=0; i < nodes.getLength(); i++){
			Constraint constraintObj = new Constraint();
			Element elmConstraint = (Element)nodes.item(i);
			
			constraintObj.setConstraintId(elmConstraint.getAttribute("ID"));
			constraintObj.setConstraintTag(elmConstraint.getAttribute("Tag"));			
			NodeList descriptionNodes = elmConstraint.getElementsByTagName("Description");
			if(descriptionNodes != null && descriptionNodes.getLength() == 1) {
				constraintObj.setDescription(descriptionNodes.item(0).getTextContent());	
			}
			this.deserializeXMLToReference(elmConstraint, constraintObj);
			this.deserializeXMLToAssertion(elmConstraint, constraintObj);
			
			byNameOrByIDObj.getConstraints().add(constraintObj);
		}
	}
	
	private void deserializeXMLToAssertion(Element elmConstraint, Constraint constraintObj) {
		Element elmAssertion = (Element)elmConstraint.getElementsByTagName("Assertion").item(0);
		
		for(int i=0; i<elmAssertion.getChildNodes().getLength();i++){
			if(!elmAssertion.getChildNodes().item(i).getNodeName().contains("#")){
				Element elmChild = (Element)elmAssertion.getChildNodes().item(i);
				
				if(elmChild.getNodeName().equals("PlainText")){
					constraintObj.setAssertion(this.deserializeXMLToPlainTextAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("PathValue")){
					constraintObj.setAssertion(this.deserializeXMLToPathValueAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("Presence")){
					constraintObj.setAssertion(this.deserializeXMLToPresenceAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("SimpleValue")){
					constraintObj.setAssertion(this.deserializeXMLToSimpleValueAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("StringList")){
					constraintObj.setAssertion(this.deserializeXMLToStringListAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("Format")){
					constraintObj.setAssertion(this.deserializeXMLToFormatAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("NumberList")){
					constraintObj.setAssertion(this.deserializeXMLToNumberListAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("AND")){
					constraintObj.setAssertion(this.deserializeXMLToANDAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("EXIST")){
					constraintObj.setAssertion(this.deserializeXMLToEXISTAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("FORALL")){
					constraintObj.setAssertion(this.deserializeXMLToFORALLAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("IMPLY")){
					constraintObj.setAssertion(this.deserializeXMLToIMPLYAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("NOT")){
					constraintObj.setAssertion(this.deserializeXMLToNOTAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("OR")){
					constraintObj.setAssertion(this.deserializeXMLToORAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("XOR")){
					constraintObj.setAssertion(this.deserializeXMLToXORAssertion(elmChild));
				}
			}
		}
		
		
	}

	private ForAllAssertion deserializeXMLToFORALLAssertion(Element elmAssertion) {
		ForAllAssertion forAllAssertion = new ForAllAssertion();
		
		for(int i=0; i<elmAssertion.getChildNodes().getLength();i++){
			if(!elmAssertion.getChildNodes().item(i).getNodeName().contains("#")){
				Element elmChild = (Element)elmAssertion.getChildNodes().item(i);
				
				if(elmChild.getNodeName().equals("PlainText")){
					forAllAssertion.getChildAssertions().add(this.deserializeXMLToPlainTextAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("PathValue")){
					forAllAssertion.getChildAssertions().add(this.deserializeXMLToPathValueAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("Presence")){
					forAllAssertion.getChildAssertions().add(this.deserializeXMLToPresenceAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("SimpleValue")){
					forAllAssertion.getChildAssertions().add(this.deserializeXMLToSimpleValueAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("StringList")){
					forAllAssertion.getChildAssertions().add(this.deserializeXMLToStringListAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("Format")){
					forAllAssertion.getChildAssertions().add(this.deserializeXMLToFormatAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("NumberList")){
					forAllAssertion.getChildAssertions().add(this.deserializeXMLToNumberListAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("AND")){
					forAllAssertion.getChildAssertions().add(this.deserializeXMLToANDAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("EXIST")){
					forAllAssertion.getChildAssertions().add(this.deserializeXMLToEXISTAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("FORALL")){
					forAllAssertion.getChildAssertions().add(this.deserializeXMLToFORALLAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("IMPLY")){
					forAllAssertion.getChildAssertions().add(this.deserializeXMLToIMPLYAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("NOT")){
					forAllAssertion.getChildAssertions().add(this.deserializeXMLToNOTAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("OR")){
					forAllAssertion.getChildAssertions().add(this.deserializeXMLToORAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("XOR")){
					forAllAssertion.getChildAssertions().add(this.deserializeXMLToXORAssertion(elmChild));
				}
			}			
		}
		
		return forAllAssertion;

	}

	private ExistAssertion deserializeXMLToEXISTAssertion(Element elmAssertion) {
		ExistAssertion existAssertion = new ExistAssertion();
		
		for(int i=0; i<elmAssertion.getChildNodes().getLength();i++){
			if(!elmAssertion.getChildNodes().item(i).getNodeName().contains("#")){
				Element elmChild = (Element)elmAssertion.getChildNodes().item(i);
				
				if(elmChild.getNodeName().equals("PlainText")){
					existAssertion.getChildAssertions().add(this.deserializeXMLToPlainTextAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("PathValue")){
					existAssertion.getChildAssertions().add(this.deserializeXMLToPathValueAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("Presence")){
					existAssertion.getChildAssertions().add(this.deserializeXMLToPresenceAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("SimpleValue")){
					existAssertion.getChildAssertions().add(this.deserializeXMLToSimpleValueAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("StringList")){
					existAssertion.getChildAssertions().add(this.deserializeXMLToStringListAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("Format")){
					existAssertion.getChildAssertions().add(this.deserializeXMLToFormatAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("NumberList")){
					existAssertion.getChildAssertions().add(this.deserializeXMLToNumberListAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("AND")){
					existAssertion.getChildAssertions().add(this.deserializeXMLToANDAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("EXIST")){
					existAssertion.getChildAssertions().add(this.deserializeXMLToEXISTAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("FORALL")){
					existAssertion.getChildAssertions().add(this.deserializeXMLToFORALLAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("IMPLY")){
					existAssertion.getChildAssertions().add(this.deserializeXMLToIMPLYAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("NOT")){
					existAssertion.getChildAssertions().add(this.deserializeXMLToNOTAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("OR")){
					existAssertion.getChildAssertions().add(this.deserializeXMLToORAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("XOR")){
					existAssertion.getChildAssertions().add(this.deserializeXMLToXORAssertion(elmChild));
				}
			}			
		}
		
		return existAssertion;
	}

	private NotAssertion deserializeXMLToNOTAssertion(Element elmAssertion) {
		NotAssertion notAssertion = new NotAssertion();
		
		for(int i=0; i<elmAssertion.getChildNodes().getLength();i++){
			if(!elmAssertion.getChildNodes().item(i).getNodeName().contains("#")){
				Element elmChild = (Element)elmAssertion.getChildNodes().item(i);
				
				if(elmChild.getNodeName().equals("PlainText")){
					notAssertion.setChildAssertion(this.deserializeXMLToPlainTextAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("PathValue")){
					notAssertion.setChildAssertion(this.deserializeXMLToPathValueAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("Presence")){
					notAssertion.setChildAssertion(this.deserializeXMLToPresenceAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("SimpleValue")){
					notAssertion.setChildAssertion(this.deserializeXMLToSimpleValueAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("StringList")){
					notAssertion.setChildAssertion(this.deserializeXMLToStringListAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("Format")){
					notAssertion.setChildAssertion(this.deserializeXMLToFormatAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("NumberList")){
					notAssertion.setChildAssertion(this.deserializeXMLToNumberListAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("AND")){
					notAssertion.setChildAssertion(this.deserializeXMLToANDAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("EXIST")){
					notAssertion.setChildAssertion(this.deserializeXMLToEXISTAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("FORALL")){
					notAssertion.setChildAssertion(this.deserializeXMLToFORALLAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("IMPLY")){
					notAssertion.setChildAssertion(this.deserializeXMLToIMPLYAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("NOT")){
					notAssertion.setChildAssertion(this.deserializeXMLToNOTAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("OR")){
					notAssertion.setChildAssertion(this.deserializeXMLToORAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("XOR")){
					notAssertion.setChildAssertion(this.deserializeXMLToXORAssertion(elmChild));
				}
			}			
		}
		return notAssertion;
	}

	private ImplyAssertion deserializeXMLToIMPLYAssertion(Element elmAssertion) {
		ImplyAssertion implyAssertion = new ImplyAssertion();
		int index = 0;
		
		for(int i=0; i<elmAssertion.getChildNodes().getLength();i++){
			if(!elmAssertion.getChildNodes().item(i).getNodeName().contains("#")){
				Element elmChild = (Element)elmAssertion.getChildNodes().item(i);
				if(elmChild.getNodeName().equals("PlainText")){
					if(index == 0) implyAssertion.setFirstChildAssertion(this.deserializeXMLToPlainTextAssertion(elmChild));
					else if(index == 1) implyAssertion.setSecondChildAssertion(this.deserializeXMLToPlainTextAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("PathValue")){
					if(index == 0) implyAssertion.setFirstChildAssertion(this.deserializeXMLToPathValueAssertion(elmChild));
					else if(index == 1) implyAssertion.setSecondChildAssertion(this.deserializeXMLToPathValueAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("Presence")){
					if(index == 0) implyAssertion.setFirstChildAssertion(this.deserializeXMLToPresenceAssertion(elmChild));
					else if(index == 1) implyAssertion.setSecondChildAssertion(this.deserializeXMLToPresenceAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("SimpleValue")){
					if(index == 0) implyAssertion.setFirstChildAssertion(this.deserializeXMLToSimpleValueAssertion(elmChild));
					else if(index == 1) implyAssertion.setSecondChildAssertion(this.deserializeXMLToSimpleValueAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("StringList")){
					if(index == 0) implyAssertion.setFirstChildAssertion(this.deserializeXMLToStringListAssertion(elmChild));
					else if(index == 1) implyAssertion.setSecondChildAssertion(this.deserializeXMLToStringListAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("Format")){
					if(index == 0) implyAssertion.setFirstChildAssertion(this.deserializeXMLToFormatAssertion(elmChild));
					else if(index == 1) implyAssertion.setSecondChildAssertion(this.deserializeXMLToFormatAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("NumberList")){
					if(index == 0) implyAssertion.setFirstChildAssertion(this.deserializeXMLToNumberListAssertion(elmChild));
					else if(index == 1) implyAssertion.setSecondChildAssertion(this.deserializeXMLToNumberListAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("AND")){
					if(index == 0) implyAssertion.setFirstChildAssertion(this.deserializeXMLToANDAssertion(elmChild));
					else if(index == 1) implyAssertion.setSecondChildAssertion(this.deserializeXMLToANDAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("EXIST")){
					if(index == 0) implyAssertion.setFirstChildAssertion(this.deserializeXMLToEXISTAssertion(elmChild));
					else if(index == 1) implyAssertion.setSecondChildAssertion(this.deserializeXMLToEXISTAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("FORALL")){
					if(index == 0) implyAssertion.setFirstChildAssertion(this.deserializeXMLToFORALLAssertion(elmChild));
					else if(index == 1) implyAssertion.setSecondChildAssertion(this.deserializeXMLToFORALLAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("IMPLY")){
					if(index == 0) implyAssertion.setFirstChildAssertion(this.deserializeXMLToIMPLYAssertion(elmChild));
					else if(index == 1) implyAssertion.setSecondChildAssertion(this.deserializeXMLToIMPLYAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("NOT")){
					if(index == 0) implyAssertion.setFirstChildAssertion(this.deserializeXMLToNOTAssertion(elmChild));
					else if(index == 1) implyAssertion.setSecondChildAssertion(this.deserializeXMLToNOTAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("OR")){
					if(index == 0) implyAssertion.setFirstChildAssertion(this.deserializeXMLToORAssertion(elmChild));
					else if(index == 1) implyAssertion.setSecondChildAssertion(this.deserializeXMLToORAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("XOR")){
					if(index == 0) implyAssertion.setFirstChildAssertion(this.deserializeXMLToXORAssertion(elmChild));
					else if(index == 1) implyAssertion.setSecondChildAssertion(this.deserializeXMLToXORAssertion(elmChild));
				}
				
				index = 1;
			}
			
			
		}
		return implyAssertion;
	}

	private XorAssertion deserializeXMLToXORAssertion(Element elmAssertion) {
		XorAssertion xorAssertion = new XorAssertion();
		int index = 0;
		
		for(int i=0; i<elmAssertion.getChildNodes().getLength();i++){
			if(!elmAssertion.getChildNodes().item(i).getNodeName().contains("#")){
				Element elmChild = (Element)elmAssertion.getChildNodes().item(i);
				if(elmChild.getNodeName().equals("PlainText")){
					if(index == 0) xorAssertion.setFirstChildAssertion(this.deserializeXMLToPlainTextAssertion(elmChild));
					else if(index == 1) xorAssertion.setSecondChildAssertion(this.deserializeXMLToPlainTextAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("PathValue")){
					if(index == 0) xorAssertion.setFirstChildAssertion(this.deserializeXMLToPathValueAssertion(elmChild));
					else if(index == 1) xorAssertion.setSecondChildAssertion(this.deserializeXMLToPathValueAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("Presence")){
					if(index == 0) xorAssertion.setFirstChildAssertion(this.deserializeXMLToPresenceAssertion(elmChild));
					else if(index == 1) xorAssertion.setSecondChildAssertion(this.deserializeXMLToPresenceAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("SimpleValue")){
					if(index == 0) xorAssertion.setFirstChildAssertion(this.deserializeXMLToSimpleValueAssertion(elmChild));
					else if(index == 1) xorAssertion.setSecondChildAssertion(this.deserializeXMLToSimpleValueAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("StringList")){
					if(index == 0) xorAssertion.setFirstChildAssertion(this.deserializeXMLToStringListAssertion(elmChild));
					else if(index == 1) xorAssertion.setSecondChildAssertion(this.deserializeXMLToStringListAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("Format")){
					if(index == 0) xorAssertion.setFirstChildAssertion(this.deserializeXMLToFormatAssertion(elmChild));
					else if(index == 1) xorAssertion.setSecondChildAssertion(this.deserializeXMLToFormatAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("NumberList")){
					if(index == 0) xorAssertion.setFirstChildAssertion(this.deserializeXMLToNumberListAssertion(elmChild));
					else if(index == 1) xorAssertion.setSecondChildAssertion(this.deserializeXMLToNumberListAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("AND")){
					if(index == 0) xorAssertion.setFirstChildAssertion(this.deserializeXMLToANDAssertion(elmChild));
					else if(index == 1) xorAssertion.setSecondChildAssertion(this.deserializeXMLToANDAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("EXIST")){
					if(index == 0) xorAssertion.setFirstChildAssertion(this.deserializeXMLToEXISTAssertion(elmChild));
					else if(index == 1) xorAssertion.setSecondChildAssertion(this.deserializeXMLToEXISTAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("FORALL")){
					if(index == 0) xorAssertion.setFirstChildAssertion(this.deserializeXMLToFORALLAssertion(elmChild));
					else if(index == 1) xorAssertion.setSecondChildAssertion(this.deserializeXMLToFORALLAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("IMPLY")){
					if(index == 0) xorAssertion.setFirstChildAssertion(this.deserializeXMLToIMPLYAssertion(elmChild));
					else if(index == 1) xorAssertion.setSecondChildAssertion(this.deserializeXMLToIMPLYAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("NOT")){
					if(index == 0) xorAssertion.setFirstChildAssertion(this.deserializeXMLToNOTAssertion(elmChild));
					else if(index == 1) xorAssertion.setSecondChildAssertion(this.deserializeXMLToNOTAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("OR")){
					if(index == 0) xorAssertion.setFirstChildAssertion(this.deserializeXMLToORAssertion(elmChild));
					else if(index == 1) xorAssertion.setSecondChildAssertion(this.deserializeXMLToORAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("XOR")){
					if(index == 0) xorAssertion.setFirstChildAssertion(this.deserializeXMLToXORAssertion(elmChild));
					else if(index == 1) xorAssertion.setSecondChildAssertion(this.deserializeXMLToXORAssertion(elmChild));
				}
				
				index = 1;
				
			}
			
		}
		return xorAssertion;
	}

	private OrAssertion deserializeXMLToORAssertion(Element elmAssertion) {
		OrAssertion orAssertion = new OrAssertion();
		int index = 0;
		
		for(int i=0; i<elmAssertion.getChildNodes().getLength();i++){
			if(!elmAssertion.getChildNodes().item(i).getNodeName().contains("#")){
				Element elmChild = (Element)elmAssertion.getChildNodes().item(i);
			
				if(elmChild.getNodeName().equals("PlainText")){
					if(index == 0) orAssertion.setFirstChildAssertion(this.deserializeXMLToPlainTextAssertion(elmChild));
					else if(index == 1) orAssertion.setSecondChildAssertion(this.deserializeXMLToPlainTextAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("PathValue")){
					if(index == 0) orAssertion.setFirstChildAssertion(this.deserializeXMLToPathValueAssertion(elmChild));
					else if(index == 1) orAssertion.setSecondChildAssertion(this.deserializeXMLToPathValueAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("Presence")){
					if(index == 0) orAssertion.setFirstChildAssertion(this.deserializeXMLToPresenceAssertion(elmChild));
					else if(index == 1) orAssertion.setSecondChildAssertion(this.deserializeXMLToPresenceAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("SimpleValue")){
					if(index == 0) orAssertion.setFirstChildAssertion(this.deserializeXMLToSimpleValueAssertion(elmChild));
					else if(index == 1) orAssertion.setSecondChildAssertion(this.deserializeXMLToSimpleValueAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("StringList")){
					if(index == 0) orAssertion.setFirstChildAssertion(this.deserializeXMLToStringListAssertion(elmChild));
					else if(index == 1) orAssertion.setSecondChildAssertion(this.deserializeXMLToStringListAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("Format")){
					if(index == 0) orAssertion.setFirstChildAssertion(this.deserializeXMLToFormatAssertion(elmChild));
					else if(index == 1) orAssertion.setSecondChildAssertion(this.deserializeXMLToFormatAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("NumberList")){
					if(index == 0) orAssertion.setFirstChildAssertion(this.deserializeXMLToNumberListAssertion(elmChild));
					else if(index == 1) orAssertion.setSecondChildAssertion(this.deserializeXMLToNumberListAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("AND")){
					if(index == 0) orAssertion.setFirstChildAssertion(this.deserializeXMLToANDAssertion(elmChild));
					else if(index == 1) orAssertion.setSecondChildAssertion(this.deserializeXMLToANDAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("EXIST")){
					if(index == 0) orAssertion.setFirstChildAssertion(this.deserializeXMLToEXISTAssertion(elmChild));
					else if(index == 1) orAssertion.setSecondChildAssertion(this.deserializeXMLToEXISTAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("FORALL")){
					if(index == 0) orAssertion.setFirstChildAssertion(this.deserializeXMLToFORALLAssertion(elmChild));
					else if(index == 1) orAssertion.setSecondChildAssertion(this.deserializeXMLToFORALLAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("IMPLY")){
					if(index == 0) orAssertion.setFirstChildAssertion(this.deserializeXMLToIMPLYAssertion(elmChild));
					else if(index == 1) orAssertion.setSecondChildAssertion(this.deserializeXMLToIMPLYAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("NOT")){
					if(index == 0) orAssertion.setFirstChildAssertion(this.deserializeXMLToNOTAssertion(elmChild));
					else if(index == 1) orAssertion.setSecondChildAssertion(this.deserializeXMLToNOTAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("OR")){
					if(index == 0) orAssertion.setFirstChildAssertion(this.deserializeXMLToORAssertion(elmChild));
					else if(index == 1) orAssertion.setSecondChildAssertion(this.deserializeXMLToORAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("XOR")){
					if(index == 0) orAssertion.setFirstChildAssertion(this.deserializeXMLToXORAssertion(elmChild));
					else if(index == 1) orAssertion.setSecondChildAssertion(this.deserializeXMLToXORAssertion(elmChild));
				}
				
				index = 1;
			}
		}
		return orAssertion;
	}

	private AndAssertion deserializeXMLToANDAssertion(Element elmAssertion) {
		AndAssertion andAssertion = new AndAssertion();
		int index = 0;
		
		for(int i=0; i<elmAssertion.getChildNodes().getLength();i++){
			if(!elmAssertion.getChildNodes().item(i).getNodeName().contains("#")){
				Element elmChild = (Element)elmAssertion.getChildNodes().item(i);
			
				if(elmChild.getNodeName().equals("PlainText")){
					if(index == 0) andAssertion.setFirstChildAssertion(this.deserializeXMLToPlainTextAssertion(elmChild));
					else if(index == 1) andAssertion.setSecondChildAssertion(this.deserializeXMLToPlainTextAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("PathValue")){
					if(index == 0) andAssertion.setFirstChildAssertion(this.deserializeXMLToPathValueAssertion(elmChild));
					else if(index == 1) andAssertion.setSecondChildAssertion(this.deserializeXMLToPathValueAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("Presence")){
					if(index == 0) andAssertion.setFirstChildAssertion(this.deserializeXMLToPresenceAssertion(elmChild));
					else if(index == 1) andAssertion.setSecondChildAssertion(this.deserializeXMLToPresenceAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("SimpleValue")){
					if(index == 0) andAssertion.setFirstChildAssertion(this.deserializeXMLToSimpleValueAssertion(elmChild));
					else if(index == 1) andAssertion.setSecondChildAssertion(this.deserializeXMLToSimpleValueAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("StringList")){
					if(index == 0) andAssertion.setFirstChildAssertion(this.deserializeXMLToStringListAssertion(elmChild));
					else if(index == 1) andAssertion.setSecondChildAssertion(this.deserializeXMLToStringListAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("Format")){
					if(index == 0) andAssertion.setFirstChildAssertion(this.deserializeXMLToFormatAssertion(elmChild));
					else if(index == 1) andAssertion.setSecondChildAssertion(this.deserializeXMLToFormatAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("NumberList")){
					if(index == 0) andAssertion.setFirstChildAssertion(this.deserializeXMLToNumberListAssertion(elmChild));
					else if(index == 1) andAssertion.setSecondChildAssertion(this.deserializeXMLToNumberListAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("AND")){
					if(index == 0) andAssertion.setFirstChildAssertion(this.deserializeXMLToANDAssertion(elmChild));
					else if(index == 1) andAssertion.setSecondChildAssertion(this.deserializeXMLToANDAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("EXIST")){
					if(index == 0) andAssertion.setFirstChildAssertion(this.deserializeXMLToEXISTAssertion(elmChild));
					else if(index == 1) andAssertion.setSecondChildAssertion(this.deserializeXMLToEXISTAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("FORALL")){
					if(index == 0) andAssertion.setFirstChildAssertion(this.deserializeXMLToFORALLAssertion(elmChild));
					else if(index == 1) andAssertion.setSecondChildAssertion(this.deserializeXMLToFORALLAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("IMPLY")){
					if(index == 0) andAssertion.setFirstChildAssertion(this.deserializeXMLToIMPLYAssertion(elmChild));
					else if(index == 1) andAssertion.setSecondChildAssertion(this.deserializeXMLToIMPLYAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("NOT")){
					if(index == 0) andAssertion.setFirstChildAssertion(this.deserializeXMLToNOTAssertion(elmChild));
					else if(index == 1) andAssertion.setSecondChildAssertion(this.deserializeXMLToNOTAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("OR")){
					if(index == 0) andAssertion.setFirstChildAssertion(this.deserializeXMLToORAssertion(elmChild));
					else if(index == 1) andAssertion.setSecondChildAssertion(this.deserializeXMLToORAssertion(elmChild));
				}else if(elmChild.getNodeName().equals("XOR")){
					if(index == 0) andAssertion.setFirstChildAssertion(this.deserializeXMLToXORAssertion(elmChild));
					else if(index == 1) andAssertion.setSecondChildAssertion(this.deserializeXMLToXORAssertion(elmChild));
				}
				
				index = 1;
				
			}
		}
		return andAssertion;
	}

	private FormatAssertion deserializeXMLToFormatAssertion(Element elmChild) {
		FormatAssertion formatAssertion = new FormatAssertion();
		formatAssertion.setPath(elmChild.getAttribute("Path"));
		formatAssertion.setRegex(elmChild.getAttribute("Regex"));
		return formatAssertion;
	}

	private NumberListAssertion deserializeXMLToNumberListAssertion(Element elmChild) {
		NumberListAssertion numberListAssertion = new NumberListAssertion();
		numberListAssertion.setCsv(elmChild.getAttribute("CSV"));
		numberListAssertion.setPath(elmChild.getAttribute("Path"));
		return numberListAssertion;
	}

	private StringListAssertion deserializeXMLToStringListAssertion(Element elmChild) {
		StringListAssertion stringListAssertion = new StringListAssertion();
		stringListAssertion.setCsv(elmChild.getAttribute("CSV"));
		stringListAssertion.setPath(elmChild.getAttribute("Path"));
		return stringListAssertion;
	}

	private SimpleValueAssertion deserializeXMLToSimpleValueAssertion(Element elmChild) {
		SimpleValueAssertion simpleValueAssertion = new SimpleValueAssertion();
		simpleValueAssertion.setOperator(elmChild.getAttribute("Operator"));
		simpleValueAssertion.setPath(elmChild.getAttribute("Path"));
		simpleValueAssertion.setType(elmChild.getAttribute("Type"));
		simpleValueAssertion.setValue(elmChild.getAttribute("Value"));
		return simpleValueAssertion;
	}

	private PresenceAssertion deserializeXMLToPresenceAssertion(Element elmChild) {
		PresenceAssertion presenceAssertion = new PresenceAssertion();
		presenceAssertion.setPath(elmChild.getAttribute("Path"));
		return presenceAssertion;
	}

	private PathValueAssertion deserializeXMLToPathValueAssertion(Element elmChild) {
		PathValueAssertion pathValueAssertion = new PathValueAssertion();
		pathValueAssertion.setOperator(elmChild.getAttribute("Operator"));
		pathValueAssertion.setPath1(elmChild.getAttribute("Path1"));
		pathValueAssertion.setPath2(elmChild.getAttribute("Path2"));
		return pathValueAssertion;
	}

	private PlainTextAssertion deserializeXMLToPlainTextAssertion(Element elmChild) {
		PlainTextAssertion plainTextAssertion = new PlainTextAssertion();
		plainTextAssertion.setIgnoreCase(Boolean.parseBoolean(elmChild.getAttribute("IgnoreCase")));
		plainTextAssertion.setPath(elmChild.getAttribute("Path"));
		plainTextAssertion.setText(elmChild.getAttribute("Text"));
		return plainTextAssertion;
	}

	private void deserializeXMLToReference(Element elmConstraint, Constraint constraintObj) {
		NodeList nodes = elmConstraint.getElementsByTagName("Reference");
		if(nodes != null && nodes.getLength() == 1) {
			Reference referenceObj = new Reference();
			Element elmReference = (Element)nodes.item(0);
			
			referenceObj.setChapter(elmReference.getAttribute("Chapter"));
			referenceObj.setPage(Integer.parseInt(elmReference.getAttribute("Page")));
			referenceObj.setSection(elmReference.getAttribute("Section"));
			referenceObj.setUrl(elmReference.getAttribute("URL"));
			
			constraintObj.setReference(referenceObj);
		}
		
	}

	private void deserializeXMLToMetaData(Element elmMetaData, ConformanceContext conformanceContext) {
		MetaData metaDataObj = new MetaData();
		
		metaDataObj.setDescription(elmMetaData.getAttribute("Description"));

		NodeList descriptionNodes = elmMetaData.getElementsByTagName("Description");
		if(descriptionNodes != null) {
			metaDataObj.setDescription(descriptionNodes.item(0).getTextContent());
		}
		
		this.deserializeXMLToAuthors(elmMetaData.getElementsByTagName("Author"), metaDataObj);
		this.deserializeXMLToStandard(elmMetaData.getElementsByTagName("Standard"), metaDataObj);
		
		conformanceContext.setMetaData(metaDataObj);
	}

	private void deserializeXMLToStandard(NodeList standard, MetaData metaDataObj) {
		if(standard != null && standard.getLength() == 1){
			Standard standardObj = new Standard();
			Element elmStandard = (Element)standard.item(0);
			
			standardObj.setStandardDate(elmStandard.getAttribute("Date"));
			standardObj.setStandardId(elmStandard.getAttribute("ID"));
			standardObj.setStandardURL(elmStandard.getAttribute("URL"));
			standardObj.setStandardVersion(elmStandard.getAttribute("Version"));
			
			NodeList descriptionNodes = elmStandard.getElementsByTagName("Description");
			if(descriptionNodes != null && descriptionNodes.getLength() == 1) {
				standardObj.setStandardDescription(descriptionNodes.item(0).getTextContent());	
			}		
			metaDataObj.setStandard(standardObj);
		}
		
	}

	private void deserializeXMLToAuthors(NodeList authors, MetaData metaDataObj) {
		for(int i=0; i<authors.getLength(); i++){
			Element elmAuthor = (Element)authors.item(i);
			Author author = new Author();
			
			author.setEmail(elmAuthor.getAttribute("Email"));
			author.setFirstName(elmAuthor.getAttribute("FirstName"));
			author.setLastName(elmAuthor.getAttribute("LastName"));
			
			metaDataObj.getAuthors().add(author);
		}
		
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
		ConstraintsXMLSerialization test = new ConstraintsXMLSerialization();
		ConformanceContext conformanceContext = test.deserializeXMLToConformanceContext(new String(Files.readAllBytes(Paths.get("src//main//resources//ConfContextSample.xml"))));
		
		System.out.println(conformanceContext.toString());
		
		System.out.println("----------------------------------------------------------------------------------------------------------");		
	}
}
