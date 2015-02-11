package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import javax.persistence.Embeddable;

@Embeddable
public class Reference implements java.io.Serializable,Cloneable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7922971557486999716L;
	
	private String chapter;
	private String section;
	private int page;
	private String url;
	
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
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
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
}
