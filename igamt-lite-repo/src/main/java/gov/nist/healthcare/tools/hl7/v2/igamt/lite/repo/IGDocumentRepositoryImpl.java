/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 */

/**
 * 
 * @author Olivier MARIE-ROSE
 * 
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentScope;

public class IGDocumentRepositoryImpl implements IGDocumentOperations {

  private Logger log = LoggerFactory.getLogger(IGDocumentRepositoryImpl.class);

  @Autowired
  private MongoOperations mongo;

  @Override
  public List<IGDocument> findPreloaded() {
    Criteria where = Criteria.where("scope").is(IGDocumentScope.PRELOADED);
    Query query = Query.query(where);
    return mongo.find(query, IGDocument.class);
  }

  @Override
  public List<IGDocument> findStandard() {
    Criteria where = Criteria.where("scope").is(IGDocumentScope.HL7STANDARD);
    Query query = Query.query(where);
    return mongo.find(query, IGDocument.class);
  }

  @Override
  public List<IGDocument> findUser() {
    Criteria where = Criteria.where("scope").is(IGDocumentScope.USER);
    Query query = Query.query(where);
    return mongo.find(query, IGDocument.class);
  }

  @Override
  public List<IGDocument> findStandardByVersion(String hl7version) {
    log.debug("findStandardByVersion");
    Criteria where = Criteria.where("scope").is(IGDocumentScope.HL7STANDARD)
        .andOperator(Criteria.where("profile.metaData.hl7Version").is(hl7version));
    Query query = Query.query(where);
    List<IGDocument> list = mongo.find(query, IGDocument.class);
    log.debug("findStandardByVersion list.size()=" + list.size());
    return list;
  }


  @Override
  public List<IGDocument> findByScopesAndVersion(List<SCOPE> scopes, String hl7Version) {
    Criteria where = Criteria.where("profile.scope").in(scopes);
    where.andOperator(Criteria.where("profile.metaData.hl7Version").is(hl7Version));
    Query qry = Query.query(where);
    qry.fields().include("_id");
    qry.fields().include("metaData.title");
    return mongo.find(qry, IGDocument.class);
  }

  @Override
  public List<IGDocument> findByScopeAndVersions(IGDocumentScope scope, List<String> hl7Versions) {
    Criteria where = Criteria.where("profile.metaData.hl7Version").in(hl7Versions);
    where.andOperator(Criteria.where("profile.scope").is(scope));
    Query qry = Query.query(where);

    return mongo.find(qry, IGDocument.class);
  }

  @Override
  public List<IGDocument> findByScopeAndVersionsInIg(IGDocumentScope scope,
      List<String> hl7Versions) {
    Criteria where = Criteria.where("metaData.hl7Version").in(hl7Versions);
    where.andOperator(Criteria.where("scope").is(scope));
    Query qry = Query.query(where);

    return mongo.find(qry, IGDocument.class);
  }

  @Override
  public List<IGDocument> findByAccountIdAndScopesAndVersion(Long accountId, List<SCOPE> scopes,
      String hl7Version) {
    Criteria where = Criteria.where("profile.scope").in(scopes);
    // where.andOperator(Criteria.where("profile.metaData.hl7Version").is(hl7Version));
    where.andOperator(Criteria.where("accountId").is(accountId)
        .andOperator(Criteria.where("profile.metaData.hl7Version").is(hl7Version)));
    Query qry = Query.query(where);
    qry.fields().include("_id");
    qry.fields().include("metaData.title");
    return mongo.find(qry, IGDocument.class);
  }

  /**
   * TODO: Refactor this to not load IG Doc in memory
   */
  @Override
  public List<String> findHl7Versions() {
    Criteria where = Criteria.where("scope").is(IGDocumentScope.HL7STANDARD);
    Query query = Query.query(where);
    List<String> rval = new ArrayList<String>();
    List<IGDocument> rs = mongo.find(query, IGDocument.class);
    for (IGDocument igd : rs) {
      rval.add(igd.getProfile().getMetaData().getHl7Version());
    }
    Collections.sort(rval);
    return rval;
  }

  @Override
  public List<IGDocument> findAllByScope(IGDocumentScope scope) {
    Criteria where = Criteria.where("scope").is(scope);
    Query qry = Query.query(where);
    return mongo.find(qry, IGDocument.class);
  }

}
