package fr.eni.bookhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BookHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookHubApplication.class, args);
    }

}
