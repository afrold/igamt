package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "Delta")
public class Delta extends DataModel implements java.io.Serializable, Cloneable,
    Comparable<Delta> {
	
		@Id
	  	private String id;
	  	private String  position;
		private String name;
		private List<Delta> children;  
	  
		private HashMap<String,Object> content= new HashMap<String,Object>();
  private static final long serialVersionUID = 1L;

  public Delta() {
    super();
  }
  
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

public List<Delta> getChildren() {
	return children;
}

public void setChildren(List<Delta> children) {
	this.children = children;
}
public HashMap<String, Object> getContent() {
	return content;
}

public void setContent(HashMap<String, Object> content) {
	this.content = content;
}

public static long getSerialversionuid() {
	return serialVersionUID;
}


public String getPosition() {
	return position;
}


public void setPosition(String position) {
	this.position = position;
}

  
@Override
public int compareTo(Delta o) {
	// TODO Auto-generated method stub
	return 0;
}


}
