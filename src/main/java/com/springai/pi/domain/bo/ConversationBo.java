package com.springai.pi.domain.bo;

import com.springai.pi.domain.Conversation;
import com.springai.pi.domain.core.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author stef
 * @version 1.0
 * @description ConversationVo
 * @date 9/19/25 12:48:59
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Conversation.class, reverseConvertGenerate = false)
public class ConversationBo extends BaseEntity {


    private Long id;
    /**
     * 会话主题
     */
    private String topic;

    /**
     * 用户id
     */
    private Long userId;
}
