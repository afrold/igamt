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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.integration;

import static org.junit.Assert.assertTrue;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.IGDocumentServiceImpl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Not a standalone test. Intended to be called by a junit test.
 * 
 * A set of tests to determine the internal integrity of references in a Profile. Checks that all
 * segmentRefs refer to valid segments. Checks that all Fields reference valid dataypes. Checks that
 * all Datatypes.Components reference valid datatypes. Intended to be called by a junit test.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IntegrationTestApplicationConfig.class})
public class IGCollectionReferentialIntegrityTest {

  private static final Logger log = LoggerFactory
      .getLogger(IGCollectionReferentialIntegrityTest.class);

  @Autowired
  IGDocumentServiceImpl igService;

  @Autowired
  private MessageService messageService;

  @Autowired
  private DatatypeService datatypeService;

  @Autowired
  private SegmentService segmentService;

  @Autowired
  private TableService tableService;

  List<IGDocument> igs;
  IGDocument ig;
  Profile profile;
  SegmentLink sl;
  DatatypeLink dl;
  TableLink tl;
  File tmpFile = null;
  String timeStamp = null;
  StringBuilder analysisRst;
  boolean okStatus = true;
  boolean found = false;


  @Before
  public void setUp() throws Exception {
    igs = igService.findAll();
  }

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testMessagesReferentialIntegrity() {
    //  Check that messages referenced in an IG can be found
    log.info("Running testMessagesIntegrity");

    timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    tmpFile = new File("testMessages_" + timeStamp + ".txt");
    analysisRst = new StringBuilder();

    log.debug("Writing to file " + tmpFile.getName());

    okStatus = true;

    for (IGDocument ig : igs){
      analysisRst.append("IGDocument: " + ig.getId() + "\n");

      profile = ig.getProfile();

      Iterator<Message> itr = profile.getMessages().getChildren().iterator();
      while (itr.hasNext()) {
        Message msg = (Message) itr.next();

        found = (messageService.findById(msg.getId()) != null);
        okStatus = okStatus && found;

        if (!found){
          analysisRst.append("\tMessage id: " + msg.getId() + " not found.\n");
        }
      }
    }
    try {
      FileUtils.writeStringToFile(tmpFile, analysisRst.toString());
    } catch (IOException e) {
      e.printStackTrace();
      log.debug(analysisRst.toString());
      log.debug("couldn't write report");
    }
    assertTrue(okStatus);
  }


  @Test
  public void testSegmentReferentialIntegrity() {
    //  Check that segments referenced in an IG can be found
    log.info("Running testSegmentIntegrity");

    timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    tmpFile = new File("testSegment_" + timeStamp + ".txt");
    analysisRst = new StringBuilder();

    log.debug("Writing to file " + tmpFile.getName());

    okStatus = true;

    for (IGDocument ig : igs){
      analysisRst.append("IGDocument: " + ig.getId() + "\n");

      profile = ig.getProfile();

      Iterator<Message> itrMsg = profile.getMessages().getChildren().iterator();
      while (itrMsg.hasNext()) {
        Message msg = (Message) itrMsg.next();
        analysisRst.append("\tMessage id: " + msg.getId() + "\n");

        List<SegmentRefOrGroup> segrefs = msg.getChildren();
        for (SegmentRefOrGroup srog : segrefs){
          List<String> segrefIds = collectSegmentRefOrGroupIds(srog);

          Iterator<String> itrSgt = segrefIds.iterator();
          while (itrSgt.hasNext()) {
            String sgtId = (String)itrSgt.next();
            found = (segmentService.findById(sgtId) != null);
            okStatus = okStatus && found;
            if (!found){
              analysisRst.append("Segment id: " + sgtId + " not found.\n");
            } 
          } 
        }
      }
    }

    try {
      FileUtils.writeStringToFile(tmpFile, analysisRst.toString());
    } catch (IOException e) {
      e.printStackTrace();
      log.debug(analysisRst.toString());
      log.debug("couldn't write report");
    }
    assertTrue(okStatus);
  }


  @Test
  public void testSegmentContentRerentialIntegrity() {
    //  Check that segments referenced in an IG can be found
    log.info("Running testSegmentContentRerentialIntegrity");

    timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    File report = new File("testSegmentContent_" + timeStamp + ".txt");

    log.debug("Writing to file " + report.getName());

    okStatus = true;

    for (IGDocument ig : igs){      
      addTextInReport(report, "IGDocument: " + ig.getId() + "\n", true);

      profile = ig.getProfile();

      Iterator<Message> itrMsg = profile.getMessages().getChildren().iterator();
      while (itrMsg.hasNext()) {
        Message msg = (Message) itrMsg.next();
        addTextInReport(report, "\tMessage id: " + msg.getId() + "\n", true);

        List<SegmentRefOrGroup> segrefs = msg.getChildren();
        for (SegmentRefOrGroup srog : segrefs){
          List<String> segrefIds = collectSegmentRefOrGroupIds(srog);

          Iterator<String> itrSgt = segrefIds.iterator();
          while (itrSgt.hasNext()) {
            analysisRst = new StringBuilder();
            String sgtId = (String)itrSgt.next();
            Segment sgt = segmentService.findById(sgtId);
            if (sgt != null){
              analysisRst.append("\t\tSegment id: "+ sgt.getId() + "\n");
              List<Field> fields = sgt.getFields();
              for (Field f : fields){
                found = true;
                analysisRst.append("\t\t\tField id: "+ f.getId() + "\n");
                Datatype d = datatypeService.findById(f.getDatatype().getId());
                found = (d != null);
                found = found && (d != null);
                if (!(d != null)){
                  analysisRst.append("\t\t\t\tDatatype id: " + f.getDatatype().getId() + " not found.\n");
                } 
//                List<TableLink> tls = f.getTables();
//                for (TableLink tl: tls){
//                  Table t = tableService.findById(tl.getId());
//                  found = found && (t != null);
//                  if (!(t != null)){
//                    analysisRst.append("\t\t\t\tValue set id: " + tl.getId() + " not found.\n");
//                  } 
//                }
                okStatus = okStatus && found;
                if (!found){
                  addTextInReport(report, analysisRst.toString(), true);
                } 
              }
            }
          }
        }
      }
    }
    assertTrue(okStatus);
  }

  @Test
  public void testComponentDatatypes() {
    // Check contents of datatype library 
    log.info("Running testComponentDataypes...");

    timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    File report = new File("testComponentDatatypes_" + timeStamp + ".txt");

    log.debug("Writing to file " + report.getName());

    okStatus = true;

    List<Datatype> dts = datatypeService.findAll();

    Iterator<Datatype> itrDt = dts.iterator();
    while (itrDt.hasNext()) {
      Datatype dt = (Datatype) itrDt.next();
      addTextInReport(report, "dt id: " + dt.getId() + "\n", true);

      Iterator<Component> itrCpt = dt.getComponents().iterator();
      while (itrCpt.hasNext()) {
        found = true;
        analysisRst = new StringBuilder();
        Component cpt = itrCpt.next();
        analysisRst.append("\tComponent id: " + cpt.getId() + "\n");

        Datatype d = datatypeService.findById(cpt.getDatatype().getId());
        found = (d != null);
        if (!(d != null)){
          analysisRst.append("\t\tDatatype id: " + cpt.getDatatype().getId() + " not found.\n");
        } 
//        List<TableLink> tls = cpt.getTables();
//        for (TableLink tl: tls){
//          Table t = tableService.findById(tl.getId());
//          found = found && (t != null);
//          if (!(t != null)){
//            analysisRst.append("\t\tValue set id: " + tl.getId() + " not found.\n");
//          } 
//        }
        if (!found){
          addTextInReport(report, analysisRst.toString(), true);
        } 
        okStatus = okStatus && found;
      }
    } 
    assertTrue(okStatus);
  }

  private List<String> collectSegmentRefOrGroupIds(SegmentRefOrGroup srog){
    List<String> segrefIds = new ArrayList<String>();
    if (srog instanceof SegmentRef) {
      segrefIds.add(collectSegmentId((SegmentRef) srog));
    } else if (srog instanceof Group) {
      segrefIds.addAll(collectGroupIds((Group)srog));
    } else {
      log.error("Neither SegRef nor Group srog=" + srog.getType() + "=");
    }
    return segrefIds;
  }

  private String collectSegmentId(SegmentRef sgtRef) {
    return sgtRef.getRef().getId();
  }

  // A little recursion to get all SegmentRefs buried in Groups.
  private List<String> collectGroupIds(Group group) {
    List<String> refs = new ArrayList<String>();
    for (SegmentRefOrGroup srog : group.getChildren()) {
      if (srog instanceof SegmentRef) {
        refs.add(collectSegmentId((SegmentRef) srog));
      } else if (srog instanceof Group) {
        refs.addAll(collectGroupIds((Group)srog));
      } else {
        log.error("Neither SegRef nor Group sog=" + srog.getType() + "=");
      }
    }
    return refs;
  }

  private void addTextInReport(File report, String text, boolean append){
    try {
      FileUtils.writeStringToFile(report, text, append);
    } catch (IOException e) {
      e.printStackTrace();
      log.debug(text);
      log.debug("couldn't add text");
    }
  }

}
