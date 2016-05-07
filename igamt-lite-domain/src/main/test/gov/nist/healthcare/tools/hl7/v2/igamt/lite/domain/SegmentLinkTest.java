package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class SegmentLinkTest {

	@Test
	public void testEquals() {
		Set<SegmentLink> links = new HashSet<SegmentLink>();
		links.add(new SegmentLink("ida", "namea", "exta"));
		links.add(new SegmentLink("idb", "nameb", "extb"));
		assertEquals(2, links.size());
		links.add(new SegmentLink("ida", "namea", "exta"));
		assertEquals(2, links.size());
		assertTrue(links.contains(new SegmentLink("idb", "nameb", "extb")));
	}

}
