package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Component responsible for validating horse details.
 */
@Component
public class HorseValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * Default constructor for the HorseValidator class.
   * No parameters are needed.
   */
  public HorseValidator() { }

  /**
   * Validates the horse details before updating.
   *
   * @param horse The horse details to validate.
   * @throws ValidationException If validation fails.
   */
  public void validateForUpdate(HorseDetailDto horse) throws ValidationException {
    LOG.trace("validateForUpdate({})", horse);
    List<String> validationErrors = new ArrayList<>();

    validationErrors.addAll(validateId(horse.id()));
    validationErrors.addAll(validation(horse));

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for update failed", validationErrors);
    }
  }

  /**
   * Validates the horse details before insertion.
   *
   * @param horse The horse details to validate.
   * @throws ValidationException If validation fails.
   */
  public void validateForInsert(HorseDetailDto horse) throws ValidationException {
    LOG.trace("validateForInsert({})", horse);

    List<String> validationErrors = validation(horse);

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for insert failed", validationErrors);
    }
  }

  /**
   * Validates the input ID before deletion.
   *
   * @param id The ID of the horse to be deleted.
   * @throws ValidationException if validation errors occur.
   */
  public void validateForDelete(Long id) throws ValidationException {

    List<String> validationErrors = validateId(id);

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for delete failed", validationErrors);
    }
  }

  /**
   * Validates the provided ID.
   *
   * @param id The ID to validate.
   * @return A list of validation errors, if any.
   */
  private List<String> validateId(Long id) {
    LOG.trace("validateFor({})", id);
    List<String> validationErrors = new ArrayList<>();

    if (id == null) {
      validationErrors.add("No ID given");
    }

    return validationErrors;
  }

  /**
   * Performs validation checks on the horse details.
   *
   * @param horse The horse details to validate.
   * @return A list of validation errors, if any.
   */
  private List<String> validation(HorseDetailDto horse) {
    List<String> validationErrors = new ArrayList<>();

    if (horse.name() == null) {
      validationErrors.add("name is not given");
    } else if (horse.name().length() > 255) {
      validationErrors.add("name is too long");
    }

    if (horse.height() < 0.5) {
      validationErrors.add("horse is too small");
    }

    if (horse.height() > 2.1) {
      validationErrors.add("horse is too big");
    }

    if (horse.weight() > 900) {
      validationErrors.add("horse is too heavy");
    }

    if (horse.weight() < 10) {
      validationErrors.add("horse is too light");
    }

    if (horse.dateOfBirth() == null) {
      validationErrors.add("date of birth is not given");
    } else {
      if (horse.dateOfBirth().compareTo(LocalDate.of(1894, 1, 1)) < 0) {
        validationErrors.add("date of birth comes bevor first valid date 1 January 1894");
      }
      if (LocalDateTime.now().compareTo(horse.dateOfBirth().atStartOfDay()) < 0) {
        validationErrors.add("date of birth is in future");
      }
    }

    if (horse.sex() == null) {
      validationErrors.add("sex is not given");
    }

    return validationErrors;
  }

}
