package com.springai.pi.tools;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springai.pi.domain.bo.SysOrderBo;
import com.springai.pi.domain.vo.SysOrderVo;
import com.springai.pi.service.SysOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @author stef
 * @version 1.0
 * @description 用户订单工具
 * @date 9/22/25 17:05:19
 */
@Slf4j
public class UserOrderTool {

    private final SysOrderService sysOrderService;

    public UserOrderTool(SysOrderService sysOrderService) {
        this.sysOrderService = sysOrderService;
    }

    @Tool(name = "用户下单", description = "帮助用户下单")
    public String createOrder(@ToolParam(description = "系统产品:包含[HRMS基础版、SCM高级版、MES旗舰版、WMS基础版、HIS高级版、CRM旗舰版、HRMS基础版、SCM高级版、MES旗舰版、WMS基础版、HIS高级版、CRM旗舰版、HRMS基础版、SCM高级版、MES旗舰版、WMS基础版、HIS高级版、CRM旗舰版]") String productName,
                              ToolContext toolContext) {
        Long userId = (Long) toolContext.getContext().get("userId");
        SysOrderBo bo = new SysOrderBo();
        bo.setProductName(productName);
        Boolean result = sysOrderService.insert(userId, bo);
        if (result) {
            return String.format("订单创建成功，订单编号: %s， 产品名称: %s,", bo.getOrderNo(), bo.getProductName());
        } else {
            return "订单创建失败";
        }
    }


    @Tool(name = "分页查询用户订单", description = "分页查询用户订单")
    public String getOrderList(
            @ToolParam(description = "页码") Integer pageNum,
            @ToolParam(description = "每页数量") Integer pageSize,
            ToolContext toolContext) {
        Long userId = (Long) toolContext.getContext().get("userId");
        Page<SysOrderVo> result = sysOrderService.pageList(userId, pageNum, pageSize);
        return JSONUtil.toJsonStr(result);
    }
}
