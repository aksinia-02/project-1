import {Component, OnInit} from '@angular/core';
import {NgForm, NgModel} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {Observable, of, retry} from 'rxjs';
import {Horse} from 'src/app/dto/horse';
import {Sex} from 'src/app/dto/sex';
import {HorseService} from 'src/app/service/horse.service';
import {ErrorFormatterService} from "../../../service/error-formatter.service";
import {Breed} from "../../../dto/breed";
import {BreedService} from "../../../service/breed.service";
import {formatIsoDate} from "../../../util/date-helper";


export enum HorseCreateInfoEditMode {
  create,
  info,
  edit,
}

@Component({
  selector: 'app-horse-create-info-edit',
  templateUrl: './horse-create-info-edit.component.html',
  styleUrls: ['./horse-create-info-edit.component.scss']
})
export class HorseCreateInfoEditComponent implements OnInit {

  mode: HorseCreateInfoEditMode = HorseCreateInfoEditMode.create;
  horse: Horse = {
    name: '',
    sex: Sex.female,
    dateOfBirth: new Date(),
    height: 0.5,
    weight: 30,
  };

  private heightSet: boolean = false;
  private weightSet: boolean = false;
  private dateOfBirthSet: boolean = false;

  private id: number = 0;

  get height(): number | null {
    return this.heightSet
      ? this.horse.height
      : null;
  }

  set height(value: number) {
    this.heightSet = true;
    this.horse.height = value;
  }

  get weight(): number | null {
    return this.weightSet
      ? this.horse.weight
      : null;
  }

  set weight(value: number) {
    this.weightSet = true;
    this.horse.weight = value;
  }

  get dateOfBirth(): string | null {
    return this.dateOfBirthSet
      ? formatIsoDate(this.horse.dateOfBirth)
      : null;
  }

  set dateOfBirth(value: Date) {
    this.dateOfBirthSet = true;
    this.horse.dateOfBirth = new Date(value);
  }


  constructor(
    private service: HorseService,
    private breedService: BreedService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private errorFormatter: ErrorFormatterService,
  ) {
  }

  public get heading(): string {
    switch (this.mode) {
      case HorseCreateInfoEditMode.create:
        return 'Create New Horse';
      case HorseCreateInfoEditMode.edit:
        return 'Edit Horse';
      case HorseCreateInfoEditMode.info:
        return 'Horse Information';
      default: {
        return '?';
      }
    }
  }

  public get submitButtonText(): string {
    switch (this.mode) {
      case HorseCreateInfoEditMode.create:
        return 'Create';
      case HorseCreateInfoEditMode.edit:
        return 'Save';
      case HorseCreateInfoEditMode.info:
        return 'Edit';
      default:
        return '?';
    }
  }

  get modeIsCreate(): boolean {
    return this.mode === HorseCreateInfoEditMode.create;
  }

  get modeIsEdit(): boolean{
    return this.mode === HorseCreateInfoEditMode.edit;
  }

  get modeIsInfo(): boolean{
    return this.mode === HorseCreateInfoEditMode.info;
  }


  get sex(): string {
    switch (this.horse.sex) {
      case Sex.male: return 'Male';
      case Sex.female: return 'Female';
      default: return '';
    }
  }

  private get modeActionFinished(): string {
    switch (this.mode) {
      case HorseCreateInfoEditMode.create:
        return 'created';
      case HorseCreateInfoEditMode.edit:
        return 'saved';
      case HorseCreateInfoEditMode.info:
        return '';
      default:
        return '?';
    }
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
    });

    if(!this.modeIsCreate){
      this.route.params.subscribe(params => {
        this.id = params['id'];
      })
      this.horse.id = this.id;
      this.loadHorse();
    }
  }

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  public formatBreedName(breed: Breed | null): string {
    return breed?.name ?? '';
  }

  breedSuggestions = (input: string) => (input === '')
    ? of([])
    :  this.breedService.breedsByName(input, 5);

  public onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.horse);
    if(this.modeIsInfo) {
      this.router.navigate(['/horses/edit', this.horse.id]);
      return;
    }
    if (form.valid) {
      let observable: Observable<Horse>;
      switch (this.mode) {
        case HorseCreateInfoEditMode.create:
          observable = this.service.create(this.horse);
          break;
        case HorseCreateInfoEditMode.edit:
          observable = this.service.update(this.id, this.horse);
          break;
        default:
          console.error('Unknown HorseCreateEditMode', this.mode);
          return;
      }
      observable.subscribe({
        next: data => {
          this.notification.success(`Horse ${this.horse.name} successfully ${this.modeActionFinished}.`);
          this.router.navigate(['/horses']);
        },
        error: error => {
          console.error(error.message, error);
          this.notification.error(this.errorFormatter.format(error), "Could Not Create Horse", {
            enableHtml: true,
            timeOut: 10000,
          });
        }
      });
    }
  }

  private loadHorse(): void{
    let observable: Observable<Horse>;
    observable = this.service.getById(this.id);
    observable.subscribe({
      next: data => {
        this.horse = data;
        this.dateOfBirth = this.horse.dateOfBirth;
        this.height = this.horse.height;
        this.weight = this.horse.weight;
      },
      error: error => {
        console.error('Error loading horse', error);
        this.notification.error(`Failed to load the horse. ${error.error.message}`);
      }
    })
  }

  public deleteHorse(id: number){
    let observable: Observable<Horse>;
    observable = this.service.delete(id);
    observable.subscribe({
      next: data => {
        this.notification.success(`Horse ${this.horse.name} successfully deleted.`);
        this.router.navigate(['/horses']);
      },
      error: error => {
        console.error('Error deleting horse', error);
        this.notification.error(this.errorFormatter.format(error), "Could Not Delete Horse", {
          enableHtml: true,
          timeOut: 10000,
        });
      }
    });
  }

  formatDate(date: Date): string {
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();
    return `${year}-${month}-${day}`;
  }

}
