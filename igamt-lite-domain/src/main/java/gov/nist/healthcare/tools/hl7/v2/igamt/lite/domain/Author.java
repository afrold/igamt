package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "AUTHOR")
public class Author implements java.io.Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Column(name = "ID")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@JsonIgnore
	@JoinColumn(name = "USER_ID")
	@OneToOne(optional = false, fetch = FetchType.LAZY)
	private User user;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public Author clone() throws CloneNotSupportedException {
		Author clonedAuthor = (Author) super.clone();
		clonedAuthor.setId(null);
		clonedAuthor.setUser(user.clone());
		return clonedAuthor;
	}

}
