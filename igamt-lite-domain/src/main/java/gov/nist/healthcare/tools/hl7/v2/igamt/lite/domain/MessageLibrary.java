package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "message-library")

public class MessageLibrary extends Library {
	@Id
	private String id;
	private Set<MessageLink> children = new HashSet<MessageLink>();
		public MessageLibrary() {
			    id = ObjectId.get().toString();
			    type=Constant.MESSAGES;
			    sectionPosition=2;
		}
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	  public Set<MessageLink> getChildren() {
	    return children;
	  }

	  public void setChildren(Set<MessageLink> children) {
	    this.children = children;
	  }

	  public void addMessage(MessageLink m) {
	    m.setPosition(children.size() + 1);
	    children.add(m);
	  }

	  public void delete(String id) {
	    MessageLink m = findOne(id);
	    if (m != null)
	      this.getChildren().remove(m);
	  }

	  public MessageLink findOne(String id) {
	    if (this.getChildren() != null)
	      for (MessageLink m : this.getChildren()) {
	        if (m.getId().equals(id)) {
	          return m;
	        }
	      }

	    return null;
	  }
	  

	  public MessageLink findOneByStrucId(String id) {
	    if (this.getChildren() != null)
	      for (MessageLink m : this.getChildren()) {
	        if (m.getStructID().equals(id)) {
	          return m;
	        }
	      }

	    return null;
	  }

	  
	  @Override
	  public String toString() {
	    return "Messages [id=" + id + "]";
	  }

}
