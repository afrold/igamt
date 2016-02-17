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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;

public class DatatypeLibraryRespositoryImpl implements DatatypeLibraryOperations {

	private Logger log = LoggerFactory.getLogger(DatatypeLibraryRespositoryImpl.class);

	 @Autowired
	 private MongoOperations mongo;
	 
	@Override
	public List<DatatypeLibrary> findAll() {
	    return mongo.findAll(DatatypeLibrary.class);
	}
	 
	@Override
	public List<DatatypeLibrary> findByScope(DatatypeLibrary.SCOPE scope) {
 	    Criteria where = Criteria.where("scope").is(scope);
	    Query query = Query.query(where);
	    return mongo.find(query, DatatypeLibrary.class);
	}
	 
	@Override
	public List<DatatypeLibrary> findByAccountId(Long accountId) {
	    Criteria where = Criteria.where("scope").is(DatatypeLibrary.SCOPE.USER)
	    	.andOperator(Criteria.where("accountId").is(accountId));
	    Query query = Query.query(where);
	    return mongo.find(query, DatatypeLibrary.class);
	}
}
