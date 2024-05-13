package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Breed;
import java.util.Collection;
import java.util.Set;

/**
 * Interface for accessing and managing breed data in the persistence layer.
 */
public interface BreedDao {

  /**
   * Retrieves all breeds from the database.
   *
   * @return A collection containing all breeds.
   */
  Collection<Breed> allBreeds();

  /**
   * Finds breeds by their IDs.
   *
   * @param breedIds The set of breed IDs to search for.
   * @return A collection containing the breeds found.
   */
  Collection<Breed> findBreedsById(Set<Long> breedIds);

  /**
   * Searches for breeds based on the provided search parameters.
   *
   * @param searchParams The parameters used for searching breeds.
   * @return A collection containing the breeds found based on the search criteria.
   */
  Collection<Breed> search(BreedSearchDto searchParams);
}
