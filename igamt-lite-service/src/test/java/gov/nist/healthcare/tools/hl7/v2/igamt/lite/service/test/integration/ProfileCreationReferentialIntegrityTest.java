/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.integration;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLibrary;

/**
 * Not a standalone test.
 * Intended to be called by a junit test.
 * 
 * A set of tests to determine the internal integrity of references in a Profile.
 * Checks that all segmentRefs refer to valid segments.
 * Checks that all Fields reference valid dataypes.
 * Checks that all Datatypes.Components reference valid datatypes.
 * Intended to be called by a junit test.
 */
public class ProfileCreationReferentialIntegrityTest {

	private static final Logger log = LoggerFactory.getLogger(ProfileCreationReferentialIntegrityTest.class);

	public void testMessagesVsSegments(Profile profile) {

		log.info("Running testMessagesVsSegments...");

		SegmentLibrary segs = profile.getSegmentLibrary();

		// First we list all the Segment ids.
		List<String> segIds = new ArrayList<String>();
		for (Segment seg : segs.getChildren()) {
			segIds.add(seg.getId());
		}

		// Second we list all the SegmentRefs from the Messages.
		List<String> segRefs = null;
		Messages msgs = profile.getMessages();
		Iterator itr = msgs.getChildren().iterator();

		Message msg = null;
		while (itr.hasNext()) {
			msg = (Message) itr.next();
			segRefs = new ArrayList<String>();
			segRefs.addAll(doGroup(msg.getChildren()));
		}

		// Third we check each segmentRef to be sure it has a corresponding
		// the Segment.id.
		for (String s : segRefs) {
			assertTrue(segIds.contains(s));
		}
	}

	// A little recursion to get all SegmentRefs buried in Groups.
	List<String> doGroup(List<SegmentRefOrGroup> sogs) {
		List<String> refs = new ArrayList<String>();

		for (SegmentRefOrGroup sog : sogs) {
			if (Constant.SEGMENTREF.equals(sog.getType())) {
				SegmentRef sr = (SegmentRef) sog;
				refs.add(sr.getRef());
			} else if (Constant.GROUP.equals(sog.getType())) {
				Group grp = (Group) sog;
				refs.addAll(doGroup(grp.getChildren()));
			} else {
				log.error("Neither SegRef nor Group sog=" + sog.getType() + "=");
			}
		}
		return refs;
	}

	public void testComponentDataypes(Profile profile) {
		log.info("Running testComponentDataypes...");

		List<String> dts = new ArrayList<String>();
		Set<Component> cts = new HashSet<Component>();
		for (Datatype dt : profile.getDatatypeLibrary().getChildren()) {
			dts.add(dt.getId());
		}
		for (Datatype dt : profile.getDatatypeLibrary().getChildren()) {
			for (Component ct : dt.getComponents()) {
				cts.add(ct);
			}
		}
		for (Component ctId : cts) {
			assertTrue(dts.contains(ctId.getDatatype()));
		}
	}

	public void testFieldDatatypes(Profile profile) {
		log.info("Running testFieldDatatypes...");
		List<String> dts = new ArrayList<String>();
		for (Datatype dt : profile.getDatatypeLibrary().getChildren()) {
			dts.add(dt.getId());
		}
		for (Segment seg : profile.getSegmentLibrary().getChildren()) {
			for (Field fld : seg.getFields()) {
				assertTrue(dts.contains(fld.getDatatype()));
			}
		}
	}
}
