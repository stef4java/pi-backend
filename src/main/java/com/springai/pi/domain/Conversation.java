package com.springai.pi.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.springai.pi.domain.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author stef
 * @version 1.0
 * @description 会话
 * @date 9/19/25 12:26:36
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("conversation")
public class Conversation extends BaseEntity {
    @TableId
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
