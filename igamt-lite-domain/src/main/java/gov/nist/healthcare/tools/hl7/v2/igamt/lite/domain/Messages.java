package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "messages")
public class Messages implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	@Transient
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
		if (m.getMessages() != null) {
			throw new IllegalArgumentException("This message "
					+ m.getIdentifier() + " already belongs to library"
					+ m.getMessages().getId());
		}
		m.setPosition(children.size() + 1);
		children.add(m);
		m.setMessages(this);
	}

	@Override
	public String toString() {
		return "Messages [id=" + id + "]";
	}

}
