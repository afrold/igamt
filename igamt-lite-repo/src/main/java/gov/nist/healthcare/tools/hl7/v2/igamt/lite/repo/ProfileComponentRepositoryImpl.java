package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;

public class ProfileComponentRepositoryImpl implements ProfileComponentOperations {
  private Logger log = LoggerFactory.getLogger(TableRepositoryImpl.class);

  @Autowired
  private MongoOperations mongo;

  @Override
  public List<ProfileComponent> findAllByIds(List<String> ids) {
    Criteria where = Criteria.where("id").in(ids);
    Query qry = Query.query(where);
    List<ProfileComponent> profileComponents = mongo.find(qry, ProfileComponent.class);
    return profileComponents;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileComponentOperations#updateAttribute(
   * java.lang.String, java.lang.String, java.lang.Object)
   */
  @Override
  public void updateAttribute(String id, String attributeName, Object value) {
    // TODO Auto-generated method stub
    Query query = new Query();
    query.addCriteria(Criteria.where("id").is(id));
    query.fields().include(attributeName);
    Update update = new Update();
    update.set(attributeName, value);
    mongo.updateFirst(query, update, ProfileComponent.class);

  }

}
