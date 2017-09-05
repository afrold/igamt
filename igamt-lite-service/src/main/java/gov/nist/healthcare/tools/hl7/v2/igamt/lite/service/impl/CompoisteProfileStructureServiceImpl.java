/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Ismail Mellouli (NIST) Mar 6, 2017
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfileStructure;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.CompositeProfileRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.CompositeProfileStructureService;

@Service
public class CompoisteProfileStructureServiceImpl implements CompositeProfileStructureService {

  @Autowired
  CompositeProfileRepository compositeProfileRepository;


  @Override
  public CompositeProfileStructure findById(String id) {

    return compositeProfileRepository.findById(id);
  }


  @Override
  public CompositeProfileStructure save(CompositeProfileStructure compositeProfileStructure) {

    return compositeProfileRepository.save(compositeProfileStructure);
  }



  @Override
  public void delete(String id) {
    compositeProfileRepository.delete(id);

  }


  /*
   * (non-Javadoc)
   * 
   * @see gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.CompositeProfileStructureService#
   * updateAttribute(java.lang.String, java.lang.String, java.lang.Object)
   */
  @Override
  public void updateAttribute(String id, String attributeName, Object value) {
    // TODO Auto-generated method stub
    compositeProfileRepository.updateAttribute(id, attributeName, value);
  }


  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.CompositeProfileStructureService#findAll(
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfileStructure)
   */
  @Override
  public List<CompositeProfileStructure> findAll() {
    // TODO Auto-generated method stub
    return compositeProfileRepository.findAll();
  }

}
