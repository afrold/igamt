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

import java.util.HashSet;
import java.util.Set;

/**
 * @author Harold Affo (harold.affo@nist.gov) Apr 28, 2015
 */
public class ProfileConfiguration {

  private Set<String> usages = new HashSet<String>();
  private Set<String> codeUsages = new HashSet<String>();
  private Set<String> codeSources = new HashSet<String>();
  private Set<String> tableStabilities = new HashSet<String>();
  private Set<String> tableContentDefinitions = new HashSet<String>();
  private Set<String> tableExtensibilities = new HashSet<String>();
  private Set<String> constraintVerbs = new HashSet<String>();
  private Set<String> constraintTypes = new HashSet<String>();
  private Set<String> predefinedFormats = new HashSet<String>();
  private Set<String> statuses = new HashSet<String>();
  private Set<String> domainVersions = new HashSet<String>();
  private Set<String> schemaVersions = new HashSet<String>();

  public Set<String> getUsages() {
    return usages;
  }

  public Set<String> getCodeUsages() {
    return codeUsages;
  }

  public Set<String> getTableStabilities() {
    return tableStabilities;
  }

  public Set<String> getTableExtensibilities() {
    return tableExtensibilities;
  }

  public Set<String> getConstraintVerbs() {
    return constraintVerbs;
  }

  public Set<String> getConstraintTypes() {
    return constraintTypes;
  }

  public Set<String> getPredefinedFormats() {
    return predefinedFormats;
  }

  public Set<String> getStatuses() {
    return statuses;
  }

  public Set<String> getDomainVersions() {
    return domainVersions;
  }

  public Set<String> getSchemaVersions() {
    return schemaVersions;
  }

  public void setUsages(Set<String> usages) {
    this.usages = usages;
  }

  public void setCodeUsages(Set<String> codeUsages) {
    this.codeUsages = codeUsages;
  }

  public void setTableStabilities(Set<String> tableStabilities) {
    this.tableStabilities = tableStabilities;
  }

  public void setTableExtensibilities(Set<String> tableExtensibilities) {
    this.tableExtensibilities = tableExtensibilities;
  }

  public void setConstraintVerbs(Set<String> constraintVerbs) {
    this.constraintVerbs = constraintVerbs;
  }

  public void setConstraintTypes(Set<String> constraintTypes) {
    this.constraintTypes = constraintTypes;
  }

  public void setPredefinedFormats(Set<String> predefinedFormats) {
    this.predefinedFormats = predefinedFormats;
  }

  public void setStatuses(Set<String> statuses) {
    this.statuses = statuses;
  }

  public void setDomainVersions(Set<String> domainVersions) {
    this.domainVersions = domainVersions;
  }

  public void setSchemaVersions(Set<String> schemaVersions) {
    this.schemaVersions = schemaVersions;
  }

  public Set<String> getCodeSources() {
    return codeSources;
  }

  public void setCodeSources(Set<String> codeSources) {
    this.codeSources = codeSources;
  }

  public Set<String> getTableContentDefinitions() {
    return tableContentDefinitions;
  }

  public void setTableContentDefinitions(Set<String> tableContentDefinitions) {
    this.tableContentDefinitions = tableContentDefinitions;
  }

}
