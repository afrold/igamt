package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import java.util.List;

import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponentLibrary;

@Service
public interface ProfileComponentLibraryService {
  List<ProfileComponentLibrary> findAll();

  List<ProfileComponent> findProfileComponentsById(String pcLibId);

  ProfileComponentLibrary findProfileComponentLibById(String LibId);

  ProfileComponentLibrary save(ProfileComponentLibrary pcLib);

  void delete(List<ProfileComponentLibrary> pcLibs);
}
