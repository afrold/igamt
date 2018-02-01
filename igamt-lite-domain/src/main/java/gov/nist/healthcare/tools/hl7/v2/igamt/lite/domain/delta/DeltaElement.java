package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.delta;

import java.util.List;

public class DeltaElement {
	protected String name; 
	protected String type;
	protected DeltaNode data;
	protected State state;
	
	List<DeltaElement>  children;

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
	public String getName() {
		return name;
	}
	public void setName(String name) {
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

