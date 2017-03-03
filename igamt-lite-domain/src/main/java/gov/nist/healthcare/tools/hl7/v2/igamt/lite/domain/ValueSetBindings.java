package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.List;

public class ValueSetBindings {

	private List<ValueSetBinding> messageBinding;
	private List<ValueSetBinding> segmentBinding;
	private List<ValueSetBinding> datatypeBinding;

	public ValueSetBindings() {
		super();
		messageBinding = new ArrayList<ValueSetBinding>();
		segmentBinding = new ArrayList<ValueSetBinding>();
		datatypeBinding = new ArrayList<ValueSetBinding>();
	}

	public List<ValueSetBinding> getMessageBinding() {
		return messageBinding;
	}

	public void setMessageBinding(List<ValueSetBinding> messageBinding) {
		this.messageBinding = messageBinding;
	}

	public List<ValueSetBinding> getSegmentBinding() {
		return segmentBinding;
	}

	public void setSegmentBinding(List<ValueSetBinding> segmentBinding) {
		this.segmentBinding = segmentBinding;
	}

	public List<ValueSetBinding> getDatatypeBinding() {
		return datatypeBinding;
	}

	public void setDatatypeBinding(List<ValueSetBinding> datatypeBinding) {
		this.datatypeBinding = datatypeBinding;
	}

}
