package at.ac.tuwien.sepr.assignment.individual.dto;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * DTO to bundle the query parameters used in searching horses.
 * Each field can be null, in which case this field is not filtered by.
 *
 * @param name The name of the horse.
 * @param sex The sex of the horse.
 * @param bornEarliest The earliest date of birth for the horse.
 * @param bornLatest The latest date of birth for the horse.
 * @param breed The breed of the horse.
 * @param limit The limit of results to return.
 */
public record HorseSearchDto(
    String name,
    Sex sex,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate bornEarliest,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate bornLatest,
    String breed,
    Integer limit
) {
}
