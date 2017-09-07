/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.integration.IntegrationTestApplicationConfig;

/**
 * @author gcr1
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IntegrationTestApplicationConfig.class})
public class DataypeServiceImplTest {

  @Autowired
  DatatypeLibraryService datatypeLibraryService;

  @Autowired
  DatatypeService datatypeService;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  /**
   * Test method for
   * {@link gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.DatatypeServiceImpl#findAll(gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.QUANTUM)}
   * .
   */
  @Test
  public void testFindAll() {
    List<Datatype> sut = datatypeService.findAll();
    assertNotNull(sut);
    assertTrue(0 < sut.size());
  }

  /**
   * Test method for
   * {@link gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.DatatypeServiceImpl#findById(java.lang.String, gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.QUANTUM)}
   * .
   */
  @Test
  public void testFindById() {
    List<Datatype> dts = datatypeService.findAll();
    String id = dts.get(0).getId();
    Datatype sut = datatypeService.findById(id);
    assertNotNull(sut);
  }


  @Test
  public void testFindByScopeAndVersion() {
    List<SCOPE> stdScope = new ArrayList<SCOPE>();
    stdScope.add(Constant.SCOPE.USER);
    stdScope.add(Constant.SCOPE.HL7STANDARD);
    stdScope.add(Constant.SCOPE.MASTER);
    List<Datatype> sut = datatypeService.findByScopesAndVersion(stdScope, "2.5.1");
    assertNotNull(sut);
    assertTrue(sut.size() > 0);
  }

  /**
   * Test method for
   * {@link gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.DatatypeServiceImpl#findByLibrary(java.lang.String, gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.QUANTUM)}
   * .

  @Test
  public void testFindLibIds() {
    List<SCOPE> stdScope = new ArrayList<SCOPE>();
    stdScope.add(Constant.SCOPE.HL7STANDARD);
    List<DatatypeLibrary> dtLib = datatypeLibraryService.findByScopesAndVersion(stdScope, "2.5.1");
    String id = dtLib.get(0).getId();
    List<Datatype> sut = datatypeService.findByLibIds(id);
    assertNotNull(sut);
    assertTrue(sut.size() > 0);
  }

  @Test
  public void testFindByIds() {
    List<SCOPE> stdScope = new ArrayList<SCOPE>();
    stdScope.add(Constant.SCOPE.HL7STANDARD);
    List<DatatypeLibrary> dtLib = datatypeLibraryService.findByScopesAndVersion(stdScope, "2.5.1");
    Set<DatatypeLink> children = dtLib.get(0).getChildren();
    List<String> ids = new ArrayList<String>();
    for (DatatypeLink dtl : children) {
      ids.add(dtl.getId());
    }
    List<Datatype> sut = datatypeService.findByIds(ids);
    assertEquals(ids.size(), sut.size());
  }*/
  
  @Test
  public void testFindShared() {
    List<Datatype> sut = datatypeService.findShared(new Long(10));
   System.out.println(sut);
    assertNotNull(sut);
    assertTrue(0 < sut.size());
  }
}
