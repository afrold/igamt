package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Notification;

public class NotificationRepositoryImpl implements NotificationOperations {
  @Autowired
  private MongoOperations mongo;

  @Override
  public List<Notification> findByIgDocumentId(String igDocumentId) {
    Criteria where = Criteria.where("igDocumentId").is(igDocumentId);
    Query qry = Query.query(where);

    List<Notification> result = mongo.find(qry, Notification.class);
    return result;
  }
}
