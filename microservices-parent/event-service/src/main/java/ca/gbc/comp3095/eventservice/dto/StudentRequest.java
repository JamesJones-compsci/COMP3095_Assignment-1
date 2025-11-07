package ca.gbc.comp3095.eventservice.dto;

public record StudentRequest (
        Long studentId,
        String firstName,
        String lastName
) { }
