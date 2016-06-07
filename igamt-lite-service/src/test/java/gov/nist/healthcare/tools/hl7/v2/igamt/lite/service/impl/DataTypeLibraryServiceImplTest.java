package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DatatypeRepositoryImpl;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.integration.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceContext.class})
public class DataTypeLibraryServiceImplTest {

  private Logger log = LoggerFactory.getLogger(DataTypeLibraryServiceImplTest.class);

  @Autowired
  DatatypeLibraryService dtlService;

  @Autowired
  DatatypeService dtService;

  @Before
  public void before() {
    List<DatatypeLibrary> dtlMUs = null;
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
  }

  @Test
  public void testFindAll() {
    List<DatatypeLibrary> dtls = dtlService.findAll();
    assertNotNull(dtls);
  }

  @Test
  public void testFindByScope() {
    List<DatatypeLibrary> dtlH = dtlService.findByScope(SCOPE.HL7STANDARD, null);
    assertNotNull(dtlH);
    assertTrue(dtlH.size() > 0);

    List<DatatypeLibrary> dtlM = dtlService.findByScope(SCOPE.MASTER, 45L);
    assertNotNull(dtlM);
    assertTrue(dtlH.size() > 0);

    List<DatatypeLibrary> dtlU = dtlService.findByScope(SCOPE.USER, 45L);
    assertNotNull(dtlU);
  }

  // @Test
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
    List<DatatypeLibrary> dtlU = dtlService.findByScopesAndVersion(userScope, "2.5.1");
    assertNotNull(dtlU);
    for (DatatypeLibrary dtl : dtlU) {
      assertEquals(Constant.SCOPE.USER, dtl.getScope());
      dtlService.delete(dtl);
    }
    dtls = dtlService.findAll();
    assertTrue(dtls.size() >= 9);
  }

  DatatypeLibrary initState() {
    // First we need a library and ensure its state of no children.
    List<SCOPE> scopes = new ArrayList<SCOPE>();
    scopes.add(Constant.SCOPE.MASTER);
    List<DatatypeLibrary> dtls = dtlService.findByScopesAndVersion(scopes, "2.7");
    DatatypeLibrary dtl = dtls.get(0);
    String dtLibId = dtl.getId();
    dtl.getChildren().clear();
    dtlService.save(dtl);

    // Be sure there are no children in the database.
    List<Datatype> dtsBefore = dtService.findByLibIds(dtLibId);
    for (Datatype dt : dtsBefore) {
      dtService.delete(dt);
    }
    return dtl;
  }

  @Test
  public void testBindDatatypes() {
    DatatypeLibrary dtl = initState();
    String dtLibId = dtl.getId();
    // We need some datatypes
    List<Datatype> dts = dtService.findAll();
    // Snapshot its size
    int libIdsSize = dts.get(0).getLibIds().size();
    List<String> datatypeIds = new ArrayList<String>();
    datatypeIds.add(dts.get(0).getId());
    datatypeIds.add(dts.get(1).getId());
    datatypeIds.add(dts.get(2).getId());

    // Lets check initial state.
    assertNull(dts.get(0).getExt());
    assertNull(dts.get(1).getExt());
    assertNull(dts.get(2).getExt());

    // Do the bind
    List<Datatype> sut = dtlService.bindDatatypes(datatypeIds, dtLibId, "tex", 45L);
    assertNotNull(sut);
    assertEquals(3, sut.size());
    assertEquals(45L, sut.get(0).getAccountId().longValue());

    for (Datatype sutDt : sut) {
      assertEquals(libIdsSize + 1, sutDt.getLibIds().size());
      assertTrue(sutDt.getLibIds().contains(dtLibId));
      assertNotNull(sutDt.getExt());
      assertNotNull(sutDt.getHl7Version());
      assertNotNull(sutDt.getDate());
      assertNotNull(sutDt.getType());
    }

    DatatypeLibrary dtl1 = dtlService.findById(dtLibId);
    assertEquals(dtLibId, dtl1.getId());
    assertEquals(dtl1.getChildren().size(), sut.size());
    for (DatatypeLink link : dtl1.getChildren()) {
      assertNotNull(link.getExt());
      Datatype dt = dtService.findById(link.getId());
      assertNotNull(dt);
      assertNotNull(dt.getExt());

    }
    List<String> exts = new ArrayList<String>();
    for (Datatype dt : sut) {
      exts.add(dt.getExt());
      log.debug(dt.getExt());
    }
    initState();
  }

  // @Test
  public void testBindDatatypesDoDups() {
    List<SCOPE> scopes = new ArrayList<SCOPE>();
    scopes.add(Constant.SCOPE.MASTER);
    List<DatatypeLibrary> dtls = dtlService.findByScope(SCOPE.MASTER, null);
    assertNotNull(dtls);
    assertEquals(0, dtls.get(0).getChildren().size());
    List<Datatype> dts = dtService.findAll();
    List<String> datatypeIds = new ArrayList<String>();
    datatypeIds.add(dts.get(0).getId());
    datatypeIds.add(dts.get(1).getId());
    datatypeIds.add(dts.get(2).getId());
    String dtLibId = dtls.get(0).getId();
    List<Datatype> sut = dtlService.bindDatatypes(datatypeIds, dtLibId, "tex", 45L);
    List<DatatypeLibrary> dtls1 = dtlService.findByScope(SCOPE.MASTER, null);
    assertNotNull(dtls1);
    assertEquals(3, dtls1.get(0).getChildren().size());
    assertNotNull(sut);
    assertTrue(sut.get(0).getLibIds().contains(dtLibId));
    assertNotNull(sut.get(0).getExt());
    assertEquals("tex", sut.get(0).getExt());
    assertEquals(45L, sut.get(0).getAccountId().longValue());
    List<Datatype> sut1 = dtService.findByScopesAndVersion(scopes, sut.get(0).getHl7Version());
    assertNotNull(sut1);
    List<String> datatypeIds1 = new ArrayList<String>();
    datatypeIds1.add(sut1.get(0).getId());
    datatypeIds1.add(sut1.get(1).getId());
    datatypeIds1.add(sut1.get(2).getId());
    List<Datatype> sut2 = dtlService.bindDatatypes(datatypeIds, dtLibId, "tex", 45L);
    assertNotNull(sut2);
  }

  @Test
  public void testDeNull() {
    DataTypeLibraryServiceImpl dtLibSvc = new DataTypeLibraryServiceImpl();
    assertNotNull(dtLibSvc.deNull(null));
    assertNotNull(dtLibSvc.deNull("  "));
    assertEquals("abc", dtLibSvc.deNull("abc"));
  }
}
