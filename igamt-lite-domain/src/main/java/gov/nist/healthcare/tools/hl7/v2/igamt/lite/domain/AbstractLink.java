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

import java.util.ArrayList;
import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;

/**
 * @author gcr1
 *
 */
public abstract class AbstractLink {

  protected String id;
  protected SCOPE scope;
  protected String type;
  protected String hl7Version;
  protected int numberOfChilren;
  protected String description;
  protected STATUS status;


protected int  publicationVersion;
  
  protected List<String> hl7versions;
  
  public String getType() {
	return type;
  }
  public void setType(String type) {
		this.type = type;
	}

  public SCOPE getScope() {
	return scope;
  }

public void setScope(SCOPE scope) {
	this.scope = scope;
}

public String getHl7Version() {
	return hl7Version;
}

public void setHl7Version(String hl7Version) {
	this.hl7Version = hl7Version;
}

public int getNumberOfChilren() {
	return numberOfChilren;
}

public void setNumberOfChilren(int numberOfChilren) {
	this.numberOfChilren = numberOfChilren;
}

public String getDescription() {
	return description;
}

public void setDescription(String description) {
	this.description = description;
}

public STATUS getStatus() {
	return status;
}

public void setStatus(STATUS status) {
	this.status = status;
}

public int getPublicationVersion() {
	return publicationVersion;
}

public void setPublicationVersion(int i) {
	this.publicationVersion = i;
}

public List<String> getHl7versions() {
	return hl7versions;
}

public void setHl7versions(List<String> hl7versions) {
	this.hl7versions = hl7versions;
}

public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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
    AbstractLink other = (AbstractLink) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }



}
