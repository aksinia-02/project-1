package at.ac.tuwien.sepr.assignment.individual;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test class for ensuring the application context loads successfully.
 */
@ActiveProfiles("test") // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
class SeprIndividualAssignmentApplicationTests {

  @Test
  void contextLoads() {
  }

}
