package org.asupg.workers.model;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SessionDTO {

    private String jsessionId;
    private String dtUuid;
    private String usernameUuid;
    private String passwordUuid;
    private String loginBtnUuid;

}
