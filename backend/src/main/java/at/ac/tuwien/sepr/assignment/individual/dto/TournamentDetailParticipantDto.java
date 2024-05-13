package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;

/**
 * DTO representing details of a participant in a tournament.
 *
 * @param id The unique identifier of the participant.
 * @param horseId The unique identifier of the horse associated with the participant.
 * @param name The name of the participant.
 * @param dateOfBirth The date of birth of the participant.
 * @param entryNumber The entry number of the participant in the tournament.
 * @param roundReached The round reached by the participant in the tournament.
 */
public record TournamentDetailParticipantDto(
    Long id,
    Long horseId,
    String name,
    LocalDate dateOfBirth,
    Integer entryNumber,
    Integer roundReached
) {
}
