package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseSelectionDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participant;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Repository class implementing TournamentDao interface for JDBC operations
 */
@Repository
public class TournamentJdbcDao implements TournamentDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String TABLE_NAME_TOURNAMENT = "tournament";
  private static final String TABLE_NAME_PARTICIPANT = "participant";
  private static final String TABLE_NAME_HORSE = "horse";

  private static final String SQL_INSERT_TOURNAMENT = "INSERT INTO " + TABLE_NAME_TOURNAMENT + "(name, start_date, end_date)"
          + " VALUES (?, ?, ?)";
  private static final String SQL_INSERT_PARTICIPANT = "INSERT INTO " + TABLE_NAME_PARTICIPANT + "(id_horse, tournament_id,"
          + " entry_number, round_reached)"
          + " VALUES (?, ?, ?, ?)";

  private static final String SQL_EQUAL = "SELECT * FROM " + TABLE_NAME_TOURNAMENT + " WHERE name = ? "
          + "AND start_date = ? AND end_date = ?";
  private static final String SQL_SELECT_BY_ID_TOURNAMENT = "SELECT * FROM " + TABLE_NAME_TOURNAMENT + " WHERE id = ?";
  private static final String SQL_SELECT_BY_ID_PARTICIPANT = "SELECT p.id as \"id\", p.id_horse as \"id_horse\", p.tournament_id as \"tournament_id\", "
          + "h.name as \"horse_name\", h.date_of_birth as \"date_of_birth\", p.entry_number as \"entry_number\", p.round_reached as \"round_reached\""
          + " FROM " + TABLE_NAME_PARTICIPANT + " p JOIN "
          + TABLE_NAME_HORSE + " h ON (p.id_horse = h.id) "
          + "WHERE tournament_id = ?";

  private static final String SQL_SELECT_SEARCH = "SELECT "
          + "id, name, start_date, end_date FROM " + TABLE_NAME_TOURNAMENT
          + " WHERE (:name IS NULL OR UPPER(name) LIKE UPPER('%'||:name||'%'))"
          + "  AND (:startDate IS NULL OR :startDate <= end_date)"
          + "  AND (:endDate IS NULL OR :endDate >= start_date)"
          + " ORDER BY start_date DESC";

  private static final String SQL_FOR_FIRST_ROUND = "SELECT p.id as \"id\", p.id_horse as \"id_horse\", p.tournament_id as \"tournament_id\", "
          + "h.name as \"horse_name\", h.date_of_birth as \"date_of_birth\", p.entry_number as \"entry_number\", p.round_reached as \"round_reached\" "
          + "FROM " + TABLE_NAME_PARTICIPANT + " p "
          + "JOIN " + TABLE_NAME_TOURNAMENT + " t ON (p.tournament_id = t.id) "
          + "JOIN " + TABLE_NAME_HORSE + " h ON (p.id_horse = h.id) "
          + "WHERE p.id_horse = ? "
          + "AND TIMESTAMPDIFF(MONTH, ?, t.end_date) < 12";

  private static final String SQL_UPDATE_PARTICIPANT = "UPDATE " + TABLE_NAME_PARTICIPANT
          + " SET round_reached = ?"
          + ", entry_number = ?"
          + " WHERE id = ?";

  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNamed;

  /**
   * Constructs a new TournamentJdbcDao with the specified JDBC named template and JDBC template.
   *
   * @param jdbcNamed   The JDBC named template used for named parameter JDBC operations.
   * @param jdbcTemplate The JDBC template used for performing basic JDBC operations.
   */
  public TournamentJdbcDao(
          NamedParameterJdbcTemplate jdbcNamed,
          JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcNamed = jdbcNamed;
  }

  @Override
  public Collection<Tournament> search(TournamentSearchDto searchParameters) {
    LOG.trace("search({})", searchParameters);
    var query = SQL_SELECT_SEARCH;
    var params = new BeanPropertySqlParameterSource(searchParameters);
    return jdbcNamed.query(query, params, this::mapRowTournament);
  }

  @Override
  public Participant[] getParticipantsByTournamentId(long id) throws NotFoundException {
    LOG.trace("get participants by tournament id({})", id);
    List<Participant> participants;
    participants = jdbcTemplate.query(SQL_SELECT_BY_ID_PARTICIPANT, this::mapRowParticipant, id);

    if (participants.isEmpty()) {
      throw new NotFoundException("No participant with ID %d found".formatted(id));
    }
    return participants.toArray(new Participant[0]);
  }

  @Override
  public Tournament getTournamentById(long id) throws NotFoundException {
    LOG.trace("get tournament by id({})", id);
    List<Tournament> tournaments;
    tournaments = jdbcTemplate.query(SQL_SELECT_BY_ID_TOURNAMENT, this::mapRowTournament, id);

    if (tournaments.isEmpty()) {
      throw new NotFoundException("No tournament with ID %d found".formatted(id));
    }
    if (tournaments.size() > 1) {
      // This should never happen!!
      throw new FatalException("Too many tournaments with ID %d found".formatted(id));
    }
    return tournaments.get(0);
  }

  @Override
  public Participant[] updateParticipants(TournamentDetailParticipantDto[] participants) throws NotFoundException {
    LOG.trace("update({})", Arrays.toString(participants));
    Participant[] result = new Participant[participants.length];
    for (int i = 0; i < participants.length; i++) {
      int updated = jdbcTemplate.update(SQL_UPDATE_PARTICIPANT,
              participants[i].roundReached(),
              participants[i].entryNumber(),
              participants[i].id());
      if (updated == 0) {
        throw new NotFoundException("Could not update participant with ID " + participants[i].id() + ", because it does not exist");
      }
      result[i] = new Participant()
              .setId(participants[i].id())
              .setHorseId(participants[i].horseId())
              .setName(participants[i].name())
              .setDateOfBirth(participants[i].dateOfBirth())
              .setEntryNumber(participants[i].entryNumber())
              .setRoundReached(participants[i].roundReached());
    }
    return result;
  }

  @Override
  public Tournament insert(TournamentCreateDto tournament) throws ConflictException {
    LOG.trace("insert({})", tournament);
    if (isEqualExist(tournament)) {
      List<String> conflictErrors = new ArrayList<>();
      conflictErrors.add("The same tournament was found.");
      throw new ConflictException("Conflict was detected", conflictErrors);
    }
    KeyHolder keyHolder = new GeneratedKeyHolder();
    int insertedTournament = jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(SQL_INSERT_TOURNAMENT, Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, tournament.name());
      ps.setDate(2, Date.valueOf(tournament.startDate()));
      ps.setDate(3, Date.valueOf(tournament.endDate()));
      return ps;
    }, keyHolder);
    if (insertedTournament > 0) {
      long generatedID = keyHolder.getKey().intValue();
      return new Tournament()
              .setId(generatedID)
              .setName(tournament.name())
              .setStartDate(tournament.startDate())
              .setEndDate(tournament.endDate())
              .setParticipants(insertParticipants(tournament.participants(), generatedID));
    } else {
      throw new IllegalStateException("No rows affected after insertion of tournament.");
    }
  }

  private Participant[] insertParticipants(HorseSelectionDto[] participants, long id) {
    LOG.trace("insert({})", Arrays.toString(participants));
    List<Participant> insertedParticipants = new ArrayList<>();
    for (HorseSelectionDto participant : participants) {
      KeyHolder keyHolder = new GeneratedKeyHolder();
      int inserted = jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement(SQL_INSERT_PARTICIPANT, Statement.RETURN_GENERATED_KEYS);
        ps.setLong(1, participant.id());
        ps.setLong(2, id);
        ps.setLong(3, -1);
        ps.setLong(4, 0);
        return ps;
      }, keyHolder);
      if (inserted > 0) {
        Long participantId = keyHolder.getKey().longValue();
        Participant insertedParticipant = new Participant()
                .setId(participantId)
                .setHorseId(participant.id())
                .setName(participant.name())
                .setDateOfBirth(participant.dateOfBirth())
                .setEntryNumber(0)
                .setRoundReached(0);
        insertedParticipants.add(insertedParticipant);
      } else {
        throw new IllegalStateException("No rows affected after insertion of participants.");
      }
    }
    return insertedParticipants.toArray(new Participant[0]);
  }

  /**
   * Maps a row from the ResultSet to a Tournament object.
   *
   * @param result the ResultSet containing the data
   * @param rownum the row number
   * @return a Tournament object mapped from the ResultSet
   * @throws SQLException if a database access error occurs
   */
  private Tournament mapRowTournament(ResultSet result, int rownum) throws SQLException {
    LOG.trace("mapRowTournament({})", result);
    return new Tournament()
            .setId(result.getLong("id"))
            .setName(result.getString("name"))
            .setStartDate(result.getDate("start_date").toLocalDate())
            .setEndDate(result.getDate("end_date").toLocalDate())
            ;
  }

  /**
   * Maps a row from the ResultSet to a Participant object.
   *
   * @param result the ResultSet containing the data
   * @param rownum the row number
   * @return a Participant object mapped from the ResultSet
   * @throws SQLException if a database access error occurs
   */
  private Participant mapRowParticipant(ResultSet result, int rownum) throws SQLException {
    LOG.trace("mapRowParticipant({})", result);
    return new Participant()
            .setId(result.getLong("id"))
            .setHorseId(result.getLong("id_horse"))
            .setName(result.getString("name"))
            .setDateOfBirth(result.getDate("date_of_birth").toLocalDate())
            .setEntryNumber(result.getInt("entry_number"))
            .setRoundReached(result.getInt("round_reached"))
            ;
  }

  /**
   * Checks if a tournament with the same name, start date, and end date already exists.
   *
   * @param tournament the TournamentCreateDto representing the tournament to check
   * @return true if a tournament with the same details exists, false otherwise
   */
  private boolean isEqualExist(TournamentCreateDto tournament) {
    LOG.trace("isEqualExist({})", tournament);
    List<Tournament> tournaments = jdbcTemplate.query(SQL_EQUAL, this::mapRowTournament,
            tournament.name(), tournament.startDate(), tournament.endDate());
    return !tournaments.isEmpty();
  }

  @Override
  public Participant[] getParticipantsForFirstRound(long id) throws NotFoundException {
    LOG.trace("get participants for first round by tournament id({})", id);
    var tournament = getTournamentById(id);
    Participant[] participants = getParticipantsByTournamentId(id);
    for (Participant participant : participants) {
      participant.setPoints(0);
      List<Participant> participants1 = jdbcTemplate.query(SQL_FOR_FIRST_ROUND, this::mapRowParticipant,
              participant.getHorseId(), tournament.getStartDate());
      for (Participant p : participants1) {
        switch (p.getRoundReached()) {
          case 2 -> participant.addPoints(1);
          case 3 -> participant.addPoints(3);
          case 4 -> participant.addPoints(5);
          default -> { }
        }
      }
    }
    return sortParticipants(participants);
  }

  /**
   * Sorts an array of Participant objects based on name and points.
   * 0-7,
   * 1-6,
   * 2-5,
   * 3-4
   *
   * @param participants the array of Participant objects to sort
   * @return the sorted array of Participant objects
   */
  private Participant[] sortParticipants(Participant[] participants) {

    LOG.trace("sort participants({})", Arrays.toString(participants));
    LOG.debug("participants as input: " + Arrays.toString(participants));

    Arrays.sort(participants, Comparator.comparing(Participant::getName));
    LOG.debug("participants sorted by names: " + Arrays.toString(participants));
    Arrays.sort(participants, Comparator.comparingInt(Participant::getPoints).reversed());
    LOG.debug("participants sorted by points: " + Arrays.toString(participants));
    participants[0].setEntryNumber(0);
    participants[7].setEntryNumber(1);
    participants[1].setEntryNumber(2);
    participants[6].setEntryNumber(3);
    participants[2].setEntryNumber(4);
    participants[5].setEntryNumber(5);
    participants[3].setEntryNumber(6);
    participants[4].setEntryNumber(7);
    Arrays.sort(participants, Comparator.comparingInt(Participant::getEntryNumber));
    LOG.debug("participants sorted by entry number: " + Arrays.toString(participants));
    return participants;
  }
}