package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.util;

import java.io.InputStream;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

public class GVTClient {


  private String authorization;
  private String accessPoint;
  private RestTemplate template;

  public GVTClient(String host, String authorization) {
    super();
    this.authorization = authorization;
    this.template = new RestTemplate();
  }

  public HttpEntity<Object> createHttpEntity(Object m) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Basic " + authorization);
    return new HttpEntity<>(m, headers);
  }

  public String URL(String resource) {
    StringBuilder strB = new StringBuilder("");
    strB.append(this.accessPoint).append(resource);
    return strB.toString();
  }

  public String getAuthorization() {
    return authorization;
  }

  public void setAuthorization(String authorization) {
    this.authorization = authorization;
  }

  public String getAccessPoint() {
    return accessPoint;
  }

  public void setAccessPoint(String accessPoint) {
    this.accessPoint = accessPoint;
  }



  public RestTemplate getTemplate() {
    return template;
  }

  public void setTemplate(RestTemplate template) {
    this.template = template;
  }

  public void send(InputStream io, String endpoint) {
    LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    map.add("file", io);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity =
        new HttpEntity<LinkedMultiValueMap<String, Object>>(map, headers);
    ResponseEntity<String> result =
        template.exchange(endpoint, HttpMethod.POST, requestEntity, String.class);
  }


}
