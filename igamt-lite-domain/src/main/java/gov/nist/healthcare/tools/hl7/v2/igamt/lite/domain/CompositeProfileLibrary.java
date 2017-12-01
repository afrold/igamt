package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "compositeProfile-library")
public class CompositeProfileLibrary extends Library {
	
	@Id
	private String id;
	  public CompositeProfileLibrary() {
		// TODO Auto-generated constructor stub
			this.id = ObjectId.get().toString();
		    this.type=Constant.COMPOSITEPROFILES;
		    sectionPosition=3;
	  }
	  private Set<CompositeProfileLink> children = new HashSet<CompositeProfileLink>();
	  public Set<CompositeProfileLink> getChildren() {
	    return children;
	  }

	  public void setChildren(Set<CompositeProfileLink> children) {
	    this.children = children;
	  }

	  public void addChild(CompositeProfileLink child) {
	    this.children.add(child);
	  }

	  public void removeChild(String id) {
		  CompositeProfileLink toRemove = new CompositeProfileLink();
	    for (CompositeProfileLink cps : this.children) {
	      if (cps.getId().equals(id)) {
	        toRemove = cps;
	      }
	    }
	    this.children.remove(toRemove);
	  }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	

}
