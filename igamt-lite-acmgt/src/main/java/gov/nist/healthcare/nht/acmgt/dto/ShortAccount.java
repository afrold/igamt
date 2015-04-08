/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgment if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 */
package gov.nist.healthcare.nht.acmgt.dto;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Class used to carry minimum necessary set of account information when needed
 * by users other than account holder and authorized users.
 * 
 * @author fdevaulx
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShortAccount implements Serializable {

	private static final long serialVersionUID = 20130625L;

	private String email;

	private Long id;

	private String firstname;
	private String lastname;

	private String phone;
	private String company;

	// private List<CehrTechnology> cehrTechnologies;

	private String username;
	private String accountType;

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
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	// /**
	// * @return the cehrTechnologies
	// */
	// public List<CehrTechnology> getCehrTechnologies() {
	// return cehrTechnologies == null ? cehrTechnologies = new
	// LinkedList<CehrTechnology>()
	// : cehrTechnologies;
	// }
	//
	// /**
	// * @param cehrTechnologies
	// * the cehrTechnologies to set
	// */
	// public void setCehrTechnologies(List<CehrTechnology> cehrTechnologies) {
	// this.cehrTechnologies = cehrTechnologies;
	// }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

}
