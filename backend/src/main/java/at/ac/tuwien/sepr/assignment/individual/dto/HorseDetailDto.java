package at.ac.tuwien.sepr.assignment.individual.dto;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;

/**
 * DTO representing details for a horse.
 *
 * @param id The unique identifier of the horse.
 * @param name The name of the horse.
 * @param sex The sex of the horse.
 * @param dateOfBirth The date of birth of the horse.
 * @param height The height of the horse.
 * @param weight The weight of the horse.
 * @param breed The breed of the horse.
 */
public record HorseDetailDto(
    Long id,
    String name,
    Sex sex,
    LocalDate dateOfBirth,
    float height,
    float weight,
    BreedDto breed
) {
  /**
   * Returns a new instance of HorseDetailDto with the specified ID.
   *
   * @param newId The new ID for the horse.
   * @return A new instance of HorseDetailDto with the specified ID.
   */
  public HorseDetailDto withId(long newId) {
    return new HorseDetailDto(
        newId,
        name,
        sex,
        dateOfBirth,
        height,
        weight,
        breed);
  }
}
