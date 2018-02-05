package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.delta;

import java.util.ArrayList;
import java.util.List;

public class DeltaElement {
	protected Delta name; 
	protected String type;
	protected DeltaNode data;
	protected State state;
	protected String path; //1.2.2 
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	List<DeltaElement>  children = new ArrayList<DeltaElement>();

	public List<DeltaElement> getChildren() {
		return children;
	}

	public void setChildren(List<DeltaElement> children) {
		this.children = children;
	}
	
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
	public DeltaElement() {
		super();
		this.state=State.UNTOUCHED;

		// TODO Auto-generated constructor stub
	}
	public DeltaNode getData() {
		return data;
	}
	public void setData(DeltaNode data) {
		this.data = data;
	}
	public Delta getName() {
		return name;
	}
	public void setName(Delta name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public enum State
	{
		ADDED,
		CHANGED,
		REMOVED,
		UNTOUCHED,
		IGNORED,
		INACCESSIBLE
	}

}

