package com.mll.mp.shiro;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.mll.mp.jwt.JwtToken;
import com.mll.mp.jwt.JwtUtil;
import com.mll.mp.pojo.User;
import com.mll.mp.service.Impl.UserServiceImpl;
import com.mll.mp.util.CacheClient;
import org.apache.shiro.authc.*;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class UserRealm extends AuthenticatingRealm {
    @Autowired
    UserServiceImpl userService;
    @Autowired
    CacheClient cacheClient;
    /*
    必须重写此方法，不然shiro会报错
     */
//    @Override
//    public boolean supports(AuthenticationToken token) {
//        return token instanceof JwtToken;
//    }

    //获取该自定义类的类名
    private String getThisName(){
        return this.getClass().getSimpleName();
    }


    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = (String)token.getPrincipal();
        User user = userService.queryUserByName(username);
        ByteSource salt = ByteSource.Util.bytes(user.getUsername());
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user,user.getUserpwd(),salt,this.getThisName());
        return info;
    }

    /**
     * token是JwtToken，禁止shiro自带的session
     * @param
     * @return
     * @throws AuthenticationException
     */
//    @Override
//    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
//        System.out.println("进入realm的login方法");
//        String token = (String)auth.getPrincipal();
//        token = JwtUtil.updateTokenFromPostman(token);
//        System.out.println("token："+token);
//        String username = JwtUtil.getUsername(token);
//        System.out.println("token的username："+username);
//        User userFromDB = userService.queryUserByName(username);
//        String redisUserInfo = (String)cacheClient.hGet("tokens", username);
//        Map result = JwtUtil.verifyToken(token, username, userFromDB.getUserpwd());
//        Exception exception = (Exception)result.get("exception");
//        if (userFromDB==null){
//            throw new UnknownAccountException("该账号不存在");
//        }else if (exception!=null && exception instanceof SignatureVerificationException){
//            throw new AuthenticationException("token错误");
//        }else if (redisUserInfo==null){
//            throw new AuthenticationException("token已失效，请重新登录");
//        }else{
//            ByteSource byteSource = ByteSource.Util.bytes(userFromDB.getSalt());
//            AuthenticationInfo info = new SimpleAuthenticationInfo(userFromDB,userFromDB.getUserpwd(),byteSource,getThisName());//存疑：是否加salt参数
//            System.out.println("realm验证成功");
//            return info;
//        }
//    }


}
