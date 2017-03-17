package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.Date;

/**
 * @author Jungyub Woo
 *
 */
public class Comment {
	protected String location;
	protected String description;
	protected Long authorId;
	protected Date lastUpdatedDate;

	public Comment() {
		super();
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}

	public Date getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	
}
