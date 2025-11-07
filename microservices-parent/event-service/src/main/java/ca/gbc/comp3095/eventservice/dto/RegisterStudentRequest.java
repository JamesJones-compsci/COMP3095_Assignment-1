package ca.gbc.comp3095.eventservice.dto;

public record RegisterStudentRequest(
        Long eventId,
        Long studentId
) { }
