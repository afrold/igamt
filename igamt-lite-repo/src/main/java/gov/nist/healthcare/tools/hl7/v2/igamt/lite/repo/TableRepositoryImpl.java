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
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;

public class TableRepositoryImpl implements TableOperations {

  private Logger log = LoggerFactory.getLogger(TableRepositoryImpl.class);

  @Autowired
  private MongoOperations mongo;

  @Override
  public List<Table> findByScopesAndVersion(List<SCOPE> scopes, String hl7Version) {
    Criteria where = Criteria.where("scope").in(scopes);
    where.andOperator(Criteria.where("hl7Version").is(hl7Version));
    Query qry = Query.query(where);
    return mongo.find(qry, Table.class);
  }

  @Override
  public List<Table> findBindingIdentifiers(List<String> tableIds) {
    Criteria where = Criteria.where("id").in(tableIds);
    Query qry = Query.query(where);
    qry.fields().include("bindingIdentifier");
    List<Table> tables = mongo.find(qry, Table.class);
    return tables;
  }

  @Override
  public List<Table> findUserTablesByIds(Set<String> ids) {
    Criteria where =
        Criteria.where("id").in(ids).andOperator(Criteria.where("scope").is(SCOPE.USER.toString()));
    Query qry = Query.query(where);
    List<Table> tables = mongo.find(qry, Table.class);
    return tables;
  }

  @Override
  public List<Table> findAllByIds(Set<String> ids) {
    Criteria where = Criteria.where("id").in(ids);
    Query qry = Query.query(where);
    List<Table> tables = mongo.find(qry, Table.class);
    return tables;
  }

  @Override
  public List<Table> findShared(Long accountId) {
    Query qry = new BasicQuery(
        "{ $and: [{\"shareParticipantIds\": {$exists: true}}, {$where : \"this.scope == 'USER'\"}, {$where : \"this.shareParticipantIds.length > 0\"}]}");
    return mongo.find(qry, Table.class);
  }

  @Override
  public List<Table> findShortAllByIds(Set<String> ids) {
    Criteria where = Criteria.where("id").in(ids);
    Query qry = Query.query(where);
    qry.fields().exclude("codes");
    qry.fields().exclude("contentDefinition");
    qry.fields().exclude("defPreText");
    qry.fields().exclude("defPostText");
    qry.fields().exclude("comment");
    qry.fields().exclude("extensibility");
    qry.fields().exclude("stability");
    List<Table> tables = mongo.find(qry, Table.class);
    return tables;
  }

  @Override
  public Table findByBindingIdentifierAndHL7VersionAndScope(String bindingIdentifier,
      String hl7Version, SCOPE scope) {
    Criteria where = Criteria.where("bindingIdentifier").is(bindingIdentifier).andOperator(
        Criteria.where("scope").is(scope.toString()), Criteria.where("hl7Version").is(hl7Version));
    Query qry = Query.query(where);
    Table table = mongo.findOne(qry, Table.class);
    return table;
  }

  @Override
  public Date updateDate(String id, Date date) {
    Query query = new Query();
    query.addCriteria(Criteria.where("id").is(id));
    query.fields().include("dateUpdated");
    Update update = new Update();
    update.set("dateUpdated", date);
    mongo.updateFirst(query, update, Table.class);
    return date;
  }

  @Override
  public void updateStatus(String id, STATUS status) {
    Query query = new Query();
    query.addCriteria(Criteria.where("id").is(id));
    query.fields().include("status");
    Update update = new Update();
    update.set("status", status);
    mongo.updateFirst(query, update, Table.class);
  }

  @Override
  public Table findOneShortById(String id) {
    Criteria where = Criteria.where("id").is(id);
    Query qry = Query.query(where);
    qry.fields().exclude("codes");
    qry.fields().exclude("contentDefinition");
    qry.fields().exclude("defPreText");
    qry.fields().exclude("defPostText");
    qry.fields().exclude("comment");
    qry.fields().exclude("extensibility");
    qry.fields().exclude("stability");
    return mongo.findOne(qry, Table.class);
  }

  @Override
  public List<Table> findByScope(String scope) {
    Criteria where = Criteria.where("scope").is(scope);
    Query qry = Query.query(where);
    List<Table> tables = mongo.find(qry, Table.class);
    return tables;
  }

  @Override
  public List<Table> findByBindingIdentifierAndScope(String bindingIdentifier, String scope) {
    Criteria where = Criteria.where("bindingIdentifier").is(bindingIdentifier)
        .regex(Pattern.compile(bindingIdentifier, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE))

        .andOperator(Criteria.where("scope").is(scope.toString()));
    Query qry = Query.query(where);
    qry.fields().exclude("codes");
    qry.fields().exclude("contentDefinition");
    qry.fields().exclude("defPreText");
    qry.fields().exclude("defPostText");
    qry.fields().exclude("comment");
    List<Table> tables = mongo.find(qry, Table.class);
    return tables;
  }

  @Override
  public Table findOneByScopeAndBindingIdentifier(String scope, String bindingIdentifier) {
    Criteria where = Criteria.where("bindingIdentifier")
        .regex(Pattern.compile(bindingIdentifier, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE))
        .andOperator(Criteria.where("scope").is(scope));
    Query qry = Query.query(where);
    Table table = mongo.findOne(qry, Table.class);
    return table;
  }

  @Override
  public List<Table> findByScopeAndVersion(String scope, String hl7Version) {
    Criteria where = Criteria.where("hl7Version").is(hl7Version)
        .andOperator(Criteria.where("scope").is(scope.toString()));
    Query qry = Query.query(where);
    List<Table> tables = mongo.find(qry, Table.class);
    return tables;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.TableOperations#updateDescription(java.lang.
   * String, java.lang.String)
   */
  @Override
  public void updateDescription(String id, String description) {
    // TODO Auto-generated method stub
    Query query = new Query();
    query.addCriteria(Criteria.where("id").is(id));
    query.fields().include("defPreText");
    Update update = new Update();
    update.set("defPreText", description);
    mongo.updateFirst(query, update, Table.class);

  }
  
  @Override
  public void updateAllDescription(String id, String description, String defPreText, String defPostText) {
    // TODO Auto-generated method stub
    Query query = new Query();
    query.addCriteria(Criteria.where("id").is(id));
    query.fields().include("description");
    query.fields().include("defPreText");
    query.fields().include("defPostText");
    Update update = new Update();
    update.set("description", description);
    update.set("defPreText", defPreText);
    update.set("defPostText", defPostText);
    mongo.updateFirst(query, update, Table.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.TableOperations#updateCodeSystem(java.lang.
   * String, java.util.Set)
   */
  @Override
  public void updateCodeSystem(String id, Set<String> codesSystemtoAdd) {
    // TODO Auto-generated method stub
    Query query = new Query();
    query.addCriteria(Criteria.where("id").is(id));
    query.fields().include("codeSystems");
    Update update = new Update();
    update.set("codeSystems", codesSystemtoAdd);
    mongo.updateFirst(query, update, Table.class);

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.TableOperations#updateAttributes(java.lang.
   * String, java.lang.String)
   */
  @Override
  public void updateAttributes(String id, String attributeName, Object value) {
    // TODO Auto-generated method stub
    Query query = new Query();
    query.addCriteria(Criteria.where("id").is(id));
    query.fields().include(attributeName);
    Update update = new Update();
    update.set(attributeName, value);
    mongo.updateFirst(query, update, Table.class);

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.TableOperations#findShortByScope(java.lang.
   * String)
   */
  @Override
  public List<Table> findShortByScope(String scope) {

    Criteria where = Criteria.where("scope").is(scope);
    Query qry = Query.query(where);
    qry.fields().exclude("codes");
    qry.fields().exclude("contentDefinition");
    qry.fields().exclude("defPreText");
    qry.fields().exclude("defPostText");
    qry.fields().exclude("comment");
    List<Table> tables = mongo.find(qry, Table.class);
    return tables;
  }

  @Override
  public Table findShortById(String id) {

    Criteria where = Criteria.where("id").is(id);
    Query qry = Query.query(where);
    qry.fields().exclude("codes");
    Table table = mongo.findOne(qry, Table.class);
    return table;
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.TableOperations#
   * findByScopeAndVersionAndBindingIdentifier(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public List<Table> findByScopeAndVersionAndBindingIdentifier(String scope, String version,
      String bindingIdentifier) {
    Criteria where = Criteria.where("bindingIdentifier")
        .regex(Pattern.compile(bindingIdentifier, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE))
        .andOperator(Criteria.where("scope").is(scope.toString()),
            Criteria.where("hl7Version").is(version));
    Query qry = Query.query(where);
    qry.fields().exclude("codes");
    qry.fields().exclude("contentDefinition");
    qry.fields().exclude("defPreText");
    qry.fields().exclude("defPostText");
    qry.fields().exclude("comment");

    return mongo.find(qry, Table.class);
  }

}
