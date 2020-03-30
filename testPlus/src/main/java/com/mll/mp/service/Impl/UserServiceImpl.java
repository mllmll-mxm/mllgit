package com.mll.mp.service.Impl;

import com.mll.mp.dao.UserDAO;
import com.mll.mp.jwt.JwtToken;
import com.mll.mp.jwt.JwtUtil;
import com.mll.mp.pojo.User;
import com.mll.mp.util.CacheClient;
import com.mll.mp.util.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl {
    @Autowired
    UserDAO userDAO;
    @Autowired CacheClient cacheClient;
    @Autowired EncryptionUtil encryptionUtil;


    public User searchById(int id){
        User user = userDAO.selectById(id);
        return user;
    }

    public void delete(int id){
        userDAO.deleteById(id);
    }

    public boolean registUser(User user){
        int count = this.findUserIsExist(user.getUsername()).size();
        if (count>0){
            return false;
        }else{
            EncryptionUtil encryptionUtil = new EncryptionUtil();
            Map<String, String> encryption = encryptionUtil.encryption(user.getUsername(),user.getUserpwd());
            User savedUser = new User(user.getUsername(),encryption.get("password"),user.getUsername(),user.getPhone(),user.getEmail());
            userDAO.insert(savedUser);
            return true;
        }
    }

    public List<User> findUserIsExist(String search){
        Map<String,Object> map = new HashMap<>();
        map.put("username",search);
        List<User> users = userDAO.selectByMap(map);
        return users;
    }

    public User queryUserByName(String name){
        List<User> userIsExist = this.findUserIsExist(name);
        if (userIsExist.size()!=0){
            return userIsExist.get(0);
        }else
            return null;
    }

    /**
     * token存入redis
     */
    public boolean addTokenToRedis(String token){
        try{
            cacheClient.hDelete("tokens",JwtUtil.getUsername(token));
        }catch (Exception e){
            System.out.println("没有这个username，不用管");
        }
        return cacheClient.hPutIfAbsent("tokens", JwtUtil.getUsername(token),token);
    }

    public String createTokenIfAuthed(String username,String pwd){
        String token = JwtUtil.createToken(username);
        return null;
    }

    public String refreshToken(String username){
        String tokenStr = JwtUtil.createToken(username);
        JwtToken jwtToken = new JwtToken(tokenStr);
        this.addTokenToRedis(tokenStr);
        return tokenStr;
    }

    public String getSecretFromDB(String username,String userpwd){
        boolean result = encryptionUtil.verifyPwd(username, userpwd);
        if (result){
            return encryptionUtil.encryption(username,userpwd).get("password");
        }
        return null;
    }

    public Map<String,Object> verifyBeforeAccess(String token){
        String username = JwtUtil.getUsername(token);
        System.out.println("util通过token获取到的username："+username);
        User user = this.queryUserByName(username);
        System.out.println("从数据库中查询到的user："+user);
        if (null==user){
            return null;
        }else {
            Map result = JwtUtil.verifyToken(token, user.getUsername());
            return result;
        }
    }

    /*
    logout-->redis的操作
     */
    public boolean logout(String username){
        try{
            cacheClient.hDelete("tokens",username);
            System.out.println("redis删除用户成功");
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
