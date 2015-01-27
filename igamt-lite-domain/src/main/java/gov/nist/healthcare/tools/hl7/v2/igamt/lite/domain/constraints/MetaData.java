package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class MetaData implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@NotNull
	@Column(nullable = false)
	protected String description;
	
	protected Set<Author> authors = new HashSet<Author>();
	
	protected Standard standard;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(Set<Author> authors) {
		this.authors = authors;
	}

	public Standard getStandard() {
		return standard;
	}

	public void setStandard(Standard standard) {
		this.standard = standard;
	}

	@Override
	public String toString() {
		return "MetaData [description=" + description + ", authors=" + authors
				+ ", standard=" + standard + "]";
	} 
	
	
	
}
