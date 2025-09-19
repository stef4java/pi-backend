package com.springai.pi.agent;

import com.springai.pi.repository.CustomMongoDBChatMemoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * @author stef
 * @date 9/13/25 5:01 PM
 */
@Component
@Slf4j
public class XiaoPiAgent {


    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = """
            您好，我是无限派科技有限公司的智能客服小派，很高兴为您服务。
            我可以协助您解决产品咨询、产品售后问题，还可以帮助您下单、查询订单和取消订单。
            无论您有什么问题或需求，请随时告诉我，我会尽力为您提供帮助。
            """;

    private final  CustomMongoDBChatMemoryRepository mongoDBChatMemoryRepository;

    //    public XiaoPiApp(ChatModel dashscopeChatModel, MysqlChatMemoryRepository mysqlChatMemoryRepository) {
    public XiaoPiAgent(ChatModel dashscopeChatModel, CustomMongoDBChatMemoryRepository mongoDBChatMemoryRepository, RedisVectorStore redisVectorStore, ToolCallback[] allTools ) {
        this.mongoDBChatMemoryRepository = mongoDBChatMemoryRepository;
        // 构建带消息窗口的记忆组件，最多保留最近 30 条消息
        MessageWindowChatMemory messageWindowChatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(mongoDBChatMemoryRepository)
                .maxMessages(AgentConstants.MESSAGE_WINDOW_MAX_SIZE)
                .build();

        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        // 默认的记忆增强 Advisor，可按需开启
                        MessageChatMemoryAdvisor.builder(messageWindowChatMemory).build()
                        // RAG，可按需开启
                        , new QuestionAnswerAdvisor(redisVectorStore)
                )
                .defaultToolCallbacks(allTools)
                .build();
    }


    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }


    public Flux<String> doChatByStream(String message, String chatId) {
        return chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();
    }

}
