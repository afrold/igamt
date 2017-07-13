package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.VersionAndUse;

public class VersionAndUseRepositoryImp implements VersionAndUseOperations {
  private Logger log = LoggerFactory.getLogger(VersionAndUseRepositoryImp.class);

  @Autowired
  private MongoOperations mongo;

  @Override
  public List<VersionAndUse> findAll() {
    Query qry = new Query();
    // qry = set4Brevis(qry);
    return mongo.find(qry, VersionAndUse.class);
  }

  @Override
  public VersionAndUse findById(String id) {
    Criteria where = Criteria.where("id").is(id);
    Query qry = Query.query(where);
    // qry = set4Brevis(qry);
    List<VersionAndUse> versionAndUses = mongo.find(qry, VersionAndUse.class);
    VersionAndUse versionAndUse = null;
    if (versionAndUses != null && versionAndUses.size() > 0) {
      versionAndUse = versionAndUses.get(0);
    }
    return versionAndUse;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.VersionAndUseOperations#findByAccountId(java.
   * lang.Long)
   */
  @Override
  public List<VersionAndUse> findByAccountId(Long accountId) {
    // TODO Auto-generated method stub
    Criteria where = Criteria.where("id").is(accountId);
    Query qry = Query.query(where);
    // qry = set4Brevis(qry);
    List<VersionAndUse> versionAndUses = mongo.find(qry, VersionAndUse.class);

    return versionAndUses;
  }



}
