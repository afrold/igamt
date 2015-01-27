package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceContext;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.MetaData;
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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ConstraintsXMLSerialization {

	public ConformanceContext deserializeXMLToConformanceContext(String xmlContents) {
		Document conformanceContextDoc = this.stringToDom(xmlContents);
		ConformanceContext conformanceContext = new ConformanceContext();
		
		
		Element elmConformanceContext = (Element)conformanceContextDoc.getElementsByTagName("ConformanceContext").item(0);
		conformanceContext.setUuid(elmConformanceContext.getAttribute("UUID"));
		
		this.deserializeXMLToMetaData((Element)elmConformanceContext.getElementsByTagName("MetaData").item(0), conformanceContext);
		
		return conformanceContext;
	}

	private void deserializeXMLToMetaData(Element elmMetaData, ConformanceContext conformanceContext) {
		MetaData metaDataObj = new MetaData();
		
		metaDataObj.setDescription(elmMetaData.getAttribute("Description"));
		metaDataObj.setAuthors(authors);
		metaDataObj.setStandard(standard);
		
		
	}

	public void serializeTableLibraryToXML(TableLibrary tableLibrary) {

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
