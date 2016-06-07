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

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Harold Affo (NIST)
 * 
 */

@org.springframework.stereotype.Component
@PropertySource(value = "classpath:app-web-config.properties")
public class AppInfo implements Serializable {

  private static final long serialVersionUID = 8805967508478985159L;

  @Value("${app.version}")
  private String version;

  @Value("${app.date}")
  private String date;

  private String uploadedImagesUrl;

  @Value("${admin.email}")
  private String adminEmail;

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getAdminEmail() {
    return adminEmail;
  }

  public void setAdminEmail(String adminEmail) {
    this.adminEmail = adminEmail;
  }

  public String getUploadedImagesUrl() {
    return uploadedImagesUrl;
  }

  public void setUploadedImagesUrl(String uploadedImagesUrl) {
    this.uploadedImagesUrl = uploadedImagesUrl;
  }

}
