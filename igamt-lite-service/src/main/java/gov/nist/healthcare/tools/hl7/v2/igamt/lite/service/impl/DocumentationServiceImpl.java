/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Abdelghani EL OUAKILI (NIST) Jan 26, 2017
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Documentation;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DocumentationRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DocumentationService;

/**
 * @author Abdelghani EL Ouakili (NIST)
 *
 */
@Service
public class DocumentationServiceImpl implements DocumentationService {


  Logger log = LoggerFactory.getLogger(DocumentationServiceImpl.class);

  @Autowired
  private DocumentationRepository documentationRepository;

  @Override
  public List<Documentation> findAll() {
    // TODO Auto-generated method stub
    return documentationRepository.findAll();

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DocumentationService#findById(java.lang.
   * String)
   */
  @Override
  public Documentation findById(String id) {
    // TODO Auto-generated method stub
    return documentationRepository.findOne(id);
  }

  @Override
  public Documentation save(Documentation d) {
    // TODO Auto-generated method stub

    return documentationRepository.save(d);
  }

  @Override
  public void delete(Documentation d) {
    // TODO Auto-generated method stub

    documentationRepository.delete(d);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DocumentationService#findByIds(java.util.
   * Set)
   */
  @Override
  public List<Documentation> findByIds(Set<String> ids) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DocumentationService#updateDate(java.lang.
   * String, java.util.Date)
   */
  @Override
  public Date updateDate(String id, Date date) {
    // TODO Auto-generated method stub
    return documentationRepository.updateDate(id, date);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DocumentationService#findByCreator(java.
   * lang.Long)
   */
  @Override
  public List<Documentation> findByOwner(Long accontId) {
    // TODO Auto-generated method stub
    return documentationRepository.findByOwner(accontId);
  }




}
