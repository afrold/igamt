/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Abdelghani EL OUAKILI (NIST) Feb 2, 2017
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ExportConfig;



/**
 * @author Abdelghani EL Ouakili (NIST)
 *
 */
public class ExportConfigRepositoryImpl implements ExportConfigOperations {

  private Logger log = LoggerFactory.getLogger(ExportConfigRepositoryImpl.class);

  @Autowired
  private MongoOperations mongo;

  /*
   * (non-Javadoc)
   * 
   * @see gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ExportConfigOperations#findByType()
   */
  @Override
  public List<ExportConfig> findByType(String type) {
    Criteria where = Criteria.where("type").is(type);
    Query qry = Query.query(where);
    // qry = set4Brevis(qry);
    return mongo.find(qry, ExportConfig.class);

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ExportConfigOperations#findByTypeAndAccountId(
   * java.lang.String, java.lang.Long)
   */
  @Override
  public List<ExportConfig> findByTypeAndAccountId(String type, Long accountId) {
    // TODO Auto-generated method stub
    Criteria where = Criteria.where("type").in(type);
    where.andOperator(Criteria.where("accountId").is(accountId));
    Query qry = Query.query(where);
    // qry = set4Brevis(qry);
    return mongo.find(qry, ExportConfig.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ExportConfigOperations#findByAccountId(java.
   * lang.Long)
   */
  @Override
  public List<ExportConfig> findByAccountId(Long accountId) {
    // TODO Auto-generated method stub
    Criteria where = Criteria.where("accountId").is(accountId);
    Query qry = Query.query(where);
    // qry = set4Brevis(qry);
    return mongo.find(qry, ExportConfig.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ExportConfigOperations#findDefault(java.lang.
   * String)
   */
  @Override
  public List<ExportConfig> findDefault(String type) {
    Criteria where = Criteria.where("type").in(type);
    where.andOperator(Criteria.where("defaultType").is(true));
    Query qry = Query.query(where);
    return mongo.find(qry, ExportConfig.class);


  }


}
