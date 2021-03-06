package com.yemu.mallportal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yemu.mall.common.Response;
import com.yemu.mall.common.TokenUtil;
import com.yemu.mallportal.Exception.LoginException;
import com.yemu.mallportal.entity.User;
import com.yemu.mallportal.mapper.UserMapper;
import com.yemu.mallportal.service.LoginService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LoginServiceImpl extends ServiceImpl<UserMapper,User> implements LoginService {
    @Resource
    private UserMapper userMapper;
    @Override
    public Response<?> login(User user) {
        User user1=userMapper.getUserByPhone(user.getPhone());
        if (user1!=null){
            if (user1.getPwd().equals(user.getPwd())){
                user1.setPwd("");
                String token=TokenUtil.createToken(user1.getId());
                return Response.ok("欢迎回来！", token);
            }
            else {
                return Response.error("密码错误");
            }
        }
        else {
            return Response.error("无此用户！");
        }

    }

    @Override
    public boolean isLogin(String token) {
        if (null == token || token.isEmpty()){
            throw new LoginException(500,"token为空");
        }
        if (TokenUtil.getUID(token)!=0){
            return true;
        }
        else{
            throw new LoginException(400,"token已过期");
        }
    }

    @Override
    public long getId(String token) {
        if (null == token || token.length() == 0) {
            return 0;
        }
        return TokenUtil.getUID(token);
    }
}
