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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.ComponentWriteConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.FieldWriteConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.ProfileReadConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.SegmentRefWriteConverter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.fakemongo.Fongo;
import com.jayway.jsonpath.JsonPath;
import com.mongodb.Mongo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ProfileLoadingTest {    

	@Autowired
	ProfileRepository profileRepository;

	@Autowired
	ProfileService profileService;

	@Before
	public void setUp() throws Exception {
		try {
			Properties p = new Properties();
			InputStream log4jFile = ProfileLoadingTest.class
					.getResourceAsStream("/igl-test-log4j.properties");
			p.load(log4jFile);
			PropertyConfigurator.configure(p);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@After
	public void tearDown() throws Exception {

	}


	@Test
	public void testStandardProfilesLoaded() {
		for (String hl7Version : Arrays.asList("2.3","2.3.1","2.4","2.5","2.5.1","2.6","2.7")){
			assertEquals(1, profileRepository.findByScopeAndMetaData_Hl7Version(IGDocumentScope.HL7STANDARD, hl7Version).size());
		}
	}

	@Test
	public void testDataPreloaded() throws IOException {		
		String profileJson;
		Profile profileSaved;

		for (String hl7Version : Arrays.asList("2.3","2.3.1","2.4","2.5","2.5.1","2.6","2.7")){
			profileSaved = profileRepository.findByScopeAndMetaData_Hl7Version(IGDocumentScope.HL7STANDARD, hl7Version).get(0);

			profileJson = IOUtils.toString(this.getClass().getClassLoader().getResource("profiles/profile-" + hl7Version + ".json"));

			String text = JsonPath.read(profileJson, "$.type");
			assertEquals("profile", text);
			
			List<Object> msgs  = JsonPath.read(profileJson, "$.messages.children");
			assertEquals(msgs.size(), profileSaved.getMessages().getChildren().size());
			
			List<Object> sgts  = JsonPath.read(profileJson, "$.segments.children");
			assertEquals(sgts.size(), profileSaved.getSegments().getChildren().size());			
			
			List<Object> dts  = JsonPath.read(profileJson, "$.datatypes.children");
			assertEquals(dts.size(), profileSaved.getDatatypes().getChildren().size());			

			List<Object> vsl  = JsonPath.read(profileJson, "$.tables.children");
			assertEquals(vsl.size(), profileSaved.getTables().getChildren().size());

		}

	}


	@Configuration
	@EnableMongoRepositories(basePackages = "gov.nist.healthcare.tools")
	@ComponentScan(basePackages = "gov.nist.healthcare.tools")
	static class ProfileTestConfiguration extends AbstractMongoConfiguration {

		@Override
		public Mongo mongo() {
			return new Fongo("igl_test").getMongo();
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
			return "igl_test";
		}

		@Override
		public String getMappingBasePackage() {
			return "gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain";
		}
	}


}
