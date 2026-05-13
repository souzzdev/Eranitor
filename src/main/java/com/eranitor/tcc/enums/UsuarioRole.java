package com.eranitor.tcc.enums;

public enum UsuarioRole {
    ADMIN("admin"),

    USER("user");

    public String role;

    UsuarioRole (String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
