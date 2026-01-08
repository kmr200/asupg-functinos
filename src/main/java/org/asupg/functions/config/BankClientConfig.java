package org.asupg.functions.config;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class BankClientConfig {

    private final String host;
    private final String username;
    private final String password;
    private final String account;

    @Inject
    public BankClientConfig(
            @Named("bankHost") String host,
            @Named("bankLogin") String username,
            @Named("bankPassword") String password,
            @Named("bankAccount") String account
    ) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.account = account;
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAccount() {
        return account;
    }
}
