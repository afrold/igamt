package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Notifications;

public interface NotificationsOperations {
  Notifications findByIgDocumentId(String igDocumentId);
}
