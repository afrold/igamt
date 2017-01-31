/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Abdelghani EL OUAKILI (NIST) Jan 26, 2017
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Decision;


/**
 * @author Abdelghani EL Ouakili (NIST)
 *
 */
public class DecisionRepositoryImp implements DecisionOperations {

  @Autowired
  private MongoOperations mongo;



  /*
   * (non-Javadoc)
   * 
   * @see gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DecisionOperations#findAll()
   */
  @Override
  public List<Decision> findAll() {
    Query qry = new Query();
    // qry = set4Brevis(qry);
    return mongo.find(qry, Decision.class);

  }



  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DecisionOperations#findById(java.lang.String)
   */
  @Override
  public Decision findById(String id) {



    Criteria where = Criteria.where("id").is(id);
    Query qry = Query.query(where);
    // qry = set4Brevis(qry);
    List<Decision> decisions = mongo.find(qry, Decision.class);
    Decision decision = null;
    if (decisions != null && decisions.size() > 0) {
      decision = decisions.get(0);
    }
    return decision;
    // TODO Auto-generated method stub
  }

  @Override
  public Date updateDate(String id, Date date) {
    Query query = new Query();
    query.addCriteria(Criteria.where("id").is(id));
    query.fields().include("dateUpdated");
    Update update = new Update();
    update.set("dateUpdated", date);
    mongo.updateFirst(query, update, Decision.class);
    return date;
  }

}
