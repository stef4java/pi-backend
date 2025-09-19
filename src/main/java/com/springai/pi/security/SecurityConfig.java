package com.springai.pi.security;

import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.httpauth.basic.SaHttpBasicUtil;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.springai.pi.domain.core.HttpStatus;
import com.springai.pi.utils.ServletUtils;
import com.springai.pi.utils.SpringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 权限安全配置
 *
 * @author Lion Li
 */

@Slf4j
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

    private final SecurityProperties securityProperties;

    /**
     * 注册sa-token的拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册路由拦截器，自定义验证规则
        registry.addInterceptor(new SaInterceptor(handler -> {
                    AllUrlHandler allUrlHandler = SpringUtils.getBean(AllUrlHandler.class);
                    // 登录验证 -- 排除多个路径
                    SaRouter
                            // 获取所有的
                            .match(allUrlHandler.getUrls())
                            // 对未排除的路径进行检查
                            .check(() -> {
                                HttpServletRequest request = ServletUtils.getRequest();
                                // 检查是否登录 是否有token
                                StpUtil.checkLogin();

                            });
                })).addPathPatterns("/**")
                // 排除不需要拦截的路径
                .excludePathPatterns(securityProperties.getExcludes());
    }

    /**
     * 对 actuator 健康检查接口 做账号密码鉴权
     */
    @Bean
    public SaServletFilter getSaServletFilter() {
        String username = SpringUtils.getProperty("spring.boot.admin.client.username");
        String password = SpringUtils.getProperty("spring.boot.admin.client.password");
        return new SaServletFilter()
                .addInclude("/actuator", "/actuator/**")
                .setAuth(obj -> {
                    SaHttpBasicUtil.check(username + ":" + password);
                })
                .setError(e -> SaResult.error(e.getMessage()).setCode(HttpStatus.UNAUTHORIZED));
    }

}