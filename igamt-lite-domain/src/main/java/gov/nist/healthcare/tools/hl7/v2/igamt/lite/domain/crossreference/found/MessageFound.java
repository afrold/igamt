package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found;

public class MessageFound {
  private String id;
  private String name;
  private String identifier;
  private String description;
  private String path; // ORDER.OBSERVATION.OBX this is just used for segments and groups
  private String positionPath; // 9.6.3

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

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getPositionPath() {
    return positionPath;
  }

  public void setPositionPath(String positionPath) {
    this.positionPath = positionPath;
  }


}
