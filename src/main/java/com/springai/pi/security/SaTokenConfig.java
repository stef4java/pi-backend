package com.springai.pi.security;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * sa-token 配置
 *
 * @author Lion Li
 */
@Configuration
public class SaTokenConfig {

    @Bean
    public StpLogic getStpLogicJwt() {
        // Sa-Token 整合 jwt (简单模式)
        return new StpLogicJwtForSimple();
    }

    /**
     * 自定义dao层存储
     */
    @Bean
    public SaTokenDao saTokenDao() {
        return new PlusSaTokenDao();
    }

//    @Bean
//    public FilterRegistrationBean<SaTokenContextFilterForJakartaServlet> saTokenContextFilterForJakartaServlet() {
//        FilterRegistrationBean<SaTokenContextFilterForJakartaServlet> bean = new FilterRegistrationBean<>(new SaTokenContextFilterForJakartaServlet());
//        // 配置 Filter 拦截的 URL 模式
//        bean.addUrlPatterns("/*");
//        // 设置 Filter 的执行顺序,数值越小越先执行
//        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
//        bean.setAsyncSupported(true);
//        bean.setDispatcherTypes(EnumSet.of(DispatcherType.ASYNC, DispatcherType.REQUEST));
//        return bean;
//    }
}