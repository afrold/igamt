package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notifications")
public class Notifications {
  @Id
  private String id;
  
  private String igDocumentId;
  
  private List<Notification> items = new ArrayList<Notification>();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getIgDocumentId() {
    return igDocumentId;
  }

  public void setIgDocumentId(String igDocumentId) {
    this.igDocumentId = igDocumentId;
  }

  public List<Notification> getItems() {
    return items;
  }

  public void setItems(List<Notification> items) {
    this.items = items;
  }

  public void addItem(Notification item) {
    this.items.add(item);
  }
}
