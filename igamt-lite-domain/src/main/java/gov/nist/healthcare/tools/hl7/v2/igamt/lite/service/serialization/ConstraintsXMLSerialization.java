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
//			constraintObj.setAssertion(assertion);
			
			byNameOrByIDObj.getConstraints().add(constraintObj);
		}
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
