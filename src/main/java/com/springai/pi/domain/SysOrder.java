package com.springai.pi.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.springai.pi.domain.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_order")
public class SysOrder extends BaseEntity {

    /**
     * 订单id
     */
    @TableId
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 订单状态（0待支付 1已完成 2已取消）
     */
    private Integer status;

    /**
     * 产品名称
     */
    private String productName;

}