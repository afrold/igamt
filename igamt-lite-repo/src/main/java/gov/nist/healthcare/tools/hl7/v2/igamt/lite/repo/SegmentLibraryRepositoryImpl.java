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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;

public class SegmentLibraryRepositoryImpl implements SegmentLibraryOperations {

  private Logger log = LoggerFactory.getLogger(SegmentLibraryRepositoryImpl.class);

  @Autowired
  private MongoOperations mongo;

  @Override
  public List<SegmentLibrary> findByScopes(List<SCOPE> scopes) {
    Criteria where = Criteria.where("scope").in(scopes);
    Query qry = Query.query(where);
    List<SegmentLibrary> list = mongo.find(qry, SegmentLibrary.class);
    log.debug("SegmentLibraryRespositoryImpl.findByScopes list.size()=" + list.size());
    return list;
  }

  @Override
  public List<SegmentLibrary> findScopesNVersion(List<SCOPE> scopes, String hl7Version) {
    log.info("SegmentLibraryRespositoryImpl.findByScopesAndVersion=" + hl7Version);
    Criteria where = Criteria.where("scope").in(scopes);
    if (hl7Version != null) {
      where.andOperator(Criteria.where("metaData.hl7Version").is(hl7Version));
    }
    Query qry = Query.query(where);
    List<SegmentLibrary> list = mongo.find(qry, SegmentLibrary.class);
    log.info("SegmentLibraryRespositoryImpl.findByScopesAndVersion list.size()=" + list.size());
    return list;
  }

  @Override
  public List<SegmentLibrary> findByAccountId(Long accountId, String hl7Version) {
    log.debug("SegmentLibraryRespositoryImpl.findStandardByVersion=" + hl7Version);
    Criteria where = Criteria.where("accountId").is(accountId)
        .andOperator(Criteria.where("scope").is(SCOPE.USER))
        .andOperator(Criteria.where("metaData.hl7Version").is(hl7Version));
    Query qry = Query.query(where);
    List<SegmentLibrary> list = mongo.find(qry, SegmentLibrary.class);
    log.debug("SegmentLibraryRespositoryImpl.findStandardByVersion list.size()=" + list.size());
    return list;
  }

  @Override
  public List<String> findHl7Versions() {
    Query qry = new Query();
    qry.fields().include("metaData.hl7Version");
    List<SegmentLibrary> dtLibs = mongo.findAll(SegmentLibrary.class);
    List<String> versions = new ArrayList<String>();
    for (SegmentLibrary dtLib : dtLibs) {
      versions.add(dtLib.getMetaData().getHl7Version());
    }
    return versions;
  }

  @Override
  public SegmentLibrary findById(String id) {
    log.debug("SegmentLibraryRespositoryImpl.findById=" + id);
    Criteria where = Criteria.where("id").is(id);
    Query qry = Query.query(where);
    SegmentLibrary segmentLibrary = null;
    List<SegmentLibrary> segmentLibraries = mongo.find(qry, SegmentLibrary.class);
    if (segmentLibraries != null && segmentLibraries.size() > 0) {
      segmentLibrary = segmentLibraries.get(0);
    }
    return segmentLibrary;
  }

  @Override
  public List<SegmentLink> findFlavors(SCOPE scope, String hl7Version, String name,
      Long accountId) {
    Criteria libCriteria = Criteria.where("scope").is(scope)
        .andOperator(Criteria.where("metaData.hl7Version").is(hl7Version)).andOperator(Criteria
            .where("accountId").is(accountId).orOperator(Criteria.where("accountId").is(null)));
    Criteria linksCriteria = Criteria.where("children").elemMatch(Criteria.where("name").is(name));
    BasicQuery query =
        new BasicQuery(libCriteria.getCriteriaObject(), linksCriteria.getCriteriaObject());
    List<SegmentLink> links = mongo.find(query, SegmentLink.class);

    return links;
  }

  @Override
  public List<SegmentLibrary> findLibrariesByFlavorName(SCOPE scope, String hl7Version, String name,
      Long accountId) {
    List<SegmentLibrary> libraries = null;
    Criteria libCriteria = null;
    if (scope.equals(SCOPE.HL7STANDARD) || scope.equals(SCOPE.MASTER)) {
      libCriteria = Criteria.where("scope").is(scope)
          .andOperator(Criteria.where("metaData.hl7Version").is(hl7Version));
      Criteria linksCriteria =
          Criteria.where("children").elemMatch(Criteria.where("name").is(name));
      BasicQuery query =
          new BasicQuery(libCriteria.getCriteriaObject(), linksCriteria.getCriteriaObject());
      libraries = mongo.find(query, SegmentLibrary.class);
      if (libraries != null) {
        Set<String> ids = new HashSet<String>();
        for (SegmentLibrary lib : libraries) {
          ids.add(lib.getId());
        }
        libraries = findLibrariesByIds(ids);
      }
    }
    return libraries;
  }

  public List<SegmentLibrary> findLibrariesByIds(Set<String> ids) {
    Criteria where = Criteria.where("id").in(ids);
    Query qry = Query.query(where);
    List<SegmentLibrary> libraries = mongo.find(qry, SegmentLibrary.class);
    return libraries;
  }



  @Override
  public List<Segment> findByIds(Set<String> ids) {
    Criteria where = Criteria.where("id").in(ids);
    Query qry = Query.query(where);
    List<Segment> segments = mongo.find(qry, Segment.class);
    return segments;
  }

  @Override
  public Set<SegmentLink> findChildrenById(String id) {
    log.debug("SegmentLibraryRespositoryImpl.findChildrenById=" + id);
    SegmentLibrary lib = findById(id);
    return lib != null ? lib.getChildren() : new HashSet<SegmentLink>(0);
  }

}
