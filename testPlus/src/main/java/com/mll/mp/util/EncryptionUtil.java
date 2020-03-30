package com.mll.mp.util;

import com.mll.mp.dao.UserDAO;
import com.mll.mp.pojo.User;
import com.mll.mp.service.Impl.UserServiceImpl;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 加密类
 */
@Component
public class EncryptionUtil {
    @Autowired
     UserServiceImpl userService;

    //加密
    public Map<String,String> encryption(String username,String pwd){
        String password = new Md5Hash(pwd,username,2).toString();//加密后的密码,salt为username
        Map<String,String> map = new HashMap<>();
        map.put("password",password);
        return map;
    }

    //判断用户输入的明文密码是否正确
    public boolean verifyPwd(String username,String pwd){
        Map<String,Object> map = new HashMap<>();
        map.put("username",username);
        User userFromDB = userService.queryUserByName(username);
        if (null==userFromDB){
            return false;
        }
        boolean f = userFromDB.getUsername().equals(username);
        if (userFromDB.getUsername().equals(username)){
            Map<String, String> encryption = encryption(username, pwd);
            if (userFromDB.getUserpwd().equals(encryption.get("password"))){
                return true;
            }else{
                return false;
            }

        }else{
            System.out.println("没走上面俩判断");
            return false;
        }


    }
}
