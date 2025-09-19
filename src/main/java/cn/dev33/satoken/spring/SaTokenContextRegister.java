package cn.dev33.satoken.spring;

import cn.dev33.satoken.filter.SaFirewallCheckFilterForJakartaServlet;
import cn.dev33.satoken.filter.SaTokenContextFilterForJakartaServlet;
import cn.dev33.satoken.filter.SaTokenCorsFilterForJakartaServlet;
import cn.dev33.satoken.spring.pathmatch.SaPathPatternParserUtil;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaTokenConsts;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import java.util.EnumSet;

/**
 * 注册 Sa-Token 框架所需要的 Bean
 *
 * @author click33
 * @since 1.34.0
 * issues: 升级1.42.0版本sse请求报SaTokenContext 上下文尚未初始化 SSE
 * issues: https://gitee.com/dromara/sa-token/issues/IC4XFE
 */
public class SaTokenContextRegister {

    public SaTokenContextRegister() {
        // 重写路由匹配算法
        SaStrategy.instance.routeMatcher = (pattern, path) -> {
            return SaPathPatternParserUtil.match(pattern, path);
        };
    }

    /**
     * 上下文过滤器
     *
     * @return /
     */
    @Bean
    @ConditionalOnMissingBean(name = "saTokenContextFilterForJakartaServlet")
    public FilterRegistrationBean<SaTokenContextFilterForJakartaServlet> saTokenContextFilterForJakartaServlet() {
        FilterRegistrationBean<SaTokenContextFilterForJakartaServlet> bean =
                new FilterRegistrationBean<>(new SaTokenContextFilterForJakartaServlet());
        // 配置 Filter 拦截的 URL 模式
        bean.addUrlPatterns("/*");
        // 设置 Filter 的执行顺序,数值越小越先执行
        bean.setOrder(SaTokenConsts.SA_TOKEN_CONTEXT_FILTER_ORDER);
        bean.setAsyncSupported(true);
        bean.setDispatcherTypes(EnumSet.of(DispatcherType.ASYNC, DispatcherType.REQUEST));
        return bean;
    }

    /**
     * CORS 跨域策略过滤器
     *
     * @return /
     */
    @Bean
    public SaTokenCorsFilterForJakartaServlet saTokenCorsFilterForJakartaServlet() {
        return new SaTokenCorsFilterForJakartaServlet();
    }

    /**
     * 防火墙过滤器
     *
     * @return /
     */
    @Bean
    public SaFirewallCheckFilterForJakartaServlet saFirewallCheckFilterForJakartaServlet() {
        return new SaFirewallCheckFilterForJakartaServlet();
    }

}
