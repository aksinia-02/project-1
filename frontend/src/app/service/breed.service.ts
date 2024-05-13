import {Injectable} from "@angular/core";
import {HttpClient, HttpParams} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Breed} from "../dto/breed";
import {Observable} from "rxjs";

const baseUri = environment.backendUrl + "/breeds";

@Injectable({
  providedIn: 'root'
})
export class BreedService {


  constructor(
    private http: HttpClient
  ) {
  }

  /**
   * Retrieves a list of breeds filtered by name and optionally limited by the specified number.
   *
   * @param name  The name to filter the breeds by.
   * @param limit The maximum number of breeds to retrieve. If null or undefined, no limit is applied.
   * @returns     An Observable emitting an array of Breed objects matching the specified criteria.
   */
  public breedsByName(name: string, limit: number | undefined): Observable<Breed[]> {
    let params = new HttpParams();
    params = params.append("name", name);
    if (limit != null) {
      params = params.append("limit", limit);
    }
    return this.http.get<Breed[]>(baseUri, { params });
  }
}
