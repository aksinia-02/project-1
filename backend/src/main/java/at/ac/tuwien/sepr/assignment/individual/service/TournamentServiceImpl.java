package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentUpdateParticipantsDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.TournamentMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

/**
 * Implementation of service interface for working with tournaments.
 */
@Service
public class TournamentServiceImpl implements TournamentService {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final TournamentDao dao;
  private final TournamentValidator validator;
  private final TournamentMapper mapper;

  /**
   * Constructs a new TournamentServiceImpl with the provided dependencies.
   *
   * @param dao       the TournamentDao used for data access
   * @param validator the TournamentValidator used for input validation
   * @param mapper    the TournamentMapper used for mapping entities to DTOs
   */
  public TournamentServiceImpl(TournamentDao dao, TournamentValidator validator, TournamentMapper mapper) {
    this.dao = dao;
    this.validator = validator;
    this.mapper = mapper;
  }

  @Override
  public Stream<TournamentListDto> search(TournamentSearchDto searchParameters) {
    LOG.trace("search({})", searchParameters);
    var tournaments = dao.search(searchParameters);

    return tournaments.stream()
            .map(mapper::entityToListDto);
  }

  @Override
  public TournamentDetailsDto insert(TournamentCreateDto tournament) throws ConflictException, ValidationException {
    LOG.trace("insert({})", tournament);
    validator.validateForInsert(tournament);
    var insertedTournament = dao.insert(tournament);
    return mapper.entityToDetailDto(insertedTournament);
  }

  @Override
  public TournamentStandingsDto getStandings(Long id) throws NotFoundException, ValidationException {
    LOG.trace("get standing with({})", id);
    validator.validateId(id);
    var tournament = dao.getTournamentById(id);
    var participants = dao.getParticipantsByTournamentId(id);
    tournament.setParticipants(participants);
    return mapper.tournamentDetailsDtoToStandingsDto(mapper.entityToDetailDto(tournament));
  }

  @Override
  public TournamentStandingsDto generateFirstRound(Long id) throws NotFoundException, ValidationException {
    LOG.trace("generateFirstRound for({})", id);
    validator.validateId(id);
    var tournament = dao.getTournamentById(id);
    var participants = dao.getParticipantsForFirstRound(id);
    tournament.setParticipants(participants);
    return mapper.tournamentDetailsDtoToStandingsDtoFirstRound(mapper.entityToDetailDto(tournament));
  }

  @Override
  public TournamentStandingsDto saveStanding(TournamentUpdateParticipantsDto updateParticipants, long id) throws NotFoundException, ValidationException {
    LOG.trace("participants to update({})", updateParticipants);
    validator.validateParticipants(updateParticipants.participants());
    var tournament = dao.getTournamentById(id);
    var newParticipants = dao.updateParticipants(updateParticipants.participants());
    tournament.setParticipants(newParticipants);
    return mapper.tournamentDetailsDtoToStandingsDto(mapper.entityToDetailDto(tournament));
  }
}
