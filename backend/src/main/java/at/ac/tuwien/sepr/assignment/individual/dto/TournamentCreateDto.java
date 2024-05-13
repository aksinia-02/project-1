package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;

/**
 * DTO for creating a new tournament.
 *
 * @param name The name of the tournament.
 * @param startDate The start date of the tournament.
 * @param endDate The end date of the tournament.
 * @param participants An array of HorseSelectionDto representing the participants of the tournament.
 */
public record TournamentCreateDto(
    String name,
    LocalDate startDate,
    LocalDate endDate,
    HorseSelectionDto[] participants
) {
}
