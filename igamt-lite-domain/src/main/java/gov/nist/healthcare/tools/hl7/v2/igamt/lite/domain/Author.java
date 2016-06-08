package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;


public class Author implements java.io.Serializable, Cloneable {

  private static final long serialVersionUID = 1L;

  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public Author clone() throws CloneNotSupportedException {
    Author clonedAuthor = new Author();
    clonedAuthor.setId(null);
    return clonedAuthor;
  }

}
