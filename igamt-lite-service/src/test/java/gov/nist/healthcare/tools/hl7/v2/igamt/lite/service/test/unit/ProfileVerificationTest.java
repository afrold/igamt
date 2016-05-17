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

import static org.junit.Assert.*;

import gov.nist.healthcare.tools.hl7.v2.igamt.hl7tools2lite.converter.ProfileReadConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatypes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ElementVerification;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segments;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Tables;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileExportService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.ComponentWriteConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.FieldWriteConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.SegmentRefWriteConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.VerificationService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
@ContextConfiguration(classes = {PersistenceContextUnit.class})
public class ProfileVerificationTest {   
	Logger logger = LoggerFactory.getLogger( ProfileVerificationTest.class ); 

	@Autowired
	private Environment env;

	@Resource
	ApplicationContext ctx;

	@Autowired
	ProfileRepository profileRepository;

	@Autowired
	ProfileService profileService;

	@Autowired 
	VerificationService verificationService;

	@Autowired
	ProfileExportService profileExport;

	Profile pSrc, pWrk;
	String referenceProfile = "561c7ffbef869a3dfafccc4a"; //VXU V04 Implementation guide

	@Before
	public void setUp() throws Exception {
		try {
			Properties p = new Properties();
			InputStream log4jFile = ProfileVerificationTest.class
					.getResourceAsStream("/igl-test-log4j.properties");
			p.load(log4jFile);
			PropertyConfigurator.configure(p);

		} catch (IOException e) {
			e.printStackTrace();
		}

		MongoClient mongo = (MongoClient)ctx.getBean("mongo");
		DB db = mongo.getDB(env.getProperty("mongo.dbname"));
		DBCollection collection = db.getCollection("profile");

		if (profileRepository.findOne(referenceProfile) == null){

			String profileJson = IOUtils.toString(this.getClass().getClassLoader().getResource("profileUserTest/profile_test.json"));
			DBObject dbObject = (DBObject) JSON.parse(profileJson);
			collection.save(dbObject);
		}

		this.pSrc = profileRepository.findOne(referenceProfile);
		this.pWrk = profileService.clone(pSrc);

	}

	@After
	public void tearDown() throws Exception {
		//		profileRepository.delete(profileRepository.findOne(referenceProfile));
	}

	@Test
	public void testVerifyDatatypes() throws CloneNotSupportedException, IOException {

	assertTrue(true);
	}

	@Test
	public void testVerifySegments() throws CloneNotSupportedException, IOException {
		assertTrue(true);
	}

	//	@Test
	public void testVerifyTables() throws CloneNotSupportedException, IOException {
		assertTrue(true);
	}


}
