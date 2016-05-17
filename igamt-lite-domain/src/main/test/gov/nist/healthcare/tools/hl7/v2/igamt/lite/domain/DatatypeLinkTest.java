package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class DatatypeLinkTest {

	@Test
	public void testEquals() {
		Set<DatatypeLink> links = new HashSet<DatatypeLink>();
		links.add(new DatatypeLink("ida", "namea", "exta"));
		links.add(new DatatypeLink("idb", "nameb", "extb"));
		assertEquals(2, links.size());
		assertFalse(links.add(new DatatypeLink("ida", "namea", "exta")));
		assertEquals(2, links.size());
		assertTrue(links.contains(new DatatypeLink("idb", "nameb", "extb")));
		assertFalse(links.contains(new DatatypeLink("ida", "nameb", "extb")));
	}
}
