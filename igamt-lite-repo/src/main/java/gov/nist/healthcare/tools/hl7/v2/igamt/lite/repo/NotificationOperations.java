package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Notification;

public interface NotificationOperations {
  List<Notification> findByIgDocumentId(String igDocumentId);
}
