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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;

public class TableLibraryRepositoryImpl implements TableLibraryOperations {

  private Logger log = LoggerFactory.getLogger(TableLibraryRepositoryImpl.class);

  @Autowired
  private MongoOperations mongo;

  @Override
  public List<TableLibrary> findByScopes(List<SCOPE> scopes) {
    Criteria where = Criteria.where("scope").in(scopes);
    Query qry = Query.query(where);
    List<TableLibrary> list = mongo.find(qry, TableLibrary.class);
    log.debug("TableLibraryRespositoryImpl.findByScopes list.size()=" + list.size());
    return list;
  }

  @Override
  public List<TableLibrary> findScopesNVersion(List<SCOPE> scopes, String hl7Version) {
    log.info("TableLibraryRespositoryImpl.findByScopesAndVersion=" + hl7Version);
    Criteria where = Criteria.where("scope").in(scopes);
    if (hl7Version != null) {
      where.andOperator(Criteria.where("metaData.hl7Version").is(hl7Version));
    }
    Query qry = Query.query(where);
    List<TableLibrary> list = mongo.find(qry, TableLibrary.class);
    log.info("TableLibraryRespositoryImpl.findByScopesAndVersion list.size()=" + list.size());
    return list;
  }

  @Override
  public List<TableLibrary> findByAccountId(Long accountId, String hl7Version) {
    log.debug("TableLibraryRespositoryImpl.findStandardByVersion=" + hl7Version);
    Criteria where = Criteria.where("accountId").is(accountId)
        .andOperator(Criteria.where("scope").is(SCOPE.USER))
        .andOperator(Criteria.where("metaData.hl7Version").is(hl7Version));
    Query qry = Query.query(where);
    List<TableLibrary> list = mongo.find(qry, TableLibrary.class);
    log.debug("TableLibraryRespositoryImpl.findStandardByVersion list.size()=" + list.size());
    return list;
  }

  @Override
  public List<String> findHl7Versions() {
    Query qry = new Query();
    qry.fields().include("metaData.hl7Version");
    List<TableLibrary> dtLibs = mongo.findAll(TableLibrary.class);
    List<String> versions = new ArrayList<String>();
    for (TableLibrary dtLib : dtLibs) {
      versions.add(dtLib.getMetaData().getHl7Version());
    }
    return versions;
  }

  @Override
  public TableLibrary findById(String id) {
    log.debug("TableLibraryRespositoryImpl.findById=" + id);
    Criteria where = Criteria.where("id").is(id);
    Query qry = Query.query(where);
    TableLibrary tableLibrary = null;
    List<TableLibrary> tableLibraries = mongo.find(qry, TableLibrary.class);
    if (tableLibraries != null && tableLibraries.size() > 0) {
      tableLibrary = tableLibraries.get(0);
    }
    return tableLibrary;
  }

  @Override
  public Set<TableLink> findChildrenById(String id) {
    log.debug("TableLibraryRespositoryImpl.findChildrenById=" + id);
    TableLibrary lib = findById(id);
    return lib != null ? lib.getChildren() : new HashSet<TableLink>(0);
  }
}
