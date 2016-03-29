package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DataTypeLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.integration.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceContext.class})
public class DataTypeLibraryServiceImplTest {

	@Autowired
	DataTypeLibraryService dtlService;
	
	@Test
	public void testFindAll() {
		List<DatatypeLibrary> dtl = dtlService.findAll();
		assertNotNull(dtl);
		assertEquals(91, dtl.get(0).getChildren().size());
	}

	@Test
	public void testFindByScope() {
		DatatypeLibrary dtlH = dtlService.findByScope(DatatypeLibrary.SCOPE.HL7STANDARD);
		assertNotNull(dtlH);
		DatatypeLibrary dtlM = dtlService.findByScope(DatatypeLibrary.SCOPE.MASTER);
		assertNotNull(dtlM);
		assertEquals(0, dtlM.getChildren().size());
	}
	
	@Test
	public void saveTest() {
		List<DatatypeLibrary> dtls = dtlService.findAll();
		assertEquals(1, dtls.size());
		DatatypeLibrary dtlH;
		dtlH = dtlService.findByScope(DatatypeLibrary.SCOPE.HL7STANDARD);
		assertNotNull(dtlH);
		dtlH.setId(null);
		dtlH.setScope(DatatypeLibrary.SCOPE.USER);
		dtlService.apply(dtlH);
		dtls = dtlService.findAll();
		assertEquals(2, dtls.size());
		DatatypeLibrary dtlU = dtlService.findByScope(DatatypeLibrary.SCOPE.USER);
		assertNotNull(dtlU);
		assertEquals(91, dtlU.getChildren().size());
		assertEquals(DatatypeLibrary.SCOPE.USER, dtlU.getScope());
		dtlService.delete(dtlU);
		dtls = dtlService.findAll();
		assertEquals(1, dtls.size());
	}
}
