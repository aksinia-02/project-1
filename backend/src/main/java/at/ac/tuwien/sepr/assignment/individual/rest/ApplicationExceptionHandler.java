package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * This class is responsible for handling ValidationException and returning appropriate HTTP responses.
 */
@RestControllerAdvice
public class ApplicationExceptionHandler {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * Default constructor for the ApplicationExceptionHandler class.
   * No parameters are needed.
   */
  public ApplicationExceptionHandler() { }

  /**
   * Handles ValidationException and returns an appropriate HTTP response with status 422 (UNPROCESSABLE_ENTITY).
   * It logs a warning message containing the exception details.
   *
   * @param e the ValidationException to handle
   * @return a ValidationErrorRestDto containing the summary and errors of the ValidationException
   */
  @ExceptionHandler
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  @ResponseBody
  public ValidationErrorRestDto handleValidationException(ValidationException e) {
    LOG.warn("Terminating request processing with status 422 due to {}: {}", e.getClass().getSimpleName(), e.getMessage());
    return new ValidationErrorRestDto(e.summary(), e.errors());
  }
}
