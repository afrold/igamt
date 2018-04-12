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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Harold Affo (harold.affo@nist.gov) Apr 28, 2015
 */
public class IGDocumentConfiguration {

	private Set<String> usages = new HashSet<String>();
	private Set<String> conditionalUsage = new HashSet<String>();
	private Set<String> codeUsages = new HashSet<String>();
	private Set<String> codeSources = new HashSet<String>();
	private Set<String> tableStabilities = new HashSet<String>();
	private Set<String> tableContentDefinitions = new HashSet<String>();
	private Set<String> tableExtensibilities = new HashSet<String>();
	private Set<String> constraintVerbs = new HashSet<String>();
	private Set<String> conditionalConstraintVerbs = new HashSet<String>();
	private Set<String> constraintTypes = new HashSet<String>();
	private Set<String> predefinedFormats = new HashSet<String>();
	private Set<String> statuses = new HashSet<String>();
	private Set<String> domainVersions = new HashSet<String>();
	private Set<String> schemaVersions = new HashSet<String>();
	private Set<String> valueSetAllowedDTs = new HashSet<String>();
	private Set<String> singleValueSetDTs = new HashSet<String>();
	private Set<DTComponent> valueSetAllowedComponents = new HashSet<DTComponent>();
	private Set<SegmentField> valueSetAllowedFields = new HashSet<SegmentField>();
	private Set<String> codedElementDTs = new HashSet<String>();
	private HashMap<String, Set<String>> bindingLocationListByHL7Version = new HashMap<String, Set<String>>();
	private Set<VariesMapItem> variesMapItems = new HashSet<VariesMapItem>();
	private HashMap<Integer, String> dtmRUsageRegexCodes = new HashMap<Integer, String>();
	private HashMap<Integer, String> dtmXUsageRegexCodes = new HashMap<Integer, String>();
	private HashMap<Integer, String> dtmCUsageIsValuedRegexCodes = new HashMap<Integer, String>();
	private HashMap<Integer, String> dtmCUsageIsNOTValuedRegexCodes = new HashMap<Integer, String>();
	private HashMap<Integer, String> dtmCUsageIsLiteralValueRegexCodes = new HashMap<Integer, String>();
	
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

	public Set<String> getConditionalUsage() {
		return conditionalUsage;
	}

	public void setConditionalUsage(Set<String> conditionalUsage) {
		this.conditionalUsage = conditionalUsage;
	}

	public Set<String> getConditionalConstraintVerbs() {
		return conditionalConstraintVerbs;
	}

	public void setConditionalConstraintVerbs(Set<String> conditionalConstraintVerbs) {
		this.conditionalConstraintVerbs = conditionalConstraintVerbs;
	}

	public Set<String> getValueSetAllowedDTs() {
		return valueSetAllowedDTs;
	}

	public void setValueSetAllowedDTs(Set<String> valueSetAllowedDTs) {
		this.valueSetAllowedDTs = valueSetAllowedDTs;
	}

	public Set<DTComponent> getValueSetAllowedComponents() {
		return valueSetAllowedComponents;
	}

	public void setValueSetAllowedComponents(Set<DTComponent> valueSetAllowedComponents) {
		this.valueSetAllowedComponents = valueSetAllowedComponents;
	}

	public Set<String> getCodedElementDTs() {
		return codedElementDTs;
	}

	public void setCodedElementDTs(Set<String> codedElementDTs) {
		this.codedElementDTs = codedElementDTs;
	}

	public HashMap<String, Set<String>> getBindingLocationListByHL7Version() {
		return bindingLocationListByHL7Version;
	}

	public void setBindingLocationListByHL7Version(HashMap<String, Set<String>> bindingLocationListByHL7Version) {
		this.bindingLocationListByHL7Version = bindingLocationListByHL7Version;
	}

	public Set<VariesMapItem> getVariesMapItems() {
		return variesMapItems;
	}

	public void setVariesMapItems(Set<VariesMapItem> variesMapItems) {
		this.variesMapItems = variesMapItems;
	}

	public Set<String> getSingleValueSetDTs() {
		return singleValueSetDTs;
	}

	public void setSingleValueSetDTs(Set<String> singleValueSetDTs) {
		this.singleValueSetDTs = singleValueSetDTs;
	}

  public HashMap<Integer, String> getDtmRUsageRegexCodes() {
    return dtmRUsageRegexCodes;
  }

  public void setDtmRUsageRegexCodes(HashMap<Integer, String> dtmRUsageRegexCodes) {
    this.dtmRUsageRegexCodes = dtmRUsageRegexCodes;
  }

  public HashMap<Integer, String> getDtmXUsageRegexCodes() {
    return dtmXUsageRegexCodes;
  }

  public void setDtmXUsageRegexCodes(HashMap<Integer, String> dtmXUsageRegexCodes) {
    this.dtmXUsageRegexCodes = dtmXUsageRegexCodes;
  }

  public HashMap<Integer, String> getDtmCUsageIsValuedRegexCodes() {
    return dtmCUsageIsValuedRegexCodes;
  }

  public void setDtmCUsageIsValuedRegexCodes(HashMap<Integer, String> dtmCUsageIsValuedRegexCodes) {
    this.dtmCUsageIsValuedRegexCodes = dtmCUsageIsValuedRegexCodes;
  }

  public HashMap<Integer, String> getDtmCUsageIsNOTValuedRegexCodes() {
    return dtmCUsageIsNOTValuedRegexCodes;
  }

  public void setDtmCUsageIsNOTValuedRegexCodes(HashMap<Integer, String> dtmCUsageIsNOTValuedRegexCodes) {
    this.dtmCUsageIsNOTValuedRegexCodes = dtmCUsageIsNOTValuedRegexCodes;
  }

  public HashMap<Integer, String> getDtmCUsageIsLiteralValueRegexCodes() {
    return dtmCUsageIsLiteralValueRegexCodes;
  }

  public void setDtmCUsageIsLiteralValueRegexCodes(HashMap<Integer, String> dtmCUsageIsLiteralValueRegexCodes) {
    this.dtmCUsageIsLiteralValueRegexCodes = dtmCUsageIsLiteralValueRegexCodes;
  }
  
  public Set<SegmentField> getValueSetAllowedFields() {
    return valueSetAllowedFields;
  }

  public void setValueSetAllowedFields(Set<SegmentField> valueSetAllowedFields) {
    this.valueSetAllowedFields = valueSetAllowedFields;
  }
}
