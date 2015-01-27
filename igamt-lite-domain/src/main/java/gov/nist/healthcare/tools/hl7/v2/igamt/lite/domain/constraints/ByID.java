package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class ByID extends ByNameOrByID{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1167310291230293964L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected long id;
	
	@NotNull
	@Column(nullable = false)
	protected String byID;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getByID() {
		return byID;
	}

	public void setByID(String byID) {
		this.byID = byID;
	}

	@Override
	public String toString() {
		return "ByID [id=" + id + ", byID=" + byID + ", constraints="
				+ constraints + "]";
	}


	
}
