package ca.gbc.comp3095.achievementservice.repository;

import ca.gbc.comp3095.achievementservice.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    List<Achievement> findByCategoryIgnoreCase(String category);
}
