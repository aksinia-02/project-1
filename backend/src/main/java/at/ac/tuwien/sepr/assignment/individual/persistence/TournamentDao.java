package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participant;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;

import java.util.Collection;

/**
 * Data Access Object for tournaments.
 * Implements access functionality to the application's persistent data store regarding tournaments.
 */
public interface TournamentDao {

  /**
   * Get the tournaments that match the given search parameters.
   * Parameters that are {@code null} are ignored.
   * The name is considered a match, if the given parameter is a substring of the field in horse.
   *
   * @param searchParameters the parameters to use in searching.
   * @return the tournaments where all given parameters match.
   */
  Collection<Tournament> search(TournamentSearchDto searchParameters);

  /**
   * Retrieves a participant of the tournament by their ID.
   *
   * @param id the ID of the tournament
   * @return the participant with the specified ID
   * @throws NotFoundException if the participant with the specified ID is not found
   */
  Participant[] getParticipantsByTournamentId(long id) throws NotFoundException;

  /**
   * Retrieves a tournament by its ID.
   *
   * @param id the ID of the tournament to retrieve
   * @return the tournament with the specified ID
   * @throws NotFoundException if the tournament with the specified ID is not found
   */
  Tournament getTournamentById(long id) throws NotFoundException;

  /**
   * Inserts a new tournament into the data store.
   *
   * @param tournament the tournament to insert
   * @return the inserted tournament
   * @throws ConflictException if there is a conflict with the data currently in the system (e.g., if the tournament already exists)
   */
  Tournament insert(TournamentCreateDto tournament) throws ConflictException;

  /**
   * Retrieves the participants for the first round of the tournament identified by the given ID.
   *
   * @param id The ID of the tournament.
   * @return An array of participants for the first round.
   * @throws NotFoundException If the tournament with the given ID is not found.
   */
  Participant[] getParticipantsForFirstRound(long id) throws NotFoundException;

  /**
   * Updates the standings of tournament participants.
   *
   * @param participants The array of tournament participant details to be updated.
   * @return An array of updated tournament participants.
   * @throws NotFoundException If a participant with some ID is not found.
   */
  Participant[] updateParticipants(TournamentDetailParticipantDto[] participants) throws NotFoundException;
}
