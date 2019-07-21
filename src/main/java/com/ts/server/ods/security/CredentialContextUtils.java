package com.ts.server.ods.security;

import java.util.Optional;

public class CredentialContextUtils {

    private static final ThreadLocal<Credential> threadLocal = new ThreadLocal<>();

    public static void setCredential(Credential credential){
        threadLocal.set(credential);
    }

    public static Optional<Credential> getCredential(){
        return Optional.ofNullable(threadLocal.get());
    }

    public static boolean hasCredential(){
        return getCredential().isPresent();
    }
}
