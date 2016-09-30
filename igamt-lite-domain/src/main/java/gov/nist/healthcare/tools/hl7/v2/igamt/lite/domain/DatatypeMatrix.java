package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashMap;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "datatypeMatrix")

public class DatatypeMatrix {
	  @Id
	  private String id;
	  private String name;
	  private HashMap<String,Integer> links ;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public HashMap<String, Integer> getLinks() {
		return links;
	}
	public void setLinks(HashMap<String, Integer> links2) {
		this.links = links2;
	}
	public DatatypeMatrix() {
		super();
	} 
	
}
