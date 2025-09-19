package com.springai.pi.controller;

import com.springai.pi.domain.bo.SysOrderBo;
import com.springai.pi.domain.core.PageQuery;
import com.springai.pi.domain.core.R;
import com.springai.pi.domain.core.TableDataInfo;
import com.springai.pi.domain.vo.SysOrderVo;
import com.springai.pi.service.SysOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/order")
public class SysOrderController {

    private final SysOrderService sysOrderService;

    @GetMapping("/list")
    public TableDataInfo<SysOrderVo> list(SysOrderBo bo, PageQuery pageQuery) {
        return sysOrderService.queryPageList(bo, pageQuery);
    }



    @PostMapping()
    public R<Void> add(@Validated @RequestBody SysOrderBo bo) {
        sysOrderService.insertByBo(bo);
        return R.ok();
    }

}
