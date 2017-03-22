/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Abdelghani EL OUAKILI (NIST) Mar 15, 2017
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ExportFontConfig;

/**
 * @author Abdelghani EL Ouakili (NIST)
 *
 */
public class ExportFontConfigRepositoryImpl implements ExportFontConfigOperations {

  @Autowired
  private MongoOperations mongo;


  @Override
  public List<ExportFontConfig> findOneByAccountId(Long accountId) {
    // TODO Auto-generated method stub
    Criteria where = Criteria.where("accountId").is(accountId);

    Query qry = Query.query(where);
    // qry = set4Brevis(qry);

    List<ExportFontConfig> result = mongo.find(qry, ExportFontConfig.class);

    return result;



  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ExportFontConfigOperations#findByDefaultConfig
   * (java.lang.Boolean)
   */
  @Override
  public ExportFontConfig findByDefaultConfig(Boolean defaultConfig) throws Exception {
    // TODO Auto-generated method stub
    ExportFontConfig defaultFontConfig = null;
    Criteria where = Criteria.where("defaultConfig").is(defaultConfig);

    Query qry = Query.query(where);
    // qry = set4Brevis(qry);

    List<ExportFontConfig> result = mongo.find(qry, ExportFontConfig.class);
    if (result.isEmpty()) {
      // throw new Exception("Cannot find User configuration ");
    } else {
      defaultFontConfig = result.get(0);
    }
    return defaultFontConfig;
  }

}
