package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.service.HorseService;
import at.ac.tuwien.sepr.assignment.individual.service.TournamentService;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the {@link TournamentEndpoint} class.
 */
@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
@EnableWebMvc
@WebAppConfiguration
public class TournamentEndpointTest extends TestBase {

  @Autowired
  private WebApplicationContext webAppContext;
  private MockMvc mockMvc;
  private TournamentService tournamentService;

  @Autowired
  ObjectMapper objectMapper;

  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
  }

  @Test
  public void gettingNonexistentUrlReturns404() throws Exception {
    mockMvc
       .perform(MockMvcRequestBuilders
           .get("/asdf123")
       ).andExpect(status().isNotFound());
  }

  @Test
  public void gettingAllTournaments() throws Exception {
    byte[] body = mockMvc
        .perform(MockMvcRequestBuilders
           .get("/tournaments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

    List<TournamentListDto> tournamentResult = objectMapper.readerFor(TournamentListDto.class)
            .<TournamentListDto>readValues(body).readAll();

    assertThat(tournamentResult).isNotNull();
    assertThat(tournamentResult)
        .hasSize(5)
        .extracting(TournamentListDto::id, TournamentListDto::name, TournamentListDto::startDate, TournamentListDto::endDate)
        .contains(
           tuple(-1L, "tournament1", LocalDate.of(2023, 12, 12), LocalDate.of(2023, 12, 13)));
  }


  private String stringAsJson(final Object o) {
    try {
      return objectMapper.writeValueAsString(o);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
