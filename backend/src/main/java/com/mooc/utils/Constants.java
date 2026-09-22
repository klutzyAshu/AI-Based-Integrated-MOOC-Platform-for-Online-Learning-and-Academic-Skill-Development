package com.mooc.utils;

public final class Constants {

    private Constants() {
    }

    public static final String AUTH_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final long JWT_EXPIRATION_MS = 24 * 60 * 60 * 1000; // 24 hours

    public static final String ROLE_STUDENT = "STUDENT";
    public static final String ROLE_INSTRUCTOR = "INSTRUCTOR";
    public static final String ROLE_ADMIN = "ADMIN";

    public static final int DEFAULT_PASSING_SCORE = 60;
}
