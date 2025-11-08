package ca.gbc.comp3095.achievementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AchievementServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AchievementServiceApplication.class, args);
    }
}
