package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ByNameOrByID implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5212340093784881862L;

}
