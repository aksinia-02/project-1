package at.ac.tuwien.sepr.assignment.individual.dto;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;

/**
 * DTO class for list of horses in search view.
 *
 * @param id The unique identifier of the horse.
 * @param name The name of the horse.
 * @param sex The sex of the horse.
 * @param dateOfBirth The date of birth of the horse.
 * @param breed The breed of the horse
 */
public record HorseListDto(
    Long id,
    String name,
    Sex sex,
    LocalDate dateOfBirth,
    BreedDto breed
) {
}
