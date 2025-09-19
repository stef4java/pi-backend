package com.springai.pi.controller;

import com.springai.pi.domain.bo.ConversationBo;
import com.springai.pi.domain.core.PageQuery;
import com.springai.pi.domain.core.R;
import com.springai.pi.domain.core.TableDataInfo;
import com.springai.pi.domain.vo.ConversationPageVo;
import com.springai.pi.domain.vo.ConversationVo;
import com.springai.pi.service.ConversationService;
import com.springai.pi.utils.LoginHelper;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author stef
 * @version 1.0
 * @description 会话控制器
 * @date 9/19/25 12:38:37
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/conversation")
public class ConversationController {

    private final ConversationService conversationService;

    @GetMapping("/list")
    public TableDataInfo<ConversationVo> list(ConversationBo bo, PageQuery pageQuery) {
        return conversationService.queryPageList(bo, pageQuery);
    }


    @PostMapping()
    public R<Void> add(@Validated @RequestBody ConversationBo bo) {
        conversationService.insertByBo(bo);
        return R.ok();
    }


    @DeleteMapping("/{id}")
    public R<Void> remove(@NotNull(message = "id不能为空")
                          @PathVariable Long id) {
        conversationService.deleteById(id);
        return R.ok();
    }

    /**
     * 根据conversationId获取最近消息，支持游标分页
     * @return 分页结果
     */
    @GetMapping("/{conversationId}/messages")
    public R<ConversationPageVo> getMessages(@PathVariable @NotBlank(message = "conversationId不能为空") String conversationId,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date cursor,
                                             @RequestParam(defaultValue = "5") @Max(value = 10, message = "size不能大于10") Integer size) {
        String userId = String.valueOf(LoginHelper.getUserId());
        ConversationPageVo result = conversationService.getMessagesByCursor(
                userId, conversationId, cursor, size);
        return R.ok(result);
    }

}
