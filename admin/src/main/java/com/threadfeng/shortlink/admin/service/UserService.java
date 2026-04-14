package com.threadfeng.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threadfeng.shortlink.admin.common.convention.result.Result;
import com.threadfeng.shortlink.admin.dao.entity.UserDO;
import com.threadfeng.shortlink.admin.dto.req.UserLoginReqDTO;
import com.threadfeng.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.threadfeng.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.threadfeng.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.threadfeng.shortlink.admin.dto.resp.UserRespDTO;
import org.springframework.stereotype.Service;

public interface UserService extends IService<UserDO> {
    UserRespDTO gerUserByUsername(String username);

    Boolean hasUsername(String username);
   void Register(UserRegisterReqDTO requestParam);

    void Update(UserUpdateReqDTO requestParam);

    UserLoginRespDTO login(UserLoginReqDTO requestParam);

    Boolean checkLogin(String username,String token);

    void logout(String username, String token);
}
