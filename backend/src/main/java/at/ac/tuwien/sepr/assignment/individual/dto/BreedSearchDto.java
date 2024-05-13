package at.ac.tuwien.sepr.assignment.individual.dto;

/**
 * DTO representing a breed for searching.
 *
 * @param name  The name of the breed to search for.
 * @param limit The maximum number of results to return.
 */
public record BreedSearchDto(
    String name,
    Integer limit
) {
}
