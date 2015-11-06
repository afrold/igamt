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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileExportService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.PropertyConfigurator;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mongodb.BasicDBList;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceContextUnit.class})
public class ProfileLoadCorrectnessTest {  
	Logger logger = LoggerFactory.getLogger( ProfileLoadCorrectnessTest.class ); 

	@Autowired
	private Environment env;

	@Autowired
	ApplicationContext ctx;

	@Autowired
	ProfileRepository profileRepository;

	@Autowired
	ProfileService profileService;

	@Autowired
	ProfileExportService profileExport;

	Profile p;

	DBObject jsonObject;

	@Before
	public void setUp() throws Exception {
		try {
			Properties p = new Properties();
			InputStream log4jFile = ProfileLoadCorrectnessTest.class
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

		//Retrieve profile version 2.5.1
		String hl7version = "2.5.1";
		p = profileRepository.findByScopeAndMetaData_Hl7Version(ProfileScope.HL7STANDARD, hl7version).get(0);

		String profileJson = IOUtils.toString(this.getClass().getClassLoader().getResource("profilesStandardJson/profile-"+hl7version+".json"));
		logger.debug("%%% "+this.getClass().getClassLoader().getResource("profilesStandardJson/profile-"+hl7version+".json").getPath());
		jsonObject = (DBObject) JSON.parse(profileJson);
	}

	@After
	public void tearDown() throws Exception {
		//		 Clear all data in DB
		profileRepository.deleteAll();
	}

	@Rule
	public TestWatcher testWatcher = new TestWatcher() {
		protected void failed(Throwable e, Description description) {
			logger.debug(description.getDisplayName() + " failed " + e.getMessage());
			super.failed(e, description);
		}
	};


	@Test
	public void testProfileInfo() {
		assertEquals((String) jsonObject.get("comment"), p.getComment());
		assertEquals((String) jsonObject.get("type"), p.getType());
		assertEquals((String) jsonObject.get("usageNote"), p.getUsageNote());
		assertEquals(ProfileScope.valueOf((String) jsonObject.get("scope")), p.getScope());
		assertEquals((String) jsonObject.get("changes"), p.getChanges());
		assertEquals(readLong(jsonObject, "accountId"), p.getAccountId());
		assertEquals(jsonObject.get("baseId"), p.getBaseId());
		assertEquals(jsonObject.get("sourceId"), p.getSourceId());
	}

	@Test
	public void testMetaData() {
		DBObject source = (DBObject) jsonObject.get("metaData");
		ProfileMetaData metaDataObj = p.getMetaData();
		assertEquals((String) source.get("name"), metaDataObj.getName());
		assertEquals((String) source.get("identifier"), metaDataObj.getIdentifier());
		assertEquals((String) source.get("orgName"), metaDataObj.getOrgName());
		assertEquals((String) source.get("status"), metaDataObj.getStatus());
		assertEquals((String) source.get("topics"), metaDataObj.getTopics());
		assertEquals((String) source.get("type"), metaDataObj.getType());
		assertEquals((String) source.get("hl7Version"), metaDataObj.getHl7Version());
		assertEquals((String) source.get("schemaVersion"), metaDataObj.getSchemaVersion());
		assertEquals((String) source.get("subTitle"), metaDataObj.getSubTitle());
		assertEquals((String) source.get("version"), metaDataObj.getVersion());
		assertEquals((String) source.get("date"), metaDataObj.getDate());
		assertEquals((String) source.get("ext"), metaDataObj.getExt());

		Object encodingObj = source.get("encodings");
		BasicDBList encodingDBObjects = (BasicDBList) encodingObj;
		Iterator<Object> it = encodingDBObjects.iterator();
		while (it.hasNext()) {
			assertTrue(metaDataObj.getEncodings().contains((String) it.next()));
		}
	}

	@Test 
	public void testSegments() {
		DBObject segmentsJson = (DBObject) jsonObject.get("segments");
		BasicDBList segmentsDBObjects = (BasicDBList) segmentsJson.get("children");
		if (segmentsDBObjects != null) {
			for (Object child : segmentsDBObjects) {
				String label = (String) ((DBObject) child).get("label");
				Segment seg = p.getSegments().findOneSegmentByLabel(label);
				if (seg == null){
					fail("Segment not found");
				} else {
					assertEquals((String) ((DBObject) child).get("type"), seg.getType());
					assertEquals((String) ((DBObject) child).get("name"), seg.getName());
					assertEquals((String) ((DBObject) child).get("description"), seg.getDescription());
					assertEquals((String) ((DBObject) child).get("comment"), seg.getComment());
					assertEquals((String) ((DBObject) child).get("text1"), seg.getText1());
					assertEquals((String) ((DBObject) child).get("text2"), seg.getText2());

					BasicDBList fieldObjects = (BasicDBList) ((DBObject) child).get("fields");
					if (fieldObjects != null) {
						for (Object fieldObject : fieldObjects) {
							String name = (String) ((DBObject) fieldObject).get("name");
							Field f = seg.findOneFieldByName(name);
//							assertEquals(readMongoId((DBObject) fieldObject), f.getId());
							assertEquals((String) ((DBObject) fieldObject).get("type"), f.getType());
							assertEquals((String) ((DBObject) fieldObject).get("name"), f.getName());
							assertEquals((String) ((DBObject) fieldObject).get("comment"), f.getComment());
							assertEquals((Integer) ((DBObject) fieldObject).get("minLength"), f.getMinLength());
							assertEquals((String) ((DBObject) fieldObject).get("maxLength"), f.getMaxLength());
							assertEquals((String) ((DBObject) fieldObject).get("confLength"), f.getConfLength());
//							assertEquals((Integer) ((DBObject) fieldObject).get("position"), f.getPosition()); TODO Check why it fails
							assertEquals((String) ((DBObject) fieldObject).get("table"), f.getTable());
							assertEquals(Usage.valueOf((String) ((DBObject) fieldObject).get("usage")), f.getUsage());
							assertEquals((String) ((DBObject) fieldObject).get("bindingLocation"), f.getBindingLocation());
							assertEquals((String) ((DBObject) fieldObject).get("bindingStrength"), f.getBindingStrength());
							assertEquals((String) ((DBObject) fieldObject).get("itemNo"), f.getItemNo());
							assertEquals((Integer) ((DBObject) fieldObject).get("min"), f.getMin());
							assertEquals((String) ((DBObject) fieldObject).get("max"), f.getMax());
							assertEquals((String) ((DBObject) fieldObject).get("text"), f.getText());
							assertEquals((String) ((DBObject) fieldObject).get("datatype"), f.getDatatype());
						}	
					}
				}

				// Check constraints TODO

				//		BasicDBList confStsObjects = (BasicDBList) source
				//				.get("conformanceStatements");
				//		if (confStsObjects != null) {
				//			List<ConformanceStatement> confStatements = new ArrayList<ConformanceStatement>();
				//			for (Object confStObject : confStsObjects) {
				//				ConformanceStatement cs = conformanceStatement((DBObject) confStObject);
				//				confStatements.add(cs);
				//			}
				//			seg.setConformanceStatements(confStatements);
				//		}
				//
				//		BasicDBList predDBObjects = (BasicDBList) source.get("predicates");
				//		if (predDBObjects != null) {
				//			List<Predicate> predicates = new ArrayList<Predicate>();
				//			for (Object predObj : predDBObjects) {
				//				DBObject predObject = (DBObject) predObj;
				//				Predicate pred = predicate(predObject);
				//				predicates.add(pred);
				//			}
				//			seg.setPredicates(predicates);
				//		}

				// Check dynamic mapping TODO
				//		BasicDBList dynamicMappingsDBObjects = (BasicDBList) source
				//				.get("dynamicMappings");
				//		if (dynamicMappingsDBObjects != null) {
				//			List<DynamicMapping> dynamicMappings = new ArrayList<DynamicMapping>();
				//			for (Object dynObj : dynamicMappingsDBObjects) {
				//				DBObject dynObject = (DBObject) dynObj;
				//				DynamicMapping dyn = dynamicMapping(dynObject, datatypes);
				//				dynamicMappings.add(dyn);
				//			}
				//			seg.setDynamicMappings(dynamicMappings);
				//		}


			}
		}
	}
	
	@Ignore
	@Test 
	public void testDatatypes() {
		//TODO
	}

	@Ignore
	@Test 
	public void testTables() {
		//TODO		
	}

	@Ignore
	@Test 
	public void testMessages() {
		//TODO		
	}


	private String readMongoId(DBObject source){
		if ( source.get("_id") != null){
			if (source.get("_id") instanceof ObjectId){
				return ((ObjectId) source.get("_id")).toString();
			} else {
				return (String) source.get("_id");
			}
		} else if ( source.get("id") != null){
			if (source.get("id") instanceof ObjectId){
				return ((ObjectId) source.get("id")).toString();
			} else {
				return (String) source.get("id");
			}
		}
		return null;
	}

	private Long readLong(DBObject source, String tag){
		if ( source.get(tag) != null){
			if (source.get(tag) instanceof Integer){
				return Long.valueOf((Integer) source.get(tag));
			} else if (source.get(tag) instanceof String) {
				return Long.valueOf((String)source.get(tag));
			}
		}
		return Long.valueOf(0);
	}


}
