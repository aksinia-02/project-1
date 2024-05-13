import { Component } from '@angular/core';
import {debounceTime, Subject} from "rxjs";
import {TournamentListDto, TournamentSearchParams} from "../../dto/tournament";
import {ToastrService} from "ngx-toastr";
import {TournamentService} from "../../service/tournament.service";

@Component({
  selector: 'app-tournament',
  templateUrl: './tournament.component.html',
  styleUrl: './tournament.component.scss'
})
export class TournamentComponent {
  search = false;
  tournaments: TournamentListDto[] = [];
  searchParams: TournamentSearchParams = {};
  searchStartDate: string | null = null;
  searchEndDate: string | null = null;
  searchChangedObservable = new Subject<void>();
  bannerError: string | null = null;

  constructor(
    private service: TournamentService,
    private notification: ToastrService,
  ) { }

  ngOnInit(): void {
    this.reloadTournaments();
    this.searchChangedObservable
      .pipe(debounceTime(300))
      .subscribe({next: () => this.reloadTournaments()});
  }

  reloadTournaments() {
    if (this.searchStartDate == null || this.searchStartDate === "") {
      delete this.searchParams.startDate;
    } else {
      this.searchParams.startDate = new Date(this.searchStartDate);
    }
    if (this.searchEndDate == null || this.searchEndDate === "") {
      delete this.searchParams.endDate;
    } else {
      this.searchParams.endDate = new Date(this.searchEndDate);
    }
    this.service.search(this.searchParams)
      .subscribe({
        next: data => {
          this.tournaments = data;
        },
        error: error => {
          console.error('Error fetching tournaments', error);
          this.bannerError = 'Could not fetch horses: ' + error.message;
          const errorMessage = error.status === 0
            ? 'Is the backend up?'
            : error.message.message;
          this.notification.error(errorMessage, 'Could Not Fetch Horses');
        }
      });
  }
  searchChanged(): void {
    this.searchChangedObservable.next();
  }

  formatDate(date: Date): string {
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
  }
}
