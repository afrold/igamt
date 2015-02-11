package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import static org.junit.Assert.assertEquals;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.xml.ConstraintsSerializationImpl;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.xml.ProfileSerializationImpl;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.xml.TableSerializationImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class SerializationTest {
	@Test
    public void testSerialization() throws IOException{                                                        
		ProfileSerializationImpl ProfileSerializationImpl = new ProfileSerializationImpl();
		TableSerializationImpl TableSerializationImpl = new TableSerializationImpl();
		ConstraintsSerializationImpl constraintsSerializationImpl = new ConstraintsSerializationImpl();
		
		Profile profile = ProfileSerializationImpl.deserializeXMLToProfile(IOUtils.toString(this.getClass().getResourceAsStream("Profile.xml"), "UTF-8"),
				IOUtils.toString(this.getClass().getResourceAsStream("ValueSets.xml"), "UTF-8"),
				IOUtils.toString(this.getClass().getResourceAsStream("PredicateConstraints.xml"), "UTF-8"),
				IOUtils.toString(this.getClass().getResourceAsStream("ConformanceStatementConstraints.xml"), "UTF-8"));
		assertEquals(55, profile.getPredicates().getDatatypeContext().getByNameOrByIDs().size());
		assertEquals(4, profile.getPredicates().getSegmentContext().getByNameOrByIDs().size());
		assertEquals(15, profile.getConformanceStatements().getDatatypeContext().getByNameOrByIDs().size());
		assertEquals(5, profile.getConformanceStatements().getSegmentContext().getByNameOrByIDs().size()); 
		assertEquals(1, profile.getMessages().getMessages().size()); 
    }
}
