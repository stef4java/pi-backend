package com.springai.pi.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * @author stef
 * @version 1.0
 * @description 会话条目
 * @date 9/19/25 12:26:36
 */
@Document(collection = "conversation_item")
//为了高效支持多条件筛选和排序，必须创建合适的索引
@CompoundIndex(name = "idx_user_conversation_timestamp",
        def = "{'userId': 1, 'conversationId': 1, 'timestamp': -1}")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ConversationItem implements Serializable {

    @Id
    private String id;
    private String userId;
    private String conversationId;
    private String content;
    /**
     * 类型
     * USER: 用户输入
     * ASSISTANT: AI输出
     */
    private String type;
    private Date timestamp;
}