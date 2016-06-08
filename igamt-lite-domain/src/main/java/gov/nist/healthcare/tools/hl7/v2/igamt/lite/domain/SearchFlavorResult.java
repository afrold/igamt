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
public class SearchFlavorResult extends AbstractLink {

  private String name;
  private String ext;
  private String libName;

  public SearchFlavorResult(String id, String name, String ext, String libName) {
    super();
    this.setId(id);
    this.name = name;
    this.setExt(ext);
    this.libName = libName;
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
    return name + (ext != null ? ext : "");
  }

  @Override
  public int hashCode() {
    return (getId() + getLabel()).hashCode();
  }

  @Override
  public boolean equals(Object obj) {

    SearchFlavorResult link = null;

    if (obj == null) {
      return false;
    }

    if (obj instanceof SearchFlavorResult) {
      link = (SearchFlavorResult) obj;
    } else {
      return false;
    }

    return getId().equals(link.getId()) && getLabel().equals(link.getLabel());
  }
}
