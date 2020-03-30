package com.mll.mp.controller;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.mll.mp.jwt.JwtToken;
import com.mll.mp.jwt.JwtUtil;
import com.mll.mp.pojo.User;
import com.mll.mp.service.Impl.UserServiceImpl;
import com.mll.mp.util.EncryptionUtil;
import com.mll.mp.util.JsonMapper;
import com.mll.mp.util.Result;
import com.mll.mp.util.ShiroUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.SavedRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    @Autowired UserServiceImpl userService;
    @Autowired EncryptionUtil encryptionUtil;


    @PostMapping(value="add",produces = "application/json; charset=UTF-8")
    public String insert(@RequestBody User user){//这个注解不加，前端axios无法将user传过来
        boolean result = userService.registUser(user);
        if (result){
            return JsonMapper.notEmptyMapper().toJson(Result.success("注册成功"));
        }else
            return JsonMapper.notEmptyMapper().toJson(Result.fail("此用户名已被占用"));
    }



    @PostMapping("foreLogin")
    public String login(@RequestBody User userInfo, ServletResponse servletResponse,ServletRequest servletRequest){
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        Subject subject = SecurityUtils.getSubject();
            try{
                UsernamePasswordToken token = new UsernamePasswordToken(userInfo.getUsername(),userInfo.getUserpwd());
                subject.login(token);
            }catch (Exception e){
                return JsonMapper.notEmptyMapper().toJson(Result.fail("用户名或密码错误"));
            }
            String newToken = userService.refreshToken(userInfo.getUsername());
            System.out.println("登录成功，已刷新token:"+newToken);
            response.setHeader("Authorization",newToken);
            response.setHeader("Access-Control-Expose-Headers",JwtUtil.TOKEN_HEADER);
            return JsonMapper.notEmptyMapper().toJson(Result.success(newToken));
    }



    @GetMapping("logout")
    public String logout(Object username){
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.logout();
            if (userService.logout(username.toString())){
                return JsonMapper.notEmptyMapper().toJson(Result.fail("success"));
            }else
                return JsonMapper.notEmptyMapper().toJson(Result.fail("fail"));
        }catch (Exception e){
            e.printStackTrace();
            return JsonMapper.notEmptyMapper().toJson(Result.fail("fail"));
        }
    }


    @GetMapping("buy")
    public String buy(){
        return JsonMapper.notEmptyMapper().toJson(Result.success("购买成功"));
    }
}
