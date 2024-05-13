package at.ac.tuwien.sepr.assignment.individual.entity;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a participant of tournament in the persistent data store.
 */
public class Participant {
  private Long id;
  private Long horseId;
  private String name;
  private LocalDate dateOfBirth;
  private int entryNumber;
  private int roundReached;
  private int points;

  /**
   * Default constructor for the Participant class.
   * No parameters are needed.
   */
  public Participant() { }

  /**
   * Constructor for creating a Participant object with the specified attributes.
   *
   * @param id            The unique identifier of the participant.
   * @param horseId       The unique identifier of the horse associated with the participant.
   * @param name          The name of the participant.
   * @param dateOfBirth   The date of birth of the participant.
   * @param entryNumber   The entry number of the participant in the tournament.
   * @param roundReached  The round reached by the participant in the tournament.
   */
  public Participant(Long id, Long horseId, String name, LocalDate dateOfBirth, int entryNumber, int roundReached) {
    this.id = id;
    this.horseId = horseId;
    this.name = name;
    this.dateOfBirth = dateOfBirth;
    this.entryNumber = entryNumber;
    this.roundReached = roundReached;
  }

  /**
   * Retrieves the ID of the participant.
   *
   * @return The ID of the participant.
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the ID of the participant.
   *
   * @param id The ID of the participant.
   * @return The updated Participant object.
   */
  public Participant setId(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Retrieves the ID of the horse associated with the participant.
   *
   * @return The ID of the horse associated with the participant.
   */
  public Long getHorseId() {
    return horseId;
  }

  /**
   * Sets the ID of the horse associated with the participant.
   *
   * @param id The ID of the horse associated with the participant.
   * @return The updated Participant object.
   */
  public Participant setHorseId(Long id) {
    this.horseId = id;
    return this;
  }

  /**
   * Retrieves the name of the participant.
   *
   * @return The name of the participant.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the participant.
   *
   * @param name The name of the participant.
   * @return The updated Participant object.
   */
  public Participant setName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Retrieves the date of birth of the participant.
   *
   * @return The date of birth of the participant.
   */
  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  /**
   * Sets the date of birth of the participant.
   *
   * @param dateOfBirth The date of birth of the participant.
   * @return The updated Participant object.
   */
  public Participant setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
    return this;
  }

  /**
   * Retrieves the entry number of the participant.
   *
   * @return The entry number of the participant.
   */
  public Integer getEntryNumber() {
    return entryNumber;
  }

  /**
   * Sets the entry number of the participant.
   *
   * @param entryNumber The entry number of the participant.
   * @return The updated Participant object.
   */
  public Participant setEntryNumber(Integer entryNumber) {
    this.entryNumber = entryNumber;
    return this;
  }

  /**
   * Retrieves the round reached by the participant.
   *
   * @return The round reached by the participant.
   */
  public int getRoundReached() {
    return roundReached;
  }

  /**
   * Sets the round reached by the participant.
   *
   * @param roundReached The round reached by the participant.
   * @return The updated Participant object.
   */
  public Participant setRoundReached(int roundReached) {
    this.roundReached = roundReached;
    return this;
  }

  /**
   * Retrieves the points earned by the participant.
   *
   * @return The points earned by the participant.
   */
  public int getPoints() {
    return points;
  }

  /**
   * Sets the points earned by the participant.
   *
   * @param points The points earned by the participant.
   * @return The updated Participant object.
   */
  public Participant setPoints(int points) {
    this.points = points;
    return this;
  }

  /**
   * Adds points to the total points earned by the participant.
   *
   * @param points The points to be added.
   */
  public void addPoints(int points) {
    this.points += points;
  }

  @Override
  public String toString() {
    return "name: " + this.name + " points: " + this.points;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    Participant participant = (Participant) o;
    return Objects.equals(id, participant.id)
            && Objects.equals(horseId, participant.horseId)
            && Objects.equals(name, participant.name)
            && Objects.equals(dateOfBirth, participant.dateOfBirth)
            && entryNumber == participant.entryNumber
            && roundReached == participant.roundReached;
  }
}
