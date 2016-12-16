package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;

public class ProfileComponentRepositoryImpl implements ProfileComponentOperations {
	private Logger log = LoggerFactory.getLogger(TableRepositoryImpl.class);

	  @Autowired
	  private MongoOperations mongo;

	@Override
	public List<ProfileComponent> findAllByIds(Set<String> ids) {
		 Criteria where = Criteria.where("id").in(ids);
		    Query qry = Query.query(where);
		    List<ProfileComponent> profileComponents = mongo.find(qry, ProfileComponent.class);
		    return profileComponents;
	}

}
