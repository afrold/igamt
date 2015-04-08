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
package gov.nist.healthcare.nht.acmgt.general.unit;

import gov.nist.healthcare.nht.acmgt.dto.domain.Account;
import gov.nist.healthcare.nht.acmgt.general.CustomPermissionEvaluator;
import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author fdevaulx
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextHierarchy({ @ContextConfiguration("classpath:app-config.xml"),
		@ContextConfiguration("classpath:action-servlet.xml") })
@Transactional
public class CustomPermissionEvaluatorTest {

	@Autowired
	CustomPermissionEvaluator pev;

	@Autowired
	AccountRepository accountRepository;

	@Test
	public void testHasPermission1() {
		List<GrantedAuthority> rolesUtUser1 = new ArrayList<GrantedAuthority>();
		rolesUtUser1.add(new SimpleGrantedAuthority("provider"));
		Authentication authUtUser1 = new TestingAuthenticationToken("ut-user1",
				"pass", rolesUtUser1);

		List<GrantedAuthority> rolesUtUser3 = new ArrayList<GrantedAuthority>();
		rolesUtUser3.add(new SimpleGrantedAuthority("provider"));
		Authentication authUtUser3 = new TestingAuthenticationToken("ut-user3",
				"pass", rolesUtUser3);

		List<GrantedAuthority> rolesUtSupervisor = new ArrayList<GrantedAuthority>();
		rolesUtSupervisor.add(new SimpleGrantedAuthority("supervisor"));
		Authentication authUtSupervisor = new TestingAuthenticationToken(
				"ut-supervisor", "pass", rolesUtSupervisor);

		Account acc = new Account();
		acc.setUsername("ut-user1");
		acc.setEmail("ut-user1@nist.gov");
		accountRepository.save(acc);

		Account accSup = new Account();
		accSup.setUsername("ut-supervisor");
		accSup.setEmail("ut-supervisor@nist.gov");
		accountRepository.save(accSup);

		Account acc2 = new Account();
		acc2.setUsername("ut-user2");
		acc2.setEmail("ut-user2@nist.gov");
		accountRepository.save(acc2);

		Account acc3 = new Account();
		acc3.setUsername("ut-user3");
		acc3.setEmail("ut-user3@nist.gov");
		accountRepository.save(acc3);

		Assert.assertTrue(pev.hasPermission(authUtUser1, acc.getId(),
				"accessAccountBasedResource"));
		Assert.assertFalse(pev.hasPermission(authUtUser1, acc2.getId(),
				"accessAccountBasedResource"));
		Assert.assertFalse(pev.hasPermission(authUtUser1, acc.getId(),
				"accessAccountBasedReso"));

		Assert.assertFalse(pev.hasPermission(authUtUser3, acc.getId(),
				"accessAccountBasedResource"));
		Assert.assertFalse(pev.hasPermission(authUtUser3, acc2.getId(),
				"accessAccountBasedResource"));
		Assert.assertFalse(pev.hasPermission(authUtUser3, acc.getId(),
				"accessAccountBasedReso"));

		Assert.assertTrue(pev.hasPermission(authUtSupervisor, acc.getId(),
				"accessAccountBasedResource"));
		Assert.assertTrue(pev.hasPermission(authUtSupervisor, acc2.getId(),
				"accessAccountBasedResource"));
		Assert.assertFalse(pev.hasPermission(authUtSupervisor, acc.getId(),
				"accessAccountBasedReso"));
	}

	@Test
	public void testHasPermission2() {
		List<GrantedAuthority> rolesUtUser1 = new ArrayList<GrantedAuthority>();
		rolesUtUser1.add(new SimpleGrantedAuthority("provider"));
		Authentication authUtUser1 = new TestingAuthenticationToken("ut-user1",
				"pass", rolesUtUser1);

		List<GrantedAuthority> rolesUtUser3 = new ArrayList<GrantedAuthority>();
		rolesUtUser3.add(new SimpleGrantedAuthority("provider"));
		Authentication authUtUser3 = new TestingAuthenticationToken("ut-user3",
				"pass", rolesUtUser3);

		List<GrantedAuthority> rolesUtSupervisor = new ArrayList<GrantedAuthority>();
		rolesUtSupervisor.add(new SimpleGrantedAuthority("supervisor"));
		Authentication authUtSupervisor = new TestingAuthenticationToken(
				"ut-supervisor", "pass", rolesUtSupervisor);

		Account acc = new Account();
		acc.setUsername("ut-user1");
		acc.setEmail("ut-user1@nist.gov");
		accountRepository.save(acc);

		Account accSup = new Account();
		accSup.setUsername("ut-supervisor");
		accSup.setEmail("ut-supervisor@nist.gov");
		accountRepository.save(accSup);

		Account acc2 = new Account();
		acc2.setUsername("ut-user2");
		acc2.setEmail("ut-user2@nist.gov");
		accountRepository.save(acc2);

		Account acc3 = new Account();
		acc3.setUsername("ut-user3");
		acc3.setEmail("ut-user3@nist.gov");
		accountRepository.save(acc3);

		Assert.assertTrue(pev.hasPermission(authUtUser1, acc.getId(),
				"Long.class", "accessAccountBasedResource"));
		Assert.assertFalse(pev.hasPermission(authUtUser1, acc2.getId(),
				"Long.class", "accessAccountBasedResource"));
		Assert.assertFalse(pev.hasPermission(authUtUser1, acc.getId(),
				"Long.class", "accessAccountBasedReso"));

		Assert.assertFalse(pev.hasPermission(authUtUser3, acc.getId(),
				"Long.class", "accessAccountBasedResource"));
		Assert.assertFalse(pev.hasPermission(authUtUser3, acc2.getId(),
				"Long.class", "accessAccountBasedResource"));
		Assert.assertFalse(pev.hasPermission(authUtUser3, acc.getId(),
				"Long.class", "accessAccountBasedReso"));

		Assert.assertTrue(pev.hasPermission(authUtSupervisor, acc.getId(),
				"Long.class", "accessAccountBasedResource"));
		Assert.assertTrue(pev.hasPermission(authUtSupervisor, acc2.getId(),
				"Long.class", "accessAccountBasedResource"));
		Assert.assertFalse(pev.hasPermission(authUtSupervisor, acc.getId(),
				"Long.class", "accessAccountBasedReso"));

	}

}
