package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization;

import java.util.ArrayList;
import java.util.List;

public class CoConstaintVerboseRow {
	List<CoConstraintRow> children;
	String rowspan;
	String id;
	public List<CoConstraintRow> getChildren() {
		return children;
	}
	public void setChildren(List<CoConstraintRow> children) {
		this.children = children;
	}
	public String getRowspan() {
		return rowspan;
	}
	public void setRowspan(String rowspan) {
		this.rowspan = rowspan;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public CoConstaintVerboseRow() {
		super();
	
		this.children=new ArrayList<CoConstraintRow>();

		// TODO Auto-generated constructor stub
	}

	

}
