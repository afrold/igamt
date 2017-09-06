package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;

public interface ProfileComponentOperations {
  List<ProfileComponent> findAllByIds(List<String> ids);

  void updateAttribute(String id, String attributeName, Object value);

}
