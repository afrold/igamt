package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="BYID")
public class ByID extends ByNameOrByID {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1167310291230293964L;

	@NotNull
	@Column(nullable = false, name="BYID")
	protected String byID;

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
	
	@Override
    public ByID clone() throws CloneNotSupportedException {
		ByID clonedByID = (ByID) super.clone();
		clonedByID.setId(null);
        return clonedByID;
    }

}
