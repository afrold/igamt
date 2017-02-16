package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.util;

import java.io.File;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @author haffo
 *
 */
public class GVTClient {

  private RestTemplate template;

  public GVTClient() {
    super();
    this.template = new RestTemplate();
  }


  public RestTemplate getTemplate() {
    return template;
  }

  public void setTemplate(RestTemplate template) {
    this.template = template;
  }

  public ResponseEntity<Map> send(File f, String endpoint, String authorization) {
    LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    map.add("file", new FileSystemResource(f));
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    headers.add("Authorization", "Basic " + authorization);
    HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
    return template.exchange(endpoint, HttpMethod.POST, requestEntity, Map.class);
  }


}
