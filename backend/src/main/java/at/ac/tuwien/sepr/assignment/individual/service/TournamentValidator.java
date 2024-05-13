package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseSelectionDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Component for validating tournament-related data
 */
@Component
public class TournamentValidator {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * Default constructor for the TournamentValidator class.
   * No parameters are needed.
   */
  public TournamentValidator() { }

  /**
   * Validates the provided ID.
   *
   * @param id The ID to validate.
   * @throws ValidationException If the ID is invalid.
   */
  public void validateId(Long id) throws ValidationException {
    LOG.trace("validateForIs({})", id);
    List<String> validationErrors = new ArrayList<>();

    if (id == null) {
      validationErrors.add("id is invalid");
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of id is failed", validationErrors);
    }
  }

  /**
   * Validates the provided tournament data for insertion.
   *
   * @param tournament the tournament data to be validated
   * @throws ValidationException if the provided data is invalid
   */
  public void validateForInsert(TournamentCreateDto tournament) throws ValidationException {
    LOG.trace("validateForInsert({})", tournament);

    List<String> validationErrors = new ArrayList<>(validation(tournament));

    validationErrors.addAll(validateParticipantsToInsert(tournament));

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of tournament for insert failed", validationErrors);
    }
  }

  /**
   * Performs validation checks on the tournament data.
   *
   * @param tournament the tournament data to be validated
   * @return a list of validation errors (empty if no errors)
   */
  private List<String> validation(TournamentCreateDto tournament) {
    LOG.trace("validate({})", tournament);
    List<String> errors = new ArrayList<>();

    if (tournament.name().length() > 255) {
      errors.add("name is too long");
    }

    if (tournament.startDate().compareTo(tournament.endDate()) > 0) {
      errors.add("end date is bevor start date");
    }

    if (tournament.participants().length != 8) {
      errors.add("tournament must have 8 participants");
    }

    if (tournament.startDate().compareTo(LocalDate.of(1894, 1, 1)) < 0) {
      errors.add("start date comes bevor first valid date 1 January 1894");
    }

    return errors;
  }

  /**
   * Validates an array of tournament participants.
   *
   * @param participants The array of tournament participants to validate.
   * @throws ValidationException If any participant's roundReached is not within the valid range (1-4).
   */
  public void validateParticipants(TournamentDetailParticipantDto[] participants) throws ValidationException {
    LOG.trace("validateParticipants({})", Arrays.toString(participants));
    List<String> errors = new ArrayList<>();

    int[] count = new int[5];
    for (TournamentDetailParticipantDto participant : participants) {
      int roundReached = participant.roundReached();
      if (roundReached > 4 || roundReached < 0) {
        errors.add("roundReached: " + participant.roundReached() + " for participant with id: " + participant.id() + " is not valid");
      } else {
        count[roundReached]++;
      }
    }

    errors.addAll(validateForRounds(count));

    if (!errors.isEmpty()) {
      throw new ValidationException("Validation of participants for update failed", errors);
    }
  }

  /**
   * Validates the number of participants staying for each round of the tournament.
   *
   * @param count An array representing the number of participants staying for each round.
   * @return A list of errors encountered during validation.
   */
  private List<String> validateForRounds(int[] count) {

    LOG.trace("validate({})", Arrays.toString(count));

    List<String> errors = new ArrayList<>();

    if (count[4] > 1) {
      errors.add("invalid number of horses, which stay for round 4");
    }
    if (count[3] > 2) {
      errors.add("invalid number of horses, which stay for round 3");
    }
    if (count[2] > 4) {
      errors.add("invalid number of horses, which stay for round 2");
    }
    if (count[1] > 8) {
      errors.add("invalid number of horses, which stay for round 1");
    }
    if (count[0] > 8) {
      errors.add("invalid number of horses, which stay for round 0");
    }
    return errors;
  }

  /**
   * Validates the eligibility of participants to be inserted into the tournament.
   *
   * @param tournament The tournament for which participants are to be validated.
   * @return A list of errors encountered during validation.
   */
  private List<String> validateParticipantsToInsert(TournamentCreateDto tournament) {

    HorseSelectionDto[] participants = tournament.participants();
    LOG.trace("validateParticipantsToInsert({})", Arrays.toString(participants));

    List<String> errors = new ArrayList<>();

    for (HorseSelectionDto participant : participants) {
      if (participant.dateOfBirth().compareTo(LocalDate.of(1894, 1, 1)) < 0) {
        errors.add("date of birth comes bevor first valid date 1 January 1894");
      }
      if ((participant.dateOfBirth().getYear() + 25) < tournament.startDate().getYear()
              || (participant.dateOfBirth().getYear() == tournament.startDate().getYear()
              && participant.dateOfBirth().getDayOfYear() > tournament.startDate().getDayOfYear())) {
        errors.add("Horse with date of birth: " + participant.dateOfBirth() + " ist too old for the tournament with start: " + tournament.startDate());
      }
      if ((participant.dateOfBirth().getYear() + 3) > tournament.startDate().getYear()
              || (participant.dateOfBirth().getYear() + 3 == tournament.startDate().getYear()
              && participant.dateOfBirth().getDayOfYear() > tournament.startDate().getDayOfYear())) {
        errors.add("Horse with date of birth: " + participant.dateOfBirth() + " is too young for the tournament with start: "
                + tournament.startDate() + ". It must be at least 3 years between");
      }
    }

    return errors;
  }
}
