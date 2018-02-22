package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.messageevents.Event;

public class MessageEventTree {
	
	private List<MessageEventTree > children =new ArrayList<MessageEventTree>();


	public List<MessageEventTree> getChildren() {
		return children;
	}

	public void setChildren(List<MessageEventTree> children) {
		this.children = children;
	}

	private MessageEventTreeData data;
	
	
	public MessageEventTreeData getData() {
		return data;
	}

	public void setData(MessageEventTreeData data) {
		this.data = data;
	}



	
	
	
}
