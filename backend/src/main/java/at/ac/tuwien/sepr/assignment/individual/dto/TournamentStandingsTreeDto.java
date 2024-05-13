package at.ac.tuwien.sepr.assignment.individual.dto;

/**
 * DTO representing tournament standings tree.
 *
 * @param thisParticipant The participant at this level of the standings tree.
 * @param branches         Array of branches representing lower levels of the standings tree.
 */
public record TournamentStandingsTreeDto(
    TournamentDetailParticipantDto thisParticipant,
    TournamentStandingsTreeDto[] branches
) {
}
