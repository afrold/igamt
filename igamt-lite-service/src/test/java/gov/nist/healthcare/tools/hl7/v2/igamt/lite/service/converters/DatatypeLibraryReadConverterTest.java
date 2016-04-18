package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.mongodb.MongoClient;

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
//			DatatypeLibrary sut = cnv.convert(source);
			BasicDBList list = (BasicDBList)source.get("children");
			int count = cur.count();
			int countL = list.size();
			BasicDBObject qry = new BasicDBObject();
			List<BasicDBObject> where = new ArrayList<BasicDBObject>();
			Object id = source.get("_ID");
			where.add(new BasicDBObject("libId", id));
			qry.put("$and", where);
			DBCursor cur1 = coll1.find(qry);
			log.info("id=" + id + " cnt=" + cur1.count());
			for (Object obj : list) {
				Object id1 = ((DBRef) obj).getId();
				assertNotNull(id);
				BasicDBObject qry1 = new BasicDBObject();
				List<BasicDBObject> where1 = new ArrayList<BasicDBObject>();
				where1.add(new BasicDBObject("id", id1));
				qry1.put("$and", where1);
				DBCursor cur2 = coll1.find(qry1);
				log.info("id1=" + id1 + " cnt=" + cur2.count());
			}
		}
	}

}
