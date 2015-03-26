package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "messages")
public class Messages implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	private final Set<Message> children = new LinkedHashSet<Message>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<Message> getChildren() {
		return children;
	}

	public void addMessage(Message m) {
		if (m.getMessages() != null) {
			throw new IllegalArgumentException(
					"This message already belong to a different messages");
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
