package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Encodings implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;

	protected Set<String> encoding = new HashSet<String>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<String> getEncoding() {
		return encoding;
	}

	public void setEncoding(Set<String> encoding) {
		this.encoding = encoding;
	}

	@Override
	public String toString() {
		return "Encodings [id=" + id + ", encoding=" + encoding + "]";
	}
	
	

}
