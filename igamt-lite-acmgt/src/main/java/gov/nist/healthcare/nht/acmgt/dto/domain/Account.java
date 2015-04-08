/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 */
package gov.nist.healthcare.nht.acmgt.dto.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * @author fdevaulx
 * 
 */
@Entity
@JsonIgnoreProperties(value = "new", ignoreUnknown = true)
public class Account extends AbstractPersistable<Long> {

	private static final long serialVersionUID = 20130625L;

	@Transient
	private String registrationPassword;

	@JsonIgnore
	private boolean entityDisabled = false;

	@JsonIgnore
	// TODO remove it and check it doesn't affect REST API security
	private boolean pending = false;

	@Length(max = 100)
	private String accountType;

	@Length(max = 100)
	@Column(unique = true)
	private String username;

	@Email
	@Length(max = 100)
	@Column(unique = true)
	private String email;

	@Length(max = 100)
	private String firstname;

	@Length(max = 100)
	private String lastname;

	@Length(max = 100)
	private String phone;

	@Length(max = 100)
	private String company;

	private Boolean signedConfidentialityAgreement = false;

	public Account() {
		this(null);
	}

	/**
	 * Creates a new account instance.
	 */
	public Account(Long id) {
		this.setId(id);
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone
	 *            the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the entityDisabled
	 */
	public boolean isEntityDisabled() {
		return entityDisabled;
	}

	/**
	 * @param entityDisabled
	 *            the entityDisabled to set
	 */
	public void setEntityDisabled(boolean entityDisabled) {
		this.entityDisabled = entityDisabled;
	}

	// Only used for registration
	/**
	 * @return the password
	 */
	public String getPassword() {
		return registrationPassword;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String registrationPassword) {
		this.registrationPassword = registrationPassword;
	}

	/**
	 * @return the accountType
	 */
	public String getAccountType() {
		return accountType;
	}

	/**
	 * @param accountType
	 *            the accountType to set
	 */
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	/**
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * @param firstname
	 *            the firstname to set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * @return the lastname
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * @param lastname
	 *            the lastname to set
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	/**
	 * @return the company
	 */
	public String getCompany() {
		return company;
	}

	/**
	 * @param company
	 *            the company to set
	 */
	public void setCompany(String company) {
		this.company = company;
	}

	/**
	 * @return the signedConfidentialityAgreement
	 */
	public Boolean getSignedConfidentialityAgreement() {
		return signedConfidentialityAgreement;
	}

	/**
	 * @param signedConfidentialityAgreement
	 *            the signedConfidentialityAgreement to set
	 */
	public void setSignedConfidentialityAgreement(
			Boolean signedConfidentialityAgreement) {
		this.signedConfidentialityAgreement = signedConfidentialityAgreement;
	}

	/**
	 * @return the pending
	 */
	public boolean isPending() {
		return pending;
	}

	/**
	 * @param pending
	 *            the pending to set
	 */
	public void setPending(boolean pending) {
		this.pending = pending;
	}

}
