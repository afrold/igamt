package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class TableLinkTest {

	@Test
	public void testEquals() {
		Set<TableLink> links = new HashSet<TableLink>();
		links.add(new TableLink("ida", "namea"));
		links.add(new TableLink("idb", "nameb"));
		assertEquals(2, links.size());
		links.add(new TableLink("ida", "namea"));
		assertEquals(2, links.size());
		assertTrue(links.contains(new TableLink("idb", "nameb")));
	}
}
