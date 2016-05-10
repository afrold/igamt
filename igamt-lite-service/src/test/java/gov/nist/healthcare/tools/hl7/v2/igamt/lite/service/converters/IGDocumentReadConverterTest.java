package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters;

import static org.junit.Assert.assertNotNull;

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

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;

public class IGDocumentReadConverterTest {

	private static final Logger log = LoggerFactory.getLogger(IGDocumentReadConverterTest.class);

	static MongoOperations mongoOps;
	static DBCollection coll;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		mongoOps = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), "igl"));
		coll = mongoOps.getCollection("igdocument");
	}

	@Test
	public void testConvert() {
		IGDocumentReadConverter cnv = new IGDocumentReadConverter();
		DBCursor cur = coll.find();
		while (cur.hasNext()) {
			DBObject source = cur.next();
			IGDocument sut = cnv.convert(source);
			log.info("converted version=" + sut.getMetaData().getHl7Version());
			assertNotNull(sut);
		}
	}

}
