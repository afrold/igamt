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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.SerializationException;
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

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentExportService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.IGDocumentServiceImpl;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.integration.IntegrationTestApplicationConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IntegrationTestApplicationConfig.class})
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

		//    sl = (SegmentLink) ig.getProfile().getSegmentLibrary().getChildren().toArray()[0];
		//    dl = (DatatypeLink) ig.getProfile().getDatatypeLibrary().getChildren().toArray()[0];
		//    tl = (TableLink) ig.getProfile().getTableLibrary().getChildren().toArray()[0];

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
			assertNotNull(ig);
			content = igExport.exportAsXmlDisplay(ig);
			assertNotNull(content);
			tmpFile = new File(setFilename("IG_", "_" + ig.getId(), "xml"));
			logger.debug("Writing to file");
			FileUtils.copyInputStreamToFile(content, tmpFile);
			logger.debug("Export done");

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SerializationException e) {
				e.printStackTrace();
		}
	}

	@Test
	public void testCallIGExportXml_All() {
		File xmlExport = new File(setFilename("export_all_report", "_all_xml", "txt"));
		StringBuilder rst = new StringBuilder();
		igs = igService.findAll();
		for (IGDocument ig : igs){
			rst.append("ig " + ig.getId());
			try {
				FileUtils.writeStringToFile(xmlExport, "ig " + ig.getId());
				content = igExport.exportAsXmlDisplay(ig);
				assertNotNull(content);
				tmpFile = new File(setFilename("IG_", "_" + ig.getId(), "xml"));
				logger.debug("Writing to file");
				FileUtils.copyInputStreamToFile(content, tmpFile);
				logger.debug("Export done");
				rst.append(": " + String.valueOf(tmpFile.length()/1024) + " kb - " + tmpFile.getName() + " " + " ok\n");
			} catch (IOException e) {
				e.printStackTrace();
				rst.append(": " + String.valueOf(tmpFile.length()/1024) + " kb - " + tmpFile.getName() + " "  + e.getMessage() + "\n");
			} catch (Exception e) {
				e.printStackTrace();
				rst.append(": " + String.valueOf(tmpFile.length()/1024) + " kb - " + tmpFile.getName() + " "  + e.getMessage() + "\n");
			}
		}
		try {
			FileUtils.writeStringToFile(xmlExport, rst.toString());
		} catch (IOException e) {
			e.printStackTrace();
			logger.debug(rst.toString());
			logger.debug("couldn't write report");
		}
	}

	@Test
	public void testCallIGExportXml_User() {
		File xmlExport = new File(setFilename("export_report", "_all_user_xml", "txt"));
		StringBuilder rst = new StringBuilder();
	    igs = igService.findAllByScope(IGDocumentScope.USER);
		for (IGDocument ig : igs){
			rst.append("ig " + ig.getId());
			try {
				FileUtils.writeStringToFile(xmlExport, "ig " + ig.getId());
				content = igExport.exportAsXmlDisplay(ig);
				assertNotNull(content);
				tmpFile = new File(setFilename("IG_", "_" + ig.getId(), "xml"));
				logger.debug("Writing to file");
				FileUtils.copyInputStreamToFile(content, tmpFile);
				logger.debug("Export done");
				rst.append(": " + String.valueOf(tmpFile.length()/1024) + " kb - " + tmpFile.getName() + " " + " ok\n");
			} catch (IOException e) {
				e.printStackTrace();
				rst.append(": " + String.valueOf(tmpFile.length()/1024) + " kb - " + tmpFile.getName() + " "  + e.getMessage() + "\n");
			} catch (Exception e) {
				e.printStackTrace();
				rst.append(": " + String.valueOf(tmpFile.length()/1024) + " kb - " + tmpFile.getName() + " "  + e.getMessage() + "\n");
			}
		}
		try {
			FileUtils.writeStringToFile(xmlExport, rst.toString());
		} catch (IOException e) {
			e.printStackTrace();
			logger.debug(rst.toString());
			logger.debug("couldn't write report");
		}
	}

	@Test
	public void testCallIGExportHtml() {
		try {
			content = igExport.exportAsHtml(ig);
			assertNotNull(content);
			tmpFile = new File(setFilename("IG_", "_" + ig.getId(), "html"));
			logger.debug("Writing to file");
			FileUtils.copyInputStreamToFile(content, tmpFile);
			logger.debug("Export done");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SerializationException e) {
				e.printStackTrace();
		}
	}

	@Test
	public void testCallIGExportHtml_All() {
		File htmlExport = new File(setFilename("export_report", "_all_user_html", "txt"));
		StringBuilder rst = new StringBuilder();
		igs = igService.findAll();
		for (IGDocument ig : igs){
			rst.append("ig " + ig.getId());
			try {
				FileUtils.writeStringToFile(htmlExport, "ig " + ig.getId());
				content = igExport.exportAsHtml(ig);
				assertNotNull(content);
				tmpFile = new File(setFilename("IG_", "_" + ig.getId(), "html"));
				logger.debug("Writing to file");
				FileUtils.copyInputStreamToFile(content, tmpFile);
				logger.debug("Export done");
				rst.append(": " + String.valueOf(tmpFile.length()/1024) + " kb - " + tmpFile.getName() + " ok\n");
			} catch (IOException e) {
				e.printStackTrace();
				rst.append(": " + String.valueOf(tmpFile.length()/1024) + " kb - " + tmpFile.getName() + " " + e.getMessage() + "\n");
			} catch (Exception e) {
				e.printStackTrace();
				rst.append(": " + String.valueOf(tmpFile.length()/1024) + " kb - " + tmpFile.getName() + " "  + e.getMessage() + "\n");
			}
		}
		try {
			FileUtils.writeStringToFile(htmlExport, rst.toString());
		} catch (IOException e) {
			e.printStackTrace();
			logger.debug(rst.toString());
			logger.debug("couldn't write report");
		}
	}

	@Test
	public void testCallIGExportHtml_User() {
		File htmlExport = new File(setFilename("export_report", "_all_user_html", "txt"));
		StringBuilder rst = new StringBuilder();
		igs = igService.findAllUser();
		for (IGDocument ig : igs){
			rst.append("ig " + ig.getId());
			try {
				FileUtils.writeStringToFile(htmlExport, "ig " + ig.getId());
				content = igExport.exportAsHtml(ig);
				assertNotNull(content);
				tmpFile = new File(setFilename("IG_", "_" + ig.getId(), "html"));
				logger.debug("Writing to file");
				FileUtils.copyInputStreamToFile(content, tmpFile);
				logger.debug("Export done");
				rst.append(": " + String.valueOf(tmpFile.length()/1024) + " kb - " + tmpFile.getName() + " ok\n");
			} catch (IOException e) {
				e.printStackTrace();
				rst.append(": " + String.valueOf(tmpFile.length()/1024) + " kb - " + tmpFile.getName() + " " + e.getMessage() + "\n");
			} catch (Exception e) {
				e.printStackTrace();
				rst.append(": " + String.valueOf(tmpFile.length()/1024) + " kb - " + tmpFile.getName() + " " + e.getMessage() + "\n");
			}
		}
		try {
			FileUtils.writeStringToFile(htmlExport, rst.toString());
		} catch (IOException e) {
			e.printStackTrace();
			logger.debug(rst.toString());
			logger.debug("couldn't write report");
		}
	}

	@Test
	public void testCallIGExportDocx() {
		try {
			content = igExport.exportAsDocx(ig);
			assertNotNull(content);
			tmpFile = new File(setFilename("IG_", "_" + ig.getId(), "docx"));
			logger.debug("Writing to file");
			FileUtils.copyInputStreamToFile(content, tmpFile);
			logger.debug("Export done");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCallIGExportDocx_All() {
		File docxExport = new File(setFilename("export_report", "_all_docx", "txt"));
		StringBuilder rst = new StringBuilder();
		igs = igService.findAll();
		for (IGDocument ig : igs){
			rst.append("ig " + ig.getId());
			try {
				content = igExport.exportAsDocx(ig);
				assertNotNull(content);
                tmpFile = new File(setFilename("IG_", "_" + ig.getId(), "docx"));
				logger.debug("Writing to file");
				FileUtils.copyInputStreamToFile(content, tmpFile);
				logger.debug("Export done");
				rst.append(": " + String.valueOf(tmpFile.length()/1024) + " kb - " + tmpFile.getName() + " ok\n");
			} catch (IOException e) {
				e.printStackTrace();
				rst.append(": " + String.valueOf(tmpFile.length()/1024) + " kb - " + tmpFile.getName() + " "  + e.getMessage() + "\n");
			} catch (Exception e) {
				e.printStackTrace();
				rst.append(": " + String.valueOf(tmpFile.length()/1024) + " kb - " + tmpFile.getName() + " " + e.getMessage() + "\n");
			}
		}
		try {
			FileUtils.writeStringToFile(docxExport, rst.toString());
		} catch (IOException e) {
			e.printStackTrace();
			logger.debug(rst.toString());
			logger.debug("couldn't write report");
		}
	}

	@Test
	public void testCallIGExportDocx_User() {
      File docxExport = new File(setFilename("export_report", "_all_user_docx", "txt"));
		StringBuilder rst = new StringBuilder();
		igs = igService.findAllUser();
		for (IGDocument ig : igs){
			rst.append("ig " + ig.getId());
			try {
				content = igExport.exportAsDocx(ig);
				assertNotNull(content);
                tmpFile = new File(setFilename("IG_", "_" + ig.getId(), "docx"));
				logger.debug("Writing to file");
				FileUtils.copyInputStreamToFile(content, tmpFile);
				logger.debug("Export done");
				rst.append(" : " + String.valueOf(tmpFile.length()/1024) + " kb - " + tmpFile.getName() + " ok\n");
			} catch (IOException e) {
				e.printStackTrace();
				rst.append(": " + String.valueOf(tmpFile.length()/1024) + " kb - " + tmpFile.getName() + " " + e.getMessage() + "\n");
			} catch (Exception e) {
				e.printStackTrace();
				rst.append(": " + String.valueOf(tmpFile.length()/1024) + " kb - " + tmpFile.getName() + " " + e.getMessage() + "\n");
			}
		}
		try {
			FileUtils.writeStringToFile(docxExport, rst.toString());
		} catch (IOException e) {
			e.printStackTrace();
			logger.debug(rst.toString());
			logger.debug("couldn't write report");
		}
	}
	@Test
	public void testCallIGExportPdf() {
		try {
			content = igExport.exportAsPdf(ig);
			assertNotNull(content);
			tmpFile = new File(setFilename("IG_", "_" + ig.getId(), "pdf"));
			logger.debug("Writing to file");
			FileUtils.copyInputStreamToFile(content, tmpFile);
			logger.debug("Export done");

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SerializationException e) {
				e.printStackTrace();
		}
	}


	@Test
	public void testCallSgtExportXml() {
		try {
			content = igExport.exportAsXmlSegment(sl);
			assertNotNull(content);
            tmpFile = new File(setFilename("SGT_" + sl.getId(), "", "xml"));
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
            tmpFile = new File(setFilename("SGT_" + sl.getId(), "", "html"));
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
            tmpFile = new File(setFilename("SGT_" + sl.getId(), "", "docx"));
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
            tmpFile = new File(setFilename("DT_" + dl.getId(), "", "xml"));
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
            tmpFile = new File(setFilename("DT_" + dl.getId(), "", "html"));
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
            tmpFile = new File(setFilename("DT_" + dl.getId(), "", "docx"));
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
            tmpFile = new File(setFilename("TBL_" + dl.getId(), "", "xml"));
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
            tmpFile = new File(setFilename("TBL_" + dl.getId(), "", "html"));
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
            tmpFile = new File(setFilename("TBL_" + dl.getId(), "", "docx"));
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
            tmpFile = new File(setFilename("DTL_" + dl.getId(), "", "docx"));
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
            tmpFile = new File(setFilename("DTL_" + dl.getId(), "", "html"));
			logger.debug("Writing to file");
			FileUtils.copyInputStreamToFile(content, tmpFile);
			logger.debug("Export done");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String setFilename(String prefix, String suffix, String extension){
	  return new String(prefix + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + suffix + "." + extension);
	}
}
