package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;

public class Messages extends SectionModel implements java.io.Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	private String id;

	/**
	 * 
	 */
	public Messages() {
		super();
		this.id = ObjectId.get().toString();
	}

	private Set<Message> children = new HashSet<Message>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Set<Message> getChildren() {
		return children;
	}

	public void setChildren(Set<Message> children) {
		this.children = children;
	}

	public void addMessage(Message m) {
		m.setPosition(children.size() + 1);
		children.add(m);
	}

	public void delete(String id) {
		Message m = findOne(id);
		if (m != null)
			this.getChildren().remove(m);
	}

	public Message findOne(String id) {
		if (this.getChildren() != null)
			for (Message m : this.getChildren()) {
				if (m.getId().equals(id)) {
					return m;
				}
			}

		return null;
	}

	public Message findOneByStrucId(String id) {
		if (this.getChildren() != null)
			for (Message m : this.getChildren()) {
				if (m.getStructID().equals(id)) {
					return m;
				}
			}

		return null;
	}

	public SegmentRefOrGroup findOneSegmentRefOrGroup(String id) {
		if (this.getChildren() != null) {
			for (Message message : this.getChildren()) {
				SegmentRefOrGroup m = message.findOneSegmentRefOrGroup(id);
				if (m != null) {
					return m;
				}
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "Messages [id=" + id + "]";
	}

	public Messages clone(HashMap<String, Datatype> dtRecords,
			HashMap<String, Segment> segmentRecords,
			HashMap<String, Table> tableRecords)
			throws CloneNotSupportedException {
		Messages clonedMessages = new Messages();
		clonedMessages.setChildren(new HashSet<Message>());
		for (Message m : this.children) {
			clonedMessages.addMessage(m.clone(dtRecords, segmentRecords,
					tableRecords));
		}

		return clonedMessages;
	}
}
