/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgment if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 */
package gov.nist.healthcare.nht.acmgt.web;

import java.util.Map.Entry;
import java.util.Properties;

import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

/**
 * @author Dom
 *
 * Jul 29, 2009
 * PMS	
 */
public class CustomPersistenceUnitPostProcessor implements PersistenceUnitPostProcessor {
	
	Properties persistenceProperties;

	/**
	 * @return the persistenceProperties
	 */
	public Properties getPersistenceProperties() {
		return persistenceProperties;
	}

	/**
	 * @param persistenceProperties the persistenceProperties to set
	 */
	public void setPersistenceProperties(Properties persistenceProperties) {
		this.persistenceProperties = persistenceProperties;
	}

	/* (non-Javadoc)
	 * @see org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor#postProcessPersistenceUnitInfo(org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo)
	 */
	@Override
	public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo arg0) {
		for (Entry<Object, Object> e:persistenceProperties.entrySet()){
		arg0.addProperty(e.getKey().toString(),e.getValue().toString());
		}
	}
	
}
//End of Class
