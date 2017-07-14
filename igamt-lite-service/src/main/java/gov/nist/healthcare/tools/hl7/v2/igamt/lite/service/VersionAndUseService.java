package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.VersionAndUse;

public interface VersionAndUseService {
  List<VersionAndUse> findAll();

  VersionAndUse findById(String id);

  List<VersionAndUse> findAllByIds(List<String> ids);

  VersionAndUse save(VersionAndUse info);

  List<VersionAndUse> findByAccountId(Long id);

}
