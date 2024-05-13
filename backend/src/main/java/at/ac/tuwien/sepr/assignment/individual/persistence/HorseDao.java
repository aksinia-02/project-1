package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import java.util.Collection;

/**
 * Data Access Object for horses.
 * Implements access functionality to the application's persistent data store regarding horses.
 */
public interface HorseDao {

  /**
   * Get the horses that match the given search parameters.
   * Parameters that are {@code null} are ignored.
   * The name is considered a match, if the given parameter is a substring of the field in horse.
   *
   * @param searchParameters the parameters to use in searching.
   * @return the horses where all given parameters match.
   */
  Collection<Horse> search(HorseSearchDto searchParameters);


  /**
   * Update the horse with the ID given in {@code horse}
   *  with the data given in {@code horse}
   *  in the persistent data store.
   *
   * @param horse the horse to update
   * @return the updated horse
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   * @throws ConflictException if there is a conflict while updating the horse
   */
  Horse update(HorseDetailDto horse) throws NotFoundException, ConflictException;

  /**
   * Get a horse by its ID from the persistent data store.
   *
   * @param id the ID of the horse to get
   * @return the horse
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   */
  Horse getById(long id) throws NotFoundException;

  /**
   * Insert a new horse into the persistent data store.
   *
   * @param horse the horse to insert
   * @return the inserted horse
   * @throws ConflictException if there is a conflict while inserting the horse
   */
  Horse insert(HorseDetailDto horse) throws ConflictException;

  /**
   * Delete the horse with the specified ID from the persistent data store.
   *
   * @param id the ID of the horse to delete
   * @throws NotFoundException if the horse with the given ID does not exist in the persistent data store
   * @throws ConflictException if horse is a participant in tournament.
   */
  void delete(long id) throws NotFoundException, ConflictException;
}
