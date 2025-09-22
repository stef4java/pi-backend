package com.springai.pi.config;

import com.springai.pi.service.SysOrderService;
import com.springai.pi.tools.FileOperationTool;
import com.springai.pi.tools.UserOrderTool;
import com.springai.pi.tools.WebSearchTool;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description  集中注册工具
 * @author stef
 * @date 9/15/25 8:09 PM
 */
@Configuration
public class ToolCallConfig {

    @Autowired
    private SysOrderService sysOrderService;
    @Bean
    public ToolCallback[] allTools() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        WebSearchTool webSearchTool = new WebSearchTool();
        UserOrderTool userOrderTool = new UserOrderTool(sysOrderService);
        return ToolCallbacks.from(
                fileOperationTool,
                webSearchTool,
                userOrderTool
        );
    }

}
