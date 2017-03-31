package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileComponentLibraryRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileComponentRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileComponentLibraryService;

@Service
public class ProfileComponentLibraryServiceImpl implements ProfileComponentLibraryService {
  @Autowired
  private ProfileComponentLibraryRepository profileComponentLibraryRepository;
  @Autowired
  private ProfileComponentRepository profileComponentRepository;

  @Override
  public List<ProfileComponentLibrary> findAll() {
    return profileComponentLibraryRepository.findAll();

  }

  @Override
  public List<ProfileComponent> findProfileComponentsById(String pcLibId) {
    Set<ProfileComponentLink> profileComponentLinks =
        profileComponentLibraryRepository.findChildrenById(pcLibId);
    if (profileComponentLinks != null && !profileComponentLinks.isEmpty()) {
      List<String> ids = new ArrayList<String>();
      for (ProfileComponentLink pcLink : profileComponentLinks) {
        ids.add(pcLink.getId());
      }
      return profileComponentRepository.findAllByIds(ids);
    }
    return new ArrayList<ProfileComponent>(0);
  }

  @Override
  public ProfileComponentLibrary findProfileComponentLibById(String LibId) {
    return profileComponentLibraryRepository.findOne(LibId);
  }

  @Override
  public ProfileComponentLibrary save(ProfileComponentLibrary pcLib) {
    profileComponentLibraryRepository.save(pcLib);
    return pcLib;

  }


  @Override
  public void delete(List<ProfileComponentLibrary> pcLibs) {
    profileComponentLibraryRepository.delete(pcLibs);;
  }


}
