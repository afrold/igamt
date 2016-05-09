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

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;

public class DatatypeRepositoryImpl implements DatatypeOperations {

	private Logger log = LoggerFactory.getLogger(DatatypeRepositoryImpl.class);

	@Autowired
	private MongoOperations mongo;

	@Override
	public List<Datatype> findByLibIds(String dtLibId) {
		Criteria where = Criteria.where("libIds").in(dtLibId);
		Query qry = Query.query(where);
		qry = set4Brevis(qry);
		return mongo.find(qry, Datatype.class);
	}

	@Override
	public List<Datatype> findAll() {
		Query qry = new Query();
		qry = set4Brevis(qry);
		return mongo.find(qry, Datatype.class);
	}

	@Override
	public List<Datatype> findByScopesAndVersion(List<SCOPE> scopes, String hl7Version) {
		Criteria where = Criteria.where("scope").in(scopes);
		where.andOperator(Criteria.where("hl7Version").is(hl7Version));
		Query qry = Query.query(where);
		qry = set4Brevis(qry);
		return mongo.find(qry, Datatype.class);
	}

	@Override
	public Datatype findById(String id) {
		Criteria where = Criteria.where("id").is(id);
		Query qry = Query.query(where);
		qry = set4Brevis(qry);
		List<Datatype> datatypes = mongo.find(qry, Datatype.class);
		Datatype datatype = null;
		if (datatypes != null && datatypes.size() > 0) {
			datatype = datatypes.get(0);
		}
		return datatype;
	}

	@Override
	public List<Datatype> findByIds(List<String> ids) {
		Criteria where = Criteria.where("id").in(ids);
		Query qry = Query.query(where);
		qry = set4Brevis(qry);
		List<Datatype> datatypes = mongo.find(qry, Datatype.class);
		return datatypes;
	}
	
	@Override
	public List<Datatype> findDups(Datatype dt) {
		Criteria where = Criteria.where("libIds").in(dt.getLibIds());
		where.andOperator(Criteria.where("ext").is(dt.getExt()));
		Query qry = Query.query(where);
		List<Datatype> datatypes = mongo.find(qry, Datatype.class);
		return datatypes;
	}

	Query set4Brevis(Query qry) {
		qry.fields().include("_id");
		qry.fields().include("name");
		qry.fields().include("label");
		qry.fields().include("ext");
		qry.fields().include("name");
		qry.fields().include("status");
		qry.fields().include("description");
		qry.fields().include("data");
		qry.fields().include("version");
		return qry;
	}
}
