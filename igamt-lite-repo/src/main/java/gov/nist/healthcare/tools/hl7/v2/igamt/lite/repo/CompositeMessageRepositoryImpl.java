package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeMessage;

public class CompositeMessageRepositoryImpl implements CompositeMessageOperations {
	private Logger log = LoggerFactory.getLogger(CompositeMessageRepositoryImpl.class);

	@Autowired
	private MongoOperations mongo;

	@Override
	public List<CompositeMessage> findByIds(Set<String> ids) {
		Criteria where = Criteria.where("id").in(ids);
		Query qry = Query.query(where);
		List<CompositeMessage> compositeMessages = mongo.find(qry, CompositeMessage.class);
		return compositeMessages;
	}

}
