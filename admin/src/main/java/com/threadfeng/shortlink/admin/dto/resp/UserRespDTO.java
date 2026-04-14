package com.threadfeng.shortlink.admin.dto.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.threadfeng.shortlink.admin.common.serialize.PhoneDesensitizationSerializer;
import lombok.Data;

import java.util.Date;

@Data
public class UserRespDTO {

    private Long id;

    /**
     * 用户名
     */
    private String username;


    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    @JsonSerialize(using = PhoneDesensitizationSerializer.class)
    private String phone;

    /**
     * 邮箱
     */
    private String mail;




}
