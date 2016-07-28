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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.unit;

import static org.junit.Assert.assertNotNull;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentExportService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.IGDocumentServiceImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceContextUnit.class})
public class IGExportTest {
  Logger logger = LoggerFactory.getLogger(IGExportTest.class);

  @Autowired
  IGDocumentServiceImpl igService;

  @Autowired
  IGDocumentExportService igExport;

  @Autowired
  private DatatypeService datatypeService;

  @Autowired
  private SegmentService segmentService;

  @Autowired
  private TableService tableService;

  List<IGDocument> igs;
  IGDocument ig;
  Profile p;
  SegmentLink sl;
  DatatypeLink dl;
  TableLink tl;
  InputStream content = null;
  File tmpFile = null;
  String timeStamp = null;


  @Before
  public void setUp() throws Exception {
    igs = igService.findAll();
    ig = igs.get(0);

    sl = (SegmentLink) ig.getProfile().getSegmentLibrary().getChildren().toArray()[0];
    dl = (DatatypeLink) ig.getProfile().getDatatypeLibrary().getChildren().toArray()[0];
    tl = (TableLink) ig.getProfile().getTableLibrary().getChildren().toArray()[0];
    
  }

  @After
  public void tearDown() throws Exception {}

  @Rule
  public TestWatcher testWatcher = new TestWatcher() {
    protected void failed(Throwable e, Description description) {
      logger.debug(description.getDisplayName() + " failed " + e.getMessage());
      super.failed(e, description);
    }
  };



  @Test
  public void testCallService() {
    assertNotNull(datatypeService);
    assertNotNull(segmentService);
    assertNotNull(tableService);
    assertNotNull(datatypeService.findAll().size());
    assertNotNull(segmentService.findAll().size());
    assertNotNull(tableService.findAll().size());

  }

  @Test
  public void testCallIGExportXml() {
    try {
      content = igExport.exportAsXmlDisplay(ig);
      assertNotNull(content);
      timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      tmpFile = new File("IG_" + timeStamp + ".xml");
      logger.debug("Writing to file");
      FileUtils.copyInputStreamToFile(content, tmpFile);
      logger.debug("Export done");

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testCallIGExportHtml() {
    try {
      content = igExport.exportAsHtml(ig);
      assertNotNull(content);
      timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      tmpFile = new File("IG_" + timeStamp + ".html");
      logger.debug("Writing to file");
      FileUtils.copyInputStreamToFile(content, tmpFile);
      logger.debug("Export done");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testCallIGExportDocx() {
    try {
      content = igExport.exportAsDocx(ig);
      assertNotNull(content);
      timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      tmpFile = new File("IG_" + timeStamp + ".docx");
      logger.debug("Writing to file");
      FileUtils.copyInputStreamToFile(content, tmpFile);
      logger.debug("Export done");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void testCallIGExportPdf() {
    try {
      content = igExport.exportAsPdf(ig);
      assertNotNull(content);
      timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      tmpFile = new File("IG_" + timeStamp + ".pdf");
      logger.debug("Writing to file");
      FileUtils.copyInputStreamToFile(content, tmpFile);
      logger.debug("Export done");

    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  @Test
  public void testCallSgtExportXml() {
    try {
      content = igExport.exportAsXmlSegment(sl);
      assertNotNull(content);
      timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      tmpFile = new File("SGT_" + timeStamp + ".xml");
      logger.debug("Writing to file");
      FileUtils.copyInputStreamToFile(content, tmpFile);
      logger.debug("Export done");

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testCallSgtExportHtml() {
    try {
      content = igExport.exportAsHtmlSegment(sl);
      assertNotNull(content);
      timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      tmpFile = new File("SGT_" + timeStamp + ".html");
      logger.debug("Writing to file");
      FileUtils.copyInputStreamToFile(content, tmpFile);
      logger.debug("Export done");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testCallSgtExportDocx() {
    try {
      content = igExport.exportAsDocxSegment(sl);
      assertNotNull(content);
      timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      tmpFile = new File("SGT_" + timeStamp + ".docx");
      logger.debug("Writing to file");
      FileUtils.copyInputStreamToFile(content, tmpFile);
      logger.debug("Export done");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testCallDTExportXml() {
    try {
      content = igExport.exportAsXmlDatatype(dl);
      assertNotNull(content);
      timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      tmpFile = new File("DT_" + timeStamp + ".xml");
      logger.debug("Writing to file");
      FileUtils.copyInputStreamToFile(content, tmpFile);
      logger.debug("Export done");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testCallDTExportHtml() {
    try {
      content = igExport.exportAsHtmlDatatype(dl);
      assertNotNull(content);
      timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      tmpFile = new File("DT_" + timeStamp + ".html");
      logger.debug("Writing to file");
      FileUtils.copyInputStreamToFile(content, tmpFile);
      logger.debug("Export done");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testCallDTExportDocx() {
    try {
      content = igExport.exportAsDocxDatatype(dl);
      assertNotNull(content);
      timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      tmpFile = new File("DT_" + timeStamp + ".docx");
      logger.debug("Writing to file");
      FileUtils.copyInputStreamToFile(content, tmpFile);
      logger.debug("Export done");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testCallTblExportXml() {
    try {
      content = igExport.exportAsXmlTable(tl);
      assertNotNull(content);
      timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      tmpFile = new File("TBL_" + timeStamp + ".xml");
      logger.debug("Writing to file");
      FileUtils.copyInputStreamToFile(content, tmpFile);
      logger.debug("Export done");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void testCallTblExportHtml() {
    try {
      content = igExport.exportAsHtmlTable(tl);
      assertNotNull(content);
      timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      tmpFile = new File("TBL_" + timeStamp + ".html");
      logger.debug("Writing to file");
      FileUtils.copyInputStreamToFile(content, tmpFile);
      logger.debug("Export done");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void testCallTblExportDocx() {
    try {
      content = igExport.exportAsDocxTable(tl);
      assertNotNull(content);
      timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      tmpFile = new File("TBL_" + timeStamp + ".docx");
      logger.debug("Writing to file");
      FileUtils.copyInputStreamToFile(content, tmpFile);
      logger.debug("Export done");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testCallDTLExportDocx() {
    try {
      content = igExport.exportAsDocxDatatypes(ig);
      assertNotNull(content);
      timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      tmpFile = new File("DTL_" + timeStamp + ".docx");
      logger.debug("Writing to file");
      FileUtils.copyInputStreamToFile(content, tmpFile);
      logger.debug("Export done");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  
  @Test
  public void testCallDTLExportHtml() {
    try {
      content = igExport.exportAsHtmlDatatypes(ig);
      assertNotNull(content);
      timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      tmpFile = new File("DTL_" + timeStamp + ".html");
      logger.debug("Writing to file");
      FileUtils.copyInputStreamToFile(content, tmpFile);
      logger.debug("Export done");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
