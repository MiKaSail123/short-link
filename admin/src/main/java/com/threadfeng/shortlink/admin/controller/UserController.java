package com.threadfeng.shortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.threadfeng.shortlink.admin.common.convention.result.Result;
import com.threadfeng.shortlink.admin.common.convention.result.Results;
import com.threadfeng.shortlink.admin.dto.req.UserLoginReqDTO;
import com.threadfeng.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.threadfeng.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.threadfeng.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.threadfeng.shortlink.admin.dto.resp.UserRespActualDTO;
import com.threadfeng.shortlink.admin.dto.resp.UserRespDTO;
import com.threadfeng.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/api/short-link/admin/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username)
    {
       return Results.success(userService.gerUserByUsername(username));
    }
    @GetMapping("/api/short-link/admin/v1/actual/user/{username}")
    public Result<UserRespActualDTO> getUserActualByUsername(@PathVariable("username") String username)
    {
        return Results.success(BeanUtil.toBean(userService.gerUserByUsername(username),UserRespActualDTO.class));
    }
    @GetMapping("/api/short-link/v1/user/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username")String username)
    {
        return Results.success(!userService.hasUsername(username));
    }
    @PostMapping("/api/short-link/admin/v1/user")
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam)
    {
        userService.Register(requestParam);
        return Results.success();
    }
    @PutMapping("/api/short-link/v1/user")
    public Result<Void>update(@RequestBody UserUpdateReqDTO requestParam){
        userService.Update(requestParam);
        return Results.success();
    }
    @PostMapping("/api/short-link/admin/v1/user/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestParam){
        return Results.success(userService.login(requestParam));
    }
    @GetMapping("/api/short-link/v1/user/check-login")
    public Result<Boolean> checkLogin(@RequestParam("username") String username ,@RequestParam("token") String token){
        return Results.success(userService.checkLogin(username,token));
    }
    @DeleteMapping("/api/short-link/v1/user/logout")
    public Result<Void>logout(@RequestParam("username") String username ,@RequestParam("token") String token){
        userService.logout(username,token);
        return Results.success();
    }
}
