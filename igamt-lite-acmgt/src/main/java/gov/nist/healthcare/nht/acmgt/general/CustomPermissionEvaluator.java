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
package gov.nist.healthcare.nht.acmgt.general;

import gov.nist.healthcare.nht.acmgt.dto.domain.Account;
import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;

import java.io.Serializable;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * @author fdevaulx
 * 
 */
@Component(value = "customPermissionEvaluator")
public class CustomPermissionEvaluator implements PermissionEvaluator {

	static final Logger logger = LoggerFactory
			.getLogger(CustomPermissionEvaluator.class);

	@Inject
	AccountRepository accountRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.access.PermissionEvaluator#hasPermission
	 * (org.springframework.security.core.Authentication, java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public boolean hasPermission(Authentication authentication,
			Object targetDomainObject, Object permission) {
		logger.debug("^^^^^^^^^^^^^^^^^ 0 ^^^^^^^^^^^^^^^^^^");
		if ("accessAccountBasedResource".equals(permission)) {
			logger.debug("^^^^^^^^^^^^^^^^^ 1 ^^^^^^^^^^^^^^^^^^");
			Account acc = accountRepository
					.findByTheAccountsUsername(authentication.getName());
			logger.debug("^^^^^^^^^^^^^^^^^ 2 " + acc + " ^^^^^^^^^^^^^^^^^^");
			if (acc == null) {
				return false;
			}
			logger.debug("^^^^^^^^^^^^^^^^^ 3 acc.getId(): " + acc.getId()
					+ " targetDomainObject: " + targetDomainObject
					+ " ^^^^^^^^^^^^^^^^^^");
			if (acc.getId().equals(targetDomainObject)
					|| authentication.getAuthorities().contains(
							new SimpleGrantedAuthority("supervisor"))
					|| authentication.getAuthorities().contains(
							new SimpleGrantedAuthority("admin"))) {
				return true;
			}
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.access.PermissionEvaluator#hasPermission
	 * (org.springframework.security.core.Authentication, java.io.Serializable,
	 * java.lang.String, java.lang.Object)
	 */
	@Override
	public boolean hasPermission(Authentication authentication,
			Serializable targetId, String targetType, Object permission) {

		if ("accessAccountBasedResource".equals(permission)) {
			Account acc = accountRepository
					.findByTheAccountsUsername(authentication.getName());
			if (acc == null) {
				return false;
			}
			if (acc.getId() == targetId
					|| authentication.getAuthorities().contains(
							new SimpleGrantedAuthority("supervisor"))
					|| authentication.getAuthorities().contains(
							new SimpleGrantedAuthority("admin"))) {
				return true;
			}
		}

		return false;
	}

}
