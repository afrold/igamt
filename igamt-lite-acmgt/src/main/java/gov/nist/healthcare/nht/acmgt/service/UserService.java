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
package gov.nist.healthcare.nht.acmgt.service;

import java.util.List;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;

/**
 * @author traveluser
 *
 */
public interface UserService {

	/**
	 * Returns whether a user exists
	 * 
	 * @param username the name of a user
	 * @return whether the user exists
	 */
	public abstract Boolean userExists(String username);

	/**
	 * Returns whether a user exists
	 * @param <T>
	 * 
	 * @param username the name of a user
	 * @return whether the user exists
	 */
	public abstract <T> T retrieveUserByUsername(String username);
	
	/**
	 * Creates a user
	 * 
	 * @param username the name of the user
	 * @param password the password
	 */
	public abstract void createUserWithDefaultAuthority(String username, String password);

	/**
	 * Creates a user with given authorities
	 * @param <T>
	 * 
	 * @param username the name of the user
	 * @param password the password
	 * @param authorities the list of the authorities
	 */
	public abstract <T> void createUserWithAuthorities(String username, String password,T authorities) throws Exception;
	
	/**
	 * Changes the password for this principal
	 * 
	 * @param oldPassword the current password for this principal
	 * @param newPassword the new password for this principal
	 * @throws BadCredentialsException the oldPassword is incorrect
	 */
	public abstract void changePasswordForPrincipal(String oldPassword, String newPassword)
			throws BadCredentialsException;

	/**
	 * Changes the password for this username
	 * 
	 * @param oldPassword the current password for this principal
	 * @param newPassword the new password for this principal
	 * @throws BadCredentialsException the oldPassword is incorrect
	 */
	public abstract void changePasswordForUser(String oldPassword, String newPassword, String username) throws BadCredentialsException; 
	
	/**
	 * Changes the password for this username
	 * 
	 * @param newPassword the new password for this principal
	 * @throws BadCredentialsException the oldPassword is incorrect
	 */
	public void changePasswordForUser(String newPassword, String username) throws BadCredentialsException;
	
	/**
	 * Deletes a user
	 * 
	 * @param username the name of the user to delete
	 */
	public abstract void deleteUser(String username);

	/**
	 * Disables a user
	 * 
	 * @param username the name of the user to disable
	 * */
	public abstract void disableUser(String username);
	
	/**
	 * Returns the list of enabled users
	 * 
	 * @return the names of the users
	 */
	public abstract List<String> findAllEnabledUsers();
	
	/**
	 * Returns the list of disabled users
	 * 
	 * @return the names of the users
	 */
	public abstract List<String> findAllDisabledUsers();
	
	/**
	 * 
	 * */
	public abstract User getCurrentUser();

        /**
         * 
         */
        public void enableUserCredentials(String username);
}