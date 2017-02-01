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

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Decision;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DecisionRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DecisionService;

/**
 * @author Abdelghani EL Ouakili (NIST)
 *
 */
@Service
public class DecisionServiceImpl implements DecisionService {


  Logger log = LoggerFactory.getLogger(DecisionServiceImpl.class);

  @Autowired
  private DecisionRepository decisionRepository;

  @Override
  public List<Decision> findAll() {
    // TODO Auto-generated method stub
    return decisionRepository.findAll();

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DecisionService#findById(java.lang.String)
   */
  @Override
  public Decision findById(String id) {
    // TODO Auto-generated method stub
    return decisionRepository.findOne(id);
  }

  @Override
  public Decision save(Decision d) {
    // TODO Auto-generated method stub

    return decisionRepository.save(d);
  }

  @Override
  public void delete(Decision d) {
    // TODO Auto-generated method stub

    decisionRepository.delete(d);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DecisionService#findByIds(java.util.Set)
   */
  @Override
  public List<Decision> findByIds(Set<String> ids) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DecisionService#updateDate(java.lang.
   * String, java.util.Date)
   */
  @Override
  public Date updateDate(String id, Date date) {
    // TODO Auto-generated method stub
    return decisionRepository.updateDate(id, date);
  }

}
