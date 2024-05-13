package at.ac.tuwien.sepr.assignment.individual.exception;

/**
 * Exception that signals, that whatever resource,
 * that has been tried to access,
 * was not found.
 */
public class NotFoundException extends Exception {

  /**
   * Constructs a new NotFoundException with the specified detail message.
   *
   * @param message the detail message.
   */
  public NotFoundException(String message) {
    super(message);
  }

  /**
   * Constructs a new NotFoundException with the specified cause.
   *
   * @param cause the cause.
   */
  public NotFoundException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new NotFoundException with the specified detail message and cause.
   *
   * @param message the detail message.
   * @param cause the cause.
   */
  public NotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
