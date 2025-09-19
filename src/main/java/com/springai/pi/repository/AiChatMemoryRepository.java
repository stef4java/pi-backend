package com.springai.pi.repository;

import com.springai.pi.domain.ConversationItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface AiChatMemoryRepository extends MongoRepository<ConversationItem, String> {

    List<ConversationItem> findByUserIdAndConversationIdOrderByIdDesc(String userId, String conversationId, Pageable pageable);

    /**
     * 游标分页查询：获取小于指定 timestamp 的记录（即更早的消息）
     *
     * @param userId          用户ID
     * @param conversationId  会话ID
     * @param timestampCursor 游标时间戳（传入上一页最后一条的 timestamp）
     *                        //     * @param pageSize        每页数量
     * @return 符合条件的 ConversationItem 列表（按时间倒序）
     */
    List<ConversationItem> findByUserIdAndConversationIdAndTimestampBeforeOrderByTimestampDesc(
            String userId,
            String conversationId,
            Date timestampCursor,
            Pageable pageable
    );

    /**
     * 首次加载（无游标）：获取最新的 N 条消息
     */
    List<ConversationItem> findByUserIdAndConversationIdOrderByTimestampDesc(
            String userId,
            String conversationId,
            Pageable pageable
    );

    void deleteByConversationId(String conversationId);

}
