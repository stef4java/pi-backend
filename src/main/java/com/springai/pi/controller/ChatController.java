package com.springai.pi.controller;

import cn.hutool.json.JSONUtil;
import com.springai.pi.agent.XiaoPiAgent;
import com.springai.pi.utils.LoginHelper;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author stef
 * @date 9/13/25 4:58 PM
 */
@Validated
@RestController
@RequestMapping("/ai")
@Slf4j
public class ChatController {

    @Resource
    private XiaoPiAgent xiaoPiApp;

    @GetMapping("/call")
    public String call(@NotBlank(message = "message can not be blank") String message, @NotBlank(message = "conversationId can not be blank") String conversationId) {
        log.info("LoginUserId: {}", LoginHelper.getUserId());
        return xiaoPiApp.doChat(message, conversationId);
    }

    /**
     * [AI 零代码应用生成平台](https://github.com/liyupi/yu-ai-code-mother)
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> stream(@NotBlank(message = "message can not be blank") String message, @NotBlank(message = "conversationId can not be blank") String conversationId) {
        String chatIdExt = String.format("%s_%s", LoginHelper.getUserId(), conversationId);
        Flux<String> contentFlux = xiaoPiApp.doChatByStream(message, chatIdExt);
        // SSE 流式返回）
        return contentFlux
                .map(chunk -> {
                    Map<String, String> wrapper = Map.of("d", chunk);
                    String jsonData = JSONUtil.toJsonStr(wrapper);
                    return ServerSentEvent.<String>builder()
                            .data(jsonData)
                            .build();
                })
                .concatWith(Mono.just(
                        // 发送结束事件
                        ServerSentEvent.<String>builder()
                                .event("done")
                                .data("")
                                .build()
                ));
    }
}
