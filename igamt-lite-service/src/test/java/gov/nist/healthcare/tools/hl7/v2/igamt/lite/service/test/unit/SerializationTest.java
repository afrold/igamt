package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.unit;

import static org.junit.Assert.*;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatypes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileExportService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ConstraintsSerializationImpl;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerializationImpl;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.TableSerializationImpl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceContextUnit.class})
public class SerializationTest {
	Logger logger = LoggerFactory.getLogger( SerializationTest.class );

	@Autowired
	ProfileRepository profileRepository;

	@Autowired
	ProfileService profileService;

	@Autowired
	ProfileExportService profileExport;

	@SuppressWarnings("unused")
	private Datatype getDatatype(String key, Datatypes datatypes) {
		for (Datatype dt : datatypes.getChildren()) {
			if (dt.getLabel().equals(key)) {
				return dt;
			}
		}
		return null;
	}

	@Ignore
	@Test
	public void testSerialization() throws IOException {
		// ProfileSerializationImpl ProfileSerializationImpl = new
		// ProfileSerializationImpl();
		// TableSerializationImpl TableSerializationImpl = new
		// TableSerializationImpl();
		// ConstraintsSerializationImpl constraintsSerializationImpl = new
		// ConstraintsSerializationImpl();
		//
		String p = IOUtils.toString(this.getClass().getResourceAsStream(
				"/vxuTest/Profile.xml"));
		String v = IOUtils.toString(this.getClass().getResourceAsStream(
				"/vxuTest/ValueSets_all.xml"));
		String c = IOUtils.toString(this.getClass().getResourceAsStream(
				"/vxuTest/Constraints.xml"));
		Profile profile = new ProfileSerializationImpl()
		.deserializeXMLToProfile(p, v, c);

		Set<Datatype> datatypeSet = new HashSet<Datatype>();
		collectDatatype(profile, datatypeSet, profile.getDatatypes());
		assertEquals(profile.getDatatypes().getChildren().size(),
				datatypeSet.size());

		// assertEquals(4, profile.getPredicates().getSegments()
		// .getByNameOrByIDs().size());
		// assertEquals(15, profile.getConformanceStatements().getDatatypes()
		// .getByNameOrByIDs().size());
		// assertEquals(5, profile.getConformanceStatements().getSegments()
		// .getByNameOrByIDs().size());
		// assertEquals(1, profile.getMessages().getMessages().size());
	}

	private void collectDatatype(Profile p, Set<Datatype> set,
			Datatypes datatypes) {
		for (Segment s : p.getSegments().getChildren()) {
			for (Field f : s.getFields()) {
				Datatype d = datatypes.findOne(f.getDatatype());
				collectDatatype(d, set, datatypes);
			}
		}
	}

	private void collectDatatype(Datatype d, Set<Datatype> set,
			Datatypes datatypes) {
		if (!set.contains(d)) {
			set.add(d);
		}
		if (d.getComponents() != null) {
			for (Component c : d.getComponents()) {
				Datatype datatype = datatypes.findOne(c.getDatatype());
				collectDatatype(datatype, set, datatypes);
			}
		}
	}

	@Test
	public void testSerializationProfileToXML() throws IOException {
		// Sax exception catches different kind of errors but knowing that our file is present and well formed, we'll catch mishaps with the serializer

		int pIndex = ThreadLocalRandom.current().nextInt(0, (int)profileRepository.count());
		Profile p = profileRepository.findAll().get(pIndex);
		ProfileSerializationImpl profileSerializationImpl = new
				ProfileSerializationImpl();
		String pS = profileSerializationImpl.serializeProfileToXML(p);

		try {
			// parse an XML document into a DOM tree
			DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
			parserFactory.setNamespaceAware(true);
			DocumentBuilder parser = parserFactory.newDocumentBuilder();

			//		Document document = profileSerializationImpl.serializeProfileToDoc(p);
			//			Document document = (Document) parser.parse(this.getClass().getClassLoader().getResource(
			//					"profilesAdtTest/Profile.xml").openStream());

			Document document = (Document) parser.parse(IOUtils.toInputStream(pS));

			// create a SchemaFactory capable of understanding WXS schemas and load schema 
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Source schemaFile = new StreamSource(this.getClass().getClassLoader().getResource(
					"profilesSchema/Profile.xsd").openStream());

			Schema schema = factory.newSchema(schemaFile);
			Validator validator = schema.newValidator();

			// validate the DOM tree; if document is invalid, SAXException is raised
			validator.validate(new DOMSource((Node) document));
		} catch (SAXException e) {
			// Instance document is invalid!
			e.printStackTrace();
			fail("Instance document is invalid!");
		} catch (ParserConfigurationException e1) {
			logger.debug("Parser configuration error!");
			e1.printStackTrace();
		}
	}

	@Test
	public void testSerializationValueSetToXML() throws IOException {
		// Sax exception catches different kind of errors but knowing that our file is present and well formed, we'll catch mishaps with the serializer

		int pIndex = ThreadLocalRandom.current().nextInt(0, (int)profileRepository.count());
		Profile p = profileRepository.findAll().get(pIndex);

		TableSerializationImpl tableSerializationImpl = new
				TableSerializationImpl();
		String tS = tableSerializationImpl.serializeTableLibraryToXML(p.getTables());

		try {
			// parse an XML document into a DOM tree
			DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
			parserFactory.setNamespaceAware(true);
			DocumentBuilder parser = parserFactory.newDocumentBuilder();

			Document document = (Document) parser.parse(IOUtils.toInputStream(tS));

			// create a SchemaFactory capable of understanding WXS schemas and load schema 
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Source schemaFile = new StreamSource(this.getClass().getClassLoader().getResource(
					"profilesSchema/ValueSets.xsd").openStream());

			Schema schema = factory.newSchema(schemaFile);
			Validator validator = schema.newValidator();

			// validate the DOM tree; if document is invalid, SAXException is raised
			validator.validate(new DOMSource((Node) document));
		} catch (SAXException e) {
			// Instance document is invalid!
			e.printStackTrace();
			fail("Instance document is invalid!");
		} catch (ParserConfigurationException e1) {
			logger.debug("Parser configuration error!");
			e1.printStackTrace();
		}
	}


	@Test
	public void testSerializationConstraintsToXML() throws IOException {
		// Sax exception catches different kind of errors but knowing that our file is present and well formed, we'll catch mishaps with the serializer

		int pIndex = ThreadLocalRandom.current().nextInt(0, (int)profileRepository.count());
		Profile p = profileRepository.findAll().get(pIndex);

		ConstraintsSerializationImpl constraintsSerializationImpl = new
				ConstraintsSerializationImpl();
		String cS = constraintsSerializationImpl.serializeConstraintsToXML(p);

		try {
			// parse an XML document into a DOM tree
			DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
			parserFactory.setNamespaceAware(true);
			DocumentBuilder parser = parserFactory.newDocumentBuilder();

			Document document = (Document) parser.parse(IOUtils.toInputStream(cS));

			// create a SchemaFactory capable of understanding WXS schemas and load schema 
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Source schemaFile = new StreamSource(this.getClass().getClassLoader().getResource(
					"profilesSchema/ConformanceContext.xsd").openStream());

			Schema schema = factory.newSchema(schemaFile);
			Validator validator = schema.newValidator();

			// validate the DOM tree; if document is invalid, SAXException is raised
			validator.validate(new DOMSource((Node) document));
		} catch (SAXException e) {
			// Instance document is invalid!
			e.printStackTrace();
			fail("Instance document is invalid!");
		} catch (ParserConfigurationException e1) {
			logger.debug("Parser configuration error!");
			e1.printStackTrace();
		}
	}

	@Ignore("Not yet implemented")
	@Test
	public void testDeserializationXMLToProfile() throws IOException {
		//TODO
		assertTrue(false);
	}	
}
