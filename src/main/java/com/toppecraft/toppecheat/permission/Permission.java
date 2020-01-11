package com.toppecraft.toppecheat.permission;

public enum Permission {

    NOTIFY("toppecheat.notify"),
    ADMIN("toppecheat.admin"),
    STAFF_TOOL("toppecheat.stafftool"),
    ALL("toppecheat.*");

    private String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

}
