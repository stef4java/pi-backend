package com.springai.pi.domain.vo;

import com.springai.pi.domain.Conversation;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author stef
 * @version 1.0
 * @description ConversationVo
 * @date 9/19/25 12:48:59
 */
@Data
@AutoMapper(target = Conversation.class)
public class ConversationVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 会话主题
     */
    private String topic;
}
