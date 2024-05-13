package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentUpdateParticipantsDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;

import java.util.stream.Stream;

/**
 * Service interface for managing tournaments
 */
public interface TournamentService {

  /**
   * Search for tournaments in the persistent data store matching all provided fields.
   * The name is considered a match, if the search string is a substring of the field in tournament.
   *
   * @param searchParameters the search parameters to use in filtering.
   * @return the tournaments where the given fields match.
   */
  Stream<TournamentListDto> search(TournamentSearchDto searchParameters);

  /**
   * Inserts a new tournament into the persistent data store.
   *
   * @param tournament the tournament to be inserted
   * @return the details of inserted tournament
   * @throws ConflictException   if there is a conflict with the data currently in the system (e.g., if tournament already exists)
   * @throws ValidationException if the data provided for the tournament is incorrect
   *                             (e.g., too long name, invalid values for start date or end date)
   */
  TournamentDetailsDto insert(TournamentCreateDto tournament) throws ConflictException, ValidationException;

  /**
   * Retrieves the standings of the tournament with the specified ID.
   *
   * @param id The ID of the tournament.
   * @return The standings of the tournament.
   * @throws NotFoundException If the tournament with the given ID is not found.
   * @throws ValidationException If the provided ID is not valid.
   */
  TournamentStandingsDto getStandings(Long id) throws NotFoundException, ValidationException;

  /**
   * Retrieves the first-round standings of the tournament identified by the given ID.
   *
   * @param id The ID of the tournament.
   * @return The first-round standings of the tournament.
   * @throws NotFoundException    If the tournament with the given ID is not found.
   * @throws ValidationException  If the provided ID is not valid.
   */
  TournamentStandingsDto generateFirstRound(Long id) throws NotFoundException, ValidationException;

  /**
   * Saves the standings of a tournament.
   *
   * @param updateParticipants The participants' standings to be saved.
   * @param id The id of tournament.
   * @return The updated standings of the tournament.
   * @throws NotFoundException   If a participant or tournament with some ID is not found.
   * @throws ValidationException If the new round is not valid.
   */
  TournamentStandingsDto saveStanding(TournamentUpdateParticipantsDto updateParticipants, long id) throws NotFoundException, ValidationException;
}
