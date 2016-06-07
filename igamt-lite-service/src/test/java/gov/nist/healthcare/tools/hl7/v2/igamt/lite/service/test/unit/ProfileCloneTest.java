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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.unit;

import static org.junit.Assert.assertEquals;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerializationImpl;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Harold Affo (harold.affo@nist.gov) Apr 16, 2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceContextUnit.class})
public class ProfileCloneTest {

  @Test
  public void testClone() throws CloneNotSupportedException {
    Profile profile = profile().clone();

    assertEquals(80, profile.getDatatypeLibrary().getChildren().size());
  }

  private Profile profile() {
    String xmlContentsProfile;
    try {
      xmlContentsProfile =
          IOUtils.toString(ProfileCloneTest.class.getResourceAsStream("/vxu/Profile.xml"));
      String xmlValueSet =
          IOUtils.toString(ProfileCloneTest.class.getResourceAsStream("/vxu/ValueSets_all.xml"));
      String xmlConstraints =
          IOUtils.toString(ProfileCloneTest.class.getResourceAsStream("/vxu/Constraints.xml"));
      Profile p =
          new ProfileSerializationImpl().deserializeXMLToProfile(xmlContentsProfile, xmlValueSet,
              xmlConstraints);

      return p;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return null;
  }
}
