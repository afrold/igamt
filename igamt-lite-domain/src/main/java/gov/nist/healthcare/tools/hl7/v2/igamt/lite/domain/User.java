package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "USER")
public class User implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@Column(nullable = false, name = "FIRSTNAME")
	private String firstname;

	@NotNull
	@Column(nullable = false, name = "LASTNAME")
	private String lastname;

	@OneToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "USERACCOUNT_ID")
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
