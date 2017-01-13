package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.Mongo;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.UnchangedDataType;

public class UnchangedDatatypeRepositoryImp implements UnchangedDatatypeOperation {
	  @Autowired
	  private MongoOperations mongo;
	@Override
	public List<UnchangedDataType> findByNameAndVersions(String name, String version) {
		List<UnchangedDataType> result= new ArrayList<UnchangedDataType>();
	    Criteria where = Criteria.where("name").is(name);
	    //where.andOperator(Criteria.where(version).("versions"));
	    Query qry = Query.query(where);
	  
	    List<UnchangedDataType> temp=mongo.find(qry, UnchangedDataType.class);
	    for(UnchangedDataType dt : temp){
	    	for(String s : dt.getVersions()){
	    		if(s.equals(version)){
	    			result.add(dt);
	    		}
	    	}
	    	
	    }
		return result;
	    
	    
	}


}