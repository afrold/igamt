package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.config.unit;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.GVTExportException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.util.ConnectServiceImpl;


public class GVTClientTest {

  // @Test
  public void testSend() throws GVTExportException, IOException {
    InputStream io = GVTClientTest.class.getResourceAsStream("/exports/IZ-Profiles-1.zip");
    ConnectServiceImpl client = new ConnectServiceImpl();
    ResponseEntity<?> response = client.send(io, "aGFmZm86MXFheiFRQVo=");
    Map<String, Object> map = (Map<String, Object>) response.getBody();
    assertNotNull(map);
    assertNotNull(map.get("token"));
    System.out.println(map.get("token"));
  }


}
