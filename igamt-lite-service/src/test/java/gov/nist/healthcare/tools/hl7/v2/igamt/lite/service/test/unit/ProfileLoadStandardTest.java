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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileExportService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.ComponentWriteConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.FieldWriteConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.ProfileReadConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.SegmentRefWriteConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.VerificationService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.integration.PersistenceContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
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
import com.jayway.jsonpath.JsonPath;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ProfileLoadStandardTest {   

	@Autowired
	private Environment env;

	@Autowired
	ProfileRepository profileRepository;

	@Autowired
	ProfileService profileService;

	@Autowired 
	VerificationService verificationService;

	@Autowired
	ProfileExportService profileExport;

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

		ApplicationContext ctx = new AnnotationConfigApplicationContext(PersistenceContext.class);
		MongoClient mongo = (MongoClient)ctx.getBean("mongo");
		DB db = mongo.getDB(env.getProperty("mongo.dbname"));
		DBCollection collection = db.getCollection("profile");

		for (String hl7version : Arrays.asList("2.1", "2.2", "2.3", "2.3.1", "2.4", "2.5", "2.5.1", "2.6", "2.7")){
			if (profileRepository.findByScopeAndMetaData_Hl7Version(ProfileScope.HL7STANDARD, hl7version).isEmpty()){
				try {
					String profileJson = IOUtils.toString(this.getClass().getClassLoader().getResource("profiles/profile-"+hl7version+".json"));
					DBObject dbObject = (DBObject) JSON.parse(profileJson);
					collection.save(dbObject);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@After
	public void tearDown() throws Exception {
		// Clear all data in DB
		//		profileRepository.deleteAll();
	}


	@Test
	public void testStandardProfilesLoaded0() throws CloneNotSupportedException, IOException {
		//		Test if there is at least one profile saved
		for (String hl7version : Arrays.asList("2.1","2.2","2.3","2.3.1","2.4","2.5","2.5.1","2.6","2.7")){
			assertEquals(true, profileRepository.findStandardByVersion(hl7version).size() >= 1);
		}

		//Select profile
		String hl7version = "2.7";
		Profile p = profileRepository.findByScopeAndMetaData_Hl7Version(ProfileScope.HL7STANDARD, hl7version).get(0);

		InputStream content = null;
		// Print one programatically with iText
		content = profileExport.exportAsPdf(p);
		try {
			writeStream(content);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error while writing stream");
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.springframework.data.mongodb.config.AbstractMongoConfiguration#
		 * getDatabaseName()
		 */
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
