package ca.gbc.comp3095.eventservice.dto;

public record StudentResponse(
    Long studentId,
    String firstName,
    String lastName
) { }
