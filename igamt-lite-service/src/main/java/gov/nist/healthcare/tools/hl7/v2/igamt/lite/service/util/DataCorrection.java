package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.prelib.converters.DatatypeReadConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.prelib.converters.SegmentReadConverter;

import java.io.IOException;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class DataCorrection{

	public void updateSegment() {

		MongoOperations mongoOps;
		try {
			mongoOps = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), "igamt"));
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			SegmentReadConverter conv = new SegmentReadConverter();
			
			DBCollection coll1 = mongoOps.getCollection("segment");
			DBCursor cur1 = coll1.find();
			while (cur1.hasNext()) {
				DBObject source = cur1.next();
				Segment seg = conv.convert(source);

				
				for(Field f:seg.getFields()){
					if(f.getConfLength() != null || f.getConfLength().equals("")){
						f.setConfLength("1");
					}
				}
				
				mongoOps.save(seg);
			}
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			e.printStackTrace();
		}
		
	}
	
	public void updateDatatype() {

		MongoOperations mongoOps;
		try {
			mongoOps = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), "igamt"));
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			DatatypeReadConverter conv = new DatatypeReadConverter();
			
			DBCollection coll1 = mongoOps.getCollection("datatype");
			DBCursor cur1 = coll1.find();
			while (cur1.hasNext()) {
				DBObject source = cur1.next();
				Datatype dt = conv.convert(source);

				for(Component c:dt.getComponents()){
					if(c.getConfLength() != null || c.getConfLength().equals("")){
						c.setConfLength("1");
					}
				}
				
				mongoOps.save(dt);
			}
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			e.printStackTrace();
		}
		
	}
	
	
	public static void main(String[] args) throws IOException {
		new DataCorrection().updateSegment();
		new DataCorrection().updateDatatype();
	}
}
