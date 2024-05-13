package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentUpdateParticipantsDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.service.TournamentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

/**
 * Controller class for handling HTTP requests related to tournaments
 */
@RestController
@RequestMapping(path = TournamentEndpoint.BASE_PATH)
public class TournamentEndpoint {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  static final String BASE_PATH = "/tournaments";

  private final TournamentService service;

  /**
   * Constructs a new TournamentEndpoint with the specified TournamentService.
   *
   * @param service The TournamentService instance used for handling tournament-related operations.
   */
  public TournamentEndpoint(TournamentService service) {
    this.service = service;
  }

  /**
   * Searches for tournaments based on the provided search parameters.
   *
   * @param searchParameters The parameters used for searching horses.
   * @return A stream of TournamentListDto objects representing the search results.
   */
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Stream<TournamentListDto> searchTournaments(TournamentSearchDto searchParameters) {
    LOG.info("GET " + BASE_PATH);
    LOG.debug("request parameters: {}", searchParameters);
    return service.search(searchParameters);
  }

  /**
   * Creates a new tournament based on the provided details.
   *
   * @param toInsert the details of the tournament to be created
   * @return the created tournament details
   * @throws ResponseStatusException with HTTP status 409 (Conflict) if there is a conflict with the data currently in the system
   *     (e.g., if the tournament already exists)
   * @throws ResponseStatusException with HTTP status 422 (Unprocessable entity) if the data provided for the tournament is incorrect
   *     (e.g., missing name, invalid values for start date or end date)
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TournamentDetailsDto create(@RequestBody TournamentCreateDto toInsert) {
    LOG.info("POST " + BASE_PATH);
    LOG.debug("Tournament to create:\n{}", toInsert);
    try {
      return service.insert(toInsert);
    } catch (ConflictException e) {
      HttpStatus status = HttpStatus.CONFLICT;
      logClientError(status, "Conflict occurred for inserting tournament", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ValidationException e) {
      HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
      logClientError(status, "Validation failed for inserting tournament", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  /**
   * Retrieves the standings of a tournament with the specified ID.
   *
   * @param id The ID of the tournament.
   * @return The standings of the tournament.
   * @throws ResponseStatusException with HTTP status 409 (Conflict) if the tournament with the given ID is not found.
   * @throws ResponseStatusException with HTTP status 422 (Unprocessable entity) if id is invalid
   */
  @GetMapping("/standings/{id}")
  @ResponseStatus(HttpStatus.OK)
  public TournamentStandingsDto getStandings(@PathVariable("id") long id) {
    LOG.info("GET " + BASE_PATH + "/standings/{}", id);
    try {
      return service.getStandings(id);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Tournament with this id: " + id + " is not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ValidationException e) {
      HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
      logClientError(status, "Id is not valid", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  /**
   * Generates the first-round standings for a tournament based on the provided ID.
   *
   * @param id The ID of the tournament for which to generate the first-round standings.
   * @return The first-round standings of the tournament.
   * @throws ResponseStatusException with HTTP status 409 (Conflict) if the tournament with the given ID is not found.
   * @throws ResponseStatusException with HTTP status 422 (Unprocessable entity) if id is invalid
   */
  @GetMapping("/standings/{id}/first-round")
  @ResponseStatus(HttpStatus.OK)
  public TournamentStandingsDto generateFirstRound(@PathVariable("id") long id) {
    LOG.info("GET " + BASE_PATH + "/standings/first-round/{}", id);
    try {
      return service.generateFirstRound(id);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Tournament with this id: " + id + " is not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ValidationException e) {
      HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
      logClientError(status, "Id is not valid", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  /**
   * Saves the standings of a tournament.
   *
   * @param updateParticipants The participants' standings to be saved.
   * @param id id of the tournament.
   * @return The updated standings of the tournament.
   * @throws ResponseStatusException with HTTP status 404 (Not Found) if a participant with some id is not found, if tournament ist not found.
   * @throws ResponseStatusException with HTTP status 422 (Unprocessable Entity) if the new round is not valid.
   */
  @PutMapping("/standings/{id}")
  @ResponseStatus(HttpStatus.OK)
  public TournamentStandingsDto saveStanding(@PathVariable("id") long id, @RequestBody TournamentUpdateParticipantsDto updateParticipants) {
    LOG.info("PUT " + BASE_PATH + "/standings/{}", id);
    LOG.debug("Participants to update:\n{}", updateParticipants);
    try {
      return service.saveStanding(updateParticipants, id);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Participant or tournament with some id is not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ValidationException e) {
      HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
      logClientError(status, "New Round is not valid", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  /**
   * Logs client errors.
   *
   * @param status  The HTTP status code associated with the error.
   * @param message A description of the error.
   * @param e       The exception that occurred.
   */
  private void logClientError(HttpStatus status, String message, Exception e) {
    LOG.warn("{} {}: {}: {}", status.value(), message, e.getClass().getSimpleName(), e.getMessage());
  }
}
