package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;

@Entity
public class Constraint implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5723342171557075960L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected long id;
	
	@NotNull
	@Column(nullable = false)
	protected String constraintId;
	
	protected String constraintTag;
	
	protected Reference reference;
	
	@NotNull
	@Column(nullable = false)
	protected String description;
	
	@NotNull
	@Column(nullable = false)
	@Lob
	protected Assertion assertion;
	
}
