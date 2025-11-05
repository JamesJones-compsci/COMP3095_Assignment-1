package ca.gbc.comp3095.goaltrackingservice.repository;

import ca.gbc.comp3095.goaltrackingservice.model.Goal;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GoalRepository extends MongoRepository<Goal,String> { // All you need to expose basic CRUD operations
}
