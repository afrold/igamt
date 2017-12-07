package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ExportFontConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ExportFontConfigRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ExportFontConfigService;

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
 * <p/>
 * Created by Maxence Lefort on 2/23/17.
 */
@Service
public class ExportFontConfigServiceImpl implements ExportFontConfigService {

  @Autowired
  ExportFontConfigRepository exportFontConfigRepository;


  @Override
  public ExportFontConfig findOne(Long id) {
    return exportFontConfigRepository.findOne(id);
  }


  @Override
  public ExportFontConfig save(ExportFontConfig exportFontConfig) {

    return exportFontConfigRepository.save(exportFontConfig);
  }

  @Override
  public void delete(ExportFontConfig exportFontConfig) {
    exportFontConfigRepository.delete(exportFontConfig);
  }


  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ExportFontConfigService#findOneByAccountId(
   * java.lang.Long)
   */
  @Override
  public List<ExportFontConfig> findOneByAccountId(Long accountId) {
    // TODO Auto-generated method stub
    return exportFontConfigRepository.findOneByAccountId(accountId);
  }


  /*
   * (non-Javadoc)
   * 
   * @see gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ExportFontConfigService#
   * getDefaultExportFontConfig()
   */
  @Override
  public ExportFontConfig getDefaultExportFontConfig() throws Exception {
      return exportFontConfigRepository.findByDefaultConfig(true);
  }
}
