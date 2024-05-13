package at.ac.tuwien.sepr.assignment.individual.dto;

/**
 * DTO representing tournament standings.
 *
 * @param id           The unique identifier of the tournament.
 * @param name         The name of the tournament.
 * @param participants Array of tournament participants.
 * @param tree         Tournament standings tree.
 */
public record TournamentStandingsDto(
      Long id,
      String name,
      TournamentDetailParticipantDto[] participants,
      TournamentStandingsTreeDto tree
) {
}
