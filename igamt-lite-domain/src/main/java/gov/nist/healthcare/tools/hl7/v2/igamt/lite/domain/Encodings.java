package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.map.annotate.JsonView;

@Embeddable
public class Encodings implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@JsonView({Views.Profile.class})
	@ElementCollection
	@CollectionTable	
	@NotNull
 	@Enumerated(EnumType.STRING)
	private Set<EncodingType> values = new HashSet<EncodingType>();

	public Set<EncodingType> getValues() {
		return values;
	}

	public void setValues(Set<EncodingType> values) {
		this.values = values;
	}

	 
	 

}
