package fr.maxlego08.bourse.zcore.enums;

public enum Permission {
	BOURSE_RELOAD, BOURSE_ADMIN,

	;

	private String permission;

	private Permission() {
		this.permission = this.name().toLowerCase().replace("_", ".");
	}

	public String getPermission() {
		return permission;
	}

}
