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
package gov.nist.healthcare.nht.acmgt.dto.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * @author fdevaulx
 * 
 */
@Entity
@JsonIgnoreProperties(value = "new", ignoreUnknown = true)
public class Issue implements Serializable {

	private static final long serialVersionUID = 20130625L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;
	
	@NotEmpty
	@Length(max = 255)
	private String title;

	@Length(max = 255)
	private String description;

	@Email
	@Length(max = 100)
	private String email;

	@Temporal(TemporalType.TIMESTAMP)
	private Date timeStamp;

	@Length(max = 255)
	private String senderAgent;

	@Length(max = 50)
	private String category;

	private boolean resolved = false;

	@Length(max = 255)
	private String resolutionComments;

	@Temporal(TemporalType.TIMESTAMP)
	private Date resolutionTimeStamp;

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
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
	 * @return the timeStamp
	 */
	public Date getTimeStamp() {
		return timeStamp;
	}

	/**
	 * @param timeStamp
	 *            the timeStamp to set
	 */
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 * @return the senderAgent
	 */
	public String getSenderAgent() {
		return senderAgent;
	}

	/**
	 * @param senderAgent
	 *            the senderAgent to set
	 */
	public void setSenderAgent(String senderAgent) {
		this.senderAgent = senderAgent;
	}

	/**
	 * @return the resolved
	 */
	public boolean isResolved() {
		return resolved;
	}

	/**
	 * @param resolved
	 *            the resolved to set
	 */
	public void setResolved(boolean resolved) {
		this.resolved = resolved;
	}

	/**
	 * @return the resolutionComments
	 */
	public String getResolutionComments() {
		return resolutionComments;
	}

	/**
	 * @param resolutionComments
	 *            the resolutionComments to set
	 */
	public void setResolutionComments(String resolutionComments) {
		this.resolutionComments = resolutionComments;
	}

	/**
	 * @return the resolutionTimeStamp
	 */
	public Date getResolutionTimeStamp() {
		return resolutionTimeStamp;
	}

	/**
	 * @param resolutionTimeStamp
	 *            the resolutionTimeStamp to set
	 */
	public void setResolutionTimeStamp(Date resolutionTimeStamp) {
		this.resolutionTimeStamp = resolutionTimeStamp;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	
	
	
}
