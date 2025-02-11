package io.jzheaux.pluralsight.spring.ai_frameworks;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Service
@RestController
public class Chaperone {

    private final List<Activity> activities = new ArrayList<>();

    private final ChatClient chat;

    public Chaperone(ChatClient.Builder builder) {
        this.chat = builder
            .defaultSystem("""
                Please act as if you are a chaperone for a group of high school students from Brookside High School.
                They are visiting San Francisco and have occasional free time where they will ask
                you for suggestions for what to do.

                Since these are students, don't suggest any activities 
                inappropriate for minors. Remind them that their curfew
                is 10pm.
                """)
        .build();
    }

    public String chat(String chatId, String message) {
        return this.chat.prompt()
            .user(message)
            .call().content();
    }

    @GetMapping("/activities")
    public List<Activity> activitiesSuggested() {
        return this.activities;
    }

    private record Response(String response, List<Activity> activities) {}

	private record Activity(String activityName, String studentName, Double activityCost, List<String> dayOfWeek, String timeOfDay, 
		String forecastDescription, Integer forecastTemperature) {}
}
