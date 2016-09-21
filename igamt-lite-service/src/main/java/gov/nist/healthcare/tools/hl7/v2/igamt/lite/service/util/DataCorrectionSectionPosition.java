package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentSaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;


@Service
public class DataCorrectionSectionPosition {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  ProfileService profileService;

  @Autowired
  IGDocumentService documentService;


  public void resetSectionPositions() throws IGDocumentSaveException {

    List<IGDocument> igDocuments = documentService.findAll();
    for (IGDocument igdoc : igDocuments) {
      logger.debug("checking ig with id: " + igdoc.getId());
      checkAndChange(igdoc.getChildSections());
      try {
        documentService.save(igdoc);
      } catch (IGDocumentException e) {
        e.printStackTrace();
      }
    }
  }

  private void checkAndChange(Set<Section> s) {
    if (needChanges(s)) {
      setCorrectSectionPosition(s);
      for (Section child : s) {
        checkAndChange(child.getChildSections());
      }
    }
  }

  private boolean needChanges(Set<Section> s) {
    boolean rst = false;
    for (gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section child : s) {
      rst = rst | (child.getSectionPosition() == 0);
    }
    return rst;
  }

  private void setCorrectSectionPosition(Set<Section> s) {
    for (gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section child : s) {
      child.setSectionPosition(child.getSectionPosition() + 1);
    }
  }

  private void fixIgDocumentLibraries() {
    List<String> hl7Versions = new ArrayList<String>();
    hl7Versions.add("2.7.1");
    hl7Versions.add("2.7.1");
    hl7Versions.add("2.7.1");
    hl7Versions.add("2.7.1");
    hl7Versions.add("2.7.1");
    hl7Versions.add("2.7.1");
    hl7Versions.add("2.8");
    hl7Versions.add("2.8.1");
    hl7Versions.add("2.8.2");
    List<IGDocument> igDocuments = documentService.findAllByScope(IGDocumentScope.USER);

    for (IGDocument igd : igDocuments) {

      Set<String> usedSegsId = new HashSet<String>();
      SegmentLibrary segmentLib = igd.getProfile().getSegmentLibrary();
      for (SegmentLink segLink : segmentLib.getChildren()) {
        usedSegsId.add(segLink.getId());
      }
      Set<String> usedDtsId = new HashSet<String>();
      DatatypeLibrary datatypeLib = igd.getProfile().getDatatypeLibrary();
      for (DatatypeLink dtLink : datatypeLib.getChildren()) {
        usedDtsId.add(dtLink.getId());
      }

      Set<String> usedTablesId = new HashSet<String>();
      TableLibrary tableLib = igd.getProfile().getTableLibrary();
      for (TableLink tableLink : tableLib.getChildren()) {
        usedTablesId.add(tableLink.getId());
      }



    }

  }


  public static void main(String[] args) throws IOException {
    try {
      new DataCorrectionSectionPosition().resetSectionPositions();
    } catch (IGDocumentSaveException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
