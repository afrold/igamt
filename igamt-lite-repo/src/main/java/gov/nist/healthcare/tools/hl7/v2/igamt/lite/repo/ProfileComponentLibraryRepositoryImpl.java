package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;

public class ProfileComponentLibraryRepositoryImpl implements ProfileComponentLibraryOperations {
	  private Logger log = LoggerFactory.getLogger(TableLibraryRepositoryImpl.class);
	  @Autowired
	  private MongoOperations mongo;
	  
	  
	  @Override
	  public ProfileComponentLibrary findById(String id) {
	    log.debug("ProfileComponentLibraryRepositoryImpl.findById=" + id);
	    Criteria where = Criteria.where("id").is(id);
	    Query qry = Query.query(where);
	    ProfileComponentLibrary profileComponentLibrary = null;
	    List<ProfileComponentLibrary> profileComponentLibraries = mongo.find(qry, ProfileComponentLibrary.class);
	    if (profileComponentLibraries != null && profileComponentLibraries.size() > 0) {
	    	profileComponentLibrary = profileComponentLibraries.get(0);
	    }
	    return profileComponentLibrary;
	  }
	@Override
	public Set<ProfileComponentLink> findChildrenById(String id) {
		 log.debug("ProfileComponentLibraryRepositoryImpl.findChildrenById=" + id);
		    ProfileComponentLibrary lib = findById(id);
		    return lib != null ? lib.getChildren() : new HashSet<ProfileComponentLink>(0);
	}

}
