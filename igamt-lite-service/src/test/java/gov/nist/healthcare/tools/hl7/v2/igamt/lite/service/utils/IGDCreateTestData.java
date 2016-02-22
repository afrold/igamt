package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentCreationService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.assemblers.MessageEvents;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.integration.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceContext.class})
public class IGDCreateTestData {

	private static final Logger log = LoggerFactory.getLogger(IGDCreateTestData.class);

	public final static File OUTPUT_DIR = new File(System.getenv("IGAMT") + "/igDocuments");

	@Autowired
	IGDocumentCreationService create;
	
	public static final String hl7Version = "2.5.1";
	public static final Long accountId = 45L;
	
	@BeforeClass
	public static void beforeClass() {
		if (!OUTPUT_DIR.exists()) {
			OUTPUT_DIR.mkdir();
		}		
	}

	@Test
	public void createMessageEventsData() {
		try {
			List<String> msgIds = new ArrayList<String>();
			IGDocument igDocumentTarget = create.createIntegratedIGDocument(msgIds, hl7Version, accountId);
			List<MessageEvents> mes = create.summary(hl7Version, msgIds);
			
			File outfile = new File(OUTPUT_DIR, "mes-" + "hl7Version" + "-" + igDocumentTarget.getScope().name() + "-" + igDocumentTarget.getMetaData().getVersion() + ".json"); 
			Writer mesJson = new FileWriter(outfile);
			ObjectMapper mapper = new ObjectMapper();
			mapper.writerWithDefaultPrettyPrinter().writeValue(mesJson, mes);
		} catch (JsonGenerationException e) {
			log.error("" , e);
		} catch (JsonMappingException e) {
			log.error("" , e);
		} catch (IOException e) {
			log.error("" , e);
		} catch (IGDocumentException e) {
			log.error("" , e);
		}	}
	
//	@Test
	public void createIntegratedIGDocumentData() {
		try {
			List<String> msgIds = new ArrayList<String>();
			IGDocument igDocumentTarget = create.createIntegratedIGDocument(msgIds, hl7Version, accountId);
			File outfile = new File(OUTPUT_DIR, "igdocument-" + "hl7Version" + ".5" + "-" + igDocumentTarget.getScope().name() + "-" + igDocumentTarget.getMetaData().getVersion() + ".json"); 
			Writer igdocumentJson = new FileWriter(outfile);
			ObjectMapper mapper = new ObjectMapper();
			mapper.writerWithDefaultPrettyPrinter().writeValue(igdocumentJson, igDocumentTarget);
		} catch (JsonGenerationException e) {
			log.error("" , e);
		} catch (JsonMappingException e) {
			log.error("" , e);
		} catch (IOException e) {
			log.error("" , e);
		} catch (IGDocumentException e) {
			log.error("" , e);
		}
	}
}
