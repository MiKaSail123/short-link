package com.threadfeng.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threadfeng.shortlink.admin.common.constant.RedisCacheConstant;
import com.threadfeng.shortlink.admin.common.convention.exception.ClientException;
import com.threadfeng.shortlink.admin.common.convention.result.Result;
import com.threadfeng.shortlink.admin.common.enums.UserErrorCodeEnum;
import com.threadfeng.shortlink.admin.dao.entity.UserDO;
import com.threadfeng.shortlink.admin.dao.mapper.UserMapper;
import com.threadfeng.shortlink.admin.dto.req.UserLoginReqDTO;
import com.threadfeng.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.threadfeng.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.threadfeng.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.threadfeng.shortlink.admin.dto.resp.UserRespDTO;
import com.threadfeng.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.threadfeng.shortlink.admin.common.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper,UserDO> implements UserService {
private  final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
private final RedissonClient redissonClient;
private final StringRedisTemplate redisTemplate;
    @Override
    public UserRespDTO gerUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper= Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername,username);
        UserDO userDO=baseMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new ClientException(UserErrorCodeEnum.USER_NULL);
        }
        UserRespDTO result =new UserRespDTO();
        BeanUtils.copyProperties(userDO,result);
        return result;
    }

    @Override
    public Boolean hasUsername(String username) {
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void Register(UserRegisterReqDTO requestParam) {
        if (hasUsername(requestParam.getUsername())){
          throw new ClientException(UserErrorCodeEnum.USER_NAME_EXIST);
        }
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY+requestParam.getUsername());
        try {
            if (lock.tryLock()) {
                int insert = baseMapper.insert(BeanUtil.toBean(requestParam, UserDO.class));
                if (insert < 1) {
                    throw new ClientException(UserErrorCodeEnum.USER_SAVE_ERROR);
                }
                userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
                return;
            }
            throw new ClientException(UserErrorCodeEnum.USER_EXIST);
        }finally {
            lock.unlock();
        }
    }

    @Override
    public void Update(UserUpdateReqDTO requestParam) {
        //TODO 需要验证
        LambdaUpdateWrapper<UserDO> updateWrapper=Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getUsername,requestParam.getUsername());
        baseMapper.update(BeanUtil.toBean(requestParam,UserDO.class),updateWrapper);
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        LambdaQueryWrapper<UserDO> queryWrapper=Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername,requestParam.getUsername())
                .eq(UserDO::getPassword,requestParam.getPassword())
                .eq(UserDO::getDelFlag,0);
        UserDO user=baseMapper.selectOne(queryWrapper);
        if (user==null){
            throw new ClientException("用户不存在");
        }
        Boolean hasLogin=redisTemplate.hasKey("login_"+requestParam.getUsername());
        if (hasLogin!=null&&hasLogin){
            throw new ClientException("用户已登录");
        }
        String uuid= UUID.randomUUID().toString();
       redisTemplate.opsForHash().put("login_"+requestParam.getUsername(),uuid, JSON.toJSONString(user));
       redisTemplate.expire("login_"+requestParam.getUsername(),30L,TimeUnit.MINUTES);
        return new UserLoginRespDTO(uuid);
    }

    @Override
    public Boolean checkLogin(String username,String token) {
        return redisTemplate.opsForHash().get("login_"+username,token)!=null;
    }

    @Override
    public void logout(String username, String token) {
        if (checkLogin(username,token)) {
redisTemplate.delete("login_"+username);
return;
        }
        throw new ClientException("用户token不存在或用户未登录");
    }
}
