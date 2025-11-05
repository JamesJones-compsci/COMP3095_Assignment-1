package ca.gbc.comp3095.goaltrackingservice.service;

import ca.gbc.comp3095.goaltrackingservice.dto.GoalRequest;
import ca.gbc.comp3095.goaltrackingservice.dto.GoalResponse;
import ca.gbc.comp3095.goaltrackingservice.model.Goal;
import ca.gbc.comp3095.goaltrackingservice.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalServiceImpl implements GoalService {

    private final GoalRepository _goalRepository;
    private final MongoTemplate mongoTemplate; // to be able to execute the queries

    @Override
    public GoalResponse createGoal(GoalRequest request) {

        log.debug("Create new goal {}", request);
        // goalRequest is placeholder for actual object value
        Goal goal = Goal.builder() // One line to call the setters
                .name(request.name())
                .description(request.description())
                .category(request.category())
                .frequency(request.frequency())
                .startDate(request.startDate())
                .targetDate(request.targetDate())
                .status(request.status())
                .targetValue(request.targetValue())
                .currentValue(request.currentValue())
                .build();

        //save goal to the database
        _goalRepository.save(goal);
        log.debug("Successfully saved new goal {}", goal);

        return new GoalResponse(
                goal.getGoalId(),
                goal.getName(),
                goal.getDescription(),
                goal.getCategory(),
                goal.getFrequency(),
                goal.getStartDate(),
                goal.getTargetDate(),
                goal.getStatus(),
                goal.getTargetValue(),
                goal.getCurrentValue()
        );

    }

    @Override
    public List<GoalResponse> getAllGoals() {

        log.debug("Returning the list of Goals");
        List<Goal> goals = _goalRepository.findAll();

        return goals
                .stream()
                .map(this::mapToGoalResponse)
                .toList();
        }

    @Override
    public GoalResponse getGoalById(String goalId) {
        log.debug("Fetching goal by id {}", goalId);

        Goal goal = _goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + goalId));

        return mapToGoalResponse(goal);
    }

    @Override
    public String updateGoal(String goalId, GoalRequest request) {

        log.debug("Updating goal with id {}",  goalId);

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(goalId));
        Goal goal = mongoTemplate.findOne(query, Goal.class);

        if(goal != null){
            goal.setName(request.name());
            goal.setDescription(request.description());
            goal.setCategory(request.category());
            goal.setFrequency(request.frequency());
            goal.setStartDate(request.startDate());
            goal.setTargetDate(request.targetDate());
            goal.setStatus(request.status());
            goal.setTargetValue(request.targetValue());
            goal.setCurrentValue(request.currentValue());
            return  _goalRepository.save(goal).getGoalId();
        }
        return goalId;
    }

    @Override
    public void deleteGoal(String goalId) {
        log.debug("Deleting goal with id {}", goalId);
        _goalRepository.deleteById(goalId);

    }

    @Override
    public List<GoalResponse> getGoalsByCategory(String category) {
        log.debug("Fetching goals by category {}", category);

        Query query = new Query();
        query.addCriteria(Criteria.where("category").is(category));
        List<Goal> goals = mongoTemplate.find(query, Goal.class);

        return goals.stream()
                .map(this::mapToGoalResponse)
                .toList();
    }

    @Override
    public List<GoalResponse> getGoalsByStatus(String status) {
        log.debug("Fetching goals by status {}", status);

        Query query = new Query();
        query.addCriteria(Criteria.where("status").is(status));
        List<Goal> goals = mongoTemplate.find(query, Goal.class);

        return goals.stream()
                .map(this::mapToGoalResponse)
                .toList();
    }

    @Override
    public GoalResponse markGoalAsCompleted(String goalId) {
        log.debug("Marking goal {} as completed", goalId);

        Goal goal = _goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + goalId));

        goal.setStatus("Completed");
        goal.setCurrentValue(goal.getTargetValue());
        Goal updatedGoal = _goalRepository.save(goal);

        log.debug("Successfully marked goal {} as completed", updatedGoal);

        return mapToGoalResponse(updatedGoal);
    }

    private GoalResponse mapToGoalResponse(Goal goal) {
        return new GoalResponse(
                goal.getGoalId(),
                goal.getName(),
                goal.getDescription(),
                goal.getCategory(),
                goal.getFrequency(),
                goal.getStartDate(),
                goal.getTargetDate(),
                goal.getStatus(),
                goal.getTargetValue(),
                goal.getCurrentValue()
        );
    }

}
