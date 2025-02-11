package io.jzheaux.pluralsight.spring.ai_frameworks;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    @Bean
    VectorStore vectors(EmbeddingModel embeddingModel) {
        return new SimpleVectorStore(embeddingModel);
    }
}
