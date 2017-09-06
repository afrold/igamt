package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileComponentLibraryRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileComponentRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileComponentService;

@Service
public class ProfileComponentServiceImpl implements ProfileComponentService {
  Logger log = LoggerFactory.getLogger(ProfileComponentServiceImpl.class);

  @Autowired
  private ProfileComponentRepository profileComponentRepository;
  @Autowired
  private ProfileComponentLibraryRepository profileComponentLibraryRepository;

  @Override
  public ProfileComponent findById(String id) {
    log.info("ProfileComponentServiceImpl.findById=" + id);
    return profileComponentRepository.findOne(id);
  }

  @Override
  public ProfileComponent create(ProfileComponent pc) {
    log.info("ProfileComponentServiceImpl.create=" + pc);

    return profileComponentRepository.insert(pc);
  }

  @Override
  public List<ProfileComponent> findAll() {
    return profileComponentRepository.findAll();
  }

  @Override
  public ProfileComponent save(ProfileComponent pc) {
    profileComponentRepository.save(pc);
    return pc;

  }

  @Override
  public List<ProfileComponent> saveAll(List<ProfileComponent> pcs) {
    profileComponentRepository.save(pcs);
    return pcs;

  }

  @Override

  public void delete(String id) {
    profileComponentRepository.delete(id);
  }

  @Override
  public List<ProfileComponent> findByIds(List<String> pcIds) {

    return profileComponentRepository.findAllByIds(pcIds);

  }


  @Override
  public void delete(List<ProfileComponent> profileComponents) {
    profileComponentRepository.delete(profileComponents);

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileComponentService#updateAttribute(
   * java.lang.String, java.lang.String, java.lang.Object)
   */
  @Override
  public void updateAttribute(String id, String attributeName, Object value) {
    // TODO Auto-generated method stub
    profileComponentRepository.updateAttribute(id, attributeName, value);
  }



}
