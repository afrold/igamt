/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 */

/**
 * 
 * @author Olivier MARIE-ROSE
 * 
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileScope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class ProfileRepositoryImpl implements ProfileOperations   {
	
	 @Autowired
	 private MongoOperations mongo;
	 
	 
	/* (non-Javadoc)
	 * @see gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileOperations#findByPreloaded(java.lang.Boolean)
	 */
	@Override
	public List<Profile> findPreloaded() {
 	    Criteria where = Criteria.where("scope").is(ProfileScope.HL7STANDARD);
	    Query query = Query.query(where);
	    return mongo.find(query, Profile.class);
 	}
	
	public List<Profile> findStandard() {
 	    Criteria where = Criteria.where("scope").is(ProfileScope.HL7STANDARD);
	    Query query = Query.query(where);
	    return mongo.find(query, Profile.class);
 	}
	
	public List<Profile> findStandardByVersion(String hl7version) {
		Criteria where = Criteria.where("scope").is(ProfileScope.HL7STANDARD).andOperator(Criteria.where("hl7version").is(hl7version));
		Query query = Query.query(where);
	    return mongo.find(query, Profile.class);
 	}
	
	public List<String> findHl7Versions(){
		return new ArrayList<String>(
				Arrays.asList("2.0","2.1","2.2","2.3","2.3.1","2.4","2.5","2.5.1","2.6","2.7", "2.8"));
	}
	
}
