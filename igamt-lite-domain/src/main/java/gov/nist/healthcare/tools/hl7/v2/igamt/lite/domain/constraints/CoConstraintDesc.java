package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import org.springframework.data.annotation.Id;

public class CoConstraintDesc {

	@Id
	private String id;
	private String title;

	public CoConstraintDesc() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
