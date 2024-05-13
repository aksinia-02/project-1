package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.entity.Participant;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class for the {@link TournamentDao} class.
 */
@ActiveProfiles({"test", "datagen"})
// enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class TournamentDaoTest extends TestBase {

  @Autowired
  TournamentDao tournamentDao;

  @Test
  public void testGetTournamentWithValidId() throws NotFoundException {
    long id = -1L;
    var tournament = tournamentDao.getTournamentById(id);
    assertNotNull(tournament);
    assertThat(tournament)
            .isEqualTo(new Tournament()
            .setId(id)
            .setName("tournament1")
            .setStartDate(LocalDate.of(2023, 12, 12))
            .setEndDate(LocalDate.of(2023, 12, 13)));

  }

  @Test
  public void testGetParticipantsByTournamentIdWithValidId() throws NotFoundException {
    long id = -1L;
    var participants = tournamentDao.getParticipantsByTournamentId(id);
    assertNotNull(participants);
    assertThat(participants)
        .hasSize(8)
        .containsExactlyInAnyOrder(
            new Participant(-1L, -1L, "Wendy", LocalDate.of(2019, 8, 5), -1, 3),
            new Participant(-2L, -2L, "Hugo", LocalDate.of(2020, 2, 20), -1, 1),
            new Participant(-3L, -3L, "Bella", LocalDate.of(2005, 4, 8), -1, 2),
            new Participant(-4L, -4L, "Thunder", LocalDate.of(2008, 7, 15), -1, 1),
            new Participant(-5L, -5L, "Luna", LocalDate.of(2012, 11, 22), -1, 4),
            new Participant(-6L, -6L, "Apollo", LocalDate.of(2003, 9, 3), -1, 1),
            new Participant(-7L, -22L, "Rocky", LocalDate.of(2007, 4, 12), -1, 2),
            new Participant(-8L, -8L, "Max", LocalDate.of(2006, 3, 27), -1, 1)
        );

  }
}
