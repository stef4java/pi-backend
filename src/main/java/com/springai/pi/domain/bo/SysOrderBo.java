package com.springai.pi.domain.bo;

import com.springai.pi.domain.SysOrder;
import com.springai.pi.domain.core.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author stef
 * @version 1.0
 * @description 订单Bo对象
 * @date 9/19/25 09:09:14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysOrder.class, reverseConvertGenerate = false)
public class SysOrderBo extends BaseEntity {

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 产品名称
     */
    private String productName;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单状态（0待支付 1已完成 2已取消）
     */
    private Integer status;
}
