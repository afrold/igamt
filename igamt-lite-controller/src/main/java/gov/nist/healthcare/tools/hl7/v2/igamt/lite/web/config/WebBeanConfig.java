/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.config;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentConfiguration;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileConfiguration;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Harold Affo (NIST)
 * 
 */

@Configuration
public class WebBeanConfig {
	
	
	@Bean
	IGDocumentConfiguration igDocumentConfig() {
		IGDocumentConfiguration config = new IGDocumentConfiguration();
		config.setStatuses(toSet(new String[] { "Draft", "Active",
				"Superceded", "Withdrawn" }));
		config.setDomainVersions(toSet(new String[] { "2.0", "2.1", "2.2",
				"2.3", "2.3.1", "2.4", "2.5", "2.5.1", "2.6", "2.7", "2.8" }));
		config.setSchemaVersions(toSet(new String[] { "1.0", "1.5", "2.0",
				"2.5" }));
		config.setUsages(toSet(new String[] { "R", "RE", "O", "C", "X", "B",
				"W" }));
		config.setCodeUsages(toSet(new String[] { "R", "P", "E" }));
		config.setCodeSources(toSet(new String[] { "HL7", "Local", "Redefined",
				"SDO" }));
		config.setTableStabilities(toSet(new String[] { "Static", "Dynamic" }));
		config.setTableContentDefinitions(toSet(new String[] { "Extensional", "Intensional" }));
		config.setTableExtensibilities(toSet(new String[] { "Open", "Close" }));
		config.setConstraintVerbs(toSet(new String[] { "SHALL be", "SHALL NOT be", "is", "is not" }));
		config.setConstraintTypes(toSet(new String[] { 
				"valued",
				"a literal value", 
				"one of list values", 
				"one of codes in ValueSet",
				"formatted value",
				"identical to the another node",
				"equal to the another node",
				"not-equal to the another node",
				"greater than the another node",
				"equal to or greater than the another node",
				"less than the another node",
				"equal to or less than the another node",
				"equal to",
				"not-equal to",
				"greater than",
				"equal to or greater than",
				"less than",
				"equal to or less than"
		}));
		config.setPredefinedFormats(toSet(new String[] { "ISO-compliant OID",
				"Alphanumeric", "YYYY", "YYYYMM", "YYYYMMDD", "YYYYMMDDhh",
				"YYYYMMDDhhmm", "YYYYMMDDhhmmss", "YYYYMMDDhhmmss.sss",
				"YYYY+-ZZZZ", "YYYYMM+-ZZZZ", "YYYYMMDD+-ZZZZ",
				"YYYYMMDDhh+-ZZZZ", "YYYYMMDDhhmm+-ZZZZ",
				"YYYYMMDDhhmmss+-ZZZZ", "YYYYMMDDhhmmss.sss+-ZZZZ" }));
		return config;
	}
	

	@Bean
	ProfileConfiguration profileConfig() {
		ProfileConfiguration config = new ProfileConfiguration();
		config.setStatuses(toSet(new String[] { "Draft", "Active",
				"Superceded", "Withdrawn" }));
		config.setDomainVersions(toSet(new String[] { "2.0", "2.1", "2.2",
				"2.3", "2.3.1", "2.4", "2.5", "2.5.1", "2.6", "2.7", "2.8" }));
		config.setSchemaVersions(toSet(new String[] { "1.0", "1.5", "2.0",
				"2.5" }));
		config.setUsages(toSet(new String[] { "R", "RE", "O", "C", "X", "B",
				"W" }));
		config.setCodeUsages(toSet(new String[] { "R", "P", "E" }));
		config.setCodeSources(toSet(new String[] { "HL7", "Local", "Redefined",
				"SDO" }));
		config.setTableStabilities(toSet(new String[] { "Static", "Dynamic" }));
		config.setTableContentDefinitions(toSet(new String[] { "Extensional", "Intensional" }));
		config.setTableExtensibilities(toSet(new String[] { "Open", "Close" }));
		config.setConstraintVerbs(toSet(new String[] { "SHALL be", "SHALL NOT be", "is", "is not" }));
		config.setConstraintTypes(toSet(new String[] { 
				"valued",
				"a literal value", 
				"one of list values", 
				"one of codes in ValueSet",
				"formatted value",
				"identical to the another node",
				"equal to the another node",
				"not-equal to the another node",
				"greater than the another node",
				"equal to or greater than the another node",
				"less than the another node",
				"equal to or less than the another node",
				"equal to",
				"not-equal to",
				"greater than",
				"equal to or greater than",
				"less than",
				"equal to or less than"
		}));
		config.setPredefinedFormats(toSet(new String[] { "ISO-compliant OID",
				"Alphanumeric", "YYYY", "YYYYMM", "YYYYMMDD", "YYYYMMDDhh",
				"YYYYMMDDhhmm", "YYYYMMDDhhmmss", "YYYYMMDDhhmmss.sss",
				"YYYY+-ZZZZ", "YYYYMM+-ZZZZ", "YYYYMMDD+-ZZZZ",
				"YYYYMMDDhh+-ZZZZ", "YYYYMMDDhhmm+-ZZZZ",
				"YYYYMMDDhhmmss+-ZZZZ", "YYYYMMDDhhmmss.sss+-ZZZZ" }));
		return config;
	}

	private Set<String> toSet(String[] values) {
		Set<String> res = new HashSet<String>();
		for (String v : values) {
			res.add(v);
		}
		return res;
	}
}
