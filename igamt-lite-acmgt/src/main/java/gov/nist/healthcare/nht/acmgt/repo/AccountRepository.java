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
package gov.nist.healthcare.nht.acmgt.repo;

import gov.nist.healthcare.nht.acmgt.dto.domain.Account;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;



/**
 * Repository interface for {@link Account} instances. Provides basic CRUD
 * operations due to the extension of {@link JpaRepository}.
 * 
 * @author fdevaulx
 */ 
public interface AccountRepository extends JpaRepository<Account, Long>,
		JpaSpecificationExecutor<Account> {

	/** 
	 * Find an account by the username of the account. Username is unique.
	 * */
	@Query("select a from Account a where a.username = ?1")
	public Account findByTheAccountsUsername(String username);

	/**
	 * Find an account by the email address of the account. Email address is
	 * unique.
	 * */
	@Query("select a from Account a where a.email = ?1")
	public Account findByTheAccountsEmail(String email);

	/**
	 * 
	 * */
	@Query("select a from Account a where a.accountType = ?1")
	public List<Account> findByTheAccountsAccountType(String accountType);
}
