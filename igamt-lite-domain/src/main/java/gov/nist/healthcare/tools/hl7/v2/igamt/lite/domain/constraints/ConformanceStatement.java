package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "CONF_STATEMENT")
public class ConformanceStatement extends Constraint implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5723342171557075960L;

	@Override
	public String toString() {
		return "Constraint [id=" + id + ", constraintId=" + constraintId
				+ ", constraintTarget=" + constraintTarget + ", reference="
				+ reference + ", description=" + description + ", assertion="
				+ assertion + "]";
	}
	
	@Override
	public ConformanceStatement clone() throws CloneNotSupportedException {
		return (ConformanceStatement)super.clone();
	}

}
