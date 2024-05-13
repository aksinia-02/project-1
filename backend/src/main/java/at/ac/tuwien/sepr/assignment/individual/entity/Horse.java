package at.ac.tuwien.sepr.assignment.individual.entity;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a horse in the persistent data store.
 */
public class Horse {
  private Long id;
  private String name;
  private Sex sex;
  private LocalDate dateOfBirth;
  private float height;
  private float weight;
  private Long breedId;

  /**
   * Default constructor for the Horse class.
   * No parameters are needed.
   */
  public Horse() { }

  /**
   * Retrieves the ID of the horse.
   *
   * @return The ID of the horse.
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the ID of the horse.
   *
   * @param id The ID of the horse.
   * @return The updated Horse object.
   */
  public Horse setId(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Retrieves the name of the horse.
   *
   * @return The name of the horse.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the horse.
   *
   * @param name The name of the horse.
   * @return The updated Horse object.
   */
  public Horse setName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Retrieves the sex of the horse.
   *
   * @return The sex of the horse.
   */
  public Sex getSex() {
    return sex;
  }

  /**
   * Sets the sex of the horse.
   *
   * @param sex The sex of the horse.
   * @return The updated Horse object.
   */
  public Horse setSex(Sex sex) {
    this.sex = sex;
    return this;
  }

  /**
   * Retrieves the date of birth of the horse.
   *
   * @return The date of birth of the horse.
   */
  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  /**
   * Sets the date of birth of the horse.
   *
   * @param dateOfBirth The date of birth of the horse.
   * @return The updated Horse object.
   */
  public Horse setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
    return this;
  }

  /**
   * Retrieves the height of the horse.
   *
   * @return The height of the horse.
   */
  public float getHeight() {
    return height;
  }

  /**
   * Sets the height of the horse.
   *
   * @param height The height of the horse.
   * @return The updated Horse object.
   */
  public Horse setHeight(float height) {
    this.height = height;
    return this;
  }

  /**
   * Retrieves the weight of the horse.
   *
   * @return The weight of the horse.
   */
  public float getWeight() {
    return weight;
  }

  /**
   * Sets the weight of the horse.
   *
   * @param weight The weight of the horse.
   * @return The updated Horse object.
   */
  public Horse setWeight(float weight) {
    this.weight = weight;
    return this;
  }

  /**
   * Retrieves the ID of the breed of the horse.
   *
   * @return The ID of the breed of the horse.
   */
  public Long getBreedId() {
    return breedId;
  }

  /**
   * Sets the ID of the breed of the horse.
   *
   * @param breedId The ID of the breed of the horse.
   * @return The updated Horse object.
   */
  public Horse setBreedId(Long breedId) {
    this.breedId = breedId;
    return this;
  }

  @Override
  public String toString() {
    return "Horse{"
        + "id=" + id
        + ", name='" + name + '\''
        + ", sex=" + sex
        + ", dateOfBirth=" + dateOfBirth
        + ", height=" + height
        + ", weight=" + weight
        + ", breed=" + breedId
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    Horse horse = (Horse) o;
    return Objects.equals(id, horse.id)
            && Objects.equals(name, horse.name)
            && sex == horse.sex
            && Objects.equals(dateOfBirth, horse.dateOfBirth)
            && Float.compare(height, horse.height) == 0
            && Float.compare(weight, horse.weight) == 0
            && Objects.equals(breedId, horse.breedId);
  }
}
