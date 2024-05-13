import {Component, OnInit, ViewChild} from '@angular/core';
import {
  TournamentDetailParticipantDto,
  TournamentStandingsDto,
} from "../../../dto/tournament";
import {TournamentService} from "../../../service/tournament.service";
import {ActivatedRoute} from "@angular/router";
import {NgForm} from "@angular/forms";
import {ToastrService} from "ngx-toastr";
import {ErrorFormatterService} from "../../../service/error-formatter.service";
import {Observable} from "rxjs";
import {TournamentStandingsBranchComponent} from "./tournament-standings-branch/tournament-standings-branch.component";

@Component({
  selector: 'app-tournament-standings',
  templateUrl: './tournament-standings.component.html',
  styleUrls: ['./tournament-standings.component.scss']
})
export class TournamentStandingsComponent implements OnInit {
  @ViewChild(TournamentStandingsBranchComponent) standingsBranch!: TournamentStandingsBranchComponent;
  standings: TournamentStandingsDto | undefined;
  id = 0;
  generatedFirstRound: boolean = false;
  initialRoundNumber: number = 4;
  updateParticipants: { participants: TournamentDetailParticipantDto[] }
  initialEntryNumber: number = 0;
  disabledBranches = false;
  complete: boolean = false;
  enableToGenerate: boolean = true;


  public constructor(
    private service: TournamentService,
    private errorFormatter: ErrorFormatterService,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
    this.updateParticipants = {
      participants: []
    };
  }

  public ifComplete() {
    return this.complete;
  }

  public ifEnableToGenerate() {
    return this.enableToGenerate;
  }

  public ngOnInit() {
    this.route.params.subscribe(params => {
      this.id = params['id'];
    })
    this.loadStandings();
  }

  public submit(form: NgForm) {
    this.saveStanding();
  }

  public generateFirstRound() {
    if (!this.standings)
      return;
    else if (this.generatedFirstRound) {
      this.notification.error(`first round has already benn generated`);
      return;
    }
    else {
      let observable: Observable<TournamentStandingsDto>;
      observable = this.service.generateFirstRound(this.id);
      observable.subscribe({
        next: data => {
          this.standings = data;
          this.generatedFirstRound = true;
          this.standings.participants.map(p => p.roundReached = 1);
        },
        error: error => {
          console.error('Error generating first round', error);
          this.notification.error(`Failed to load the standings. ${error.error.message}`);
        }
      })
    }
  }

  private loadStandings(): void {
    let observable: Observable<TournamentStandingsDto>;
    observable = this.service.getStandings(this.id);
    observable.subscribe({
      next: data => {
        this.standings = data;
        this.setComplete();
        if (this.standings.tree.thisParticipant != null)
          this.disabledBranches = true;
        if(this.standingsBranch)
          this.enableToGenerate = this.standingsBranch.checkDisabled();
      },
      error: error => {
        console.error('Error loading standings', error);
        this.notification.error(`Failed to load the standings. ${error.error.message}`);
      }
    })
  }

  private saveStanding(): void {
    if (!this.standings) {
      return;
    }
    this.updateParticipants.participants = this.standings.participants;
    let observable: Observable<TournamentStandingsDto>;
    observable = this.service.saveStandings(this.standings.id, this.updateParticipants);
    observable.subscribe({
      next: data => {
        this.standings = data;
        this.setComplete();
        this.standingsBranch.updateDisable();
        this.enableToGenerate = this.standingsBranch.checkDisabled();
        },
      error: error => {
        console.error('Error saving standings', error);
        this.notification.error(`Failed to saving the standings. ${error.error.message}`);
      }
    })
  }

  private setComplete() {
    if (!this.complete && this.standings &&
      this.standings.participants.filter(p => p.roundReached == 4).length == 1)
      this.complete = true;
  }
}
