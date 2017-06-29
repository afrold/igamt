package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public class ShareParticipant {

  private String username;
  private String fullname;
  private Long id;
  private String email;


  public ShareParticipant() {
    super();
    this.username = null;
    this.fullname = null;
    this.id = null;
  }

  public ShareParticipant(Long id) {
    super();
    this.username = null;
    this.fullname = null;
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getFullname() {
    return fullname;
  }

  public void setFullname(String fullname) {
    this.fullname = fullname;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * @param email the email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }

}
