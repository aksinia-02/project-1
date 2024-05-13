import {HorseSelection} from "./horse";

/**
 * Interface representing parameters of tournaments for search.
 */
export interface TournamentSearchParams {
  name?: string;
  startDate?: Date;
  endDate?: Date;
}

/**
 * Interface representing tournament for search page.
 */
export interface TournamentListDto {
  id: number;
  name: string;
  startDate: Date;
  endDate: Date;
}

/**
 * Interface representing tournament for creating.
 */
export interface TournamentCreateDto {
  name: string;
  startDate: Date;
  endDate: Date;
  participants: HorseSelection[];
}

/**
 * Interface representing the details of tournament.
 */
export interface TournamentDetailDto {
  id: number;
  name: string;
  startDate: Date;
  endDate: Date;
  participants: TournamentDetailParticipantDto[];
}

/**
 * Interface representing the details of participant.
 */
export interface TournamentDetailParticipantDto {
  id: number;
  horseId: number;
  name: string;
  dateOfBirth: Date;
  entryNumber?: number;
  roundReached?: number;
}

/**
 * Interface representing the tree of standing.
 */
export interface TournamentStandingsTreeDto {
  thisParticipant: TournamentDetailParticipantDto | null;
  branches?: TournamentStandingsTreeDto[];
}

/**
 * Interface representing the standing of tournament.
 */
export interface TournamentStandingsDto {
  id: number;
  name: string;
  participants: TournamentDetailParticipantDto[];
  tree: TournamentStandingsTreeDto;
}

/**
 * Interface representing the details of participants to update.
 */
export interface TournamentUpdateParticipantsDto {
  participants: TournamentDetailParticipantDto[];
}
