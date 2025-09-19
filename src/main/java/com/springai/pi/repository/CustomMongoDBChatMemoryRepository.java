package com.springai.pi.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.springai.pi.agent.AgentConstants;
import com.springai.pi.domain.ConversationItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author stef
 * @version 1.0
 * @description CustomMongoDBChatMemoryRepository
 * @date 9/18/25 15:31:31
 */
@Slf4j
@Repository
public class CustomMongoDBChatMemoryRepository implements ChatMemoryRepository {

    @Autowired
    private AiChatMemoryRepository aiChatMemoryRepository;

    @Override
    public List<String> findConversationIds() {
        log.info("---->findConversationIds");
        return Lists.newArrayList();
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {
        String[] userIdAndConversationId = conversationId.split("_");
        // 根据会话ID查找所有消息并按时间排序
        List<ConversationItem> chatMemories = aiChatMemoryRepository.findByUserIdAndConversationIdOrderByIdDesc(userIdAndConversationId[0],
                userIdAndConversationId[1],
                PageRequest.of(0, AgentConstants.MESSAGE_WINDOW_MAX_SIZE));
        return chatMemories.stream()
                .map(this::aiChatMemoryToMessage)
                .collect(Collectors.toList());
    }

    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        if (CollUtil.isNotEmpty(messages)) {
            String[] userIdAndConversationId = conversationId.split("_");
            Message lastMessage = messages.getLast();
            log.info("saveAll: {}", messages.toString());
            ConversationItem aiChatMemory = new ConversationItem();
            aiChatMemory.setUserId(userIdAndConversationId[0]);
            aiChatMemory.setConversationId(userIdAndConversationId[1]);
            aiChatMemory.setContent(lastMessage.getText());
            aiChatMemory.setType(lastMessage.getMessageType().toString());
            aiChatMemory.setTimestamp(new Date(System.currentTimeMillis()));
            aiChatMemoryRepository.insert(aiChatMemory);
        }
    }


    @Override
    public void deleteByConversationId(String conversationId) {
        log.info("---->deleteByConversationId");
    }

    /**
     * 将 AiChatMemory 转换为 Message 对象
     *
     * @param aiChatMemory 数据库实体
     * @return Message 对象
     */
    private Message aiChatMemoryToMessage(ConversationItem aiChatMemory) {
        String content = aiChatMemory.getContent();
        String type = aiChatMemory.getType();
        if (StrUtil.isBlank(type)) {
            return new UserMessage("Error: Invalid message data");
        }

        try {
            MessageType messageType = MessageType.valueOf(type);
            return switch (messageType) {
                case USER -> new UserMessage(content);
                case ASSISTANT -> new AssistantMessage(content);
                case SYSTEM -> new SystemMessage(content);
                default -> new UserMessage("Error: Unknown message type");
            };
        } catch (Exception e) {
            return new UserMessage("Error: " + e.getMessage());
        }
    }
}
