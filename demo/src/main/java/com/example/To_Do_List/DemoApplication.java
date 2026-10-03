package com.example.To_Do_List;

import com.example.To_Do_List.Service.ProjectService;
import com.example.To_Do_List.repository.ProjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}




	@Bean
	CommandLineRunner initDatabase(ProjectService projectService) {
		return args -> {
			projectService.getOrCreateInbox();
		};

	}
}
