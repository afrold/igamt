package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class CoConstraint {

	private String description;
	private String comments;
	
	private List<CCValue> values = new ArrayList<CCValue>();

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public List<CCValue> getValues() {
		return values;
	}

	public void setValues(List<CCValue> values) {
		this.values = values;
	}
	
	
}
