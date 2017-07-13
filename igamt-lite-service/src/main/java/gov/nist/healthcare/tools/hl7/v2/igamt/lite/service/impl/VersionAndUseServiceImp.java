package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.VersionAndUse;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.VersionAndUseRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.VersionAndUseService;

@Service
public class VersionAndUseServiceImp implements VersionAndUseService {
  Logger log = LoggerFactory.getLogger(VersionAndUseServiceImp.class);

  @Autowired
  private VersionAndUseRepository VersionAndUseRepository;

  public VersionAndUseServiceImp() {
    // TODO Auto-generated constructor stub
  }

  @Override
  public List<VersionAndUse> findAll() {

    // TODO Auto-generated method stub
    return VersionAndUseRepository.findAll();
  }

  @Override
  public VersionAndUse findById(String id) {
    // TODO Auto-generated method stub
    return VersionAndUseRepository.findById(id);
  }

  @Override
  public VersionAndUse save(VersionAndUse inf) {
    // TODO Auto-generated method stub
    return VersionAndUseRepository.save(inf);
  }

  @Override
  public List<VersionAndUse> findAllByIds(List<String> ids) {
    // TODO Auto-generated method stub
    return (List<VersionAndUse>) VersionAndUseRepository.findAll(ids);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.VersionAndUseService#findByAccountId(java.
   * lang.Long)
   */
  @Override
  public List<VersionAndUse> findByAccountId(Long id) {
    // TODO Auto-generated method stub
    return VersionAndUseRepository.findByAccountId(id);
  }



}
