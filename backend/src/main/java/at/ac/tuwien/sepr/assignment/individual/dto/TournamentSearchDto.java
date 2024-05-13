package at.ac.tuwien.sepr.assignment.individual.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * DTO to bundle the query parameters used in searching tournaments.
 * Each field can be null, in which case this field is not filtered by.
 *
 * @param name      The name of the tournament.
 * @param startDate The start date of the tournament.
 * @param endDate   The end date of the tournament.
 */
public record TournamentSearchDto(
    String name,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate startDate,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate endDate
) {
}
