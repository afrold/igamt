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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;

public class DatatypeLibraryRepositoryImpl implements DatatypeLibraryOperations {

  private Logger log = LoggerFactory.getLogger(DatatypeLibraryRepositoryImpl.class);

  @Autowired
  private MongoOperations mongo;

  @Override
  public List<DatatypeLibrary> findByScope(SCOPE scope, Long accountId) {

    Criteria where = Criteria.where("scope").is(scope);

    if (scope == SCOPE.USER) {
      where.andOperator(Criteria.where("accountId").is(accountId));
    }

    Query qry = Query.query(where);
    List<DatatypeLibrary> list = mongo.find(qry, DatatypeLibrary.class);
    log.debug("DatatypeLibraryRespositoryImpl.findByScopes list.size()=" + list.size());
    return list;
  }

  @Override
  public List<DatatypeLibrary> findScopesNVersion(List<SCOPE> scopes, String hl7Version) {
    log.info("DatatypeLibraryRespositoryImpl.findByScopesAndVersion=" + hl7Version);
    Criteria where = Criteria.where("scope").in(scopes);
    if (hl7Version != null) {
      where.andOperator(Criteria.where("metaData.hl7Version").is(hl7Version));
    }
    Query qry = Query.query(where);
    List<DatatypeLibrary> list = mongo.find(qry, DatatypeLibrary.class);
    log.info("DatatypeLibraryRespositoryImpl.findByScopesAndVersion list.size()=" + list.size());
    return list;
  }

  @Override
  public List<DatatypeLibrary> findByAccountId(Long accountId, String hl7Version) {
    log.debug("DatatypeLibraryRespositoryImpl.findStandardByVersion=" + hl7Version);
    Criteria where = Criteria.where("accountId").is(accountId).andOperator(
        Criteria.where("scope").is(SCOPE.USER.name()),
        Criteria.where("metaData.hl7Version").is(hl7Version));
    Query qry = Query.query(where);
    List<DatatypeLibrary> list = mongo.find(qry, DatatypeLibrary.class);
    log.debug("DatatypeLibraryRespositoryImpl.findStandardByVersion list.size()=" + list.size());
    return list;
  }

  @Override
  public List<String> findHl7Versions() {
    Criteria where = Criteria.where("scope").is(SCOPE.HL7STANDARD);
    Query qry = Query.query(where);
    qry.fields().include("metaData.hl7Version");
    List<DatatypeLibrary> dtLibs = mongo.find(qry, DatatypeLibrary.class);
    List<String> versions = new ArrayList<String>();
    for (DatatypeLibrary dtLib : dtLibs) {
      versions.add(dtLib.getMetaData().getHl7Version());
    }
    return versions;
  }

  @Override
  public DatatypeLibrary findById(String id) {
    log.debug("DatatypeLibraryRespositoryImpl.findById=" + id);
    Criteria where = Criteria.where("id").is(id);
    Query qry = Query.query(where);
    DatatypeLibrary datatypeLibrary = null;
    List<DatatypeLibrary> datatypeLibraries = mongo.find(qry, DatatypeLibrary.class);
    if (datatypeLibraries != null && datatypeLibraries.size() > 0) {
      datatypeLibrary = datatypeLibraries.get(0);
    }
    return datatypeLibrary;
  }

  @Override
  public List<DatatypeLibrary> findDups(DatatypeLibrary dtl) {
    Criteria where = Criteria.where("scope").in(dtl.getScope());
    where.andOperator(Criteria.where("ext").is(dtl.getExt()));
    Query qry = Query.query(where);
    List<DatatypeLibrary> datatypeLibraries = mongo.find(qry, DatatypeLibrary.class);
    return datatypeLibraries;
  }

  @Override
  public List<DatatypeLibrary> findByIds(Set<String> ids) {
    Criteria where = Criteria.where("id").in(ids);
    Query qry = Query.query(where);
    List<DatatypeLibrary> libraries = mongo.find(qry, DatatypeLibrary.class);
    return libraries;
  }

  @Override
  public List<DatatypeLibrary> findLibrariesByFlavorName(SCOPE scope, String hl7Version,
      String name, Long accountId) {
    List<DatatypeLibrary> libraries = null;
    Criteria libCriteria = null;
    if (scope.equals(SCOPE.HL7STANDARD) || scope.equals(SCOPE.MASTER)) {
      libCriteria = Criteria.where("scope").is(scope)
          .andOperator(Criteria.where("metaData.hl7Version").is(hl7Version));
      Criteria linksCriteria =
          Criteria.where("children").elemMatch(Criteria.where("name").is(name));
      BasicQuery query =
          new BasicQuery(libCriteria.getCriteriaObject(), linksCriteria.getCriteriaObject());
      libraries = mongo.find(query, DatatypeLibrary.class);
      if (libraries != null) {
        Set<String> ids = new HashSet<String>();
        for (DatatypeLibrary lib : libraries) {
          ids.add(lib.getId());
        }
        libraries = findByIds(ids);
      }
    }
    return libraries;
  }

  @Override
  public List<DatatypeLink> findFlavors(SCOPE scope, String hl7Version, String name,
      Long accountId) {
    List<DatatypeLink> links = null;
    if (scope.equals(SCOPE.HL7STANDARD) || scope.equals(SCOPE.MASTER)) {
      Criteria libCriteria = Criteria.where("scope").is(scope)
          .andOperator(Criteria.where("metaData.hl7Version").is(hl7Version))
          .andOperator(Criteria.where("accountId").is(accountId)
              .andOperator(Criteria.where("children").elemMatch(Criteria.where("name").is(name)
                  .andOperator(Criteria.where("status").is(STATUS.PUBLISHED)))));
      Query query = Query.query(libCriteria);
      links = mongo.find(query, DatatypeLink.class);
    }
    return links;
  }

  @Override
  public Set<DatatypeLink> findChildrenById(String id) {
    log.debug("DatatypeLibraryRespositoryImpl.findChildrenById=" + id);
    DatatypeLibrary lib = findById(id);
    return lib != null ? lib.getChildren() : new HashSet<DatatypeLink>(0);
  }


}
