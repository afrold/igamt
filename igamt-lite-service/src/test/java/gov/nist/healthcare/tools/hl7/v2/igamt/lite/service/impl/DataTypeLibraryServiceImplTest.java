package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
<<<<<<< HEAD:igamt-lite-service/src/test/java/gov/nist/healthcare/tools/hl7/v2/igamt/lite/service/impl/DataTypeLibraryServiceImplTest.java
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DataTypeLibraryService;
=======
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryService;
>>>>>>> datatype-management:igamt-lite-service/src/test/java/gov/nist/healthcare/tools/hl7/v2/igamt/lite/service/impl/DTLibServiceImplTest.java
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.integration.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceContext.class})
public class DataTypeLibraryServiceImplTest {

	@Autowired
<<<<<<< HEAD:igamt-lite-service/src/test/java/gov/nist/healthcare/tools/hl7/v2/igamt/lite/service/impl/DataTypeLibraryServiceImplTest.java
	DataTypeLibraryService dtlService;
=======
	DatatypeLibraryService dtlService;
>>>>>>> datatype-management:igamt-lite-service/src/test/java/gov/nist/healthcare/tools/hl7/v2/igamt/lite/service/impl/DTLibServiceImplTest.java
	
//	@Test
	public void testFindAll() {
		List<DatatypeLibrary> dtl = dtlService.findAll();
		assertNotNull(dtl);
		assertEquals(91, dtl.get(0).getChildren().size());
	}

	@Test
	public void testFindByScope() {
		DatatypeLibrary dtl9 = dtlService.findByScope(DatatypeLibrary.SCOPE.USER, null);
		List<DatatypeLibrary> dtl0 = dtlService.findAll();
		assertEquals(91, dtl0.get(0).getChildren().size());
<<<<<<< HEAD:igamt-lite-service/src/test/java/gov/nist/healthcare/tools/hl7/v2/igamt/lite/service/impl/DataTypeLibraryServiceImplTest.java

		DatatypeLibrary dtl1 = dtlService.findByScope(DatatypeLibrary.SCOPE.HL7STANDARD, null);
		assertNotNull(dtl1);
		assertEquals(dtl0.get(0).getChildren().size(), dtl1.getChildren().size());
		DatatypeLibrary dtl2 = dtl0.get(0);
		assertNotNull(dtl2);
		Datatype dt = dtl2.getChildren().iterator().next();
		Set<Datatype> dts = new HashSet<Datatype>();
		dts.add(dt);
		dtl2.setChildren(dts);
		DatatypeLibrary dtl3 = dtlService.findByScope(DatatypeLibrary.SCOPE.HL7STANDARD, dtl2);
		assertEquals(dtl0.get(0).getChildren().size(), dtl3.getChildren().size() - dtl2.getChildren().size());
=======
>>>>>>> datatype-management:igamt-lite-service/src/test/java/gov/nist/healthcare/tools/hl7/v2/igamt/lite/service/impl/DTLibServiceImplTest.java
	}
	
//	@Test
	public void saveTest() {
		DatatypeLibrary dtl;
<<<<<<< HEAD:igamt-lite-service/src/test/java/gov/nist/healthcare/tools/hl7/v2/igamt/lite/service/impl/DataTypeLibraryServiceImplTest.java
		dtl = dtlService.findByScope(DatatypeLibrary.SCOPE.HL7STANDARD, null);
		assertNotNull(dtl);
		dtl.setId(null);
		dtl.setScope(DatatypeLibrary.SCOPE.USER);
		dtlService.apply(dtl);
=======
>>>>>>> datatype-management:igamt-lite-service/src/test/java/gov/nist/healthcare/tools/hl7/v2/igamt/lite/service/impl/DTLibServiceImplTest.java
	}
}
