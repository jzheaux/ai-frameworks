package io.jzheaux.pluralsight.spring.ai_frameworks;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Service
@RestController
public class Chaperone {

    private final List<Activity> activities = new ArrayList<>();

    private final ChatClient chat;

    public Chaperone(ChatClient.Builder builder, VectorStore vectors) {
        this.chat = builder
            .defaultSystem("""
                Please act as if you are a chaperone for a group of high school students from Brookside High School.
                They are visiting San Francisco and have occasional free time where they will ask
                you for suggestions for what to do.

                Your suggestions MUST honor school policy, including curfew,
                MUST resonably fit into the free time indicated in the itinerary,
                and SHOULD stick to the list of pre-approved activities.

                If your response includes activities, summarize them as part 
                of your human-readable response and also duplicate them in the 
                JSON format indicated.
                """)
        .defaultAdvisors(List.of(new QuestionAnswerAdvisor(vectors)))
        .build();
    }

    public String chat(String chatId, String message) {
        Response response = this.chat.prompt()
            .user(message)
            .call().entity(Response.class);
        if (response.activities() != null) {
            this.activities.addAll(response.activities());
        }
        return response.response;
    }

    @GetMapping("/activities")
    public List<Activity> activitiesSuggested() {
        return this.activities;
    }

    private record Response(String response, List<Activity> activities) {}

	private record Activity(String activityName, String studentName, Double activityCost, List<String> dayOfWeek, String timeOfDay, 
		String forecastDescription, Integer forecastTemperature) {}
}
