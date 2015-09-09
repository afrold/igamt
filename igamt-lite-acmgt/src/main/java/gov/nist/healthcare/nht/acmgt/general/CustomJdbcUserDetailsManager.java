/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.nist.healthcare.nht.acmgt.general;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.provisioning.GroupManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.util.Assert;

/**
 * 
 * @author fdevaulx
 */
public class CustomJdbcUserDetailsManager extends JdbcDaoImpl implements
		UserDetailsManager, GroupManager {

	// ~ Static fields/initializers
	// =====================================================================================

	// UserDetailsManager SQL
	// Custom
	public static final String DEF_CREATE_USER_SQL = "insert into users (username, password, enabled, accountNonExpired"
			+ ", accountNonLocked, credentialsNonExpired) values (?,?,?,?,?,?)";
	public static final String DEF_DELETE_USER_SQL = "delete from users where username = ?";
	// Custom
	public static final String DEF_UPDATE_USER_SQL = "update users set password = ?, enabled = ?, accountNonExpired = ?"
			+ ", accountNonLocked = ?, credentialsNonExpired = ? where username = ?";
	public static final String DEF_UPDATE_USER_NO_PASS_SQL = "update users set enabled = ?, accountNonExpired = ?"
			+ ", accountNonLocked = ?, credentialsNonExpired = ? where username = ?";
	public static final String DEF_INSERT_AUTHORITY_SQL = "insert into authorities (username, authority) values (?,?)";
	public static final String DEF_DELETE_USER_AUTHORITIES_SQL = "delete from authorities where username = ?";
	public static final String DEF_USER_EXISTS_SQL = "select username from users where username = ?";
	public static final String DEF_CHANGE_PASSWORD_SQL = "update users set password = ? where username = ?";

	// GroupManager SQL
	public static final String DEF_FIND_GROUPS_SQL = "select group_name from groups";
	public static final String DEF_FIND_USERS_IN_GROUP_SQL = "select username from group_members gm, groups g "
			+ "where gm.group_id = g.id" + " and g.group_name = ?";
	public static final String DEF_INSERT_GROUP_SQL = "insert into groups (group_name) values (?)";
	public static final String DEF_FIND_GROUP_ID_SQL = "select id from groups where group_name = ?";
	public static final String DEF_INSERT_GROUP_AUTHORITY_SQL = "insert into group_authorities (group_id, authority) values (?,?)";
	public static final String DEF_DELETE_GROUP_SQL = "delete from groups where id = ?";
	public static final String DEF_DELETE_GROUP_AUTHORITIES_SQL = "delete from group_authorities where group_id = ?";
	public static final String DEF_DELETE_GROUP_MEMBERS_SQL = "delete from group_members where group_id = ?";
	public static final String DEF_RENAME_GROUP_SQL = "update groups set group_name = ? where group_name = ?";
	public static final String DEF_INSERT_GROUP_MEMBER_SQL = "insert into group_members (group_id, username) values (?,?)";
	public static final String DEF_DELETE_GROUP_MEMBER_SQL = "delete from group_members where group_id = ? and username = ?";
	public static final String DEF_GROUP_AUTHORITIES_QUERY_SQL = "select g.id, g.group_name, ga.authority "
			+ "from groups g, group_authorities ga "
			+ "where g.group_name = ? " + "and g.id = ga.group_id ";
	public static final String DEF_DELETE_GROUP_AUTHORITY_SQL = "delete from group_authorities where group_id = ? and authority = ?";

	// JdbcDaoImpl Custom SQL
	public static final String DEF_USERS_BY_USERNAME_QUERY = "select username,password,enabled,accountNonExpired"
			+ ",accountNonLocked,credentialsNonExpired "
			+ "from users "
			+ "where username = ?";

	// ~ Instance fields
	// ================================================================================================

	Logger logger = LoggerFactory.getLogger(CustomJdbcUserDetailsManager.class);

	private String createUserSql = DEF_CREATE_USER_SQL;
	private String deleteUserSql = DEF_DELETE_USER_SQL;
	private String updateUserSql = DEF_UPDATE_USER_SQL;
	private String createAuthoritySql = DEF_INSERT_AUTHORITY_SQL;
	private String deleteUserAuthoritiesSql = DEF_DELETE_USER_AUTHORITIES_SQL;
	private String userExistsSql = DEF_USER_EXISTS_SQL;
	private String changePasswordSql = DEF_CHANGE_PASSWORD_SQL;

	private String findAllGroupsSql = DEF_FIND_GROUPS_SQL;
	private String findUsersInGroupSql = DEF_FIND_USERS_IN_GROUP_SQL;
	private String insertGroupSql = DEF_INSERT_GROUP_SQL;
	private String findGroupIdSql = DEF_FIND_GROUP_ID_SQL;
	private String insertGroupAuthoritySql = DEF_INSERT_GROUP_AUTHORITY_SQL;
	private String deleteGroupSql = DEF_DELETE_GROUP_SQL;
	private String deleteGroupAuthoritiesSql = DEF_DELETE_GROUP_AUTHORITIES_SQL;
	private String deleteGroupMembersSql = DEF_DELETE_GROUP_MEMBERS_SQL;
	private String renameGroupSql = DEF_RENAME_GROUP_SQL;
	private String insertGroupMemberSql = DEF_INSERT_GROUP_MEMBER_SQL;
	private String deleteGroupMemberSql = DEF_DELETE_GROUP_MEMBER_SQL;
	private String groupAuthoritiesSql = DEF_GROUP_AUTHORITIES_QUERY_SQL;
	private String deleteGroupAuthoritySql = DEF_DELETE_GROUP_AUTHORITY_SQL;

	private AuthenticationManager authenticationManager;

	private UserCache userCache = new NullUserCache();

	// ~ Methods
	// ========================================================================================================

	@Override
	protected void initDao() throws ApplicationContextException {
		if (authenticationManager == null) {
			logger.info("No authentication manager set. Reauthentication of users when changing passwords will "
					+ "not be performed.");
		}

		super.initDao();

		setUsersByUsernameQuery(DEF_USERS_BY_USERNAME_QUERY);
	}

	// ~ UserDetailsManager implementation
	// ==============================================================================

	@Override
	public void createUser(final UserDetails user) {
		validateUserDetails(user);
		getJdbcTemplate().update(createUserSql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, user.getUsername());
				ps.setString(2, user.getPassword());
				ps.setBoolean(3, user.isEnabled());
				ps.setBoolean(4, user.isAccountNonExpired());
				ps.setBoolean(5, user.isAccountNonLocked());
				ps.setBoolean(6, user.isCredentialsNonExpired());
			}

		});

		if (getEnableAuthorities()) {
			insertUserAuthorities(user);
		}
	}

	@Override
	public void updateUser(final UserDetails user) {
		validateUserDetails(user);
		getJdbcTemplate().update(updateUserSql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, user.getPassword());
				ps.setBoolean(2, user.isEnabled());
				ps.setBoolean(3, user.isAccountNonExpired());
				ps.setBoolean(4, user.isAccountNonLocked());
				ps.setBoolean(5, user.isCredentialsNonExpired());
				ps.setString(6, user.getUsername());

			}
		});

		if (getEnableAuthorities()) {
			deleteUserAuthorities(user.getUsername());
			insertUserAuthorities(user);
		}

		userCache.removeUserFromCache(user.getUsername());
	}

	private void insertUserAuthorities(UserDetails user) {
		for (GrantedAuthority auth : user.getAuthorities()) {
			getJdbcTemplate().update(createAuthoritySql, user.getUsername(),
					auth.getAuthority());
		}
	}

	@Override
	public void deleteUser(String username) {
		if (getEnableAuthorities()) {
			deleteUserAuthorities(username);
		}
		getJdbcTemplate().update(deleteUserSql, username);
		userCache.removeUserFromCache(username);
	}

	private void deleteUserAuthorities(String username) {
		getJdbcTemplate().update(deleteUserAuthoritiesSql, username);
	}

	@Override
	public void changePassword(String oldPassword, String newPassword)
			throws org.springframework.security.core.AuthenticationException {
		Authentication currentUser = SecurityContextHolder.getContext()
				.getAuthentication();

		if (currentUser == null) {
			// This would indicate bad coding somewhere
			throw new org.springframework.security.access.AccessDeniedException(
					"Can't change password as no Authentication object found in context "
							+ "for current user.");
		}

		String username = currentUser.getName();

		// If an authentication manager has been set, re-authenticate the user
		// with the supplied password.
		if (authenticationManager != null) {
			logger.debug("Reauthenticating user '" + username
					+ "' for password change request.");

			authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(
							username, oldPassword));
		} else {
			logger.debug("No authentication manager set. Password won't be re-checked.");
		}

		logger.debug("Changing password for user '" + username + "'");

		getJdbcTemplate().update(changePasswordSql, newPassword, username);

		SecurityContextHolder.getContext().setAuthentication(
				createNewAuthentication(currentUser, newPassword));

		userCache.removeUserFromCache(username);
	} 
	
	

	protected Authentication createNewAuthentication(
			Authentication currentAuth, String newPassword) {
		UserDetails user = loadUserByUsername(currentAuth.getName());

		UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
				user, user.getPassword(), user.getAuthorities());
		newAuthentication.setDetails(currentAuth.getDetails());

		return newAuthentication;
	}

	@Override
	public boolean userExists(String username) {
		List<String> users = getJdbcTemplate().queryForList(userExistsSql,
				new String[] { username }, String.class);

		if (users.size() > 1) {
			throw new IncorrectResultSizeDataAccessException(
					"More than one user found with name '" + username + "'", 1);
		}

		return users.size() == 1;
	}

	// ~ GroupManager implementation
	// ====================================================================================

	@Override
	public List<String> findAllGroups() {
		return getJdbcTemplate().queryForList(findAllGroupsSql, String.class);
	}

	@Override
	public List<String> findUsersInGroup(String groupName) {
		Assert.hasText(groupName);
		return getJdbcTemplate().queryForList(findUsersInGroupSql,
				new String[] { groupName }, String.class);
	}

	@Override
	public void createGroup(final String groupName,
			final List<GrantedAuthority> authorities) {
		Assert.hasText(groupName);
		Assert.notNull(authorities);

		logger.debug("Creating new group '" + groupName + "' with authorities "
				+ AuthorityUtils.authorityListToSet(authorities));

		getJdbcTemplate().update(insertGroupSql, groupName);

		final int groupId = findGroupId(groupName);

		for (GrantedAuthority a : authorities) {
			final String authority = a.getAuthority();
			getJdbcTemplate().update(insertGroupAuthoritySql,
					new PreparedStatementSetter() {
						@Override
						public void setValues(PreparedStatement ps)
								throws SQLException {
							ps.setInt(1, groupId);
							ps.setString(2, authority);
						}
					});
		}
	}

	@Override
	public void deleteGroup(String groupName) {
		logger.debug("Deleting group '" + groupName + "'");
		Assert.hasText(groupName);

		final int id = findGroupId(groupName);
		PreparedStatementSetter groupIdPSS = new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, id);
			}
		};
		getJdbcTemplate().update(deleteGroupMembersSql, groupIdPSS);
		getJdbcTemplate().update(deleteGroupAuthoritiesSql, groupIdPSS);
		getJdbcTemplate().update(deleteGroupSql, groupIdPSS);
	}

	@Override
	public void renameGroup(String oldName, String newName) {
		logger.debug("Changing group name from '" + oldName + "' to '"
				+ newName + "'");
		Assert.hasText(oldName);
		Assert.hasText(newName);

		getJdbcTemplate().update(renameGroupSql, newName, oldName);
	}

	@Override
	public void addUserToGroup(final String username, final String groupName) {
		logger.debug("Adding user '" + username + "' to group '" + groupName
				+ "'");
		Assert.hasText(username);
		Assert.hasText(groupName);

		final int id = findGroupId(groupName);
		getJdbcTemplate().update(insertGroupMemberSql,
				new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setInt(1, id);
						ps.setString(2, username);
					}
				});

		userCache.removeUserFromCache(username);
	}

	@Override
	public void removeUserFromGroup(final String username,
			final String groupName) {
		logger.debug("Removing user '" + username + "' to group '" + groupName
				+ "'");
		Assert.hasText(username);
		Assert.hasText(groupName);

		final int id = findGroupId(groupName);

		getJdbcTemplate().update(deleteGroupMemberSql,
				new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setInt(1, id);
						ps.setString(2, username);
					}
				});

		userCache.removeUserFromCache(username);
	}

	@Override
	public List<GrantedAuthority> findGroupAuthorities(String groupName) {
		logger.debug("Loading authorities for group '" + groupName + "'");
		Assert.hasText(groupName);

		return getJdbcTemplate().query(groupAuthoritiesSql,
				new String[] { groupName }, new RowMapper<GrantedAuthority>() {
					@Override
					public GrantedAuthority mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						String roleName = getRolePrefix() + rs.getString(3);

						return new SimpleGrantedAuthority(roleName);
					}
				});
	}

	@Override
	public void removeGroupAuthority(String groupName,
			final GrantedAuthority authority) {
		logger.debug("Removing authority '" + authority + "' from group '"
				+ groupName + "'");
		Assert.hasText(groupName);
		Assert.notNull(authority);

		final int id = findGroupId(groupName);

		getJdbcTemplate().update(deleteGroupAuthoritySql,
				new PreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setInt(1, id);
						ps.setString(2, authority.getAuthority());
					}
				});
	}

	@Override
	public void addGroupAuthority(final String groupName,
			final GrantedAuthority authority) {
		logger.debug("Adding authority '" + authority + "' to group '"
				+ groupName + "'");
		Assert.hasText(groupName);
		Assert.notNull(authority);

		final int id = findGroupId(groupName);
		getJdbcTemplate().update(insertGroupAuthoritySql,
				new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setInt(1, id);
						ps.setString(2, authority.getAuthority());
					}
				});
	}

	private int findGroupId(String group) {
		return getJdbcTemplate().queryForInt(findGroupIdSql, group);
	}

	public void setAuthenticationManager(
			AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	public void setCreateUserSql(String createUserSql) {
		Assert.hasText(createUserSql);
		this.createUserSql = createUserSql;
	}

	public void setDeleteUserSql(String deleteUserSql) {
		Assert.hasText(deleteUserSql);
		this.deleteUserSql = deleteUserSql;
	}

	public void setUpdateUserSql(String updateUserSql) {
		Assert.hasText(updateUserSql);
		this.updateUserSql = updateUserSql;
	}

	public void setCreateAuthoritySql(String createAuthoritySql) {
		Assert.hasText(createAuthoritySql);
		this.createAuthoritySql = createAuthoritySql;
	}

	public void setDeleteUserAuthoritiesSql(String deleteUserAuthoritiesSql) {
		Assert.hasText(deleteUserAuthoritiesSql);
		this.deleteUserAuthoritiesSql = deleteUserAuthoritiesSql;
	}

	public void setUserExistsSql(String userExistsSql) {
		Assert.hasText(userExistsSql);
		this.userExistsSql = userExistsSql;
	}

	public void setChangePasswordSql(String changePasswordSql) {
		Assert.hasText(changePasswordSql);
		this.changePasswordSql = changePasswordSql;
	}

	public void setFindAllGroupsSql(String findAllGroupsSql) {
		Assert.hasText(findAllGroupsSql);
		this.findAllGroupsSql = findAllGroupsSql;
	}

	public void setFindUsersInGroupSql(String findUsersInGroupSql) {
		Assert.hasText(findUsersInGroupSql);
		this.findUsersInGroupSql = findUsersInGroupSql;
	}

	public void setInsertGroupSql(String insertGroupSql) {
		Assert.hasText(insertGroupSql);
		this.insertGroupSql = insertGroupSql;
	}

	public void setFindGroupIdSql(String findGroupIdSql) {
		Assert.hasText(findGroupIdSql);
		this.findGroupIdSql = findGroupIdSql;
	}

	public void setInsertGroupAuthoritySql(String insertGroupAuthoritySql) {
		Assert.hasText(insertGroupAuthoritySql);
		this.insertGroupAuthoritySql = insertGroupAuthoritySql;
	}

	public void setDeleteGroupSql(String deleteGroupSql) {
		Assert.hasText(deleteGroupSql);
		this.deleteGroupSql = deleteGroupSql;
	}

	public void setDeleteGroupAuthoritiesSql(String deleteGroupAuthoritiesSql) {
		Assert.hasText(deleteGroupAuthoritiesSql);
		this.deleteGroupAuthoritiesSql = deleteGroupAuthoritiesSql;
	}

	public void setDeleteGroupMembersSql(String deleteGroupMembersSql) {
		Assert.hasText(deleteGroupMembersSql);
		this.deleteGroupMembersSql = deleteGroupMembersSql;
	}

	public void setRenameGroupSql(String renameGroupSql) {
		Assert.hasText(renameGroupSql);
		this.renameGroupSql = renameGroupSql;
	}

	public void setInsertGroupMemberSql(String insertGroupMemberSql) {
		Assert.hasText(insertGroupMemberSql);
		this.insertGroupMemberSql = insertGroupMemberSql;
	}

	public void setDeleteGroupMemberSql(String deleteGroupMemberSql) {
		Assert.hasText(deleteGroupMemberSql);
		this.deleteGroupMemberSql = deleteGroupMemberSql;
	}

	public void setGroupAuthoritiesSql(String groupAuthoritiesSql) {
		Assert.hasText(groupAuthoritiesSql);
		this.groupAuthoritiesSql = groupAuthoritiesSql;
	}

	public void setDeleteGroupAuthoritySql(String deleteGroupAuthoritySql) {
		Assert.hasText(deleteGroupAuthoritySql);
		this.deleteGroupAuthoritySql = deleteGroupAuthoritySql;
	}

	/**
	 * Optionally sets the UserCache if one is in use in the application. This
	 * allows the user to be removed from the cache after updates have taken
	 * place to avoid stale data.
	 * 
	 * @param userCache
	 *            the cache used by the AuthenticationManager.
	 */
	public void setUserCache(UserCache userCache) {
		Assert.notNull(userCache, "userCache cannot be null");
		this.userCache = userCache;
	}

	private void validateUserDetails(UserDetails user) {
		Assert.hasText(user.getUsername(), "Username may not be empty or null");
		validateAuthorities(user.getAuthorities());
	}

	private void validateAuthorities(
			Collection<? extends GrantedAuthority> authorities) {
		Assert.notNull(authorities, "Authorities list must not be null");

		for (GrantedAuthority authority : authorities) {
			Assert.notNull(authority, "Authorities list contains a null entry");
			Assert.hasText(authority.getAuthority(),
					"getAuthority() method must return a non-empty string");
		}
	}

	// ~ JdbcDaoImpl override
	// ==============================================================================

	@Override
	protected List<UserDetails> loadUsersByUsername(String username) {
		return getJdbcTemplate().query(getUsersByUsernameQuery(),
				new String[] { username }, new RowMapper<UserDetails>() {
					@Override
					public UserDetails mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						String username = rs.getString(1);
						String password = rs.getString(2);
						boolean enabled = rs.getBoolean(3);
						boolean accountNonExpired = rs.getBoolean(4);
						boolean accountNonLocked = rs.getBoolean(5);
						boolean credentialsNonExpired = rs.getBoolean(6);
						return new User(username, password, enabled,
								accountNonExpired, credentialsNonExpired,
								accountNonLocked, AuthorityUtils.NO_AUTHORITIES);
					}

				});
	} 

	@Override
	protected UserDetails createUserDetails(String username,
			UserDetails userFromUserQuery,
			List<GrantedAuthority> combinedAuthorities) {
		String returnUsername = userFromUserQuery.getUsername();

		if (!isUsernameBasedPrimaryKey()) {
			returnUsername = username;
		}
		logger.debug("[SEC] creating user details");
		return new User(returnUsername, userFromUserQuery.getPassword(),
				userFromUserQuery.isEnabled(),
				userFromUserQuery.isAccountNonExpired(),
				userFromUserQuery.isCredentialsNonExpired(),
				userFromUserQuery.isAccountNonLocked(), combinedAuthorities);
	}
}
