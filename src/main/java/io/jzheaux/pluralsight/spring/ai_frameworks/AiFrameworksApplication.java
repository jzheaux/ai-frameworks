package io.jzheaux.pluralsight.spring.ai_frameworks;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;

@SpringBootApplication
public class AiFrameworksApplication implements CommandLineRunner {

	private final Chaperone chaperone;

	private String chatId = UUID.randomUUID().toString();
	private Map<String, String> chats = new LinkedHashMap<>();

	public AiFrameworksApplication(Chaperone chaperone,
			VectorStore vectors,
			@Value("classpath:rag/*.txt") List<Resource> resources) {
		this.chaperone = chaperone;
		resources.forEach((resource) -> {
			vectors.add(new TokenTextSplitter().apply(new TextReader(resource).read()));
		});
	}

	@Override
	public void run(String... args) throws Exception {
		runApp();
	}

	private void runApp() {
		System.out.print("""
			Welcome to Our AI Tour Guide App! Begin by entering your name and the AI will greet you.
			Once you are all done, type the word DONE so another student can use it.
			""");
		try (Scanner scanner = new Scanner(System.in)) {
			while (true) {
				System.out.print("Student Name >>> ");
				String name = scanner.nextLine();
				this.chatId = this.chats.computeIfAbsent(name, (n) -> UUID.randomUUID().toString());
				System.out.println(this.chaperone.chat(this.chatId, "hi, my name is " + name));
				while (true) {
					System.out.print(">>> ");
					String message = scanner.nextLine();
					if ("DONE".equals(message)) {
						break;
					}
					System.out.println(this.chaperone.chat(this.chatId, message));
				}				
			}
		}
	}

	
	public static void main(String[] args) {
		SpringApplication.run(AiFrameworksApplication.class, args);
	}

}
