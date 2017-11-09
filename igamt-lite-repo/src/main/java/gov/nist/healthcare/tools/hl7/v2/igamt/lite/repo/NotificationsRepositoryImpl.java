package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Notifications;

public class NotificationsRepositoryImpl implements NotificationsOperations {
  @Autowired
  private MongoOperations mongo;

  @Override
  public Notifications findByIgDocumentId(String igDocumentId) {
    Criteria where = Criteria.where("igDocumentId").is(igDocumentId);
    Query qry = Query.query(where);
    return mongo.findOne(qry, Notifications.class);
  }
}
