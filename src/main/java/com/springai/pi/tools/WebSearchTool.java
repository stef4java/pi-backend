package com.springai.pi.tools;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网页搜索工具
 */
public class WebSearchTool {

    private static final String SEARCH_URL = "http://localhost:8088/search";

    @Tool(description = "Search for information from SearXNG search engine")
    public String webSearch(
            @ToolParam(description = "Search query keyword") String query) {
        if (query == null || query.isEmpty()) {
            return "Error searching SearXNG: Query is null or empty.";
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("q", query);
        paramMap.put("format", "json");

        try {
            String response = HttpUtil.get(SEARCH_URL, paramMap);
            SearxngResponse searxngResponse = JSONUtil.toBean(response, SearxngResponse.class);
            if (searxngResponse == null || searxngResponse.getResults() == null) {
                return "Error searching SearXNG: Invalid response format.";
            }
            return searxngResponse.getResults().stream()
                    .limit(5)
                    .map(SearchResult::toString)
                    .collect(java.util.stream.Collectors.joining(","));

        } catch (Exception e) {
            return "Error searching SearXNG: " + e.getMessage();
        }
    }

    /**
     * SearXNG 搜索响应结果类
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SearxngResponse {
        private String query;
        private int numberOfResults;
        private List<SearchResult> results;
    }

    /**
     * 搜索结果单项类
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SearchResult {
        private String url;
        private String title;
        private String content;
    }
}
