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

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;

public class TableRespositoryImpl implements TableOperations {

	private Logger log = LoggerFactory.getLogger(TableRespositoryImpl.class);

	 @Autowired
	 private MongoOperations mongo;

		@Override
		public List<Table> findByLibIds(String tabLibId) {
			Criteria where = Criteria.where("libIds").in(tabLibId);
			Query qry = Query.query(where);
			return mongo.find(qry, Table.class);
		}

		@Override
		public List<Table> findByScopesAndVersion(List<SCOPE> scopes, String hl7Version) {
			Criteria where = Criteria.where("scope").in(scopes);
			where.andOperator(Criteria.where("hl7Version").is(hl7Version));
			Query qry = Query.query(where);
			return mongo.find(qry, Table.class);
		}
}
