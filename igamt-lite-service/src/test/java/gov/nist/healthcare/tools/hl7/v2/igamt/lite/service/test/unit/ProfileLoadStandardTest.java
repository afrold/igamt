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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatypes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segments;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Tables;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileExportService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.ComponentWriteConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.FieldWriteConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.ProfileReadConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.SegmentRefWriteConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.VerificationService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.fakemongo.Fongo;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
//@ContextConfiguration(classes = {PersistenceContext.class})
public class ProfileLoadStandardTest {  
	Logger logger = LoggerFactory.getLogger( ProfileLoadStandardTest.class ); 

	@Autowired
	private Environment env;

	@Autowired
	ApplicationContext ctx;

	@Autowired
	ProfileRepository profileRepository;

	@Autowired
	ProfileService profileService;

	@Autowired 
	VerificationService verificationService;

	@Autowired
	ProfileExportService profileExport;

	Profile profile251;

	@Before
	public void setUp() throws Exception {
		try {
			Properties p = new Properties();
			InputStream log4jFile = ProfileLoadStandardTest.class
					.getResourceAsStream("/igl-test-log4j.properties");
			p.load(log4jFile);
			PropertyConfigurator.configure(p);

		} catch (IOException e) {
			e.printStackTrace();
		}

		//Load all the standard profiles
		MongoClient mongo = (MongoClient)ctx.getBean("mongo");
		DB db = mongo.getDB(env.getProperty("mongo.dbname"));
		DBCollection collection = db.getCollection("profile");

		for (String hl7version : Arrays.asList("2.1", "2.2", "2.3", "2.3.1", "2.4", "2.5", "2.5.1", "2.6", "2.7")){
			if (profileRepository.findByScopeAndMetaData_Hl7Version(ProfileScope.HL7STANDARD, hl7version).isEmpty()){
				{
					logger.debug("Profile " + hl7version + " not found");
					try {
						String profileJson = IOUtils.toString(this.getClass().getClassLoader().getResource("profilesStandardJson/profile-"+hl7version+".json"));
						logger.debug("%%% "+this.getClass().getClassLoader().getResource("profilesStandardJson/profile-"+hl7version+".json").getPath());
						DBObject dbObject = (DBObject) JSON.parse(profileJson);
						collection.save(dbObject);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		//				//      //Snipnet used when json are built from mongoexport
		//				//		List<String> profilesjson = IOUtils.readLines(this.getClass().getClassLoader().getResourceAsStream("profilesStandardJson/profile-export.json"));
		//				//		for (String pp:profilesjson){
		//				//			DBObject dbObject = (DBObject) JSON.parse(pp);
		//				//			collection.save(dbObject);
		//				//		}

		//Retrieve profile version 2.5.1
		String hl7version = "2.5.1";
		profile251 = profileRepository.findByScopeAndMetaData_Hl7Version(ProfileScope.HL7STANDARD, hl7version).get(0);

	}

	@After
	public void tearDown() throws Exception {
		// Clear all data in DB
		//		profileRepository.deleteAll();
	}

	@Rule
	public TestWatcher testWatcher = new TestWatcher() {
		protected void failed(Throwable e, Description description) {
			logger.debug(description.getDisplayName() + " failed " + e.getMessage());
			super.failed(e, description);
		}
	};


	@Test
	public void testStandardPresence() {
		//		Test if there is at least one profile saved
		logger.info("Testing standard presence\n");
		for (String hl7version : Arrays.asList("2.1","2.2","2.3","2.3.1","2.4","2.5","2.5.1","2.6","2.7")){
			assertTrue( profileRepository.findStandardByVersion(hl7version).size() >= 1);
		}
	}

	@Test
	public void testMessageContent() {
		//Test ADT_A03 Discharge/End visit (Event A03) (chap 03 p.6)

		assertTrue(false); //FIXME write testMessageContent

		Messages msgs = profile251.getMessages();
		Segments sgts = profile251.getSegments();

		Message m = msgs.findOneByStrucId("A03");
		assertNotNull(m);

		int i = 0;
		for (Object sg : m.getChildren().toArray()){
			if (sg instanceof SegmentRef)
				logger.debug("--------> " + String.valueOf(i) + " " + sgts.findOneSegmentById(((SegmentRef) sg).getRef()) + ((SegmentRef) sg).toString());
			if (sg instanceof Group)	
				logger.debug("--------> " + String.valueOf(i) + " " + ((Group) sg).toString());

			i += 1;
		}

	}


	@Test
	public void testSegmentContent() {
		//Test EVN - Event Type Segment in version 2.5.1 (chap 03 p.69)
		logger.debug("Testing EVN segment");

		Segment sgt = profile251.getSegments().findOneSegmentByName("EVN");

		assertNotNull(sgt);
		assertEquals("event type", sgt.getDescription().toLowerCase());
		assertEquals(7, sgt.getFields().size());

		Field f1 = sgt.getFields().get(0);
		assertEquals( "-1" , String.valueOf(f1.getMinLength()));
		assertEquals("3", f1.getMaxLength());
		Datatype dt = profile251.getDatatypes().findOneDatatypeByBase("ID");
		assertEquals(dt.getName(), f1.getDatatype());
		assertEquals(Usage.B, f1.getUsage());
		assertEquals("0",String.valueOf(f1.getMin()));
		assertEquals("1", f1.getMax());

		Table t = profile251.getTables().findOneTableByName("0003");
		assertEquals(t.getId(), f1.getTable());
		assertEquals("00099", f1.getItemNo());
		assertEquals("event type code", f1.getName().toLowerCase());

		Field f2 = sgt.getFields().get(4);
		assertEquals("*", f2.getMax());

	}

	@Test
	public void testDatatypeContent() {
		// Test datatype Coded With Exceptions CWE in version 2.5.1 (chap 02A p.17)
		logger.debug("Testing CWE datatype");

		//Retrieve datatype library
		Datatypes dtps = profile251.getDatatypes();

		//Checking datatype info
		Datatype dt = dtps.findOneDatatypeByLabel("CWE"); //For standard data types, label and name are not flavored yet and therefore equal)
		assertNotNull(dt);
		assertEquals("CWE", dt.getName());
		assertEquals("coded with exceptions", dt.getDescription().toLowerCase());
		assertEquals(9, dt.getComponents().size());

		//Checking component
		Component cp = dt.getComponents().get(2);
		assertEquals("name of coding system", cp.getName().toLowerCase());
		assertEquals("20", cp.getMaxLength());
		assertEquals(dtps.findOneDatatypeByBase("ID").getId(), cp.getDatatype());
		Table t = profile251.getTables().findOneTableByName("0396");
		assertNotNull(t);
		assertEquals(t.getName(), cp.getTable()); 

	}

	@Test
	public void testValueSetsContent() {
		// Test value set administrative sex in version 2.5.1
		logger.debug("Testing Administrative sex datatype");

		// Retrieve value set library
		Tables tbls = profile251.getTables();
		//Retrieve value set
		Table tbl = tbls.findOneTableByName("0001");
		//Check value set definition
		assertEquals("Administrative Sex", tbl.getDescription());
		assertEquals("2.16.840.1.113883.12.1", tbl.getOid());
		assertEquals("Static", tbl.getStability());
		assertEquals("Closed", tbl.getExtensibility());
		assertEquals("Extensional", tbl.getContentDefinition());
		//Check set content
		assertEquals(6, tbl.getCodes().size());
		for (String value: Arrays.asList("A" ,"F" ,"M" ,"N" ,"O" ,"U")){
			assertNotNull(tbl.findOneCodeByValue(value));
		}
		Code c = tbl.findOneCodeByValue("A");
		assertEquals("A", c.getValue());
		//		assertEquals("Ambiguous", c.getLabel()); FIXME  (remove when new json profiles are pushed)
		assertEquals("O", c.getCodeUsage());
		assertEquals("0001", c.getCodeSystem());
		//		assertEquals("2.5.1", c.getCodeSystemVersion()); FIXME (remove when new json profiles are pushed)
	}

	//	@Test
	public void printStandard() throws CloneNotSupportedException, IOException {

		//Select profile
		String hl7version = "2.5.1";
		Profile p = profileRepository.findByScopeAndMetaData_Hl7Version(ProfileScope.HL7STANDARD, hl7version).get(0);

		InputStream content = null;

		// Print one programmatically with iText
		content = profileExport.exportAsPdf(p);
		try {
			writeStream(content);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("Error while writing stream");
		}

		//		// Print one using XSL stylesheet
		//		String inlineConstraints = "true";
		//		content = profileExport.exportAsPdfFromXsl(p, inlineConstraints);
		//
		//		try {
		//			writeStream(content);
		//		} catch (Exception e) {
		//			e.printStackTrace();
		//			System.out.println("Error while writing stream");
		//		}

	}

	private void writeStream(InputStream content) throws IOException{
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File tmpPdfFile = new File("Profile_"+timeStamp+".pdf");
		OutputStream out=new FileOutputStream(tmpPdfFile);
		byte buf[]=new byte[1024];
		int len;
		while((len=content.read(buf))>0)
			out.write(buf,0,len);
		out.close();
	}


	@Configuration
	@EnableMongoRepositories(basePackages = "gov.nist.healthcare.tools")
	@ComponentScan(basePackages = "gov.nist.healthcare.tools")
	static class ProfileTestConfiguration extends AbstractMongoConfiguration {

		@Override
		public Mongo mongo() {
			return new Fongo("igl").getMongo();
		}

		@Override
		@Bean
		public CustomConversions customConversions() {
			List<Converter<?, ?>> converterList = new ArrayList<Converter<?, ?>>();
			converterList.add(new FieldWriteConverter());
			converterList.add(new ComponentWriteConverter());
			converterList.add(new SegmentRefWriteConverter());
			converterList.add(new ProfileReadConverter());
			return new CustomConversions(converterList);
		}

		@Override
		protected String getDatabaseName() {
			return "igl";
		}

		@Override
		public String getMappingBasePackage() {
			return "gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain";
		}
	}


}
