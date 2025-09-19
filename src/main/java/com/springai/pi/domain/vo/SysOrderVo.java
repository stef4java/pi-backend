package com.springai.pi.domain.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.springai.pi.domain.SysOrder;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author stef
 * @version 1.0
 * @description 订单Vo对象
 * @date 9/19/25 09:04:47
 */
@Data
@AutoMapper(target = SysOrder.class)
public class SysOrderVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
     * 订单状态（0待支付 1已完成 2已取消）
     */
    private Integer status;

    /**
     * 产品名称
     */
    private String productName;

    private Date createTime;
}
