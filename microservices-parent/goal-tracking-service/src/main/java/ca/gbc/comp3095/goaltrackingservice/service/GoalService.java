package ca.gbc.comp3095.goaltrackingservice.service;

import ca.gbc.comp3095.goaltrackingservice.dto.GoalRequest;
import ca.gbc.comp3095.goaltrackingservice.dto.GoalResponse;

import java.util.List;

public interface GoalService {

    GoalResponse createGoal(GoalRequest request);
    List<GoalResponse> getAllGoals();
    GoalResponse getGoalById(String goalId);
    String updateGoal(String goalId, GoalRequest request);
    void deleteGoal(String goalId);
    List<GoalResponse> getGoalsByCategory(String category);
    List<GoalResponse> getGoalsByStatus(String status);
    GoalResponse markGoalAsCompleted(String goalId);


}
