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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;

public class SegmentRepositoryImpl implements SegmentOperations {

  private Logger log = LoggerFactory.getLogger(SegmentRepositoryImpl.class);

  @Autowired
  private MongoOperations mongo;

  @Override
  public List<Segment> findByScopesAndVersion(List<SCOPE> scopes, String hl7Version) {
    Criteria where = Criteria.where("scope").in(scopes);
    where.andOperator(Criteria.where("hl7Version").is(hl7Version));
    Query qry = Query.query(where);
    return mongo.find(qry, Segment.class);
  }

  @Override
  public Segment findByNameAndVersionAndScope(String name, String version, String scope) {
    Criteria where = Criteria.where("name").is(name);
    where.andOperator(Criteria.where("hl7Version").is(version));
    // where.andOperator(Criteria.where("scope").is(scope));

    Query qry = Query.query(where);
    List<Segment> segments = mongo.find(qry, Segment.class);
    for (Segment seg : segments) {
      if (seg.getScope().toString().equals(scope)) {
        return seg;
      }

    }
    Segment segment = null;

    return segment;

  }


  @Override
  public List<Segment> findByIds(Set<String> ids) {
    Criteria where = Criteria.where("id").in(ids);
    Query qry = Query.query(where);
    List<Segment> segments = mongo.find(qry, Segment.class);
    return segments;
  }

  @Override
  public List<Segment> findUserSegmentsByIds(Set<String> ids) {
    Criteria where =
        Criteria.where("id").in(ids).andOperator(Criteria.where("scope").is(SCOPE.USER.toString()));
    Query qry = Query.query(where);
    // qry = set4Brevis(qry);
    List<Segment> segments = mongo.find(qry, Segment.class);
    return segments;
  }

  @Override
  public Date updateDate(String id, Date date) {
    Query query = new Query();
    query.addCriteria(Criteria.where("id").is(id));
    query.fields().include("dateUpdated");
    Update update = new Update();
    update.set("dateUpdated", date);
    mongo.updateFirst(query, update, Segment.class);
    return date;
  }

  @Override
  public void updateStatus(String id, STATUS status) {
    Query query = new Query();
    query.addCriteria(Criteria.where("id").is(id));
    query.fields().include("status");
    Update update = new Update();
    update.set("status", status);
    mongo.updateFirst(query, update, Segment.class);
  }

  @Override
  public List<Segment> findByScope(String scope) {
    Criteria where = Criteria.where("scope").is(scope);
    Query qry = Query.query(where);
    List<Segment> segments = mongo.find(qry, Segment.class);
    return segments;
  }

  @Override
  public List<Segment> findByNameAndScope(String name, String scope) {
    Criteria where = Criteria.where("scope").is(scope);
    where.andOperator(Criteria.where("name").is(name));
    Query qry = Query.query(where);
    List<Segment> segments = mongo.find(qry, Segment.class);
    return segments;
  }

  @Override
  public List<Segment> findByScopeAndVersion(String scope, String version) {
    Criteria where = Criteria.where("scope").is(SCOPE.USER.toString());
    where.andOperator(Criteria.where("hl7Version").is(version));
    Query qry = Query.query(where);
    List<Segment> segments = mongo.find(qry, Segment.class);
    return segments;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.SegmentOperations#updateAttribute(java.lang.
   * String, java.lang.String, java.lang.Object)
   */
  @Override
  public void updateAttribute(String id, String attributeName, Object value) {
    // TODO Auto-generated method stub
    Query query = new Query();
    query.addCriteria(Criteria.where("id").is(id));
    query.fields().include(attributeName);
    Update update = new Update();
    update.set(attributeName, value);
    mongo.updateFirst(query, update, Segment.class);

  }
}
