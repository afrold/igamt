package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.integration;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ValidationService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IntegrationTestApplicationConfig.class})

public class ValidationServiceIntegrationTest {


  @Autowired
  private ValidationService validationService;

  @Autowired
  private DatatypeService datatypeService;


  @Autowired
  private MongoTemplate mongoTemplate;


  protected void importOneJson(String collection, String file) {
    try {
      Iterator<JsonNode> it = parseArray(file);
      while (it.hasNext()) {
        JsonNode node = it.next();
        String c = node.toString();
        mongoTemplate.save(c, collection);
      }
    } catch (IOException e) {
      throw new RuntimeException("Could not import file: " + file, e);
    }
  }


  private Iterator<JsonNode> parseArray(String file) throws IOException {
    String content = FileUtils.readFileToString(new File(file));
    ObjectMapper mapper = new ObjectMapper();
    JsonNode actualObj = mapper.readTree(content);
    return actualObj.elements();
  }


  @Test
  public void testValidateDatatype() throws InvalidObjectException {
    // String path =
    // "src/test/resources/api/datatype-library/588f2d3e84ae56b0b8a3f34e/datatypes/GET.json";
    // importOneJson("datatype", path);

    Datatype d = new Datatype();
    datatypeService.save(d);
    int samplesInCollection = mongoTemplate.findAll(Datatype.class).size();
    assertTrue(samplesInCollection > 0);
    // List<String> versions = new ArrayList<String>(1);
    // versions.add("2.8.2");
    // Datatype reference = datatypeService.findById("588f2d3e84ae56b0b8a3f52c");
    // Datatype toValidate = datatypeService.findById("579655655455fa35176cd625");
    // assertNotNull(reference);
    // assertNotNull(toValidate);
    // ValidationResult result = validationService.validateDatatype(reference, toValidate,
    // toValidate.getId(), versions.get(0));
    // assertNotNull(result);


  }

}
