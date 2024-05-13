package at.ac.tuwien.sepr.assignment.individual.mapper;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsTreeDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participant;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;

/**
 * Mapper class responsible for converting between Tournament entities and DTOs
 */
@Component
public class TournamentMapper {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * Default constructor for the TournamentMapper class.
   * No parameters are needed.
   */
  public TournamentMapper() { }

  /**
   * Convert a tournament entity object to a {@link TournamentListDto}.
   *
   * @param tournament the tournament to convert
   * @return the converted {@link TournamentListDto}
   */
  public TournamentListDto entityToListDto(Tournament tournament) {
    LOG.trace("entity({}) to TournamentListDto", tournament);
    if (tournament == null) {
      return null;
    }

    return new TournamentListDto(
            tournament.getId(),
            tournament.getName(),
            tournament.getStartDate(),
            tournament.getEndDate()
    );
  }

  /**
   * Maps a Tournament entity to a TournamentDetailsDto.
   *
   * @param tournament The Tournament entity to be mapped.
   * @return The mapped TournamentDetailsDto.
   */
  public TournamentDetailsDto entityToDetailDto(Tournament tournament) {
    LOG.trace("Tournament({}) to TournamentDetailsDto", tournament);
    if (tournament == null) {
      return null;
    }

    return new TournamentDetailsDto(
      tournament.getId(),
      tournament.getName(),
      tournament.getStartDate(),
      tournament.getEndDate(),
      participantToParticipantDto(tournament.getParticipants())
    );
  }

  /**
   * Maps an array of Participant entities to an array of TournamentDetailParticipantDto.
   *
   * @param participants The array of Participant entities to be mapped.
   * @return The mapped array of TournamentDetailParticipantDto.
   */
  private TournamentDetailParticipantDto[] participantToParticipantDto(Participant[] participants) {
    LOG.trace("Participant[]({}) to TournamentDetailParticipantDto[]", Arrays.toString(participants));
    TournamentDetailParticipantDto[] participantsDto = new TournamentDetailParticipantDto[8];
    for (int i = 0; i < 8; i++) {
      participantsDto[i] = new TournamentDetailParticipantDto(
              participants[i].getId(),
              participants[i].getHorseId(),
              participants[i].getName(),
              participants[i].getDateOfBirth(),
              participants[i].getEntryNumber(),
              participants[i].getRoundReached()
      );
    }
    return participantsDto;
  }

  /**
   * Maps a TournamentDetailsDto to a TournamentStandingsDto.
   * This method is used for generating standings with participants.
   *
   * @param tournament The TournamentDetailsDto to be mapped.
   * @return The mapped TournamentStandingsDto.
   */
  public TournamentStandingsDto tournamentDetailsDtoToStandingsDto(TournamentDetailsDto tournament) {
    LOG.trace("TournamentDetailsDto({}) to TournamentStandingsDto", tournament);

    TournamentDetailParticipantDto[] participants = tournament.participants();

    TournamentStandingsTreeDto tree = participantsToTree(participants, 0);
    return new TournamentStandingsDto(tournament.id(), tournament.name(), participants, tree);
  }

  /**
   * Generates a TournamentStandingsTreeDto representing the standing's tree for the tournament participants.
   *
   * @param participants The array of TournamentDetailParticipantDto representing the participants.
   * @param option       The option indicating tournamentDetailsDtoToStandingsDto or tournamentDetailsDtoToStandingsDtoFirstRound must be generated.
   * @return The generated TournamentStandingsTreeDto representing the standings tree.
   */
  private TournamentStandingsTreeDto participantsToTree(TournamentDetailParticipantDto[] participants, int option) {
    LOG.trace("TournamentDetailParticipantDto({}) with option({}) to TournamentStandingsTreeDto", participants, option);

    TournamentStandingsTreeDto[] trees = new TournamentStandingsTreeDto[participants.length];

    //generating first round of tree

    //set all participants, which was saved
    for (TournamentDetailParticipantDto participant : participants) {
      if (participant.entryNumber() != -1) {
        trees[participant.entryNumber()] = new TournamentStandingsTreeDto(participant, null);
      }
    }
    //set rest participants
    for (int i = 0; i < participants.length; i++) {
      if (trees[i] == null) {
        // important only for tournamentDetailsDtoToStandingsDto
        if (participants[i].roundReached() == option) {
          trees[i] = new TournamentStandingsTreeDto(null, null);
        } else if (participants[i].entryNumber() == -1) {
          trees[i] = new TournamentStandingsTreeDto(participants[i], null);
        } else {
          int newKey = participants[i].entryNumber();
          while (participants[newKey].entryNumber() != -1) {
            newKey = participants[newKey].entryNumber();
          }
          trees[i] = new TournamentStandingsTreeDto(participants[newKey].roundReached() == option ? null : participants[newKey], null);
        }
      }
    }

    //generating of all rounds except first round, which has been already generated
    while (trees.length > 1) {
      TournamentStandingsTreeDto[] nextRound = new TournamentStandingsTreeDto[trees.length / 2];
      for (int i = 0; i < nextRound.length; i++) {
        TournamentStandingsTreeDto[] branches = {trees[i * 2], trees[i * 2 + 1]};
        nextRound[i] = createTree(branches);
      }
      trees = nextRound;
    }
    return trees[0];
  }

  /**
   * Creates a single node of the standings tree based on the given branches.
   * The node contains information about the higher-ranking participant among the two branches.
   *
   * @param branches The array of two TournamentStandingsTreeDto representing the branches.
   * @return The created TournamentStandingsTreeDto representing the node.
   */
  private TournamentStandingsTreeDto createTree(TournamentStandingsTreeDto[] branches) {
    LOG.trace("createTree with branches({}, {})", branches[0], branches[1]);

    TournamentStandingsTreeDto result;
    if (branches[0].thisParticipant() != null && branches[1].thisParticipant() != null
            && branches[0].thisParticipant().roundReached() < branches[1].thisParticipant().roundReached()) {
      result = new TournamentStandingsTreeDto(branches[1].thisParticipant(), branches);
    } else if (branches[0].thisParticipant() != null && branches[1].thisParticipant() != null
            && branches[0].thisParticipant().roundReached() > branches[1].thisParticipant().roundReached()) {
      result = new TournamentStandingsTreeDto(branches[0].thisParticipant(), branches);
    } else {
      result = new TournamentStandingsTreeDto(null, branches);
    }
    return result;
  }

  /**
   * Converts a TournamentDetailsDto to a TournamentStandingsDto representing the first round standings.
   *
   * @param tournament The TournamentDetailsDto to be mapped.
   * @return The mapped TournamentStandingsDto for the first round.
   */
  public TournamentStandingsDto tournamentDetailsDtoToStandingsDtoFirstRound(TournamentDetailsDto tournament) {
    LOG.trace("TournamentDetailsDto({}) to TournamentStandingsDto for first Round", tournament);

    //set roundReached for all participants to 0, because the tree must be loaded form clear data
    TournamentDetailParticipantDto[] participants = tournament.participants();
    TournamentDetailParticipantDto[] newParticipants = new TournamentDetailParticipantDto[participants.length];
    for (int i = 0; i < participants.length; i++) {
      newParticipants[i] = new TournamentDetailParticipantDto(participants[i].id(), participants[i].horseId(),
              participants[i].name(), participants[i].dateOfBirth(), participants[i].entryNumber(), 0);
    }

    TournamentStandingsTreeDto tree = participantsToTree(newParticipants, 1);
    return new TournamentStandingsDto(tournament.id(), tournament.name(), newParticipants, tree);
  }
}
