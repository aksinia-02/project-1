package at.ac.tuwien.sepr.assignment.individual.mapper;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Breed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

/**
 * Mapper class responsible for converting between Breed entities and DTOs
 */
@Component
public class BreedMapper {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * Default constructor for the BreedMapper class.
   * No parameters are needed.
   */
  public BreedMapper() { }

  /**
   * Maps a Breed entity to a BreedDto object.
   *
   * @param breed The Breed entity to be mapped.
   * @return The corresponding BreedDto object.
   */
  public BreedDto entityToDto(Breed breed) {
    LOG.trace("Breed({}) to BreedDto", breed);
    return new BreedDto(breed.getId(), breed.getName());
  }
}
