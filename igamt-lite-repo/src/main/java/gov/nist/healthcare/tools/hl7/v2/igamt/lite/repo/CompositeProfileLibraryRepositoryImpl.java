package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfileLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;

public class CompositeProfileLibraryRepositoryImpl implements CompositeProfileLibrayOperation  {
	  @Autowired
	  private MongoOperations mongo;

		@Override
	public CompositeProfileLibrary findById(String id) {
		Criteria where = Criteria.where("id").is(id);
	    Query qry = Query.query(where);
	    CompositeProfileLibrary CompositeProfileLibrary= null;
	    List<CompositeProfileLibrary> tableLibraries = mongo.find(qry, CompositeProfileLibrary.class);
	    if (tableLibraries != null && tableLibraries.size() > 0) {
	    	 CompositeProfileLibrary = tableLibraries.get(0);
	    }
	    return  CompositeProfileLibrary;
	}

	@Override
	public List<CompositeProfileLibrary> findAll() {
		// TODO Auto-generated method stub
			return  mongo.findAll(CompositeProfileLibrary.class);
	}


}