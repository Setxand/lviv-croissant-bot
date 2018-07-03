package com.bots.lvivcroissantbot.constantenum.security;

public enum SecurityConstants {
    SECRET("SecretKeyToGenJWTs"),
    TOKEN_PREFIX("Bearer "),
    HEADER_STRING("Authorization");
    private final String value;

    SecurityConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

