package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.ResponseEntity;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.GVTExportException;

/**
 * 
 * @author haffo
 *
 */

public interface GVTService {

  public ResponseEntity<?> send(InputStream io, String authorization)
      throws GVTExportException, IOException;

  public File toFile(InputStream io);

  public boolean validCredentials(String authorization) throws GVTExportException;

}
