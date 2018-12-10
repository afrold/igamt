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
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ShareParticipantPermission;

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
  public List<IGDocument> findByScopeAndVersion(IGDocumentScope scope, String hl7Version) {
    Criteria where = Criteria.where("profile.metaData.hl7Version").is(hl7Version);
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

  @Override
  public List<IGDocument> findByAccountIdAndScope(Long accountId, IGDocumentScope scope) {
    Criteria where = Criteria.where("scope").is(scope);
    // where.andOperator(Criteria.where("profile.metaData.hl7Version").is(hl7Version));
    where.andOperator(Criteria.where("accountId").is(accountId));
    Query qry = Query.query(where);
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


  @Override
  public List<IGDocument> findByParticipantId(Long participantId) {
    Criteria accountCreteria = Criteria.where("scope").is(IGDocumentScope.USER).andOperator(
        Criteria.where("accountId").ne(participantId),
        Criteria.where("shareParticipantIds").exists(true));
    // Criteria participantsCr =
    // Criteria.where("shareParticipantIds").all(Collections.singleton(participantId));
    // Criteria.where("shareParticipantIds").exists(true);
    BasicQuery query = new BasicQuery(accountCreteria.getCriteriaObject()); // ,
                                                                            // participantsCr.getCriteriaObject());
    List<IGDocument> igdocuments = mongo.find(query, IGDocument.class);
    List<IGDocument> igdocumentsShareWithParticipantsId = new ArrayList<IGDocument>();
    for (IGDocument doc : igdocuments) {
     if(doc.getShareParticipantIds() != null && !doc.getShareParticipantIds().isEmpty()){
      for (ShareParticipantPermission participant : doc.getShareParticipantIds()) {
        if (participant.getAccountId().equals(participantId)) {
          if (!participant.isPendingApproval()) {
            igdocumentsShareWithParticipantsId.add(doc);
          } else {
            IGDocument tempDoc = doc;
            tempDoc.setChildSections(null);
            tempDoc.setComment("PENDING_APPROVAL");
            igdocumentsShareWithParticipantsId.add(tempDoc);
          }
        }
      }
      }
    }
    return igdocumentsShareWithParticipantsId;
  }

  @Override
  public Date updateDate(String id, Date date) {
    Query query = new Query();
    query.addCriteria(Criteria.where("id").is(id));
    query.fields().include("dateUpdated");
    Update update = new Update();
    update.set("dateUpdated", date);
    mongo.updateFirst(query, update, IGDocument.class);
    return date;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.IGDocumentOperations#updatePosition(java.lang.
   * String, int)
   */
  @Override
  public int updatePosition(String id, int position) {
    Query query = new Query();
    query.addCriteria(Criteria.where("id").is(id));
    query.fields().include("position");
    Update update = new Update();
    update.set("position", position);
    mongo.updateFirst(query, update, IGDocument.class);
    return position;
  }

}
