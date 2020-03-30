package com.mll.mp.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mll.mp.pojo.User;
import com.mll.mp.service.Impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    public static final String TOKEN_HEADER = "Authorization";
    public final static String SECRET = "mxm893862613";
    public final static int EXPIRE = 5; //过期时间 分钟

    /**
     *  创建token，其中生成签名的密钥是用户名明文密码secret
     * @param username 用户名
     * @return
     */
    public static String createToken(String username){
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE,EXPIRE);
        Date expireTime = nowTime.getTime();
        return JWT.create()
                .withClaim("username",username)
                .withExpiresAt(expireTime)
                .withIssuedAt(new Date())
                .sign(Algorithm.HMAC256(SECRET));
    }

    /**
     * 验证token是否正确
     */
    public static Map verifyToken(String token,String username){
       Map<String,Object> result = new HashMap<>(2);
       try{
           Algorithm algorithm = Algorithm.HMAC256(SECRET);
           System.out.println(algorithm);
           JWTVerifier verifier = JWT.require(algorithm).withClaim("username", username).build();
           DecodedJWT jwt = verifier.verify(token);
           result.put("isSuccess",true);
           result.put("exception",null);
       }catch (Exception e){
           result.put("isSuccess", false);
           result.put("exception",e);
       }
       return result;
    }



    /**
     * 通过claim获取其中的参数username，不需要secret
     */
    public static String getUsername(String token){
        try{
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        }catch (Exception e){
            return null;
        }
    }

    /**
     *  清除postman发送的token，前面的bearer开头
     */
    public static String updateTokenFromPostman(String token){
        try{
            if (!StringUtils.isEmpty(token)){
                String[] strings = token.split(" ");
                return strings[1];
            }
        }catch (Exception e){
            return null;
        }
        return null;
    }


}
