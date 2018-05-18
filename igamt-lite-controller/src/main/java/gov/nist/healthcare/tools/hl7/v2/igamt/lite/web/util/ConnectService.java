package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.GVTExportException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.GVTLoginException;

/**
 * 
 * @author haffo
 *
 */

public interface ConnectService {

  public ResponseEntity<?> send(InputStream io, String authorization, String url,String domain)
      throws GVTExportException, IOException;

  public boolean validCredentials(String authorization, String url) throws GVTLoginException;
  
  public ResponseEntity<?> getDomains(String url) throws GVTLoginException;
 
  ResponseEntity<?> createDomain(String authorization, String url, String key, String name,String homeTitle)
      throws GVTExportException, IOException;
  

}
