package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

// @Embeddable
public class Reference implements java.io.Serializable, Cloneable {

  /**
	 * 
	 */
  private static final long serialVersionUID = -7922971557486999716L;

  // @Column(name = "CHAPTER")
  private String chapter;
  // @Column(name = "SECTION")
  private String section;
  // @Column(name = "PAGE")
  private Integer page;
  // @Column(name = "URL")
  private String url;

  private String source;

  private String generatedBy;

  private String referenccePath;

  private String testDataCategorization;

  public String getChapter() {
    return chapter;
  }

  public void setChapter(String chapter) {
    this.chapter = chapter;
  }

  public String getSection() {
    return section;
  }

  public void setSection(String section) {
    this.section = section;
  }

  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Override
  public Reference clone() throws CloneNotSupportedException {
    Reference clonedReference = (Reference) super.clone();
    return clonedReference;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getGeneratedBy() {
    return generatedBy;
  }

  public void setGeneratedBy(String generatedBy) {
    this.generatedBy = generatedBy;
  }

  public String getReferenccePath() {
    return referenccePath;
  }

  public void setReferenccePath(String referenccePath) {
    this.referenccePath = referenccePath;
  }

  public String getTestDataCategorization() {
    return testDataCategorization;
  }

  public void setTestDataCategorization(String testDataCategorization) {
    this.testDataCategorization = testDataCategorization;
  }

}
