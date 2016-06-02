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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;

/**
 * @author gcr1
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceContext.class})
public class DatatypeServiceTest {

  private Logger log = LoggerFactory.getLogger(DatatypeServiceTest.class);

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Autowired
  DatatypeService datatypeService;

  @Test
  public void testFindAll() {
    List<Datatype> dts = datatypeService.findAll();
    log.info("datatypes.size()=" + dts.size());
    assertTrue(dts.size() > 0);
  }

  @Test
  public void testById() {
    List<Datatype> dts = datatypeService.findAll();
    assertTrue(dts.size() > 0);
    Datatype dtbefore = dts.get(0);
    String idbefore = dtbefore.getId();
    Datatype dtafter = datatypeService.findById(idbefore);
    String idafter = dtafter.getId();
    assertTrue(idbefore.equals(idafter));
  }

  @Test
  public void testExtSave() {
    List<Datatype> dts = datatypeService.findAll();
    assertTrue(dts.size() > 0);
    Datatype dtbefore = dts.get(0);
    dtbefore.setExt("ABC");
    Datatype dtafter = datatypeService.findById(dtbefore.getId());
    assertEquals("ABC", dtafter.getExt());
  }
}
