package at.ac.tuwien.sepr.assignment.individual.rest;

import java.util.List;

/**
 * Represents a DTO used for validation errors in REST.
 *
 * @param message A message describing the validation error.
 * @param errors A list of specific error messages.
 */
public record ValidationErrorRestDto(
    String message,
    List<String> errors
) {
}
