/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Abdelghani EL OUAKILI (NIST) Feb 2, 2017
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

/**
 * @author Abdelghani EL Ouakili (NIST)
 *
 */
public class GeneralColumnConfig extends ColumnsConfig {

  /**
   * 
   */

  boolean name;
  boolean cardinality;
  boolean Length;
  boolean ConfLength;
  boolean datatype;
  boolean ValueSet;
  boolean predicate;
  boolean defText;


  public boolean getName() {
    return name;
  }


  public void setName(boolean name) {
    this.name = name;
  }


  public boolean getCardinality() {
    return cardinality;
  }


  public void setCardinality(boolean cardinality) {
    this.cardinality = cardinality;
  }


  public boolean getLength() {
    return Length;
  }


  public void setLength(boolean length) {
    Length = length;
  }


  public boolean getConfLength() {
    return ConfLength;
  }


  public void setConfLength(boolean confLength) {
    ConfLength = confLength;
  }


  public boolean getDatatype() {
    return datatype;
  }


  public void setDatatype(boolean datatype) {
    this.datatype = datatype;
  }


  public boolean getValueSet() {
    return ValueSet;
  }


  public void setValueSet(boolean valueSet) {
    ValueSet = valueSet;
  }


  public boolean getPredicate() {
    return predicate;
  }


  public void setPredicate(boolean predicate) {
    this.predicate = predicate;
  }


  public boolean getDefText() {
    return defText;
  }


  public void setDefText(boolean defText) {
    this.defText = defText;
  }


  public generalColumnConfig() {
    super();
  }

}
