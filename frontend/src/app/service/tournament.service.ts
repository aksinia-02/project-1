import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Observable, tap} from 'rxjs';
import {formatIsoDate} from '../util/date-helper';
import {
  TournamentCreateDto, TournamentDetailDto,
  TournamentListDto,
  TournamentSearchParams,
  TournamentStandingsDto, TournamentUpdateParticipantsDto
} from "../dto/tournament";
const baseUri = environment.backendUrl + '/tournaments';

@Injectable({
  providedIn: 'root'
})
export class TournamentService {
  constructor(
    private http: HttpClient
  ) {
  }

  /**
   * Searches for tournaments based on the provided search parameters.
   *
   * @param searchParams The search parameters to filter tournaments.
   * @returns An Observable emitting an array of TournamentListDto objects matching the search criteria.
   */
  search(searchParams: TournamentSearchParams): Observable<TournamentListDto[]> {
    if (searchParams.name === '') {
      delete searchParams.name;
    }
    let params = new HttpParams();
    if (searchParams.name) {
      params = params.append('name', searchParams.name);
    }
    if (searchParams.startDate) {
      params = params.append('startDate', formatIsoDate(searchParams.startDate));
    }
    if (searchParams.endDate) {
      params = params.append('endDate', formatIsoDate(searchParams.endDate));
    }
    return this.http.get<TournamentListDto[]>(baseUri, { params })
      .pipe(tap(tournaments => tournaments.map(t => {
        t.startDate = new Date(t.startDate);
        t.endDate = new Date(t.endDate);
      })));
  }

  /**
   * Creates a new tournament in the system.
   *
   * @param tournament The data for the tournament that should be created.
   * @returns An Observable for the created tournament.
   */
  public create(tournament: TournamentCreateDto): Observable<TournamentDetailDto> {
    return this.http.post<TournamentDetailDto>(baseUri, tournament);
  }

  /**
   * Retrieves the standings of a tournament by its id.
   *
   * @param id The unique identifier of the tournament to retrieve standings for.
   * @returns  An Observable emitting the standings of the tournament.
   */
  public getStandings(id: number): Observable<TournamentStandingsDto> {
    return this.http.get<TournamentStandingsDto>(`${baseUri}/standings/${id}`)
      .pipe(tap(standing => standing.participants.map(p => {
        p.dateOfBirth = new Date(p.dateOfBirth);
      })));
  }

  /**
   * Generates the first round of a tournament.
   *
   * @param id The unique identifier of the tournament to generate the first round for.
   * @returns  An Observable emitting the standings after generating the first round.
   */
  public generateFirstRound(id: number): Observable<TournamentStandingsDto> {
    return this.http.get<TournamentStandingsDto>(`${baseUri}/standings/${id}/first-round`)
      .pipe(tap(standing => standing.participants.map(p => {
        p.dateOfBirth = new Date(p.dateOfBirth);
      })));
  }

  /**
   * Saves the standings of a tournament after updating participants' details.
   *
   * @param id                The unique identifier of the tournament to save standings for.
   * @param updateParticipants The data containing updated participant details.
   * @returns                 An Observable emitting the updated standings of the tournament.
   */
  public saveStandings(id: number, updateParticipants: TournamentUpdateParticipantsDto): Observable<TournamentStandingsDto> {
    return this.http.put<TournamentStandingsDto>(`${baseUri}/standings/${id}`, updateParticipants);
  }

}
