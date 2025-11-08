package ca.gbc.comp3095.goaltrackingservice.dto;

import java.time.LocalDate;

public record GoalRequest(   String goalId,
                             String name,
                             String description,
                             String category,
                             String frequency,
                             LocalDate startDate,
                             LocalDate targetDate,
                             String status,

                             double targetValue,
                             double currentValue)
{}
