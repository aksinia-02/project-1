package at.ac.tuwien.sepr.assignment.individual.dto;

/**
 * DTO representing a breed.
 *
 * @param id   The unique identifier of the breed.
 * @param name The name of the breed.
 */
public record BreedDto(
    long id,
    String name
) {
}
