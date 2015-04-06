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

import java.util.Enumeration;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import com.mongodb.MongoCredential;

/**
 * @author Harold Affo (harold.affo@nist.gov) Mar 30, 2015
 */
public class CustomMongoJNDIFactory implements ObjectFactory {

	@Override
	public Object getObjectInstance(Object obj, Name name, Context nameCtx,
			Hashtable<?, ?> environment) throws Exception {

		validateProperty(obj, "Invalid JNDI object reference");

		String db = null;
		String host = null;
		String username = null;
		String password = null;
		int port = 27017;

		Reference ref = (Reference) obj;
		Enumeration<RefAddr> props = ref.getAll();
		while (props.hasMoreElements()) {
			RefAddr addr = props.nextElement();
			String propName = addr.getType();
			String propValue = (String) addr.getContent();
			if (propName.equals("db")) {
				db = propValue;
			} else if (propName.equals("host")) {
				host = propValue;
			} else if (propName.equals("username")) {
				username = propValue;
			} else if (propName.equals("password")) {
				password = propValue;
			} else if (name.equals("port")) {
				try {
					port = Integer.parseInt(propValue);
				} catch (NumberFormatException e) {
					throw new NamingException("Invalid port value " + propValue);
				}
			}

		}

		// validate properties
		validateProperty(db, "Invalid or empty mongo database name");
		validateProperty(host, "Invalid or empty mongo host");
		validateProperty(username, "Invalid or empty mongo username");
		validateProperty(password, "Invalid or empty mongo password");

		MongoCredential credential = MongoCredential.createMongoCRCredential(
				username, db, password.toCharArray());
		// MongoClient mongo = new MongoClient(new ServerAddress(host,
		// Integer.valueOf(port)), Arrays.asList(credential));
		return credential;
	}

	/**
	 * Validate internal String properties
	 * 
	 * @param property
	 * @param errorMessage
	 * @throws NamingException
	 */
	private void validateProperty(String property, String errorMessage)
			throws NamingException {
		if (property == null || property.trim().equals("")) {
			throw new NamingException(errorMessage);
		}
	}

	/**
	 * Validate internal Object properties
	 * 
	 * @param property
	 * @param errorMessage
	 * @throws NamingException
	 */
	private void validateProperty(Object property, String errorMessage)
			throws NamingException {
		if (property == null) {
			throw new NamingException(errorMessage);
		}
	}

}
