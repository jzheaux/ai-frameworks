package io.jzheaux.pluralsight.spring.ai_frameworks;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

@Configuration
public class FeedbackTools {
    private final VectorStore vectors;
    
    public FeedbackTools(VectorStore vectors) {
        this.vectors = vectors;
    }

    @Bean
	@Description("Save Student Feedback")
	Consumer<Feedback> saveStudentFeedback() {
		return (feedback) -> this.vectors.write(List.of(new Document(feedback.feedback())));
	}

	private record Feedback(String feedback) {}
}
