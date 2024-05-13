package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;

/**
 * DTO representing the details of a tournament.
 *
 * @param id           The unique identifier of the tournament.
 * @param name         The name of the tournament.
 * @param startDate    The start date of the tournament.
 * @param endDate      The end date of the tournament.
 * @param participants An array of TournamentDetailParticipantDto representing the participants in the tournament.
 */
public record TournamentDetailsDto(
    long id,
    String name,
    LocalDate startDate,
    LocalDate endDate,
    TournamentDetailParticipantDto[] participants
) {}
