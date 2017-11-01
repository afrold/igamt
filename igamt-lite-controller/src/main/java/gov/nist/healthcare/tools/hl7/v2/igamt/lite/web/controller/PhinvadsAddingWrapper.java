/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Abdelghani EL OUAKILI (NIST) Nov 1, 2017
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import java.util.HashMap;
import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;

/**
 * @author Abdelghani EL Ouakili (NIST)
 *
 */
public class PhinvadsAddingWrapper {

  List<Table> tables;
  HashMap<String, Boolean> codesPresence;

  /**
   * @return the tables
   */
  public List<Table> getTables() {
    return tables;
  }

  /**
   * @param tables the tables to set
   */
  public void setTables(List<Table> tables) {
    this.tables = tables;
  }

  /**
   * @return the codesPresence
   */
  public HashMap<String, Boolean> getCodesPresence() {
    return codesPresence;
  }

  /**
   * @param codesPresence the codesPresence to set
   */
  public void setCodesPresence(HashMap<String, Boolean> codesPresence) {
    this.codesPresence = codesPresence;
  }

  /**
   * @param tables
   * @param codesPresence
   */
  public PhinvadsAddingWrapper(List<Table> tables, HashMap<String, Boolean> codesPresence) {
    super();
    this.tables = tables;
    this.codesPresence = codesPresence;
  }

  /**
   * 
   */
  public PhinvadsAddingWrapper() {
    super();
    // TODO Auto-generated constructor stub
  }


}
