import {HttpClient, HttpErrorResponse, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {catchError, map, Observable, tap} from 'rxjs';
import {environment} from 'src/environments/environment';
import {Horse, HorseListDto} from '../dto/horse';
import {HorseSearch} from '../dto/horse';
import {formatIsoDate} from '../util/date-helper';

const baseUri = environment.backendUrl + '/horses';

@Injectable({
  providedIn: 'root'
})
export class HorseService {

  constructor(
    private http: HttpClient,
  ) { }

  /**
   * Retrieves a horse by its unique identifier.
   *
   * @param id The unique identifier of the horse to retrieve.
   * @returns  An Observable emitting the horse object.
   */
  getById(id: number): Observable<Horse> {
    return this.http.get<Horse>(`${baseUri}/${id}`)
      .pipe(tap(horse => {
        horse.dateOfBirth = new Date(horse.dateOfBirth);
      }));
  }

  /**
   * Searches for horses based on the provided search parameters.
   *
   * @param searchParams The search parameters to filter horses.
   * @returns            An Observable emitting an array of HorseListDto objects matching the search criteria.
   */
  search(searchParams: HorseSearch): Observable<HorseListDto[]> {
    if (searchParams.name === '') {
      delete searchParams.name;
    }
    let params = new HttpParams();
    if (searchParams.name) {
      params = params.append('name', searchParams.name);
    }
    if (searchParams.sex) {
      params = params.append('sex', searchParams.sex);
    }
    if (searchParams.bornEarliest) {
      params = params.append('bornEarliest', formatIsoDate(searchParams.bornEarliest));
    }
    if (searchParams.bornLastest) {
      params = params.append('bornLatest', formatIsoDate(searchParams.bornLastest));
    }
    if (searchParams.breedName) {
      params = params.append('breed', searchParams.breedName);
    }
    if (searchParams.limit) {
      params = params.append('limit', searchParams.limit);
    }
    return this.http.get<HorseListDto[]>(baseUri, { params })
      .pipe(tap(horses => horses.map(h => {
        h.dateOfBirth = new Date(h.dateOfBirth); // Parse date string
      })));
  }

  /**
   * Create a new horse in the system.
   *
   * @param horse the data for the horse that should be created
   * @return an Observable for the created horse
   */
  create(horse: Horse): Observable<Horse> {
    return this.http.post<Horse>(baseUri, horse);
  }

  /**
   * Updates an existing horse in the system.
   *
   * @param id       The unique identifier of the horse to update.
   * @param toUpdate The updated data for the horse.
   * @returns        An Observable for the updated horse.
   */
  update(id: number, toUpdate: Horse): Observable<Horse> {
    return this.http.put<Horse>(`${baseUri}/${id}`, toUpdate);
  }

  /**
   * Deletes a horse from the system.
   *
   * @param id The unique identifier of the horse to delete.
   * @returns  An Observable for the deleted horse.
   */
  delete(id: number): Observable<Horse> {
    return this.http.delete<Horse>(`${baseUri}/${id}`);
  }

}
