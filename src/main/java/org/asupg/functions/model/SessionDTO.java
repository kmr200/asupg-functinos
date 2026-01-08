package org.asupg.functions.model;

public class SessionDTO {

    private String jsessionId;
    private String dtUuid;
    private String usernameUuid;
    private String passwordUuid;
    private String loginBtnUuid;

    public SessionDTO() {}

    public SessionDTO(String jsessionId, String dtUuid, String usernameUuid, String passwordUuid, String loginBtnUuid) {
        this.jsessionId = jsessionId;
        this.dtUuid = dtUuid;
        this.usernameUuid = usernameUuid;
        this.passwordUuid = passwordUuid;
        this.loginBtnUuid = loginBtnUuid;
    }

    public String getJsessionId() {
        return jsessionId;
    }

    public void setJsessionId(String jsessionId) {
        this.jsessionId = jsessionId;
    }

    public String getDtUuid() {
        return dtUuid;
    }

    public void setDtUuid(String dtUuid) {
        this.dtUuid = dtUuid;
    }

    public String getUsernameUuid() {
        return usernameUuid;
    }

    public void setUsernameUuid(String usernameUuid) {
        this.usernameUuid = usernameUuid;
    }

    public String getPasswordUuid() {
        return passwordUuid;
    }

    public void setPasswordUuid(String passwordUuid) {
        this.passwordUuid = passwordUuid;
    }

    public String getLoginBtnUuid() {
        return loginBtnUuid;
    }

    public void setLoginBtnUuid(String loginBtnUuid) {
        this.loginBtnUuid = loginBtnUuid;
    }

    @Override
    public String toString() {
        return "SessionDTO{" +
                "jsessionId='" + jsessionId + '\'' +
                ", dtUuid='" + dtUuid + '\'' +
                ", usernameUuid='" + usernameUuid + '\'' +
                ", passwordUuid='" + passwordUuid + '\'' +
                ", loginBtnUuid='" + loginBtnUuid + '\'' +
                '}';
    }
}
