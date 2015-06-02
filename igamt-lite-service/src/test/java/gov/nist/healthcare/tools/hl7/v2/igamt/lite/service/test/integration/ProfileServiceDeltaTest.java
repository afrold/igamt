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

import static org.junit.Assert.*;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.HL7Version;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SchemaVersion;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileDiffImpl;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerialization4ExportImpl;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerializationImpl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
public class ProfileServiceDeltaTest {

	@Autowired
	ProfileService service;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@BeforeClass
	public static void setup() {
		try {
			Properties p = new Properties();
			InputStream log4jFile = ProfileServiceDeltaTest.class
					.getResourceAsStream("/igl-test-log4j.properties");
			p.load(log4jFile);
			PropertyConfigurator.configure(p);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testDelta() throws Exception {
		ProfileSerialization4ExportImpl test1 = new ProfileSerialization4ExportImpl();

		Profile p1 = test1.deserializeXMLToProfile(
				new String(Files.readAllBytes(Paths
						.get("src//main//resources//vxu//Profile.xml"))),
						new String(Files.readAllBytes(Paths
								.get("src//main//resources//vxu//ValueSets_all.xml"))),
								new String(Files.readAllBytes(Paths
										.get("src//main//resources//vxu//Constraints.xml"))));

		ProfileMetaData metaData = p1.getMetaData();
		DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
		Date date = new Date();
		metaData.setDate(dateFormat.format(date));
		metaData.setName("IZ_VXU");
		metaData.setOrgName("NIST");
		metaData.setSubTitle("Specifications");
		metaData.setVersion("1.0");

		metaData.setHl7Version(HL7Version.V2_7.value());
		metaData.setSchemaVersion(SchemaVersion.V1_0.value());
		metaData.setStatus("Draft");

		p1.setMetaData(metaData);

		Profile p2 = p1.clone();
		p1.setId("1");
		p2.setId("2");


		Message message = p2.getMessages().getChildren()
				.toArray(new Message[] {})[0];
		SegmentRef segmentRef = (SegmentRef) message.getChildren().get(0);
		Group group = (Group) message.getChildren().get(5);
		Segment segment = p2.getSegments().findOne(segmentRef.getRef());
		Field field = segment.getFields().get(0);
		Datatype datatype = p2.getDatatypes().getChildren()
				.toArray(new Datatype[] {})[0];

		//Fake addition
		SegmentRef segmentRef3 = (SegmentRef) message.getChildren().get(2);
		Segment segment3 = p1.getSegments().findOne(segmentRef3.getRef());
		p1.getSegments().delete(segment3.getId());


		segmentRef.setMin(3);
		segmentRef.setMax("94969");
		field.setComment("wawa");
		field.setName("new field name-illegal change");
		group.setMax("*");
		group.setComment("new group comment");
		p2.getMetaData().setName(new String("IZ_VXU_X"));
		datatype.setComment("new dt comment");
		segment.setComment("<h2>Tqqqqqqqq</h2>");
		segment.setText1("<h2>Test format!</h2><p>textAngular WYSIWYG Text Editor</p><p><b>Features:</b></p><ol><li>Two-Way-Binding</li><li style=\"color: ;\"><b>Theming</b> Options</li><li>Simple Editor Instance Creation</li></ol><p><b>Link test:</b> <a href=\"https://github.com/fraywing/textAngular\">Here</a> </p>");

		ProfileDiffImpl cmp = new  ProfileDiffImpl();
		cmp.compare(p1, p2);
		
		assertEquals(40, cmp.getFieldsChanges().size());
		assertEquals(2, cmp.getSegmentsChanges().size());
		assertEquals(1, cmp.getDatatypesChanges().size());
	}

}
