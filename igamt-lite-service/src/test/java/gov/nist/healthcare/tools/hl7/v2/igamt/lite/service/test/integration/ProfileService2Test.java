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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.DatatypesService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.xml.ProfileSerializationImpl;

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
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Harold Affo (harold.affo@nist.gov) Mar 4, 2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PersistenceContext.class)
@TransactionConfiguration
@Transactional(readOnly = false)
public class ProfileService2Test extends
		AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	ProfileService service;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@BeforeClass
	public static void setup() {
		try {
			Properties p = new Properties();
			InputStream log4jFile = ProfileService2Test.class
					.getResourceAsStream("/igl-test-log4j.properties");
			p.load(log4jFile);
			PropertyConfigurator.configure(p);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testApply() throws Exception {
		String p = IOUtils.toString(this.getClass().getResourceAsStream(
				"/vxuTest/Profile.xml"));
		String v = IOUtils.toString(this.getClass().getResourceAsStream(
				"/vxuTest/ValueSets_all.xml"));
		String c = IOUtils.toString(this.getClass().getResourceAsStream(
				"/vxuTest/Constraints.xml"));
		Profile profile = new ProfileSerializationImpl()
				.deserializeXMLToProfile(p, v, c);
		Profile p1 = service.save(profile);

		String p1Id = String.valueOf(p1.getId());

		String jsonString = 
				"{"
						+	"\"profile\": "
						+		"{\"" + p1Id + "\": "
						+			"{ \"identifier\": \"IG1_234\", \"type\": \"Constrainable-d\", \"name\": \"Implementation Guide for Immunization Messaging_\",\"orgName\": \"NIST_\",\"status\": \"Active\"}"
						+	"}"
						+"}";
		
		service.apply(jsonString);
		assertEquals("IG1_234", p1.getMetaData().getIdentifier());


	}

}
