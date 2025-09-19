package com.springai.pi.rag;

import com.springai.pi.controller.RagDocumentController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author stef
 * @date 9/15/25 10:16 AM
 */
@SpringBootTest
class AppDocumentLoaderTest {

    @Autowired
    RagDocumentController appDocumentLoader;

    @Test
    void loadDocuments() {
        appDocumentLoader.loadDocuments();
    }
}