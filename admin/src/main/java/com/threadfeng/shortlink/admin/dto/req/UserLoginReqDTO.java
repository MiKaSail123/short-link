package com.threadfeng.shortlink.admin.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class UserLoginReqDTO {
    private String username;
    private String password;
}
