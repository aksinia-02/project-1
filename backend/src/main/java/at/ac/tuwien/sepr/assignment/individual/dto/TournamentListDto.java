package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;

/**
 * DTO representing the list of tournaments in a search view.
 *
 * @param id        The unique identifier of the tournament.
 * @param name      The name of the tournament.
 * @param startDate The start date of the tournament.
 * @param endDate   The end date of the tournament.
 */
public record TournamentListDto(
    Long id,
    String name,
    LocalDate startDate,
    LocalDate endDate
) {
}
