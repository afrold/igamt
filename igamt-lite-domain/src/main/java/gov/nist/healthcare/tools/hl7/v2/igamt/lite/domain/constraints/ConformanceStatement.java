package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

// @Entity
// @Table(name = "CONF_STATEMENT")
@Document(collection = "conformanceStatement")
public class ConformanceStatement extends Constraint implements Serializable {

  /**
	 * 
	 */
  private static final long serialVersionUID = 5723342171557075960L;

  public ConformanceStatement() {
    super();
  }

  @Override
  public String toString() {
    return "Constraint [id=" + id + ", constraintId=" + constraintId + ", constraintTarget="
        + constraintTarget + ", reference=" + reference + ", description=" + description
        + ", assertion=" + assertion + "]";
  }

  @Override
  public ConformanceStatement clone() throws CloneNotSupportedException {
    return (ConformanceStatement) super.clone();
  }

}
