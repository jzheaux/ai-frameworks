package io.jzheaux.pluralsight.spring.ai_frameworks;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
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

    @Bean
	@Description("Save Student Feedback")
	Consumer<Feedback> saveStudentFeedback() {
		return (feedback) -> addFeedback(feedback.feedback());
	}

    @PostMapping("/feedback")
    void manualFeedback(@RequestBody String feedback) {
        addFeedback(feedback);
    }

    void addFeedback(String feedback) {
        this.vectors.add(new TokenTextSplitter().transform(List.of(new Document(feedback))));
    }

    private record Feedback(String feedback) {}
}
