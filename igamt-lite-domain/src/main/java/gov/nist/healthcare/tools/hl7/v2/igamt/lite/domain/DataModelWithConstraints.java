package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

public class DataModelWithConstraints extends DataModel {
  protected String hl7Version;

  protected Long accountId;

  protected SCOPE scope;

  protected String parentVersion;

  public String getParentVersion() {
    return parentVersion;
  }

  public void setParentVersion(String parentVersion) {
    this.parentVersion = parentVersion;
  }

  @Deprecated
  protected String date;

  protected String version;


  protected STATUS status = STATUS.UNPUBLISHED;

  protected Set<String> participants = new HashSet<String>();

  protected Set<String> libIds = new HashSet<String>();

  protected List<Predicate> predicates = new ArrayList<Predicate>();

  protected List<ConformanceStatement> conformanceStatements =
      new ArrayList<ConformanceStatement>();

  public String getHl7Version() {
    return hl7Version;
  }

  public void setHl7Version(String hl7Version) {
    this.hl7Version = hl7Version;
  }

  public Long getAccountId() {
    return accountId;
  }

  public void setAccountId(Long accountId) {
    this.accountId = accountId;
  }

  public Set<String> getLibIds() {
    if (libIds == null) {
      libIds = new HashSet<String>();
    }
    return libIds;
  }

  public void setLibId(Set<String> libIds) {
    this.libIds = libIds;
  }

  public List<Predicate> getPredicates() {
    return predicates;
  }

  public void setPredicates(List<Predicate> predicates) {
    this.predicates = predicates;
  }

  public List<ConformanceStatement> getConformanceStatements() {
    return conformanceStatements;
  }

  public void setConformanceStatements(List<ConformanceStatement> conformanceStatements) {
    this.conformanceStatements = conformanceStatements;
  }

  public void addPredicate(Predicate p) {
    predicates.add(p);
  }

  public void addConformanceStatement(ConformanceStatement cs) {
    conformanceStatements.add(cs);
  }

  public Predicate findOnePredicate(String predicateId) {
    for (Predicate predicate : this.getPredicates()) {
      if (predicate.getId().equals(predicateId)) {
        return predicate;
      }
    }
    return null;
  }

  public ConformanceStatement findOneConformanceStatement(String confId) {
    for (ConformanceStatement conf : this.getConformanceStatements()) {
      if (conf.getId().equals(confId)) {
        return conf;
      }
    }
    return null;
  }

  public boolean deletePredicate(String predicateId) {
    Predicate p = findOnePredicate(predicateId);
    return p != null && this.getPredicates().remove(p);
  }

  public boolean deleteConformanceStatement(String cId) {
    ConformanceStatement c = findOneConformanceStatement(cId);
    return c != null && this.getConformanceStatements().remove(c);
  }

  public SCOPE getScope() {
    return scope;
  }

  public void setScope(SCOPE scope) {
    this.scope = scope;
  }

  public Set<String> getParticipants() {
    return participants;
  }

  public void setParticipants(Set<String> participants) {
    this.participants = participants;
  }

  @Deprecated
  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public void setLibIds(Set<String> libIds) {
    this.libIds = libIds;
  }

  public STATUS getStatus() {
    return status;
  }

  public void setStatus(STATUS status) {
    this.status = status;
  }



}
