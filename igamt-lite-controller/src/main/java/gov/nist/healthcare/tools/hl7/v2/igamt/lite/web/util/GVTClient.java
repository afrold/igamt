package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.GVTExportException;

/**
 * 
 * @author haffo
 *
 */
public class GVTClient {

  static {
    // for localhost testing only
    javax.net.ssl.HttpsURLConnection
        .setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier() {

          @Override
          public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
            if (hostname.equals("hl7v2.gvt.nist.gov")) {
              return true;
            }
            return false;
          }
        });
  }

  public GVTClient() {
    super();
  }


  public ResponseEntity<?> send(InputStream io, String endpoint, String authorization)
      throws GVTExportException, IOException {
    LinkedMultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
    File oFile = toFile(io);
    parts.add("file", new FileSystemResource(oFile));
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    headers.add("Authorization", "Basic " + authorization);
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity =
        new HttpEntity<LinkedMultiValueMap<String, Object>>(parts, headers);
    ResponseEntity<?> response =
        restTemplate.exchange(endpoint, HttpMethod.POST, requestEntity, Map.class);
    return response;
  }

  public File toFile(InputStream io) {
    OutputStream outputStream = null;
    File f = null;
    try {
      // write the inputStream to a FileOutputStream
      f = File.createTempFile("IGAMT", ".zip");
      outputStream = new FileOutputStream(f);
      int read = 0;
      byte[] bytes = new byte[1024];
      while ((read = io.read(bytes)) != -1) {
        outputStream.write(bytes, 0, read);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (io != null) {
        try {
          io.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (outputStream != null) {
        try {
          // outputStream.flush();
          outputStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }

      }
    }
    return f;
  }
}
