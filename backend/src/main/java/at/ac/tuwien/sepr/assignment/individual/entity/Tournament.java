package at.ac.tuwien.sepr.assignment.individual.entity;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a tournament in the persistent data store
 */
public class Tournament {
  private Long id;
  private String name;
  private LocalDate startDate;
  private LocalDate endDate;
  private Participant[] participants;

  /**
   * Default constructor for the Tournament class.
   * No parameters are needed.
   */
  public Tournament() { }

  /**
   * Retrieves the ID of the tournament.
   *
   * @return The ID of the tournament.
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the ID of the tournament.
   *
   * @param id The ID of the tournament.
   * @return The updated Tournament object.
   */
  public Tournament setId(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Retrieves the name of the tournament.
   *
   * @return The name of the tournament.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the tournament.
   *
   * @param name The name of the tournament.
   * @return The updated Tournament object.
   */
  public Tournament setName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Retrieves the start date of the tournament.
   *
   * @return The start date of the tournament.
   */
  public LocalDate getStartDate() {
    return startDate;
  }

  /**
   * Sets the start date of the tournament.
   *
   * @param startDate The start date of the tournament.
   * @return The updated Tournament object.
   */
  public Tournament setStartDate(LocalDate startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * Retrieves the end date of the tournament.
   *
   * @return The end date of the tournament.
   */
  public LocalDate getEndDate() {
    return endDate;
  }

  /**
   * Sets the end date of the tournament.
   *
   * @param endDate The end date of the tournament.
   * @return The updated Tournament object.
   */
  public Tournament setEndDate(LocalDate endDate) {
    this.endDate = endDate;
    return this;
  }

  /**
   * Retrieves the participants of the tournament.
   *
   * @return The participants of the tournament.
   */
  public Participant[] getParticipants() {
    return participants;
  }

  /**
   * Sets the participants of the tournament.
   *
   * @param participants The participants of the tournament.
   * @return The updated Tournament object.
   */
  public Tournament setParticipants(Participant[] participants) {
    this.participants = participants;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    Tournament tournament = (Tournament) o;
    boolean equalParticipants = true;
    if (this.participants != null) {
      for (int i = 0; i < participants.length; i++) {
        if (!participants[i].equals(tournament.participants[i])) {
          equalParticipants = false;
        }
      }
    }
    return Objects.equals(id, tournament.id)
            && Objects.equals(name, tournament.name)
            && Objects.equals(startDate, tournament.startDate)
            && Objects.equals(endDate, tournament.endDate)
            && equalParticipants;
  }
}
