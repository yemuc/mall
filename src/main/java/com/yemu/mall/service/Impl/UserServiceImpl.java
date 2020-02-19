package com.yemu.mall.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yemu.mall.common.Response;
import com.yemu.mall.entity.User;
import com.yemu.mall.mapper.UserMapper;
import com.yemu.mall.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public User findUserByName(String userName) {
        return baseMapper.getUserByName(userName);
    }

    @Override
    public User findUserByPhone(String phone) {
        User user=baseMapper.getUserByPhone(phone);
        if (null!=user){
            user.setPwd("");
        }
        return user;
    }

    @Override
    public boolean existUserByPhone(String phone) {
        User user=baseMapper.getUserByPhone(phone);
        return null != user;
    }

    @Override
    public Response register(User user){
        if (existUserByPhone(user.getPhone())){
            return Response.error("该手机号已被注册！");
        }
        else{
            return Response.ok("注册成功！",baseMapper.insert(user));
        }
    }
}