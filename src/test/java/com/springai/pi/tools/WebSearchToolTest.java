package com.springai.pi.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author stef
 * @date 9/16/25 4:07 PM
 */
@SpringBootTest
class WebSearchToolTest {

    @Test
    void search() {
        WebSearchTool webSearchTool = new WebSearchTool();
        String result = webSearchTool.webSearch("Spring Boot");
        Assertions.assertNotNull( result);
    }


}