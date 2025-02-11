package io.jzheaux.pluralsight.spring.ai_frameworks;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@RestController
public class FeedbackTools {
    private final VectorStore vectors;

    FeedbackTools(VectorStore vectors) {
        this.vectors = vectors;
    }

    @PostMapping("/feedback")
    void manualFeedback(@RequestBody String feedback) {
        addFeedback(feedback);
    }

    void addFeedback(String feedback) {
        this.vectors.add(new TokenTextSplitter().transform(List.of(new Document(feedback))));
    }
}
