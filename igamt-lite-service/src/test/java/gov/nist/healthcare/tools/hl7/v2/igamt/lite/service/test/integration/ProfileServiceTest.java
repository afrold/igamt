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

import static org.junit.Assert.assertNotNull;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerializationImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Harold Affo (harold.affo@nist.gov) Mar 4, 2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PersistenceContext.class)
// @TransactionConfiguration
// @Transactional(readOnly = false)
public class ProfileServiceTest {

  @Autowired
  ProfileService service;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @BeforeClass
  public static void setup() {
    try {
      Properties p = new Properties();
      InputStream log4jFile =
          ProfileServiceTest.class.getResourceAsStream("/igl-test-log4j.properties");
      p.load(log4jFile);
      PropertyConfigurator.configure(p);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  public void testGetById() throws Exception {
    String p = IOUtils.toString(this.getClass().getResourceAsStream("/vxuTest/Profile.xml"));
    String v = IOUtils.toString(this.getClass().getResourceAsStream("/vxuTest/ValueSets_all.xml"));
    String c = IOUtils.toString(this.getClass().getResourceAsStream("/vxuTest/Constraints.xml"));
    Profile profile = new ProfileSerializationImpl().deserializeXMLToProfile(p, v, c);
    assertNotNull("Profile is null.", profile);
    service.save(profile);
    assertNotNull("Profile not saved", profile.getId());
    profile = service.findOne(profile.getId());
    assertNotNull("Profile not saved", profile.getId());
  }

  // @Test
  public void testSave() throws Exception {
    String p = IOUtils.toString(this.getClass().getResourceAsStream("/vxuTest/Profile.xml"));
    String v = IOUtils.toString(this.getClass().getResourceAsStream("/vxuTest/ValueSets_all.xml"));
    String c = IOUtils.toString(this.getClass().getResourceAsStream("/vxuTest/Constraints.xml"));
    Profile profile = new ProfileSerializationImpl().deserializeXMLToProfile(p, v, c);
    assertNotNull("Profile is null.", profile);
    // checkUniquenessOfReference(profile);
    service.save(profile);
    assertNotNull("Profile not saved", profile.getId());
    service.save(profile);
    System.out.println(profile.getId());

  }

  // @Test
  public void testDelete() throws Exception {
    String p = IOUtils.toString(this.getClass().getResourceAsStream("/vxuTest/Profile.xml"));
    String v = IOUtils.toString(this.getClass().getResourceAsStream("/vxuTest/ValueSets_all.xml"));
    String c = IOUtils.toString(this.getClass().getResourceAsStream("/vxuTest/Constraints.xml"));
    Profile profile = new ProfileSerializationImpl().deserializeXMLToProfile(p, v, c);
    assertNotNull("Profile is null.", profile);
    // checkUniquenessOfReference(profile);
    service.save(profile);
    assertNotNull("Profile not saved", profile.getId());
    service.delete(profile.getId());
  }

  // private void checkUniquenessOfReference(Profile profile) {
  // Set<Datatype> ds = profile.getDatatypes().getChildren();
  // java.util.Iterator<Datatype> it = ds.iterator();
  // while (it.hasNext()) {
  // Datatype one = it.next();
  // java.util.Iterator<Datatype> it2 = ds.iterator();
  // while (it2.hasNext()) {
  // Datatype two = it2.next();
  // java.util.Iterator<ConformanceStatement> itConfStatment2 = two
  // .getConformanceStatements().iterator();
  // java.util.Iterator<Predicate> itPred2 = two.getPredicates()
  // .iterator();
  // while (itConfStatment2.hasNext()) {
  // ConformanceStatement cf = itConfStatment2.next();
  // if (one.getConformanceStatements().contains(cf)
  // && one != two) {
  // throw new IllegalArgumentException(cf.getId() + " - "
  // + cf.getDescription() + " is shared by"
  // + one.getName() + " and  " + two.getName());
  // }
  // }
  // while (itPred2.hasNext()) {
  // Predicate pred = itPred2.next();
  // if (one.getPredicates().contains(pred) && one != two) {
  // throw new IllegalArgumentException(pred.getId() + " - "
  // + pred.getDescription() + " is shared by"
  // + one.getName() + " and  " + two.getName());
  // }
  // }
  //
  // }
  // }
  // }
}
