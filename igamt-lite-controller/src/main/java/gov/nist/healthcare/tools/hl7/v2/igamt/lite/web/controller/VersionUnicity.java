/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Abdelghani EL OUAKILI (NIST) Mar 30, 2017
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

/**
 * @author Abdelghani EL Ouakili (NIST)
 *
 */
public class VersionUnicity {

  String parentVersion;
  String hl7Version;

  /**
   * @param parentVersion2
   * @param parentVersion3
   */
  public VersionUnicity(String parentVersion, String hl7Version) {
    // TODO Auto-generated constructor stub
    this.parentVersion = parentVersion;
    this.hl7Version = hl7Version;
  }

  /**
   * @return the parentVersion
   */
  public String getParentVersion() {
    return parentVersion;
  }

  /**
   * @param parentVersion the parentVersion to set
   */
  public void setParentVersion(String parentVersion) {
    this.parentVersion = parentVersion;
  }

  /**
   * @return the hl7Version
   */
  public String getHl7Version() {
    return hl7Version;
  }

  /**
   * @param hl7Version the hl7Version to set
   */
  public void setHl7Version(String hl7Version) {
    this.hl7Version = hl7Version;
  }
}
