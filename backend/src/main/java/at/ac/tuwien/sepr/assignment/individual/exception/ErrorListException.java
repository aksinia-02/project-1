package at.ac.tuwien.sepr.assignment.individual.exception;

import java.util.Collections;
import java.util.List;

/**
 * Common super class for exceptions that report a list of errors
 * back to the user, when the given data did not pass a certain kind of checks.
 */
public abstract class ErrorListException extends Exception {

  /**
   * List of error messages detailing specific issues.
   */
  private final List<String> errors;

  /**
   * A summary of the overall issue or reason for the exception.
   */
  private final String messageSummary;

  /**
   * A descriptor for the error list, describing the nature of the errors.
   */
  private final String errorListDescriptor;

  /**
   * Constructs a new ErrorListException with the specified error list descriptor,
   * message summary, and list of errors.
   *
   * @param errorListDescriptor A descriptor for the error list, describing the nature of the errors.
   * @param messageSummary     A summary of the overall issue or reason for the exception.
   * @param errors             A list of individual error messages detailing specific issues.
   */
  public ErrorListException(String errorListDescriptor, String messageSummary, List<String> errors) {
    super(messageSummary);
    this.errorListDescriptor = errorListDescriptor;
    this.messageSummary = messageSummary;
    this.errors = errors;
  }

  /**
   * See {@link Throwable#getMessage()} for general information about this method.
   *
   * <p>Note: this implementation produces the message
   * from the {@link #summary} and the list of {@link #errors}
   */
  @Override
  public String getMessage() {
    return "%s. %s: %s."
        .formatted(messageSummary, errorListDescriptor, String.join(", ", errors));
  }

  /**
   * Retrieves the summary of the overall issue or reason for the exception.
   *
   * @return The summary message.
   */
  public String summary() {
    return messageSummary;
  }

  /**
   * Retrieves the list of individual error messages detailing specific issues.
   *
   * @return The list of errors.
   */
  public List<String> errors() {
    return Collections.unmodifiableList(errors);
  }
}
