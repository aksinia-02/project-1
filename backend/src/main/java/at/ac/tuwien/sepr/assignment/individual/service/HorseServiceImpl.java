package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.HorseMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementation of service interface for working with horses.
 */
@Service
public class HorseServiceImpl implements HorseService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseDao dao;
  private final HorseMapper mapper;
  private final HorseValidator validator;
  private final BreedService breedService;

  /**
   * Constructor for the HorseServiceImpl class.
   * Initializes the service with the provided dependencies: dao, mapper, validator, and breedService.
   *
   * @param dao          The data access object for horses.
   * @param mapper       The mapper for converting between DTOs and entity objects.
   * @param validator    The validator for validating horse details.
   * @param breedService The service for managing horse breeds.
   */
  public HorseServiceImpl(HorseDao dao, HorseMapper mapper, HorseValidator validator, BreedService breedService) {
    this.dao = dao;
    this.mapper = mapper;
    this.validator = validator;
    this.breedService = breedService;
  }

  @Override
  public Stream<HorseListDto> search(HorseSearchDto searchParameters) {
    LOG.trace("search({})", searchParameters);
    var horses = dao.search(searchParameters);
    // First get all breed ids…
    var breeds = horses.stream()
        .map(Horse::getBreedId)
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableSet());
    // … then get the breeds all at once.
    var breedsPerId = breedMapForHorses(breeds);

    return horses.stream()
        .map(horse -> mapper.entityToListDto(horse, breedsPerId));
  }


  @Override
  public HorseDetailDto update(HorseDetailDto horse) throws NotFoundException, ValidationException, ConflictException {
    LOG.trace("update({})", horse);
    validator.validateForUpdate(horse);
    var updatedHorse = dao.update(horse);
    var breeds = breedMapForSingleHorse(updatedHorse);
    return mapper.entityToDetailDto(updatedHorse, breeds);
  }


  @Override
  public HorseDetailDto getById(long id) throws NotFoundException {
    LOG.trace("get({})", id);
    Horse horse = dao.getById(id);
    var breeds = breedMapForSingleHorse(horse);
    return mapper.entityToDetailDto(horse, breeds);
  }

  @Override
  public HorseDetailDto insert(HorseDetailDto horse) throws ConflictException, ValidationException {
    LOG.trace("insert({})", horse);
    validator.validateForInsert(horse);
    var insertedHorse = dao.insert(horse);
    var breeds = breedMapForSingleHorse(insertedHorse);
    return mapper.entityToDetailDto(insertedHorse, breeds);
  }

  @Override
  public void delete(long id) throws NotFoundException, ValidationException, ConflictException {
    LOG.trace("delete({})", id);
    validator.validateForDelete(id);
    dao.delete(id);
  }

  /**
   * Maps breed IDs to their corresponding BreedDto objects for a single horse.
   *
   * @param horse The horse for which to map breed IDs.
   * @return A map of breed IDs to BreedDto objects.
   */
  private Map<Long, BreedDto> breedMapForSingleHorse(Horse horse) {
    LOG.trace("breedMapForSingleHorse({})", horse);
    return breedMapForHorses(Collections.singleton(horse.getBreedId()));
  }

  /**
   * Maps breed IDs to their corresponding BreedDto objects for a set of horses.
   *
   * @param horse The set of horses for which to map breed IDs.
   * @return A map of breed IDs to BreedDto objects.
   */
  private Map<Long, BreedDto> breedMapForHorses(Set<Long> horse) {
    LOG.trace("breedMapForHorses({})", horse);
    return breedService.findBreedsByIds(horse)
        .collect(Collectors.toUnmodifiableMap(BreedDto::id, Function.identity()));
  }
}
