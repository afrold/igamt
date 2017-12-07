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
public class TableLink extends AbstractLink implements Cloneable, Comparable<TableLink> {

  private String bindingIdentifier;

  private String bindingStrength;

  private String bindingLocation;

  private Boolean includeCode;

  public TableLink() {
    super();
    this.includeCode = true;
  }


  public TableLink(String id, String bindingIdentifier) {
    super();
    this.setId(id);
    this.bindingIdentifier = bindingIdentifier;
  }


  /**
   * @param bindingIdentifier
   * @param bindingStrength
   * @param bindingLocation
   */
  public TableLink(String id, String bindingIdentifier, String bindingStrength,
      String bindingLocation) {
    this.id = id;
    this.bindingIdentifier = bindingIdentifier;
    this.bindingStrength = bindingStrength;
    this.bindingLocation = bindingLocation;
  }

  public String getBindingIdentifier() {
    return bindingIdentifier;
  }

  public void setBindingIdentifier(String bindingIdentifier) {
    this.bindingIdentifier = bindingIdentifier;
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
    TableLink other = (TableLink) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

  @Override
  public TableLink clone() {
    TableLink clonedLink = new TableLink();
    clonedLink.setBindingIdentifier(this.getBindingIdentifier());
    clonedLink.setBindingLocation(this.bindingLocation);
    clonedLink.setBindingStrength(this.bindingStrength);
    clonedLink.setId(this.getId());
    return clonedLink;
  }

  @Override
  public int compareTo(TableLink o) {
    int x = String.CASE_INSENSITIVE_ORDER.compare(
        this.getBindingIdentifier() != null ? this.getBindingIdentifier() : "",
        o.getBindingIdentifier() != null ? o.getBindingIdentifier() : "");
    if (x == 0) {
      x = (this.getBindingIdentifier() != null ? this.getBindingIdentifier() : "")
          .compareTo(o.getBindingIdentifier() != null ? o.getBindingIdentifier() : "");
    }
    return x;
  }

  public String getBindingStrength() {
    return bindingStrength;
  }

  public void setBindingStrength(String bindingStrength) {
    this.bindingStrength = bindingStrength;
  }

  public String getBindingLocation() {
    return bindingLocation;
  }

  public void setBindingLocation(String bindingLocation) {
    this.bindingLocation = bindingLocation;
  }


  @Override
  public String toString() {
    return "TableLink [bindingIdentifier=" + bindingIdentifier + ", bindingStrength="
        + bindingStrength + ", bindingLocation=" + bindingLocation + ", id=" + id + "]";
  }



}
