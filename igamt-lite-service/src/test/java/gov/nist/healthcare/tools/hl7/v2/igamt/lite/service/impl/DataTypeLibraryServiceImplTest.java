package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.integration.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceContext.class})
public class DataTypeLibraryServiceImplTest {

	@Autowired
	DatatypeLibraryService dtlService;
	
	@Before
	public void before() {
		List<DatatypeLibrary>  dtlMUs = null;
		List<SCOPE> stdScope = new ArrayList<SCOPE>();
		stdScope.add(Constant.SCOPE.MASTER);
		stdScope.add(Constant.SCOPE.USER);
		dtlMUs = dtlService.findByScopesAndVersion(stdScope, "2.5.1");
		assertNotNull(dtlMUs);
		for (DatatypeLibrary dtl : dtlMUs) {
			dtlService.delete(dtl);
		}
		List<DatatypeLibrary> dtls = dtlService.findAll();
		assertNotNull(dtls);
		assertEquals(10, dtls.size());
	}
	
	@Test
	public void testFindAll() {
		List<DatatypeLibrary> dtls = dtlService.findAll();
		assertNotNull(dtls);
	}

	@Test
	public void testFindByScopes() {
		List<DatatypeLibrary>  dtlMs;
		List<SCOPE> stdScope = new ArrayList<SCOPE>();
		stdScope.add(Constant.SCOPE.HL7STANDARD);
		dtlMs = dtlService.findByScopesAndVersion(stdScope, "2.5.1");
		assertNotNull(dtlMs);
		DatatypeLibrary dtlM = dtlMs.get(0);
		dtlM.setId(null);
		dtlM.setScope(Constant.SCOPE.MASTER);
		dtlService.save(dtlM);
		
		List<DatatypeLibrary>  dtlUs;
		dtlUs = dtlService.findByScopesAndVersion(stdScope, "2.5.1");
		assertNotNull(dtlUs);
		DatatypeLibrary dtlU = dtlUs.get(0);
		dtlU.setId(null);
		dtlU.setScope(Constant.SCOPE.USER);
		dtlService.save(dtlU);

		List<SCOPE> scopes = new ArrayList<SCOPE>();
		scopes.add(SCOPE.MASTER);
		scopes.add(SCOPE.USER);
		List<DatatypeLibrary> dtl = dtlService.findByScopes(scopes);
		assertNotNull(dtl);
	}
	
	@Test 
	public void testFindHl7Versions() {
		List<DatatypeLibrary> dtl = dtlService.findAll();
		List<String> versions = dtlService.findHl7Versions();
		assertNotNull(versions);
		assertEquals(dtl.size(), versions.size());
	}
	
	@Test
	public void testFindByScopeAndVersion() {
		List<SCOPE> stdScope = new ArrayList<SCOPE>();
		stdScope.add(Constant.SCOPE.HL7STANDARD);
		List<DatatypeLibrary> dtlHs = dtlService.findByScopesAndVersion(stdScope, "2.5.1");
		assertNotNull(dtlHs);
		assertEquals(91, dtlHs.get(0).getChildren().size());

		List<SCOPE> muScope = new ArrayList<SCOPE>();
		muScope.add(Constant.SCOPE.USER);
		muScope.add(Constant.SCOPE.MASTER);
		List<DatatypeLibrary> dtlMUs = dtlService.findByScopesAndVersion(muScope, "2.7");
		assertNotNull(dtlMUs);
		assertEquals(1, dtlMUs.size());
	}
	
	@Test
	public void saveTest() {
		List<DatatypeLibrary> dtls = dtlService.findAll();
		assertTrue(dtls.size() >= 9);
		List<DatatypeLibrary> dtlHs;
		List<SCOPE> stdScope = new ArrayList<SCOPE>();
		stdScope.add(Constant.SCOPE.HL7STANDARD);
		dtlHs = dtlService.findByScopesAndVersion(stdScope, "2.5.1");
		assertNotNull(dtlHs);
		DatatypeLibrary dtlH = dtlHs.get(0);
		dtlH.setId(null);
		dtlH.setScope(Constant.SCOPE.USER);
		dtlService.save(dtlH);
		dtls = dtlService.findAll();
		assertTrue(dtls.size() >= 9);
		List<SCOPE> userScope = new ArrayList<SCOPE>();
		userScope.add(Constant.SCOPE.USER);
		List<DatatypeLibrary>  dtlU = dtlService.findByScopesAndVersion(userScope, "2.5.1");
		assertNotNull(dtlU);
		for(DatatypeLibrary dtl : dtlU) {
			assertEquals(Constant.SCOPE.USER, dtl.getScope());
			dtlService.delete(dtl);
		}
		dtls = dtlService.findAll();
		assertTrue(dtls.size() >= 9);
	}
}
