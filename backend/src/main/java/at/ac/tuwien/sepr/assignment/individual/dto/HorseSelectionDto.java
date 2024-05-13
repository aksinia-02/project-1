package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;

/**
 * DTO representing horse of tournament (search page).
 *
 * @param id The unique identifier of the horse.
 * @param name The name of the horse.
 * @param dateOfBirth The date of birth of the horse.
 */
public record HorseSelectionDto(
    long id,
    String name,
    LocalDate dateOfBirth
) {
}
