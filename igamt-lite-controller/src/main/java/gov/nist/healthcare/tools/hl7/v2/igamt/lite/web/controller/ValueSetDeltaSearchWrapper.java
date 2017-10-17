/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Abdelghani EL OUAKILI (NIST) Oct 13, 2017
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

/**
 * @author Abdelghani EL Ouakili (NIST)
 *
 */
public class ValueSetDeltaSearchWrapper {

  private String bindingIdentifier;
  private String version;
  private String scope;

  /**
   * @return the bindingIdentifier
   */
  public String getBindingIdentifier() {
    return bindingIdentifier;
  }

  /**
   * @param bindingIdentifier the bindingIdentifier to set
   */
  public void setBindingIdentifier(String bindingIdentifier) {
    this.bindingIdentifier = bindingIdentifier;
  }

  /**
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * @param version the version to set
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * @return the scope
   */
  public String getScope() {
    return scope;
  }

  /**
   * @param scope the scope to set
   */
  public void setScope(String scope) {
    this.scope = scope;
  }

}
