/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Abdelghani EL OUAKILI (NIST) Feb 3, 2017
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

/**
 * @author Abdelghani EL Ouakili (NIST)
 *
 */
public class NameAndPositionAndPresence {

  private String name;
  private int position;
  private boolean present;
  private boolean disabled;


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public NameAndPositionAndPresence(String name, int position, boolean present, boolean disabled) {
    this.name = name;
    this.position = position;
    this.present = present;
    this.disabled = disabled;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public NameAndPositionAndPresence() {
    super();
    // TODO Auto-generated constructor stub
  }

  public boolean isPresent() {
    return present;
  }

  public void setPresent(boolean presence) {
    this.present = presence;
  }

  /**
   * @return the disabled
   */
  public boolean isDisabled() {
    return disabled;
  }

  /**
   * @param disabled the disabled to set
   */
  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

}
