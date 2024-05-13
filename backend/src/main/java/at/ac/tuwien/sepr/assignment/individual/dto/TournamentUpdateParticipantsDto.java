package at.ac.tuwien.sepr.assignment.individual.dto;

/**
 * DTO representing tournament's participants for update.
 *
 * @param participants Array of participant details to be updated in the tournament.
 */
public record TournamentUpdateParticipantsDto(
    TournamentDetailParticipantDto[] participants
) {
}
