package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;

// @Entity
// @Table(name = "PREDICATE")

@Document(collection = "predicate")
public class Predicate extends Constraint implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 5723342171557075960L;

  public Predicate() {
    super();
  }

  // @Column(name = "TRUEUSAGE")
  private Usage trueUsage;

  // @Column(name = "FALSEUSAGE")
  private Usage falseUsage;

  private PredicateContext context;

  public Usage getTrueUsage() {
    return trueUsage;
  }

  public void setTrueUsage(Usage trueUsage) {
    this.trueUsage = trueUsage;
  }

  public Usage getFalseUsage() {
    return falseUsage;
  }

  public void setFalseUsage(Usage falseUsage) {
    this.falseUsage = falseUsage;
  }


  public PredicateContext getContext() {
    return context;
  }

  public void setContext(PredicateContext context) {
    this.context = context;
  }

  @Override
  public String toString() {
    return "Constraint [id=" + id + ", constraintId=" + constraintId + ", constraintTarget="
        + constraintTarget + ", reference=" + reference + ", description=" + description
        + ", assertion=" + assertion + "]";
  }

  @Override
  public Predicate clone() throws CloneNotSupportedException {
    return (Predicate) super.clone();
  }

}
