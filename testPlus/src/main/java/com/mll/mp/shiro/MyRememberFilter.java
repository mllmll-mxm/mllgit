package com.mll.mp.shiro;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class MyRememberFilter extends FormAuthenticationFilter {

    /*
    对于跨域的POST请求，浏览器发起POST请求前都会发送一个OPTIONS请求已确定服务器是否可用，
    OPTIONS请求通过后继续执行POST请求，而shiro自带的权限验证是无法处理OPTIONS请求的，所以这里需要重写isAccessAllowed方法。
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        if ("OPTIONS".equals(httpServletRequest.getMethod())){
            return true;
        }
        return super.isAccessAllowed(request,response,mappedValue);
    }

    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        WebUtils.getAndClearSavedRequest(request);
        return super.onLoginSuccess(token, subject, request, response);
    }


}
