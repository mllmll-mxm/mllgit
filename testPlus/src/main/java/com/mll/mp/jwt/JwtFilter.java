package com.mll.mp.jwt;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.mll.mp.pojo.User;
import com.mll.mp.service.Impl.UserServiceImpl;
import com.mll.mp.util.CacheClient;
import com.mll.mp.util.EncryptionUtil;
import com.mll.mp.util.JsonMapper;
import com.mll.mp.util.Result;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class JwtFilter extends BasicHttpAuthenticationFilter {
    @Autowired UserServiceImpl userService;
    @Autowired EncryptionUtil encryptionUtil;
    @Autowired CacheClient cacheClient;

    @Value("${jwt.anonymous.urls}")
    private String anonymousStr;//可匿名访问的urls


    /**
     * 将客户端发来的token和redis中的token对比，如果一致，则刷新token，将新的token存入redis，并返回给客户端
     * @param servletRequest
     * @param servletResponse
     * @return
     */
    private boolean refreshToken(ServletRequest servletRequest,ServletResponse servletResponse){
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获取请求头中的token
        String oldToken = request.getHeader("Authorization");
        oldToken = JwtUtil.updateTokenFromPostman(oldToken);
        String username = JwtUtil.getUsername(oldToken);
        //从redis中读取该用户的token
        String redisUserInfo = (String)cacheClient.hGet("tokens", username);
        if (redisUserInfo!=null){
            if (oldToken.equals(redisUserInfo)){
                System.out.println("refreshToken------:比对token成功，即将创建新token");
                //用户请求发来的token，和redis中的token相同，则刷新token（即重新生成token）
                User userFromDB = userService.queryUserByName(username);
                System.out.println(userFromDB);
                String newTokenStr = JwtUtil.createToken(username);
                JwtToken jwtToken = new JwtToken(newTokenStr);
                userService.addTokenToRedis(newTokenStr);
                response.setHeader("Authorization",newTokenStr);//将刷新后的token返回给客户端
                response.setHeader("Access-Control-Expose-Headers",JwtUtil.TOKEN_HEADER);
                System.out.println("refreshToken方法成功！！！！！！！");
                return true;
            }
        }
        return false;
    }



    /*
    token有问题，返回401
     */
    private void handler401(ServletResponse response,int code,String msg){
        try{
            HttpServletResponse httpServletResponse = (HttpServletResponse)response;
            httpServletResponse.setStatus(HttpStatus.OK.value());
            httpServletResponse.setContentType("application/json;charset=utf-8");
            httpServletResponse.getWriter().write("{\"code\":" + code + ", \"msg\":\"" + msg + "\"}");
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    /**
          * 支持跨域
          *
          * @param request
          * @param response
          * @return
          * @throws Exception
          */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Credentials","true");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "Authorization,Origin,X-Requested-With,Content-Type,Accept");

     // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
         if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
                httpServletResponse.setStatus(HttpStatus.OK.value());
                return false;
         }
        return super.preHandle(httpServletRequest, httpServletResponse);
    }

    @Override
    protected void postHandle(ServletRequest request, ServletResponse response) throws Exception {
        super.postHandle(request, response);
    }

    /**
     *判断是否携带有效的token
     * 返回true：直接访问请求的url
     * 返回false：执行onaAccessDenied方法
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object mappedValue) {
        System.out.println("进入过滤器：isAccessAllowed方法");
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        String contextPath = WebUtils.getPathWithinApplication(WebUtils.toHttp(request));
        if (!StringUtils.isEmpty(anonymousStr)){
            String[] anonUrls = anonymousStr.split(",");
            for (int i=0;i<anonUrls.length;i++){ //可匿名访问的url，继续下去放行
                if (contextPath.contains(anonUrls[i])){
                    System.out.println("放行："+request.getRequestURI());
                    return true;
                }
            }
        }
        String tokenStr = request.getHeader("Authorization");
        tokenStr = JwtUtil.updateTokenFromPostman(tokenStr);
        System.out.println("前端来的token："+tokenStr);
        if (null!=tokenStr){
            JwtToken jwtToken = new JwtToken(tokenStr);
            String username = JwtUtil.getUsername(tokenStr);
            if (null==username){
                return false;
            }
            String tokensFromRedis = (String)cacheClient.hGet("tokens",username );
            System.out.println("redis中的token："+tokensFromRedis);
            Map<String, Object> result = userService.verifyBeforeAccess(tokenStr);
            if ((boolean)result.get("isSuccess") && tokenStr.equals(tokensFromRedis)){
                this.refreshToken(servletRequest,servletResponse);
                System.out.println("token一切正常，已刷新token");
                System.out.println("result为true，进入目标资源");
                return true;
            }
            if (result==null){
                System.out.println("result为空");
                return false;
            }
            Exception e = (Exception)result.get("exception");
            if (e!=null && e instanceof TokenExpiredException){
                System.out.println("token已过期");
                if (this.refreshToken(servletRequest,servletResponse)){
                    System.out.println("再次刷新token成功，即将访问目标资源");
                    return true;
                }else
                {
                    System.out.println("刷新token失败");
                    return false;
                }
            }else if(e!=null && e instanceof SignatureVerificationException){
                System.out.println("token错误");
                return false;
            }else if (e!=null && e instanceof UnsupportedTokenException){
                System.out.println("token错误");
                return false;
            }
            else if(null==tokensFromRedis){
                System.out.println("token已失效");
                return false;
            }else{
                if (tokensFromRedis==tokenStr){
                    System.out.println("验证成功");
                    return true;
                }else return false;
            }
        }else
            return false;
    }

    /**
     *没有携带token则进行登录。
     * 验证成功，返回true,继续向下执行，若下面没有过滤器，则到目标资源
     * 验证失败，返回false
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        System.out.println("token失败，进入onAccessDenied");
        this.handler401(servletResponse,401,"token失效");
        return false;
    }
}
