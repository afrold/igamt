/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Ismail Mellouli (NIST) Mar 6, 2017
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfileStructure;

public class CompositeProfileRepositoryImpl implements CompositeProfileOperations {


  private Logger log = LoggerFactory.getLogger(CompositeProfileRepositoryImpl.class);

  @Autowired
  private MongoOperations mongo;

  @Override
  public CompositeProfileStructure findById(String id) {
    CompositeProfileStructure compositeProfileStructure = new CompositeProfileStructure();
    System.out.println("++++++");
    Criteria where = Criteria.where("id").is(id);
    Query qry = Query.query(where);
    List<CompositeProfileStructure> compositeProfileStructures =
        mongo.find(qry, CompositeProfileStructure.class);

    if (compositeProfileStructures != null && compositeProfileStructures.size() > 0) {
      compositeProfileStructure = compositeProfileStructures.get(0);
    }
    return compositeProfileStructure;
  }

  @Override
  public void updateAttribute(String id, String attributeName, Object value) {
    // TODO Auto-generated method stub
    Query query = new Query();
    query.addCriteria(Criteria.where("id").is(id));
    query.fields().include(attributeName);
    Update update = new Update();
    update.set(attributeName, value);
    mongo.updateFirst(query, update, CompositeProfileStructure.class);

  }

}
