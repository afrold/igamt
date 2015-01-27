package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

public class Author implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;
	
	@NotNull
	@Column(nullable = false)
	protected String firstName;
	
	@NotNull
	@Column(nullable = false)
	protected String lastName;
	
	private String email;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Author [id=" + id + ", firstName=" + firstName + ", lastName="
				+ lastName + ", email=" + email + "]";
	}
	
	
	
	
}
