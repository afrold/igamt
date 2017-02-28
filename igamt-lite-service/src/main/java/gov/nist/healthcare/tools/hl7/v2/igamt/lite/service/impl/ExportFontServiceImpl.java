package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ExportFont;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ExportFontRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ExportFontService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
 * Created by Maxence Lefort on 2/25/17.
 */
@Service
public class ExportFontServiceImpl implements ExportFontService {

    @Autowired
    ExportFontRepository exportFontRepository;

    @Override public ExportFont findOne(Long id) {
        return exportFontRepository.findOne(id);
    }

    @Override public ExportFont save(ExportFont exportFont) {
        ExportFont existingFont = exportFontRepository.findOneByName(exportFont.getName());
        if(existingFont != null){
            exportFontRepository.delete(existingFont);
        }
        return exportFontRepository.save(exportFont);
    }

    @Override public void deleteAll() {
        exportFontRepository.deleteAll();
    }

    @Override public List<ExportFont> findAll() {
        return exportFontRepository.findAll();
    }


}
