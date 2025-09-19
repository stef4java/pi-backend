package com.springai.pi.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stef
 * @date 9/14/25 7:43 PM
 */
@RestController
@RequestMapping("/rag")
@Slf4j
@RequiredArgsConstructor
public class RagDocumentController {
    private final ResourcePatternResolver resourcePatternResolver;
    private final VectorStore redisVectorStore;

    @GetMapping("/load")
    public void loadDocuments() {
        log.info("开始加载文档");
        List<Document> allDocuments = new ArrayList<>();
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:data/*.md");
            log.info("找到 {} 个文档需要加载", resources.length);
            for (Resource resource : resources) {
                String fileName = resource.getFilename();
                if (fileName == null) {
                    log.warn("资源 {} 的文件名为空，跳过处理", resource.getDescription());
                    continue;
                }
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)
                        .withIncludeBlockquote(false)
                        .withAdditionalMetadata("filename", fileName)
                        .build();
                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                allDocuments.addAll(reader.get());
            }
        } catch (Exception e) {
            log.error("文档加载过程中发生错误", e);
        }
        log.info("文档加载完毕，共加载 {} 个文档", allDocuments.size());
        // 直接添加所有文档，批处理由RedisVectorStore内部处理
        redisVectorStore.add(allDocuments);
        log.info("Redis向量存储初始化完成，共添加 {} 个文档", allDocuments.size());
    }
}
