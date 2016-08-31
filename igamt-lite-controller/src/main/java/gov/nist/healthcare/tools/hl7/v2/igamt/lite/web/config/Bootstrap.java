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

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentSaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerializationImpl;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.DataCorrectionSectionPosition;

@Service
public class Bootstrap implements InitializingBean {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  ProfileService profileService;

  @Autowired
  IGDocumentService documentService;

  @Autowired
  MessageService messageService;

  @Autowired
  SegmentService segmentService;
  @Autowired
  DatatypeService datatypeService;

  @Autowired
  TableService tableService;
  @Autowired
  DataCorrectionSectionPosition dataCorrectionSectionPosition;

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    // Carefully use this. It will delete all of existing IGDocuments and
    // make new ones converted from the "igdocumentPreLibHL7",
    // "igdocumentPreLibPRELOADED" , and ""igdocumentPreLibUSER"
    // new IGDocumentConverterFromOldToNew().convert();
    // new DataCorrection().updateSegment();
    // new DataCorrection().updateDatatype();
    // new DataCorrection().updateSegmentLibrary();
    // new DataCorrection().updateDatatypeLibrary();
    // new DataCorrection().updateTableLibrary();
    // new DataCorrection().updateMessage();
    //
    // dataCorrectionSectionPosition.resetSectionPositions();
    // new DataCorrection().updateValueSetForSegment();
    // new DataCorrection().updateValueSetsForDT();
    //addVersionAndScopetoPRELOADEDIG();
    //addVersionAndScopetoHL7IG();

  }

  private void loadPreloadedIGDocuments() throws Exception {
    IGDocument d = new IGDocument();

    String p = IOUtils.toString(this.getClass().getResourceAsStream("/profiles/IZ_Profile.xml"));
    String v =
        IOUtils.toString(this.getClass().getResourceAsStream("/profiles/IZ_ValueSetLibrary.xml"));
    String c =
        IOUtils.toString(this.getClass().getResourceAsStream("/profiles/IZ_Constraints.xml"));
    Profile profile = new ProfileSerializationImpl().deserializeXMLToProfile(p, v, c);

    profile.setScope(IGDocumentScope.PRELOADED);

    d.addProfile(profile);

    boolean existPreloadedDocument = false;

    String documentID = d.getMetaData().getIdentifier();
    String documentVersion = d.getMetaData().getVersion();

    List<IGDocument> igDocuments = documentService.findAll();

    for (IGDocument igd : igDocuments) {
      if (igd.getScope().equals(IGDocumentScope.PRELOADED)
          && documentID.equals(igd.getMetaData().getIdentifier())
          && documentVersion.equals(igd.getMetaData().getVersion())) {
        existPreloadedDocument = true;
      }
    }
    if (!existPreloadedDocument)
      documentService.save(d);
  }

  // NOTE:ADD version to preloaded segs,dts,vs
  private void addVersionAndScopetoHL7IG() {
    List<String> hl7Versions = new ArrayList<String>();
    hl7Versions.add("2.1");
    hl7Versions.add("2.2");
    hl7Versions.add("2.3");
    hl7Versions.add("2.3.1");
    hl7Versions.add("2.4");
    hl7Versions.add("2.5");
    hl7Versions.add("2.5.1");
    hl7Versions.add("2.6");
    hl7Versions.add("2.7");

    List<IGDocument> igDocuments =
        documentService.findByScopeAndVersions(IGDocumentScope.HL7STANDARD, hl7Versions);
    for (IGDocument igd : igDocuments) {
      Messages msgs = igd.getProfile().getMessages();
      System.out.println(msgs.getChildren().size());
      for (Message msg : msgs.getChildren()) {
        msg.setScope(SCOPE.HL7STANDARD);
        msg.setHl7Version(igd.getMetaData().getHl7Version());


      }
      messageService.save(msgs.getChildren());


    }
  }

  private void addVersionAndScopetoPRELOADEDIG() {
    List<String> hl7Versions = new ArrayList<String>();
    hl7Versions.add("2.1");
    hl7Versions.add("2.2");
    hl7Versions.add("2.3");
    hl7Versions.add("2.3.1");
    hl7Versions.add("2.4");
    hl7Versions.add("2.5");
    hl7Versions.add("2.5.1");
    hl7Versions.add("2.6");
    hl7Versions.add("2.7");

    List<IGDocument> igDocuments =
        documentService.findByScopeAndVersions(IGDocumentScope.PRELOADED, hl7Versions);
    Set<String> segIds = new HashSet<String>();
    for (IGDocument igd : igDocuments) {
      Messages msgs = igd.getProfile().getMessages();
      for (Message msg : msgs.getChildren()) {
        msg.setScope(SCOPE.PRELOADED);
        for (SegmentRefOrGroup segRef : msg.getChildren()) {

          if (segRef instanceof SegmentRef) {
            segIds.add(((SegmentRef) segRef).getRef().getId());
          } else if (segRef instanceof Group) {
            segIds.addAll(processGrp((Group) segRef));
          }
        }
        List<Segment> preSegs = segmentService.findByIds(segIds);
        Set<String> preDtsId = new HashSet<String>();
        Set<String> preVssId = new HashSet<String>();
        List<Segment> segToSave = new ArrayList<Segment>();
        List<Datatype> dtToSave = new ArrayList<Datatype>();
        List<Table> tableToSave = new ArrayList<Table>();
        for (Segment seg : preSegs) {
          if (seg.getScope() == SCOPE.USER) {
            seg.setScope(SCOPE.PRELOADED);
            for (Field fld : seg.getFields()) {
              preDtsId.add(fld.getDatatype().getId());
              for (TableLink t : fld.getTables()) {
                preVssId.add(t.getId());
              }
            }
            List<Datatype> preDts = datatypeService.findByIds(preDtsId);
            // List<Table> preVss=tableService.findAllByIds(preVssId);
            for (Datatype dt : preDts) {
              if (dt.getScope() == SCOPE.USER) {
                for (Component comp : dt.getComponents()) {
                  for (TableLink t : comp.getTables()) {
                    preVssId.add(t.getId());
                  }
                }
                dt.setScope(SCOPE.PRELOADED);
                dtToSave.add(dt);

              }
            }
            Set<String> preDtsIdInComp = new HashSet<String>();
            for (Datatype dtInComp : dtToSave) {
              for (Component comp : dtInComp.getComponents()) {

                preDtsIdInComp.add(comp.getDatatype().getId());
              }

            }
            List<Datatype> preDtsInComp = datatypeService.findByIds(preDtsIdInComp);
            for (Datatype dt : preDtsInComp) {
              if (dt.getScope() == SCOPE.USER) {
                for (Component comp : dt.getComponents()) {
                  for (TableLink t : comp.getTables()) {
                    preVssId.add(t.getId());
                  }
                }
                dt.setScope(SCOPE.PRELOADED);
                dtToSave.add(dt);

              }
            }
            List<Table> preVs = tableService.findAllByIds(preVssId);

            for (Table preTable : preVs) {
              if (preTable.getScope() == SCOPE.USER) {
                preTable.setScope(SCOPE.PRELOADED);
                tableToSave.add(preTable);
              }
            }
            System.out.println(dtToSave);
            segToSave.add(seg);

          }
        }
        segmentService.save(segToSave);
        datatypeService.save(dtToSave);
        tableService.save(tableToSave);

        msg.setHl7Version(igd.getMetaData().getHl7Version());



      }
      messageService.save(msgs.getChildren());


    }
  }

  // private Set<String> SegIdsInMsg(Message msg, Set<String> result){
  // for(SegmentRefOrGroup segRef: msg.getChildren()){
  //
  // }
  // }
  private Set<String> processGrp(Group grp) {
    Set<String> result = new HashSet<String>();
    for (SegmentRefOrGroup segOrGrp : grp.getChildren()) {
      if (segOrGrp instanceof SegmentRef) {
        result.add(((SegmentRef) segOrGrp).getRef().getId());
      } else if (segOrGrp instanceof Group) {
        result.addAll(processGrp((Group) segOrGrp));
      }
    }
    return result;

  }

  private void checkTableNameForAllIGDocuments() throws IGDocumentSaveException {

    List<IGDocument> igDocuments = documentService.findAll();

    for (IGDocument igd : igDocuments) {
      boolean ischanged = false;
      TableLibrary tables = igd.getProfile().getTableLibrary();

      for (TableLink tl : tables.getChildren()) {
        // if (t.getName() == null || t.getName().equals("")) {
        // if (t.getDescription() != null) {
        // t.setName(t.getDescription());
        // ischanged = true;
        // } else
        // t.setName("NONAME");
        // }
      }

      if (ischanged)
        documentService.apply(igd);
    }
  }

}
