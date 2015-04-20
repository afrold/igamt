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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.unit;

import static org.junit.Assert.assertEquals;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileSaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerializationImpl;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.integration.ProfileServiceImplIntegrationTest;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.ProfileChangeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.ProfilePropertySaveError;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Harold Affo (harold.affo@nist.gov) Apr 16, 2015
 */
public class ProfileChangeServiceTest {

	@BeforeClass
	public static void setup() {
		try {
			Properties p = new Properties();
			InputStream log4jFile = ProfileServiceImplIntegrationTest.class
					.getResourceAsStream("/igl-test-log4j.properties");
			p.load(log4jFile);
			PropertyConfigurator.configure(p);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testApplyProfileMetaDataChanges() throws IOException {
		String p = IOUtils.toString(this.getClass().getResourceAsStream(
				"/vxuTest/Profile.xml"));
		String v = IOUtils.toString(this.getClass().getResourceAsStream(
				"/vxuTest/ValueSets_all.xml"));
		String c = IOUtils.toString(this.getClass().getResourceAsStream(
				"/vxuTest/Constraints.xml"));
		Profile p1 = new ProfileSerializationImpl().deserializeXMLToProfile(p,
				v, c);
		String jsonChanges = "\r\n\r\n {\r\n  \"profile\": {\r\n    \"edit\": [\r\n      {\r\n        \"id\": \"552014603004d0a9f09caf16\",\r\n        \"identifier\": \"ddddddddddddd\",\r\n        \"subTitle\": \"ddddddddd\",\r\n        \"orgName\": \"NISTddddd\",\r\n        \"name\": \"VXU_V04ddddd\"\r\n      }\r\n    ]\r\n  }\r\n}\r\n                ";
		try {
			List<ProfilePropertySaveError> errors = new ProfileChangeService()
					.apply(jsonChanges, p1);
			assertEquals(0, errors.size());
			assertEquals("ddddddddddddd", p1.getMetaData().getIdentifier());
			assertEquals("ddddddddd", p1.getMetaData().getSubTitle());
			assertEquals("NISTddddd", p1.getMetaData().getOrgName());
			assertEquals("VXU_V04ddddd", p1.getMetaData().getName());
			// assertEquals(jsonChanges, p1.getChanges());
		} catch (ProfileSaveException e) {
			if (e.getErrors() != null && !e.getErrors().isEmpty()) {
				for (ProfilePropertySaveError error : e.getErrors()) {
					System.out.println(error);
				}
			}
		}

	}

	@Test
	public void testApplyMessageChanges() throws IOException {
		String p = IOUtils.toString(this.getClass().getResourceAsStream(
				"/vxuTest/Profile.xml"));
		String v = IOUtils.toString(this.getClass().getResourceAsStream(
				"/vxuTest/ValueSets_all.xml"));
		String c = IOUtils.toString(this.getClass().getResourceAsStream(
				"/vxuTest/Constraints.xml"));
		Profile p1 = new ProfileSerializationImpl().deserializeXMLToProfile(p,
				v, c);

		Message message = p1.getMessages().getChildren()
				.toArray(new Message[] {})[0];
		SegmentRef segmentRef = (SegmentRef) message.getChildren().get(0);
		Group group = (Group) message.getChildren().get(5);
		Segment segment = segmentRef.getRef();
		Field field = segment.getFields().get(0);
		Datatype datatype = p1.getDatatypes().getChildren()
				.toArray(new Datatype[] {})[0];
		String jsonChanges = "{\r\n  \"segmentRef\": {\r\n    \"edit\": [\r\n      {\r\n        \"id\": \""
				+ segmentRef.getId()
				+ "\",\r\n        \"usage\": \"X\",\r\n        \"min\": \"100\",\r\n        \"max\": \"100\"\r\n      }\r\n    ]\r\n  },\r\n  \"message\": {\r\n    \"edit\": [\r\n      {\r\n        \"id\": \""
				+ message.getId()
				+ "\",\r\n        \"identifier\": \"identifier\",\r\n        \"version\": \"version\",\r\n        \"oid\": \"oid\",\r\n        \"date\": \"today\",\r\n        \"comment\": \"comment\"\r\n      }\r\n    ]\r\n  }\r\n,  \"group\": {\r\n    \"edit\": [\r\n      {\r\n        \"id\": \""
				+ message.getChildren().get(5).getId()
				+ "\",\r\n        \"usage\": \"W\"\r\n      }\r\n    ]\r\n  }, \"segment\": {\r\n    \"edit\": [\r\n      {\r\n        \"id\": \""
				+ segment.getId()
				+ "\",\r\n        \"label\": \"label\",\r\n        \"description\": \"desc\",\r\n        \"comment\": \"comment\",\r\n        \"text2\": \"posttest\",\r\n        \"text1\": \"pretest\"\r\n      }\r\n    ]\r\n  },\r\n  \"field\": {\r\n    \"edit\": [\r\n      {\r\n        \"id\": \""
				+ field.getId()
				+ "\",\r\n        \"usage\": \"X\",\r\n        \"min\": \"10\",\r\n        \"max\": \"10\",\r\n        \"minLength\": \"10\",\r\n        \"maxLength\": \"10\",\r\n        \"confLength\": \"10\",\r\n        \"datatype\": {\r\n          \"id\": \""
				+ datatype.getId()
				+ "\"\r\n        }\r\n      }\r\n    ]\r\n  }, " + "}\r\n    ";
		try {
			List<ProfilePropertySaveError> errors = new ProfileChangeService()
					.apply(jsonChanges, p1);
			assertEquals(0, errors.size());
			assertEquals("identifier", message.getIdentifier());
			assertEquals("version", message.getVersion());
			assertEquals("oid", message.getOid());
			assertEquals("today", message.getDate());
			assertEquals("comment", message.getComment());

			assertEquals("X", segmentRef.getUsage().toString());
			assertEquals("100", segmentRef.getMin() + "");

			assertEquals("W", group.getUsage().toString());

			assertEquals("label", segment.getLabel());
			assertEquals("comment", segment.getComment());
			assertEquals("pretest", segment.getText1());
			assertEquals("posttest", segment.getText2());
			assertEquals("desc", segment.getDescription());

			assertEquals(datatype.getId(), field.getDatatype().getId());

			assertEquals("X", field.getUsage().value());
			assertEquals(new Integer(10), field.getMin());
			assertEquals("10", field.getMax());
			assertEquals(new Integer(10), field.getMinLength());
			assertEquals("10", field.getMaxLength());
			assertEquals("10", field.getConfLength());
		} catch (ProfileSaveException e) {
			if (e.getErrors() != null && !e.getErrors().isEmpty()) {
				for (ProfilePropertySaveError error : e.getErrors()) {
					System.out.println(error);
				}
			}
		}

		//
	}
}
