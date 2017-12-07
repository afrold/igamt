package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.MessageLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;

public class MessageLibraryRepositoryImp implements MessageLibraryOperations {
	  @Autowired
	  private MongoOperations mongo;

		@Override
	public MessageLibrary findById(String id) {
		Criteria where = Criteria.where("id").is(id);
	    Query qry = Query.query(where);
	    MessageLibrary messageLibrary= null;
	    List<MessageLibrary> tableLibraries = mongo.find(qry, MessageLibrary.class);
	    if (tableLibraries != null && tableLibraries.size() > 0) {
	    	 messageLibrary = tableLibraries.get(0);
	    }
	    return  messageLibrary;
	}

	@Override
	public List<MessageLibrary> findAll() {
		// TODO Auto-generated method stub
			return  mongo.findAll(MessageLibrary.class);
	}


}
