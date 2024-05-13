package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.entity.Participant;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.lang.invoke.MethodHandles;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 * Repository class implementing HorseDao interface for JDBC operations
 */
@Repository
public class HorseJdbcDao implements HorseDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String TABLE_NAME = "horse";
  private static final String TABLE_PARTICIPANT = "participant";
  private static final String SQL_SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
  private static final String SQL_SELECT_SEARCH = "SELECT  "
          + "    h.id as \"id\", h.name as \"name\", h.sex as \"sex\", h.date_of_birth as \"date_of_birth\""
          + "    , h.height as \"height\", h.weight as \"weight\", h.breed_id as \"breed_id\""
          + " FROM " + TABLE_NAME + " h LEFT OUTER JOIN breed b ON (h.breed_id = b.id)"
          + " WHERE (:name IS NULL OR UPPER(h.name) LIKE UPPER('%'||:name||'%'))"
          + "  AND (:sex IS NULL OR :sex = sex)"
          + "  AND (:bornEarliest IS NULL OR :bornEarliest <= h.date_of_birth)"
          + "  AND (:bornLatest IS NULL OR :bornLatest >= h.date_of_birth)"
          + "  AND (:breed IS NULL OR UPPER(b.name) LIKE UPPER('%'||:breed||'%'))";

  private static final String SQL_LIMIT_CLAUSE = " LIMIT :limit";

  private static final String SQL_UPDATE = "UPDATE " + TABLE_NAME
      + " SET name = ?"
      + "  , sex = ?"
      + "  , date_of_birth = ?"
      + "  , height = ?"
      + "  , weight = ?"
      + "  , breed_id = ?"
      + " WHERE id = ?";

  private static final String SQL_INSERT = "INSERT INTO " + TABLE_NAME + "(name, sex, date_of_birth, height, weight, breed_id)"
          + " VALUES (?, ?, ?, ?, ?, ?)";

  private static final String SQL_EQUAL = "SELECT * FROM " + TABLE_NAME + " WHERE name = ? "
          + "AND sex = ? AND date_of_birth = ? AND height = ? AND weight = ? AND (breed_id = ? OR (breed_id IS NULL AND ? IS NULL))";

  private static final String SQL_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";

  private static final String SQL_IS_PARTICIPANT = "SELECT * FROM " + TABLE_PARTICIPANT + " WHERE id_horse = ?";

  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNamed;

  /**
   * Constructs a new HorseJdbcDao with the specified JDBC named template and JDBC template.
   *
   * @param jdbcNamed   The JDBC named template used for named parameter JDBC operations.
   * @param jdbcTemplate The JDBC template used for performing basic JDBC operations.
   */
  public HorseJdbcDao(
      NamedParameterJdbcTemplate jdbcNamed,
      JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcNamed = jdbcNamed;
  }

  @Override
  public Horse getById(long id) throws NotFoundException {
    LOG.trace("get({})", id);
    List<Horse> horses;
    horses = jdbcTemplate.query(SQL_SELECT_BY_ID, this::mapRow, id);

    if (horses.isEmpty()) {
      throw new NotFoundException("No horse with ID %d found".formatted(id));
    }
    if (horses.size() > 1) {
      // This should never happen!!
      throw new FatalException("Too many horses with ID %d found".formatted(id));
    }
    return horses.get(0);
  }

  @Override
  public Collection<Horse> search(HorseSearchDto searchParameters) {
    LOG.trace("search({})", searchParameters);
    var query = SQL_SELECT_SEARCH;
    if (searchParameters.limit() != null) {
      query += SQL_LIMIT_CLAUSE;
    }
    var params = new BeanPropertySqlParameterSource(searchParameters);
    params.registerSqlType("sex", Types.VARCHAR);

    return jdbcNamed.query(query, params, this::mapRow);
  }


  @Override
  public Horse update(HorseDetailDto horse) throws NotFoundException, ConflictException {
    LOG.trace("update({})", horse);
    if (isEqualExist(horse)) {
      List<String> conflictErrors = new ArrayList<>();
      conflictErrors.add("The same horse was found.");
      throw new ConflictException("Conflict was detected", conflictErrors);
    }
    int updated = jdbcTemplate.update(SQL_UPDATE,
        horse.name(),
        horse.sex().toString(),
        horse.dateOfBirth(),
        horse.height(),
        horse.weight(),
            horse.breed() != null ? horse.breed().id() : null,
        horse.id());
    if (updated == 0) {
      throw new NotFoundException("Could not update horse with ID " + horse.id() + ", because it does not exist");
    }

    return new Horse()
        .setId(horse.id())
        .setName(horse.name())
        .setSex(horse.sex())
        .setDateOfBirth(horse.dateOfBirth())
        .setHeight(horse.height())
        .setWeight(horse.weight())
            .setBreedId(horse.breed() != null ? horse.breed().id() : null);
  }

  @Override
  public Horse insert(HorseDetailDto horse) throws ConflictException {
    LOG.trace("insert({})", horse);
    if (isEqualExist(horse)) {
      List<String> conflictErrors = new ArrayList<>();
      conflictErrors.add("The same horse was found.");
      throw new ConflictException("Conflict was detected", conflictErrors);
    }
    KeyHolder keyHolder = new GeneratedKeyHolder();
    int inserted = jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, horse.name());
      ps.setString(2, horse.sex().toString());
      ps.setDate(3, Date.valueOf(horse.dateOfBirth()));
      ps.setDouble(4, horse.height());
      ps.setDouble(5, horse.weight());
      if (horse.breed() != null) {
        ps.setLong(6, horse.breed().id());
      } else {
        ps.setNull(6, Types.INTEGER);
      }
      return ps;
    }, keyHolder);
    if (inserted > 0) {
      long generatedID = keyHolder.getKey().intValue();
      return new Horse()
              .setId(generatedID)
              .setName(horse.name())
              .setSex(horse.sex())
              .setDateOfBirth(horse.dateOfBirth())
              .setHeight(horse.height())
              .setWeight(horse.weight())
              .setBreedId(horse.breed() != null ? horse.breed().id() : null);
    } else {
      throw new IllegalStateException("No rows affected after insertion.");
    }
  }

  @Override
  public void delete(long id) throws NotFoundException, ConflictException {
    LOG.trace("delete horse with id({})", id);

    if (isParticipant(id)) {
      List<String> errors = new ArrayList<>();
      errors.add("Horse can't be deleted, because this horse is a participant");
      throw new ConflictException("Conflict occurred for deleting horse", errors);
    }
    int deleted = jdbcTemplate.update(SQL_DELETE, id);

    if (deleted == 0) {
      throw new NotFoundException("No horse with ID %d found".formatted(id));
    }
    if (deleted > 1) {
      // This should never happen!!
      throw new FatalException("Too many horses with ID %d found".formatted(id));
    }
    LOG.info("Horse with ID {} deleted successfully", id);
  }

  /**
   * Maps a row from the ResultSet to a Horse object.
   *
   * @param result the ResultSet containing the data
   * @param rownum the current row index
   * @return the mapped Horse object
   * @throws SQLException if an SQL exception occurs
   */
  private Horse mapRow(ResultSet result, int rownum) throws SQLException {
    LOG.trace("mapRow of({})", result);
    return new Horse()
        .setId(result.getLong("id"))
        .setName(result.getString("name"))
        .setSex(Sex.valueOf(result.getString("sex")))
        .setDateOfBirth(result.getDate("date_of_birth").toLocalDate())
        .setHeight(result.getFloat("height"))
        .setWeight(result.getFloat("weight"))
        .setBreedId(result.getObject("breed_id", Long.class))
        ;
  }

  /**
   * Checks if a horse with the same attributes already exists in the database.
   *
   * @param horse the HorseDetailDto object representing the horse to be checked
   * @return true if a horse with the same attributes exists, false otherwise
   */
  private boolean isEqualExist(HorseDetailDto horse) {
    LOG.trace("isEqualExist({})", horse);
    List<Horse> horses = jdbcTemplate.query(SQL_EQUAL, this::mapRow,
            horse.name(), horse.sex().toString(), horse.dateOfBirth(),
            horse.height(), horse.weight(), horse.breed() != null ? horse.breed().id() : null,
            horse.breed() != null ? horse.breed().id() : null);
    return !horses.isEmpty();
  }

  /**
   * Checks if a participant with the given ID exists.
   *
   * @param id The ID of the participant to check.
   * @return true if a participant with the given ID exists, false otherwise.
   */
  private boolean isParticipant(Long id) {
    LOG.trace("isParticipant with id({})", id);
    List<Participant> participants = jdbcTemplate.query(SQL_IS_PARTICIPANT, this::mapRowParticipant, id);

    return !participants.isEmpty();
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
}
