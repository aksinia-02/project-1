import {Component, OnInit} from '@angular/core';
import {ToastrService} from 'ngx-toastr';
import {HorseService} from 'src/app/service/horse.service';
import {Horse, HorseListDto} from '../../dto/horse';
import {HorseSearch} from '../../dto/horse';
import {debounceTime, map, Observable, of, Subject} from 'rxjs';
import {BreedService} from "../../service/breed.service";
import {ErrorFormatterService} from "../../service/error-formatter.service";

@Component({
  selector: 'app-horse',
  templateUrl: './horse.component.html',
  styleUrls: ['./horse.component.scss']
})
export class HorseComponent implements OnInit {
  search = false;
  horses: HorseListDto[] = [];
  bannerError: string | null = null;
  searchParams: HorseSearch = {};
  searchBornEarliest: string | null = null;
  searchBornLatest: string | null = null;
  horseForDeletion: Horse | undefined;
  searchChangedObservable = new Subject<void>();

  constructor(
    private service: HorseService,
    private breedService: BreedService,
    private notification: ToastrService,
    private errorFormatter: ErrorFormatterService,
  ) { }

  ngOnInit(): void {
    this.reloadHorses();
    this.searchChangedObservable
      .pipe(debounceTime(300))
      .subscribe({next: () => this.reloadHorses()});
  }

  reloadHorses() {
    if (this.searchBornEarliest == null || this.searchBornEarliest === "") {
      delete this.searchParams.bornEarliest;
    } else {
      this.searchParams.bornEarliest = new Date(this.searchBornEarliest);
    }
    if (this.searchBornLatest == null || this.searchBornLatest === "") {
      delete this.searchParams.bornLastest;
    } else {
      this.searchParams.bornLastest = new Date(this.searchBornLatest);
    }
    this.service.search(this.searchParams)
      .subscribe({
        next: data => {
          this.horses = data;
        },
        error: error => {
          console.error('Error fetching horses', error);
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

  deleteHorse(id: number): void {
    let observable: Observable<Horse>;
    observable = this.service.delete(id);
    observable.subscribe({
      next: data => {
        this.reloadHorses();
        this.notification.success(`Horse ${this.horseForDeletion?.name} successfully deleted.`);
      },
      error: error => {
        console.error('Error deleting horse', error);
        this.notification.error(this.errorFormatter.format(error), `Failed to delete the horse "${this.horseForDeletion?.name}".`, {
          enableHtml: true,
          timeOut: 10000,
        });
      }
    });
  }

  setHorseForDeletion(id: number): void{
    let observable: Observable<Horse>;
    observable = this.service.getById(id);
    observable.subscribe({
      next: data => {
        this.horseForDeletion = data;
      },
      error: error => {
        console.error('Error loading horse', error);
        this.notification.error(this.errorFormatter.format(error), `Failed to load the horse. ${this.horseForDeletion?.name}`, {
          enableHtml: true,
          timeOut: 10000,
        });
      }
    })
  }

  breedSuggestions = (input: string): Observable<string[]> =>
    this.breedService.breedsByName(input, 5)
      .pipe(map(bs =>
        bs.map(b => b.name)));

  formatBreedName = (name: string) => name; // It is already the breed name, we just have to give a function to the component

  formatDate(date: Date): string {
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
  }
}
