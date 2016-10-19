package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.Objects;

public class ShareParticipantPermission {
	
	public enum Permission {
		VIEW, WRITE
	}
	
	private Long accountId;
	private Permission permission;
	private boolean pendingApproval;
	
	public ShareParticipantPermission() {
		this.pendingApproval = true;
	}
	
	public ShareParticipantPermission(Long accountId) {
		this.pendingApproval = true;
		this.accountId = accountId;
		this.permission = Permission.VIEW;
	}
	
	public ShareParticipantPermission(Long accountId, Permission permission) {
		this.pendingApproval = true;
		this.accountId = accountId;
		this.permission = permission;
	}

	public ShareParticipantPermission(Long accountId, Permission permission, boolean pendingApproval) {
		super();
		this.accountId = accountId;
		this.permission = permission;
		this.pendingApproval = pendingApproval;
	}
	
	@Override
	public String toString() {
		return "Participant: ID=" + this.getAccountId() + ", permission=" + this.getPermission() + ", pending=" + this.isPendingApproval();
	}
	
	@Override
	public boolean equals(Object o) {
	    // self check
	    if (this == o)
	        return true;
	    // null check
	    if (o == null)
	        return false;
	    // type check and cast
	    if (getClass() != o.getClass())
	        return false;
	    ShareParticipantPermission person = (ShareParticipantPermission) o;
	    // field comparison
	    return Objects.equals(accountId, person.getAccountId());
	}
	
	@Override
    public int hashCode() {
		return this.accountId.hashCode();
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	public boolean isPendingApproval() {
		return pendingApproval;
	}

	public void setPendingApproval(boolean pendingApproval) {
		this.pendingApproval = pendingApproval;
	}

}
