/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

/**
 * @author gcr1
 *
 */
public class SegmentLink extends AbstractLink implements Cloneable, Comparable<SegmentLink> {

  private String name;

  private String ext;

  public SegmentLink() {
    super();
  }

  public SegmentLink(String id, String name, String ext) {
    super();
    this.setId(id);
    this.ext = ext;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getExt() {
    return ext;
  }

  public void setExt(String ext) {
    this.ext = ext;
  }

  public String getLabel() {
    return name + (ext != null ? "_" + ext : "");
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SegmentLink other = (SegmentLink) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }


  public SegmentLink clone() {
    SegmentLink clonedLink = new SegmentLink();
    clonedLink.setExt(this.ext);
    clonedLink.setName(this.name);
    clonedLink.setId(this.getId());
    return clonedLink;
  }

  @Override
  public int compareTo(SegmentLink o) {
    int x =
        String.CASE_INSENSITIVE_ORDER.compare(this.getLabel() != null ? this.getLabel() : "",
            o.getLabel() != null ? o.getLabel() : "");
    if (x == 0) {
      x =
          (this.getLabel() != null ? this.getLabel() : "").compareTo(o.getLabel() != null ? o
              .getLabel() : "");
    }
    return x;
  }

  @Override
  public String toString() {
    return "SegmentLink [id=" + id + ", name=" + name + ", ext=" + ext + ", label= " + getLabel()
        + "]";
  }
}
