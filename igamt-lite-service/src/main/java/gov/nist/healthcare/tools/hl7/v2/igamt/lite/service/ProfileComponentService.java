package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;

public interface ProfileComponentService {

  ProfileComponent findById(String id);

  ProfileComponent create(ProfileComponent pc);

  List<ProfileComponent> findAll();

  ProfileComponent save(ProfileComponent pc);

  List<ProfileComponent> saveAll(List<ProfileComponent> pc);

  void delete(String id);

  List<ProfileComponent> findByIds(List<String> pcIds);

  void delete(List<ProfileComponent> profileComponents);

  void updateAttribute(String id, String attributeName, Object value);



}
