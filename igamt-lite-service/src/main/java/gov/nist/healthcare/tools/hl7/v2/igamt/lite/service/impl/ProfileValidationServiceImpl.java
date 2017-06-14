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

/**
 * 
 * @author Olivier MARIE-ROSE
 * 
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DocumentMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileValidationService;

@Service
public class ProfileValidationServiceImpl implements ProfileValidationService {
	Logger logger = LoggerFactory.getLogger(ProfileValidationServiceImpl.class);

	@Override
	public void validate(Profile p) throws Exception {
//
//		ProfileSerializationImpl profileSerializationImpl = new ProfileSerializationImpl();
//		String pS = profileSerializationImpl.serializeProfileToXML(p, new DocumentMetaData(), p.getDateUpdated());
//		String schemaPath = "validation/profilesSchema/Profile.xsd";
//		validate(pS, schemaPath);
//
//		TableSerializationImpl tableSerializationImpl = new TableSerializationImpl();
//		String tS = tableSerializationImpl.serializeTableLibraryToXML(p.getTableLibrary(), new DocumentMetaData(),
//				p.getDateUpdated());
//		schemaPath = "validation/profilesSchema/ValueSets.xsd";
//		validate(tS, schemaPath);
//
//		ConstraintsSerializationImpl constraintsSerializationImpl = new ConstraintsSerializationImpl();
//		String cS = constraintsSerializationImpl.serializeConstraintsToXML(p, new DocumentMetaData(),
//				p.getDateUpdated());
//		schemaPath = "validation/profilesSchema/ConformanceContext.xsd";
//		validate(cS, schemaPath);

	}

	public void validate(String xml, String schemaPath) throws Exception {

		try {
			DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
			parserFactory.setNamespaceAware(true);
			DocumentBuilder parser = parserFactory.newDocumentBuilder();

			Document document = parser.parse(IOUtils.toInputStream(xml));

			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Source schemaFile = new StreamSource(this.getClass().getClassLoader().getResource(schemaPath).openStream());

			Schema schema = factory.newSchema(schemaFile);
			Validator validator = schema.newValidator();

			// validate the DOM tree; if document is invalid, SAXException is
			// raised
			validator.validate(new DOMSource(document));
		} catch (SAXException e) {
			// Instance document is invalid!
			e.printStackTrace();
			logger.debug("Instance document is invalid.");
			throw e;
		} catch (ParserConfigurationException e1) {
			logger.debug("Parser configuration error!");
			e1.printStackTrace();
			throw e1;
		} catch (IOException e) {
			logger.debug("Error serializing profile");
			e.printStackTrace();
			throw e;
		}
	}

}
