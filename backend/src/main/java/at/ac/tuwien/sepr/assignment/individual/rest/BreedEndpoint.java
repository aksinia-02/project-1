package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.BreedSearchDto;
import at.ac.tuwien.sepr.assignment.individual.service.BreedService;
import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling HTTP requests related to breed
 */
@RestController
@RequestMapping(path = BreedEndpoint.BASE_PATH)
public class BreedEndpoint {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  /**
   * Base path for the breeds endpoint.
   * This constant represents the base path used for handling breed-related requests in the REST API.
   */
  public static final String BASE_PATH = "/breeds";

  private final BreedService service;

  /**
   * Constructs a new BreedEndpoint with the specified BreedService.
   *
   * @param service the BreedService used by the BreedEndpoint
   */
  public BreedEndpoint(BreedService service) {
    this.service = service;
  }

  /**
   * Searches for breeds based on the provided search parameters.
   *
   * @param searchParams The parameters used for searching breed.
   * @return A stream of BreedDto objects representing the search results.
   */
  @GetMapping
  public Stream<BreedDto> search(BreedSearchDto searchParams) {
    LOG.info("GET " + BASE_PATH);
    LOG.debug("Request Params: {}", searchParams);
    return service.search(searchParams);
  }
}
