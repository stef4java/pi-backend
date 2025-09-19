package com.springai.pi.domain.vo;

import com.springai.pi.domain.ConversationItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationPageVo {
    private List<ConversationItem> data;
    private Date nextCursor;
    private Boolean hasMore;
}