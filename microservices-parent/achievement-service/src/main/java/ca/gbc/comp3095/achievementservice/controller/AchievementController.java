package ca.gbc.comp3095.achievementservice.controller;

import ca.gbc.comp3095.achievementservice.model.Achievement;
import ca.gbc.comp3095.achievementservice.service.AchievementService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {

    private final AchievementService service;

    public AchievementController(AchievementService service) {
        this.service = service;
    }

    @GetMapping
    public List<Achievement> list(@RequestParam(required = false) String category) {
        return service.list(category);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Achievement create(@RequestBody Achievement payload) {
        return service.create(payload);
    }

    @GetMapping("/{id}")
    public Achievement get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public Achievement update(@PathVariable Long id, @RequestBody Achievement payload) {
        return service.update(id, payload);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
