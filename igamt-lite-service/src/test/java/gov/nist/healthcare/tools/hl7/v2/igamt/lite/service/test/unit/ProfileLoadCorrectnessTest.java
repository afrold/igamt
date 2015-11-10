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
import static org.junit.Assert.fail;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Mapping;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Tables;
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
							//							assertEquals((Integer) ((DBObject) fieldObject).get("position"), f.getPosition()); //TODO Check why it fails
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

					BasicDBList dynamicMappingsDBObjects = (BasicDBList) ((DBObject) child).get("dynamicMappings");
					if (dynamicMappingsDBObjects != null) {
						for (Object mapObj : dynamicMappingsDBObjects) {
							DBObject dynObject = (DBObject) ((DBObject) mapObj).get("mappings");
							String position = (String) ((DBObject) dynObject).get("position");
							String reference = (String) ((DBObject) dynObject).get("reference");
							Mapping m = seg.findOneMappingByPositionAndByReference(Integer.valueOf(position), Integer.valueOf(reference));
							assertNotNull(m); //TODO We should iterate through the cases...
						}
					}
				}		
				//TODO Check constraints are loaded 
			}
		}
	}

	@Test 
	public void testDatatypes() {
		DBObject datatypesJson = (DBObject) jsonObject.get("datatypes");
		BasicDBList datatypesDBObjects = (BasicDBList) datatypesJson.get("children");
		if (datatypesDBObjects != null) {
			for (Object childObj : datatypesDBObjects) {
				DBObject child = (DBObject) childObj;
				String label = (String) ((DBObject) child).get("label");
				Datatype dt = p.getDatatypes().findOneDatatypeByLabel(label);

				if (dt == null){
					fail("Datatype not found");
				} else {

					assertEquals((String) ((DBObject) child).get("type"), dt.getType());
					assertEquals((String) ((DBObject) child).get("name"), dt.getName());
					assertEquals((String) ((DBObject) child).get("description"), dt.getDescription());
					assertEquals((String) ((DBObject) child).get("comment"), dt.getComment());
					assertEquals((String) ((DBObject) child).get("hl7Version"), dt.getHl7Version());
					assertEquals((String) ((DBObject) child).get("usageNote"), dt.getUsageNote());

					BasicDBList componentsObjects = (BasicDBList) ((DBObject) child).get("components");
					if (componentsObjects != null) {
						for (Object componentObject : componentsObjects) {
							String id = (String) ((DBObject) componentObject).get("id");
							Component c = p.getDatatypes().findOneComponent(id, dt);

							assertEquals((String) ((DBObject) componentObject).get("type"), c.getType());
							assertEquals((String) ((DBObject) componentObject).get("name"), c.getName());
							assertEquals((String) ((DBObject) componentObject).get("comment"), c.getComment());
							assertEquals((Integer) ((DBObject) componentObject).get("minLength"), c.getMinLength());
							assertEquals((String) ((DBObject) componentObject).get("maxLength"), c.getMaxLength());
							assertEquals((String) ((DBObject) componentObject).get("confLength"), c.getConfLength());
							assertEquals((Integer) ((DBObject) componentObject).get("position"), c.getPosition()); 
							assertEquals((String) ((DBObject) componentObject).get("table"), c.getTable());
							assertEquals(Usage.valueOf((String) ((DBObject) componentObject).get("usage")), c.getUsage());
							assertEquals((String) ((DBObject) componentObject).get("bindingLocation"), c.getBindingLocation());
							assertEquals((String) ((DBObject) componentObject).get("bindingStrength"), c.getBindingStrength());
							assertEquals((String) ((DBObject) componentObject).get("text"), c.getText());
							assertEquals((String) ((DBObject) componentObject).get("datatype"), c.getDatatype());

						}
					}
					//TODO Check constraints are loaded
				}
			}
		}
	}

	@Test 
	public void testTables() {

		DBObject tablesJson = (DBObject) jsonObject.get("tables");
		Tables vsd = p.getTables();

		assertEquals((String) ((DBObject) tablesJson).get("valueSetLibraryIdentifier"), vsd.getValueSetLibraryIdentifier());
		assertEquals((String) ((DBObject) tablesJson).get("valueSetLibraryVersion"), vsd.getValueSetLibraryVersion());
		assertEquals((String) ((DBObject) tablesJson).get("valueSetLibraryStatus"), vsd.getStatus());
		assertEquals((String) ((DBObject) tablesJson).get("organizationName"), vsd.getOrganizationName());
		assertEquals((String) ((DBObject) tablesJson).get("Description"), vsd.getDescription());
		assertEquals((String) ((DBObject) tablesJson).get("Name"), vsd.getName());
		assertEquals((String) ((DBObject) tablesJson).get("dateCreated"), vsd.getDateCreated());

		BasicDBList valueSetsDBObjects = (BasicDBList) tablesJson.get("children");
		if (valueSetsDBObjects != null) {
			for (Object childObj : valueSetsDBObjects) {
				DBObject child = (DBObject) childObj;
				String name = (String) ((DBObject) child).get("name");
				Table vs = p.getTables().findOneTableByName(name);

				if (vs == null){
					fail("ValueSet not found");
				} else {
					assertEquals((String) ((DBObject) child).get("type"), vs.getType());
					assertEquals((String) ((DBObject) child).get("name"), vs.getName());
					assertEquals((String) ((DBObject) child).get("description"), vs.getDescription());
					assertEquals((String) ((DBObject) child).get("bindingIdentifier"), vs.getBindingIdentifier());
					assertEquals((String) ((DBObject) child).get("description"), vs.getDescription());
					assertEquals((String) ((DBObject) child).get("version"), vs.getVersion());
					assertEquals((String) ((DBObject) child).get("oid"), vs.getOid());
					assertEquals((String) ((DBObject) child).get("stability"), vs.getStability());
					assertEquals((String) ((DBObject) child).get("extensibility"), vs.getExtensibility());
					assertEquals((String) ((DBObject) child).get("contentDefinition"), vs.getContentDefinition());
					assertEquals((Integer) ((DBObject) child).get("group"), vs.getGroup());
//					assertEquals((Integer) ((DBObject) child).get("order"), vs.getOrder());

					BasicDBList codesObjects = (BasicDBList) ((DBObject) child).get("codes");
					if (codesObjects != null) {
						for (Object codeObject : codesObjects) {
							DBObject codeElm = (DBObject) codeObject;

							String id = (String) ((DBObject) codeElm).get("id");
							Code c = p.getTables().findOneCodeById(id);

							assertEquals((String) ((DBObject) codeElm).get("type"), c.getType());
							assertEquals((String) ((DBObject) codeElm).get("comment"), c.getComments());
							assertEquals((String) ((DBObject) codeElm).get("value"), c.getValue());
							assertEquals((String) ((DBObject) codeElm).get("codeSystem"), c.getCodeSystem());
							assertEquals((String) ((DBObject) codeElm).get("codeSystemVersion"), c.getCodeSystemVersion());
							assertEquals((String) ((DBObject) codeElm).get("codeUsage"), c.getCodeUsage());
							assertEquals((String) ((DBObject) codeElm).get("label"), c.getLabel());
						}
					}
				}
			}
		}
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
