package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.VersionAndUse;

public interface VersionAndUseOperations {

  List<VersionAndUse> findAll();

  VersionAndUse findById(String id);

  List<VersionAndUse> findByAccountId(Long accountId);

}
