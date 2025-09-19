package com.springai.pi.service;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springai.pi.domain.Conversation;
import com.springai.pi.domain.ConversationItem;
import com.springai.pi.domain.bo.ConversationBo;
import com.springai.pi.domain.core.PageQuery;
import com.springai.pi.domain.core.TableDataInfo;
import com.springai.pi.domain.vo.ConversationPageVo;
import com.springai.pi.domain.vo.ConversationVo;
import com.springai.pi.exception.ServiceException;
import com.springai.pi.mapper.ConversationMapper;
import com.springai.pi.repository.AiChatMemoryRepository;
import com.springai.pi.utils.LoginHelper;
import com.springai.pi.utils.MapstructUtils;
import com.springai.pi.utils.StringUtils;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author stef
 * @version 1.0
 * @description ConversationService
 * @date 9/19/25 12:52:26
 */
@RequiredArgsConstructor
@Service
public class ConversationService {

    private final ConversationMapper baseMapper;

    private final AiChatMemoryRepository aiChatMemoryRepository;

    public TableDataInfo<ConversationVo> queryPageList(ConversationBo bo, PageQuery pageQuery) {
        bo.setUserId(LoginHelper.getUserId());
        LambdaQueryWrapper<Conversation> lqw = buildQueryWrapper(bo);
        Page<ConversationVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    public Boolean insertByBo(ConversationBo bo) {
        bo.setUserId(LoginHelper.getUserId());
        if (StringUtils.isBlank(bo.getTopic())) {
            String topic = DateUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + RandomUtil.randomString(3);
            bo.setTopic(topic);
        }
        Conversation add = MapstructUtils.convert(bo, Conversation.class);
        int insert = baseMapper.insert(add);
        if (insert == 0) {
            throw new ServiceException("新增失败");
        }
        return true;
    }

    private LambdaQueryWrapper<Conversation> buildQueryWrapper(ConversationBo bo) {
        LambdaQueryWrapper<Conversation> lqw = Wrappers.lambdaQuery();
        lqw.eq(Conversation::getUserId, bo.getUserId());
        lqw.orderByDesc(Conversation::getId);
        return lqw;
    }

    /**
     * 根据cursor获取消息
     *
     * @param query
     * @return
     */
    public ConversationPageVo getMessagesByCursor(
            String userId,
            String conversationId,
            Date cursor,        // 可选：上一页最后一条消息的 timestamp
            Integer pageSize) {
        Pageable pageable = PageRequest.of(0, pageSize + 1, Sort.by(Sort.Order.desc("timestamp")));

        List<ConversationItem> results;

        if (Objects.isNull(cursor)) {
            // 首次请求：获取最新消息
            results = aiChatMemoryRepository.findByUserIdAndConversationIdOrderByTimestampDesc(
                    userId, conversationId, pageable);
        } else {
            // 后续请求：基于游标继续加载更早的数据
            results = aiChatMemoryRepository.findByUserIdAndConversationIdAndTimestampBeforeOrderByTimestampDesc(
                    userId, conversationId, cursor, pageable);
        }

        ConversationPageVo pageVo = new ConversationPageVo();
        boolean hasMore = results.size() > pageSize;
        if (hasMore) {
            // trim to requested pageSize
            results = results.subList(0, pageSize);
        }
        Date newCursor = results.isEmpty() ? null : results.get(results.size() - 1).getTimestamp();
        pageVo.setHasMore(hasMore);
        pageVo.setNextCursor(newCursor);
        pageVo.setData(results);
        return pageVo;
    }

    public void deleteById(@NotNull(message = "id不能为空") Long id) {
        Long userId = LoginHelper.getUserId();
        baseMapper.delete(Wrappers.<Conversation>lambdaQuery()
                .eq(Conversation::getId, id)
                .eq(Conversation::getUserId, userId)
        );
        aiChatMemoryRepository.deleteByConversationId(String.valueOf(id));
    }
}
