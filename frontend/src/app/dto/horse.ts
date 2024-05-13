import {Sex} from './sex';
import {Breed} from "./breed";

/**
 * Interface representing a horse.
 */
export interface Horse {
  id?: number;
  name: string;
  sex: Sex;
  dateOfBirth: Date;
  height: number;
  weight: number;
  breed?: Breed;
}

/**
 * Interface representing horse for search page.
 */
export interface HorseListDto {
  id: number,
  name: string,
  sex: Sex,
  dateOfBirth: Date;
  breed: Breed;
}

/**
 * Interface representing parameters of horse for search page.
 */
export interface HorseSearch {
  name?: string;
  sex?: Sex;
  bornEarliest?: Date;
  bornLastest?: Date;
  breedName?: string;
  limit?: number;
}

/**
 * Interface representing horse-participant for creating of tournament.
 */
export interface HorseSelection {
    id: number;
    name: string;
    dateOfBirth: Date;
}
