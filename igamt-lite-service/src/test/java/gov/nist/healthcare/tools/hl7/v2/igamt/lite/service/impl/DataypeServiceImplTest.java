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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.QUANTUM;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.integration.PersistenceContext;

/**
 * @author gcr1
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceContext.class})
public class DataypeServiceImplTest {

	@Autowired
	DatatypeService dtlService;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * Test method for {@link gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.DataypeServiceImpl#findAll(gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.QUANTUM)}.
	 */
	@Test
	public void testFindAll() {
		List<Datatype> sut = dtlService.findAll(QUANTUM.BREVIS);
		assertNotNull(sut);
		assertTrue(0 < sut.size());
	}

	/**
	 * Test method for {@link gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.DataypeServiceImpl#findById(java.lang.String, gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.QUANTUM)}.
	 */
	@Test
	public void testFindById() {
		String id = "565f3ab7d4c6e52cfd44841b";
		Datatype sut = dtlService.findById(id, QUANTUM.BREVIS);
		assertNotNull(sut);
	}

	/**
	 * Test method for {@link gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.DataypeServiceImpl#findByLibrary(java.lang.String, gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.QUANTUM)}.
	 */
	@Test
	public void testFindByLibrary() {
		String id = "5714dc6cda08ce8aa9510320";
		List<Datatype> sut = dtlService.findByLibrary(id, QUANTUM.BREVIS);
		assertNotNull(sut);
	}
}
