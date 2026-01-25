package org.asupg.functions.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class BankClientConfig {

    @Value("${asupg.bank-config.bank-host}")
    private String host;

    @Value("${asupg.bank-config.bank-login}")
    private String username;

    @Value("${asupg.bank-config.bank-client-password}")
    private String password;

    @Value("${asupg.bank-config.bank-account}")
    private String account;

}
