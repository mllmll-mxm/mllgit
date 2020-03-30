package com.mll.mp.shiro;

import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;

/**
 * 自定义sessionid获取
 * 传统结构项目中，shiro从cookie中读取sessionId以此来维持会话，在前后端分离的项目中，
 * 我们选择在ajax的请求头中传递sessionId，因此需要重写shiro获取sessionId的方式。
 * 自定义MySessionManager类继承DefaultWebSessionManager类，重写getSessionId方法
 */
public class MySessionManager extends DefaultWebSessionManager {
    private static final String AUTHORIZATION = "Authorization";

    private static final String REFERENCED_SESSION_ID_SOURCE = "Stateless request";

    public MySessionManager() {
        super();
    }

    protected Serializable getSessionId(ServletRequest request, ServletResponse response){
        String id = WebUtils.toHttp(request).getHeader(AUTHORIZATION);
        //如果请求头中有Authorization，则其值为sessionid
        if (!StringUtils.isEmpty(id)){
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, REFERENCED_SESSION_ID_SOURCE);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return id;
        }else{
            return super.getSessionId(request,response);
        }

    }
}
