package ca.gbc.comp3095.achievementservice.service;

import ca.gbc.comp3095.achievementservice.model.Achievement;
import ca.gbc.comp3095.achievementservice.repository.AchievementRepository;
import ca.gbc.comp3095.achievementservice.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AchievementService {

    private final AchievementRepository repo;

    public AchievementService(AchievementRepository repo) {
        this.repo = repo;
    }

    public List<Achievement> list(String category) {
        if (category == null || category.isBlank()) {
            return repo.findAll();
        }
        return repo.findByCategoryIgnoreCase(category);
    }

    public Achievement create(Achievement a) {
        return repo.save(a);
    }

    public Achievement get(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Achievement not found: " + id));
    }

    public Achievement update(Long id, Achievement payload) {
        Achievement existing = get(id);
        existing.setTitle(payload.getTitle());
        existing.setDescription(payload.getDescription());
        existing.setCategory(payload.getCategory());
        existing.setIconUrl(payload.getIconUrl());
        return repo.save(existing);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NotFoundException("Achievement not found: " + id);
        }
        repo.deleteById(id);
    }
}
