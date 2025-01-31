package io.jzheaux.pluralsight.spring.ai_frameworks;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.ChatOptionsBuilder;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Service
@RestController
public class Chaperone {

    private final List<Activity> activities = new ArrayList<>();

    private final ChatClient chat;

    public Chaperone(ChatClient.Builder builder, VectorStore vectors, ChatMemory memory) {
        String dave = """
                        { "amount": null }""";
        this.chat = builder
            .defaultSystem("""
                Please act as if you are a chaperone for a group of high school students from Brookside High School.
                They are visiting San Francisco and have occasional free time where they will ask
                you for suggestions for what to do.

                If you don't know the student's name, begin by asking, so that you can retreive
                any previous conversations with them.
                
                If you have provided suggestions to them in the past,
                ask them what activities they did and how much they enjoyed them. When you receive
                feedback, make sure to use the appropriate function to store it and improve later suggestions.

                To create a list of suggestions, take the following into account:
                
                * What day of their trip they are wanting suggestions for
                * What time of day they'd likely be doing the activity, given the trip itinerary you
                were given (if you aren't sure, you can ask)
                * What the weather conditions are for that time period (use the provided function
                to check the weather forecast)
                * If it is an activity they've told you they didn't like (you probably shouldn't suggest
                that again)
                * If it is an activity they've told you they already did (you probably shouldn't suggest
                that again) 
                * If it abides school policy. You MUST follow all school policies and SHOULD stick 
                to the list of pre-approved activities from the brochure you were given.
                * If there the venue is open at that time and there is enough time to travel there, enjoy
                the activity without being rushed, and get back to the group for the rest of their itinerary.
                * NOTE: Sometimes they have a performance. Remember that they will need extra time to get ready
                before and will be in tuxedos and dresses. Any free time they have between two performances
                on the same day should keep in mind that there likely isn't time to go far or to get out of their 
                nice clothes.
                
                IMPORTANT If the activity cannot be done during their free-time, either due to time constraints,
                weather conditions, it's against school policy, or the venue is not open DO NOT SUGGEST that activity.

                The first day of their trip is the upcoming Sunday. They will always have an adult chaperone
                with them. Remember that they are high-school students; many of them are new to travel and
                will be both excited and nervous. Even if you know that they have enough time, they may not,
                so advise them how much time they need to plan for travel with each suggestion.
                Also suggest the ideal attire for that activity and anything else you feel they will need to
                know.
                
                If they are particularly excited about an activity that simply isn't realistic or allowed, 
                let them down slowly and with kindness. If there is a fee, you MUST advise them of it. 

                Today is {current_date}. They've worked hard to get here, help them have fun!
            """)
            .defaultAdvisors(new PromptChatMemoryAdvisor(memory), new QuestionAnswerAdvisor(vectors))
            .defaultFunctions("getWeatherForecast", "saveStudentFeedback")
            .build();
    }

    public String chat(String chatId, String message) {
        Response response = this.chat.prompt()
            .system(s -> s.param("current_date", LocalDate.now().toString()))
            .user(message)
            .advisors(a -> a
                .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
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
