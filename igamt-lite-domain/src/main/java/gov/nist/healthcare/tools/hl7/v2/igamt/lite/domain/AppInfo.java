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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

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

  @Value("${wsEndpoints}")
  private String wsEndpoints;

  @Value("${hl7Versions}")
  private String versions;

  HashMap<String, String> properties = new HashMap<String, String>();

  List<String> hl7Versions = new ArrayList<String>();

  private String uploadedImagesUrl;

  @Value("${admin.email}")
  private String adminEmail;


  @Value("${gvt.url}")
  private String gvtUrl;

  @Value("${gvt.uploadTokenContext}")
  private String gvtUploadTokenContext;



  /**
   * 
   */
  @PostConstruct
  public void init() throws Exception {
    String[] urls = this.wsEndpoints.split(",");
    if (urls != null)
      for (String url : urls) {
        String[] parts = url.split(Pattern.quote("|"));
        properties.put(parts[0], parts[1]);

      }

    String[] vrs = this.versions.split(",");
    if (vrs != null)
      for (String v : vrs) {
        hl7Versions.add(v);

      }


  }



  /**
   * @return the hl7Versions
   */
  public List<String> getHl7Versions() {
    return hl7Versions;
  }



  /**
   * @param hl7Versions the hl7Versions to set
   */
  public void setHl7Versions(List<String> hl7Versions) {
    this.hl7Versions = hl7Versions;
  }



  /**
   * @return the properties
   */
  public HashMap<String, String> getProperties() {
    return properties;
  }



  /**
   * @param properties the properties to set
   */
  public void setProperties(HashMap<String, String> properties) {
    this.properties = properties;
  }



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

  public String getGvtUrl() {
    return gvtUrl;
  }

  public void setGvtUrl(String gvtUrl) {
    this.gvtUrl = gvtUrl;
  }

  public String getGvtUploadTokenContext() {
    return gvtUploadTokenContext;
  }

  public void setGvtUploadTokenContext(String gvtUploadTokenContext) {
    this.gvtUploadTokenContext = gvtUploadTokenContext;
  }



}
