/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Abdelghani EL OUAKILI (NIST) Feb 3, 2017
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ExportConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ExportConfigRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ExportConfigService;

/**
 * @author Abdelghani EL Ouakili (NIST)
 *
 */
@Service

public class ExportConfigServiceImpl implements ExportConfigService {

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ExportConfigService#findByType(java.lang.
   * String)
   */

  @Autowired
  ExportConfigRepository exportConfig;

  @Override
  public List<ExportConfig> findByType(String type) {
    // TODO Auto-generated method stub
    return exportConfig.findByType(type);

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ExportConfigService#findByTypeAndAccountId(
   * java.lang.String, java.lang.Long)
   */
  @Override
  public List<ExportConfig> findByTypeAndAccountId(String type, Long accountId) {
    // TODO Auto-generated method stub
    return exportConfig.findByTypeAndAccountId(type, accountId);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ExportConfigService#findByAccountId(java.
   * lang.Long)
   */
  @Override
  public List<ExportConfig> findByAccountId(Long accountId) {
    // TODO Auto-generated method stub
    return exportConfig.findByAccountId(accountId);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ExportConfigService#findDefault(java.lang.
   * String)
   */
  @Override
  public List<ExportConfig> findDefault(String type) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override public ExportConfig findOneByAccountId(Long accountId) {
    return exportConfig.findOneByAccountId(accountId);
  }


}
