/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import java.util.List;
import java.util.Set;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;

public interface DatatypeService {

  Datatype findById(String id);

  Datatype save(Datatype datatype);

  List<Datatype> findAll();

  List<Datatype> findByIds(Set<String> ids);

  List<Datatype> findByScopesAndVersion(List<SCOPE> scopes, String hl7Version);

  void delete(Datatype dt);

  void delete(String id);

  void save(List<Datatype> datatypes);

  Set<Datatype> collectDatatypes(Datatype datatype);

  List<Datatype> findByScope(String scope);

  Datatype findByNameAndVersionAndScope(String name, String version, String scope);

  Datatype findByNameAndVersionsAndScope(String name, String[] string, String string2);


}
