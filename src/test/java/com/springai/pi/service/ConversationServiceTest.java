package com.springai.pi.service;

import com.springai.pi.domain.ConversationItem;
import com.springai.pi.domain.vo.ConversationPageVo;
import com.springai.pi.repository.AiChatMemoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author stef
 * @version 1.0
 * @description
 * @date 9/19/25 19:24:38
 */
@SpringBootTest
class ConversationServiceTest {

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private AiChatMemoryRepository aiChatMemoryRepository;

    @Test
    void queryPageList() {
        String userId = "1968883485351391234";
        String conversationId = "1968974667049898000";
        ConversationPageVo pageVo = conversationService.getMessagesByCursor(userId, conversationId, null, 10);
        Assertions.assertNotNull(pageVo.getData());
        Pageable pageable = Pageable.ofSize(10);
        List<ConversationItem> results = aiChatMemoryRepository.findByUserIdAndConversationIdOrderByTimestampDesc(userId, conversationId, pageable);
        Assertions.assertNotNull(results);

    }

}