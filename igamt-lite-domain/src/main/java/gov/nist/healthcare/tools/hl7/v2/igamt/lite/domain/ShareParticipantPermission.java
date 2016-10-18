package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

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
