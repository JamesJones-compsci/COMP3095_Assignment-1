package ca.gbc.comp3095.goaltrackingservice.controller;

import ca.gbc.comp3095.goaltrackingservice.dto.GoalRequest;
import ca.gbc.comp3095.goaltrackingservice.dto.GoalResponse;
import ca.gbc.comp3095.goaltrackingservice.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService _goalService;

    // Post - Create a new goal
    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(@RequestBody GoalRequest request){
        GoalResponse createdGoal = _goalService.createGoal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGoal);
    }

    // Get - Retrieve a list of all goals
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<GoalResponse> getAllGoals() {
        return _goalService.getAllGoals();
    }

    // Get - Retrieve a goal based on id
    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getGoalById(@PathVariable("id") String goalId) {
        GoalResponse goal = _goalService.getGoalById(goalId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(goal);
    }


    // Put - Update a goal based on id
    @PutMapping("/{id}")
    public  ResponseEntity<?> updateGoal(@PathVariable("id") String goalId,
                                            @RequestBody GoalRequest request){

        String updatedGoalId = _goalService.updateGoal(goalId, request);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/api/goals/" + updatedGoalId);
        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);

    }

    // Delete - Delete a goal based on id
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGoal(@PathVariable("id") String goalId){
        _goalService.deleteGoal(goalId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Get - Retrieve a list of goals based on category
    @GetMapping("/category/{category}")
    public ResponseEntity<List<GoalResponse>> getGoalsByCategory(@PathVariable("category") String category) {
        List<GoalResponse> goalsByCategory = _goalService.getGoalsByCategory(category);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(goalsByCategory);
    }

    // Get - Retrieve a list of goals based on status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<GoalResponse>> getGoalsByStatus(@PathVariable("status") String status) {
        List<GoalResponse> goalsByStatus = _goalService.getGoalsByStatus(status);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(goalsByStatus);
    }

    // Put - Update a goal as completed
    @PutMapping("/{id}/complete")
    public ResponseEntity<GoalResponse> markGoalAsCompleted(@PathVariable("id") String goalId) {
        GoalResponse completedGoal = _goalService.markGoalAsCompleted(goalId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/api/goals/" + goalId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(completedGoal);
    }


}
