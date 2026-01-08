package org.asupg.functions.model;

public class AuthDTO {

    private String statementBtnUuid;

    public AuthDTO() {}

    public AuthDTO(String statementBtnUuid) {
        this.statementBtnUuid = statementBtnUuid;
    }

    public String getStatementBtnUuid() {
        return statementBtnUuid;
    }

    public void setStatementBtnUuid(String statementBtnUuid) {
        this.statementBtnUuid = statementBtnUuid;
    }

    @Override
    public String toString() {
        return "AuthDTO{" +
                "statementBtnUuid='" + statementBtnUuid + '\'' +
                '}';
    }
}
