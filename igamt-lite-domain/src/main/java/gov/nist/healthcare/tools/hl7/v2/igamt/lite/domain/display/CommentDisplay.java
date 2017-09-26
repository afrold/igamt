package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.display;

import java.util.Date;

public class CommentDisplay {
  protected String description;
  protected Long authorId;
  protected Date lastUpdatedDate;
  protected DisplayLevel level;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getAuthorId() {
    return authorId;
  }

  public void setAuthorId(Long authorId) {
    this.authorId = authorId;
  }

  public Date getLastUpdatedDate() {
    return lastUpdatedDate;
  }

  public void setLastUpdatedDate(Date lastUpdatedDate) {
    this.lastUpdatedDate = lastUpdatedDate;
  }

  public DisplayLevel getLevel() {
    return level;
  }

  public void setLevel(DisplayLevel level) {
    this.level = level;
  }


}
