package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;

public class DatatypeLibraryReadConverterTest {

	private static final Logger log = LoggerFactory.getLogger(DatatypeLibraryReadConverterTest.class);
	
	static MongoOperations mongoOps;
	static DBCollection coll;
	static DBCollection coll1;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		mongoOps = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), "igl"));
		coll = mongoOps.getCollection("datatype-library");
		coll1 = mongoOps.getCollection("datatype");
	}
	
	@Test
	public void testConvert() {
		DatatypeLibraryReadConverter cnv = new DatatypeLibraryReadConverter();
		DBCursor cur = coll.find();
		while(cur.hasNext()) {
			DBObject source = cur.next();
			DatatypeLibrary sut = cnv.convert(source);
			assertNotNull(sut);
			assertTrue(0 < sut.getChildren().size());
		}
	}
}
