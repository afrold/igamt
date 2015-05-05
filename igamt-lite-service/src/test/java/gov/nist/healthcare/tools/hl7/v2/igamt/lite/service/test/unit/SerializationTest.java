package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.unit;

import static org.junit.Assert.assertEquals;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatypes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerializationImpl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class SerializationTest {

	private Datatype getDatatype(String key, Datatypes datatypes) {
		for (Datatype dt : datatypes.getChildren()) {
			if (dt.getLabel().equals(key)) {
				return dt;
			}
		}
		return null;
	}

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

}
