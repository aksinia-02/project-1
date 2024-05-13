package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.service.HorseService;
import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller class for handling HTTP requests related to horse
 */
@RestController
@RequestMapping(path = HorseEndpoint.BASE_PATH)
public class HorseEndpoint {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  static final String BASE_PATH = "/horses";

  private final HorseService service;

  /**
   * Constructs a new HorseEndpoint with the specified HorseService.
   *
   * @param service the HorseService used by the HorseEndpoint
   */
  public HorseEndpoint(HorseService service) {
    this.service = service;
  }

  /**
   * Searches for horses based on the provided search parameters.
   *
   * @param searchParameters The parameters used for searching horses.
   * @return A stream of HorseListDto objects representing the search results.
   */
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Stream<HorseListDto> searchHorses(HorseSearchDto searchParameters) {
    LOG.info("GET " + BASE_PATH);
    LOG.debug("request parameters: {}", searchParameters);
    return service.search(searchParameters);
  }

  /**
   * Creates a new horse based on the provided details.
   *
   * @param toInsert The details of the horse to be created.
   * @return The details of the created horse.
   * @throws ResponseStatusException If a conflict occurs during insertion (HTTP status code 409) or if validation fails (HTTP status code 422).
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public HorseDetailDto create(@RequestBody HorseDetailDto toInsert) {
    LOG.info("POST " + BASE_PATH);
    LOG.debug("Horse to create:\n{}", toInsert);
    try {
      return service.insert(toInsert);
    } catch (ConflictException e) {
      HttpStatus status = HttpStatus.CONFLICT;
      logClientError(status, "Conflict occurred for inserting horse", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ValidationException e) {
      HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
      logClientError(status, "Validation of horse for insert failed", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  /**
   * Retrieves details of a horse by its ID.
   *
   * @param id The ID of the horse to retrieve details for.
   * @return The DTO containing details of the horse.
   * @throws ResponseStatusException If the horse with the specified ID is not found (HTTP status code 404).
   */
  @GetMapping("{id}")
  @ResponseStatus(HttpStatus.OK)
  public HorseDetailDto getById(@PathVariable("id") long id) {
    LOG.info("GET " + BASE_PATH + "/{}", id);
    try {
      return service.getById(id);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse with given id is not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  /**
   * Updates details of a horse with the specified ID.
   *
   * @param id       The ID of the horse to update.
   * @param toUpdate The DTO containing updated details of the horse.
   * @return The DTO containing updated details of the horse.
   * @throws ValidationException If the updated details fail validation (HTTP status code 422).
   * @throws ConflictException  If there is a conflict during the update process (HTTP status code 409).
   * @throws NotFoundException If the horse with the specified ID is not found (HTTP status code 404).
   */
  @PutMapping("{id}")
  @ResponseStatus(HttpStatus.OK)
  public HorseDetailDto update(@PathVariable("id") long id, @RequestBody HorseDetailDto toUpdate) throws ValidationException,
          ConflictException, NotFoundException {
    LOG.info("PUT " + BASE_PATH + "/{}", id);
    LOG.debug("Horse to update:\n{}", toUpdate);
    try {
      return service.update(toUpdate.withId(id));
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to update is not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ConflictException e) {
      HttpStatus status = HttpStatus.CONFLICT;
      logClientError(status, "Conflict occurred for updating horse", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ValidationException e) {
      HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
      logClientError(status, "Validation failed for updating horse", e);
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

  /**
   * Deletes a horse with the specified ID.
   *
   * @param id The ID of the horse to delete.
   * @throws ResponseStatusException If the horse with the specified ID is not found (HTTP status code 404).
   * @throws ResponseStatusException If id is null (HTTP status code 422).
   * @throws ResponseStatusException If horse is a participant in tournament (HTTP status code 409).
   */
  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("id") long id) {
    LOG.info("DELETE " + BASE_PATH + "/{}", id);
    try {
      service.delete(id);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to delete is not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ValidationException e) {
      HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
      logClientError(status, "Validation failed for deleting horse", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ConflictException e) {
      HttpStatus status = HttpStatus.CONFLICT;
      logClientError(status, "Conflict occurred for updating horse", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }
}
