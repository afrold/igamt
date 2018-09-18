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

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Documentation;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;


/**
 * @author Abdelghani EL Ouakili (NIST)
 *
 */
public class DocumentationRepositoryImp implements DocumentationOperations {

  @Autowired
  private MongoOperations mongo;



  /*
   * (non-Javadoc)
   * 
   * @see gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DocumentationOperations#findAll()
   */
  @Override
  public List<Documentation> findAll() {
    Query qry = new Query();
    // qry = set4Brevis(qry);
    return mongo.find(qry, Documentation.class);

  }



  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DocumentationOperations#findById(java.lang.
   * String)
   */
  @Override
  public Documentation findById(String id) {



    Criteria where = Criteria.where("id").is(id);
    Query qry = Query.query(where);
    // qry = set4Brevis(qry);
    List<Documentation> decisions = mongo.find(qry, Documentation.class);
    Documentation decision = null;
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
    mongo.updateFirst(query, update, Documentation.class);
    return date;
  }



  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DocumentationOperations#findByCreator(java.
   * lang.Long)
   */
  @Override
  public List<Documentation> findByOwner(Long accountId) {
    Criteria where = Criteria.where("owner").is(accountId);
    Query qry = Query.query(where);
    // qry = set4Brevis(qry);
    List<Documentation> documentations = mongo.find(qry, Documentation.class);

    return documentations;
  }
  
//  @Override
//  public int order(String id, int position) {
//    Query query = new Query();
//    query.addCriteria(Criteria.where("id").is(id));
//    query.fields().include("position");
//    Update update = new Update();
//    update.set("position", position);
//    mongo.updateFirst(query, update, Documentation.class);
//    return position;
//  }

}
