package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.display;

import java.util.HashSet;
import java.util.Set;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

public class FieldDisplayModel extends Field {

  /**
   * 
   */
  private static final long serialVersionUID = 6630718075180960867L;

  private DatatypeDisplay displayDatatype;
  private Set<TableDisplay> displayTables = new HashSet<TableDisplay>();
  private Set<CommentDisplay> displayComments = new HashSet<CommentDisplay>();
  private Set<ConstantDisplay> displayConstants = new HashSet<ConstantDisplay>();
  private Predicate predicate;

  public DatatypeDisplay getDisplayDatatype() {
    return displayDatatype;
  }

  public void setDisplayDatatype(DatatypeDisplay displayDatatype) {
    this.displayDatatype = displayDatatype;
  }

  public Set<TableDisplay> getDisplayTables() {
    return displayTables;
  }

  public void setDisplayTables(Set<TableDisplay> displayTables) {
    this.displayTables = displayTables;
  }


  public Predicate getPredicate() {
    return predicate;
  }

  public void setPredicate(Predicate predicate) {
    this.predicate = predicate;
  }

  public Set<CommentDisplay> getDisplayComments() {
    return displayComments;
  }

  public void setDisplayComments(Set<CommentDisplay> displayComments) {
    this.displayComments = displayComments;
  }

  public Set<ConstantDisplay> getDisplayConstants() {
    return displayConstants;
  }

  public void setDisplayConstants(Set<ConstantDisplay> displayConstants) {
    this.displayConstants = displayConstants;
  }


}
