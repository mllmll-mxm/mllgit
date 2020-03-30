package com.mll.mp.shiro;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.mll.mp.jwt.JwtFilter;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig{
//    @Value("${sessionOutTime}")
//    private String serverSessionTimeout;


    //new
    @Bean(name = "subjectFactory")
    public StatelessDefaultSubjectFactory subjectFactory(){
        StatelessDefaultSubjectFactory statelessDefaultSubjectFactory = new StatelessDefaultSubjectFactory();
        return statelessDefaultSubjectFactory;
    }

    //new
    @Bean(name="sessionManager")
    public DefaultSessionManager sessionManager(){
        DefaultSessionManager defaultSessionManager = new DefaultSessionManager();
        defaultSessionManager.setSessionValidationSchedulerEnabled(false);
        return defaultSessionManager;
    }

    //new
    @Bean(name = "defaultSessionStorageEvaluator")
    public DefaultSessionStorageEvaluator defaultSessionStorageEvaluator(){
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        return defaultSessionStorageEvaluator;
    }

    //new
    @Bean(name = "subjectDAO")
    public DefaultSubjectDAO subjectDAO(@Qualifier("defaultSessionStorageEvaluator")DefaultSessionStorageEvaluator defaultSessionStorageEvaluator){
        DefaultSubjectDAO defaultSubjectDAO = new DefaultSubjectDAO();
        defaultSubjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        return defaultSubjectDAO;
    }

    //new
    @Bean(name = "securityManager")
    public SecurityManager securityManager(@Qualifier("userRealm")UserRealm userRealm,
                                           @Qualifier("subjectDAO")DefaultSubjectDAO subjectDAO,
                                           @Qualifier("sessionManager")DefaultSessionManager sessionManager,
                                           @Qualifier("subjectFactory")StatelessDefaultSubjectFactory subjectFactory
                                           ){
        DefaultWebSecurityManager securityManager =  new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm);
        securityManager.setSubjectDAO(subjectDAO);
        securityManager.setSessionManager(sessionManager);
        securityManager.setSubjectFactory(subjectFactory);
        return securityManager;
    }

    //new
    @Bean(name = "userRealm")
    public UserRealm getUserRealm(){
        UserRealm userRealm = new UserRealm();
        userRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        userRealm.setCachingEnabled(true);
        return userRealm;
    }

    //new
    @Bean(name = "jwtFilter")
    public JwtFilter jwtFilter(){
        return new JwtFilter();
    }



    /**new
     *  开启shiro aop注解支持.
     *  使用代理方式;所以需要开启代码支持;
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    //new
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(@Qualifier("securityManager") SecurityManager securityManager,@Qualifier("jwtFilter")JwtFilter jwtFilter){
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(securityManager);
        //bean.setLoginUrl("/login");
        //bean.setSuccessUrl("/successLogin");
        Map<String, Filter> filters = new HashMap<>(1);

        filters.put("jwtFilter",jwtFilter);
        //filters.put("myRememberFilter",new MyRememberFilter());
        bean.setFilters(filters);
        //bean.setLoginUrl("/login");
        //bean.setSuccessUrl("/loginSuccess");

        //拦截链
        LinkedHashMap<String, String> filterChainDefinitionMap=new LinkedHashMap<>();
        filterChainDefinitionMap.put("/**","jwtFilter");
        //filterChainDefinitionMap.put("/home","anon");
        //filterChainDefinitionMap.put("/pay","auth");
        //filterChainDefinitionMap.put("/learn","user");
        bean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        bean.setSuccessUrl("/home");
        return bean;
    }

    //new
    @Bean
    public FilterRegistrationBean delegatingFilterProxy() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        DelegatingFilterProxy filterProxy = new DelegatingFilterProxy();
        filterProxy.setTargetFilterLifecycle(true);
        filterProxy.setTargetBeanName("shiroFilter");
        filterRegistrationBean.setFilter(filterProxy);
//        filterRegistrationBean.setFilter(new ShiroSessionFilter());
//        filterRegistrationBean.setOrder(FilterRegistrationBean.LOWEST_PRECEDENCE);
//        filterRegistrationBean.setEnabled(true);
//        filterRegistrationBean.addUrlPatterns("/*");
//        Map<String, String> initParameters = new HashMap<>();
//        initParameters.put("serverSessionTimeout", serverSessionTimeout);
//        initParameters.put("excludes", "/favicon.ico,/images/*,/js/*,/css/*,/static/*,/upload/*");
//        filterRegistrationBean.setInitParameters(initParameters);
        return filterRegistrationBean;
    }

    /*
   开启注解支持,new
    */
    @Bean
    //@DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }


    @Bean
    public SimpleCookie rememberMeCookie() {
        //这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        //如果httyOnly设置为true，则客户端不会暴露给客户端脚本代码，使用HttpOnly cookie有助于减少某些类型的跨站点脚本攻击；
        simpleCookie.setHttpOnly(true);
        //记住我cookie生效时间,单位是秒
        simpleCookie.setMaxAge(60*60*7);
        return simpleCookie;
    }

    /**
     * cookie管理器;
     */
    @Bean
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        //rememberme cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度（128 256 512 位），通过以下代码可以获取
        //KeyGenerator keygen = KeyGenerator.getInstance("AES");
        //SecretKey deskey = keygen.generateKey();
        //System.out.println(Base64.encodeToString(deskey.getEncoded()));
        byte[] cipherKey = Base64.decode("wGiHplamyXlVB11UXWol8g==");
        cookieRememberMeManager.setCipherKey(cipherKey);
        cookieRememberMeManager.setCookie(rememberMeCookie());
        return cookieRememberMeManager;
    }



//    @Bean
//    public MyRememberFilter rememberFilter(){
//        return new MyRememberFilter();
//    }

    @Bean
    public ShiroDialect shiroDialect(){
        return new ShiroDialect();
    }


    @Bean
    public CredentialsMatcher hashedCredentialsMatcher(){
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        hashedCredentialsMatcher.setHashIterations(2);
        return hashedCredentialsMatcher;
    }






}