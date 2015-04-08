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

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * @author fdevaulx
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountChangeCredentials {

	private String username;
	private String newUsername;
	private String password;
	private String newPassword;
	private Boolean signedConfidentialityAgreement;

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
	 * @return the newUsername
	 */
	public String getNewUsername() {
		return newUsername;
	}

	/**
	 * @param newUsername
	 *            the newUsername to set
	 */
	public void setNewUsername(String newUsername) {
		this.newUsername = newUsername;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
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
	 * @return the newPassword
	 */
	public String getNewPassword() {
		return newPassword;
	}

	/**
	 * @param newPassword
	 *            the newPassword to set
	 */
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

}
