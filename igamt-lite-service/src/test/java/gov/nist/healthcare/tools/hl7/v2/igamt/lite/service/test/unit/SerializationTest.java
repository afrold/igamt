package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.unit;

import static org.junit.Assert.assertEquals;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerializationImpl;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class SerializationTest {
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
		assertEquals(153, profile.getDatatypes().getChildren().size());

		// assertEquals(4, profile.getPredicates().getSegments()
		// .getByNameOrByIDs().size());
		// assertEquals(15, profile.getConformanceStatements().getDatatypes()
		// .getByNameOrByIDs().size());
		// assertEquals(5, profile.getConformanceStatements().getSegments()
		// .getByNameOrByIDs().size());
		// assertEquals(1, profile.getMessages().getMessages().size());
	}
}
