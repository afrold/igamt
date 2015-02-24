package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

 
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Messages implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

 	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

 	@OneToMany(mappedBy = "messages", cascade = CascadeType.ALL)
	private Set<Message> messages = new HashSet<Message>();

 	@JsonIgnore
 	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Profile profile;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<Message> getMessages() {
		return messages;
	}

	public void setMessages(Set<Message> messages) {
		this.messages = messages;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public void addMessage(Message m) {
		if (m.getMessages() != null) {
			throw new IllegalArgumentException(
					"This message already below to a different messages");
		}
		messages.add(m);
		m.setMessages(this);
	}

	@Override
	public String toString() {
		return "Messages [id=" + id + ", messages=" + messages + "]";
	}

}
