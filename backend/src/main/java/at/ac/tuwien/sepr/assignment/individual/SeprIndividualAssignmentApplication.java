package at.ac.tuwien.sepr.assignment.individual;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the individual assignment application.
 * This class initializes and runs the Spring Boot application.
 */
@SpringBootApplication
public class SeprIndividualAssignmentApplication {

  /**
   * Default constructor for the SeprIndividualAssignmentApplication class.
   * No parameters are needed.
   */
  public SeprIndividualAssignmentApplication() { }

  /**
   * Main entry point for the individual assignment application.
   * Initializes and runs the Spring Boot application.
   *
   * @param args The command-line arguments passed to the application.
   */
  public static void main(String[] args) {
    SpringApplication.run(SeprIndividualAssignmentApplication.class, args);
  }

}
