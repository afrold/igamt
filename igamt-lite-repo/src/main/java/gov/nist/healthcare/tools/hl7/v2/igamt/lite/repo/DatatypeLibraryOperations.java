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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.List;
import java.util.Set;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;

/**
 * @author gcr1
 *
 */
public interface DatatypeLibraryOperations {

  public DatatypeLibrary findById(String id);

  List<DatatypeLibrary> findByScope(SCOPE scope, Long accountId);

  public List<DatatypeLibrary> findByAccountId(Long accountId, String hl7Version);

  public List<DatatypeLibrary> findScopesNVersion(List<SCOPE> scopes, String hl7version);

  public List<String> findHl7Versions();

  List<DatatypeLibrary> findDups(DatatypeLibrary dtl);

  List<DatatypeLink> findFlavors(SCOPE scope, String hl7Version, String name, Long accountId);

  List<DatatypeLibrary> findLibrariesByFlavorName(SCOPE scope, String hl7Version, String name,
      Long accountId);

  List<DatatypeLibrary> findByIds(Set<String> ids);

  public Set<DatatypeLink> findChildrenById(String id);


}
