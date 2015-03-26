package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	private String firstname;

	private String lastname;

	private Author author;

	private UserAccount userAccount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	@Override
	public String toString() {
		return "Author [id=" + id + ", firstName=" + firstname + ", lastName="
				+ lastname + "]";
	}

	@Override
	public User clone() throws CloneNotSupportedException {
		User clonedUser = (User) super.clone();
		clonedUser.setId(null);
		return clonedUser;
	}

}
