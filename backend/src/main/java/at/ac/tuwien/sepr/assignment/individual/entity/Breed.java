package at.ac.tuwien.sepr.assignment.individual.entity;

/**
 * Represents a breed of horse in the persistent data store.
 */
public class Breed {
  private long id;
  private String name;

  /**
   * Default constructor for the Breed class.
   * No parameters are needed.
   */
  public Breed() { }

  /**
   * Retrieves the ID of the breed.
   *
   * @return The ID of the breed.
   */
  public long getId() {
    return id;
  }

  /**
   * Sets the ID of the breed.
   *
   * @param id The ID of the breed.
   * @return The updated Breed object.
   */
  public Breed setId(long id) {
    this.id = id;
    return this;
  }

  /**
   * Retrieves the name of the breed.
   *
   * @return The name of the breed.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the breed.
   *
   * @param name The name of the breed.
   * @return The updated Breed object.
   */
  public Breed setName(String name) {
    this.name = name;
    return this;
  }

}
