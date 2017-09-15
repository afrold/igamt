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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  ExportConfigRepository exportConfigRepository;

  @Autowired
  static final Logger logger = LoggerFactory.getLogger(ExportConfigService.class);



  @Override public ExportConfig findOneByAccountId(Long accountId) {
    return exportConfigRepository.findOneByAccountId(accountId);
  }

  /*
     * (non-Javadoc)
     *
     * @see
     * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ExportConfigService#findDefault(java.lang.
     * String)
     */
  @Override
  public ExportConfig findDefault(Boolean setAllTrue) {
    return ExportConfig.getBasicExportConfig(setAllTrue);
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ExportConfigService#delete(gov.nist.
   * healthcare.tools.hl7.v2.igamt.lite.domain.ExportConfig)
   */
  @Override
  public void delete(ExportConfig exportConfig) {
    exportConfigRepository.delete(exportConfig);
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ExportConfigService#save(gov.nist.
   * healthcare.tools.hl7.v2.igamt.lite.domain.ExportConfig)
   */
  @Override
  public void save(ExportConfig exportConfig) {
    exportConfigRepository.save(exportConfig);
  }


}
