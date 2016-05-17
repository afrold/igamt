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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

/**
 * @author gcr1
 *
 */
public class DatatypeLibraryTest {

	DatatypeLibrary sut; 

	@Before
	public void setUp() throws Exception {
		sut = new DatatypeLibrary();
	}

	@Test
	public void testAdd() {
		assertEquals(0, sut.getChildren().size());
		sut.addDatatype(new DatatypeLink("123", "abc", ""));
		assertEquals(1, sut.getChildren().size());
		sut.addDatatype(new DatatypeLink("234", "def", ""));
		assertEquals(2, sut.getChildren().size());
		sut.addDatatype(new DatatypeLink("123", "def", ""));
		assertEquals(2, sut.getChildren().size());
		Iterator<DatatypeLink> itr = sut.getChildren().iterator();
		while (itr.hasNext()) {
			assertEquals("def", ((DatatypeLink)itr.next()).getName());
		}
	}

	@Test
	public void testDelete() {
		assertEquals(0, sut.getChildren().size());
		sut.addDatatype(new DatatypeLink("123", "abc", ""));
		assertEquals(1, sut.getChildren().size());
		sut.addDatatype(new DatatypeLink("234", "def", ""));
		assertEquals(2, sut.getChildren().size());
//		sut.delete(new DatatypeLink("123", "def", ""));
		assertEquals(1, sut.getChildren().size());
		Iterator<DatatypeLink> itr = sut.getChildren().iterator();
		assertTrue(itr.hasNext());
		assertEquals("234", ((DatatypeLink)itr.next()).getId());
	}

	@Test
	public void testFindOne() {
		assertEquals(0, sut.getChildren().size());
		sut.addDatatype(new DatatypeLink("123", "abc", ""));
		assertEquals(1, sut.getChildren().size());
		sut.addDatatype(new DatatypeLink("234", "def", ""));
		assertEquals(2, sut.getChildren().size());
		DatatypeLink link = sut.findOne(new DatatypeLink("123", "abc", ""));
		assertEquals("123", link.getId());
	}
}
