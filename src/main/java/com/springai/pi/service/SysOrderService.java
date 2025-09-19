package com.springai.pi.service;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springai.pi.domain.SysOrder;
import com.springai.pi.domain.bo.SysOrderBo;
import com.springai.pi.domain.core.PageQuery;
import com.springai.pi.domain.core.TableDataInfo;
import com.springai.pi.domain.vo.SysOrderVo;
import com.springai.pi.exception.ServiceException;
import com.springai.pi.mapper.SysOrderMapper;
import com.springai.pi.utils.LoginHelper;
import com.springai.pi.utils.MapstructUtils;
import com.springai.pi.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author stef
 * @version 1.0
 * @description 订单Service
 * @date 9/19/25 09:11:42
 */
@RequiredArgsConstructor
@Service
public class SysOrderService {

    private final SysOrderMapper baseMapper;

    public TableDataInfo<SysOrderVo> queryPageList(SysOrderBo bo, PageQuery pageQuery) {
        bo.setUserId(LoginHelper.getUserId());
        LambdaQueryWrapper<SysOrder> lqw = buildQueryWrapper(bo);
        Page<SysOrderVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    public Boolean insertByBo(SysOrderBo bo) {
        bo.setUserId(LoginHelper.getUserId());
        // 生成订单号,订单号规则：yyyyMMddHHmmss + 3位随机数
        String orderNo = DateUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_MS_PATTERN) + RandomUtil.randomNumbers(3);
        bo.setOrderNo(orderNo);
        SysOrder add = MapstructUtils.convert(bo, SysOrder.class);
        int insert = baseMapper.insert(add);
        if (insert == 0) {
            throw new ServiceException("新增失败");
        }
        return true;
    }

    private LambdaQueryWrapper<SysOrder> buildQueryWrapper(SysOrderBo bo) {
        LambdaQueryWrapper<SysOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysOrder::getUserId, bo.getUserId());
        lqw.eq(StringUtils.isNotBlank(bo.getOrderNo()), SysOrder::getOrderNo, bo.getOrderNo());
        lqw.like(StringUtils.isNotBlank(bo.getProductName()), SysOrder::getProductName, bo.getProductName());
        lqw.eq(!Objects.isNull(bo.getStatus()), SysOrder::getStatus, bo.getStatus());
        lqw.orderByDesc(SysOrder::getId);
        return lqw;
    }
}