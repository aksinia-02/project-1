import {Component, Input, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {TournamentDetailParticipantDto, TournamentStandingsTreeDto} from "../../../../dto/tournament";
import {of} from "rxjs";
import {NgForm} from "@angular/forms";

enum TournamentBranchPosition {
  FINAL_WINNER,
  UPPER,
  LOWER,
}

@Component({
  selector: 'app-tournament-standings-branch',
  templateUrl: './tournament-standings-branch.component.html',
  styleUrls: ['./tournament-standings-branch.component.scss']
})
export class TournamentStandingsBranchComponent {
  @ViewChildren(TournamentStandingsBranchComponent) childBranches!: QueryList<TournamentStandingsBranchComponent>;
  options = {
    day: 'numeric',
    month: 'long',
    year: 'numeric'
  }
  protected readonly TournamentBranchPosition = TournamentBranchPosition;
  @Input() branchPosition = TournamentBranchPosition.FINAL_WINNER;
  @Input() treeBranch: TournamentStandingsTreeDto | undefined;
  @Input() allParticipants: TournamentDetailParticipantDto[] = [];
  @Input() roundNumber: number = 0;
  @Input() entryNumber: number = 0;
  @Input() disabledBranches = false;
  @Input() thisBrachDisable = false;
  thisParticipant: TournamentDetailParticipantDto | undefined;

  get isUpperHalf(): boolean {
    return this.branchPosition === TournamentBranchPosition.UPPER;
  }

  public ngOnInit() {
    if (this.treeBranch && this.treeBranch.thisParticipant) {
      this.treeBranch.thisParticipant.dateOfBirth = new Date(this.treeBranch.thisParticipant.dateOfBirth);
    }
    this.updateDisable();
  }

  get isLowerHalf(): boolean {
    return this.branchPosition === TournamentBranchPosition.LOWER;
  }

  get isFinalWinner(): boolean {
    return this.branchPosition === TournamentBranchPosition.FINAL_WINNER;
  }

  suggestions = (input: string) => {
    // The candidates are either the participants of the previous round matches in this branch
    // or, if this is the first round, all participant horses
    const allCandidates =
      this.treeBranch?.branches?.map(b => b.thisParticipant)
      ?? this.allParticipants;
    if (allCandidates.includes(null))
      return of([]);
    if (this.treeBranch && this.treeBranch.thisParticipant) {
      if (this.treeBranch.thisParticipant.roundReached != undefined && this.treeBranch.thisParticipant.roundReached < 1) {
        this.setEntryNumber(this.treeBranch.thisParticipant.id);
      }
      this.setRoundReached(this.treeBranch.thisParticipant.id);
      this.thisParticipant = this.treeBranch.thisParticipant;
    }
    const results = allCandidates
        .filter(x => !!x)
      .filter(x => x?.roundReached != undefined && x?.roundReached < this.roundNumber)
        .map(x => <TournamentDetailParticipantDto><unknown>x)
        .filter((x) =>
            x.name.toUpperCase().match(new RegExp(`.*${input.toUpperCase()}.*`)));
    return of(results);
  };

  public formatParticipant(participant: TournamentDetailParticipantDto | null): string {
    if (participant != null) {
      participant.dateOfBirth = new Date(participant.dateOfBirth);
      return participant
        ? `${participant.name} (${participant.dateOfBirth.toLocaleDateString("en-GB")})`
        : "";
    } else {
      return "";
    }
  }

  onModelChange(newValue: any) {
    if (newValue === null) {
      if (this.thisParticipant) {
        this.disabledBranches = false;
        if (typeof this.thisParticipant.roundReached === 'number' && this.thisParticipant.roundReached < 2) {
          this.setPreviousEntryNumber(this.thisParticipant.id);
        }
        this.setPreviousRoundReached(this.thisParticipant.id);
      }
    }
  }

  private setRoundReached(id: number) {
    this.allParticipants.forEach(participant => {
      if (participant.id === id) {
        participant.roundReached = this.roundNumber;
      }
    })
  }

  private setEntryNumber(id: number) {
    this.allParticipants.forEach(participant => {
      if (participant.id === id) {
        participant.entryNumber = this.entryNumber;
      }
    })
  }

  private setPreviousEntryNumber(id: number) {
    this.allParticipants.forEach(participant => {
      if (participant.id === id) {
        participant.entryNumber = -1;
      }
    })
  }

  private setPreviousRoundReached(id: number) {
    this.allParticipants.forEach(participant => {
      if (participant.roundReached && participant.id === id) {
        participant.roundReached = participant.roundReached - 1;
      }
    })
  }



  public updateDisable() {
    if(this.treeBranch && this.treeBranch.thisParticipant != null) {
      this.disabledBranches = true;
    }
    if(this.childBranches) {
      this.childBranches.forEach(p => p.updateDisable());
    }
  }

  public checkDisabled(): boolean {
    if (this.roundNumber === 2 && this.disabledBranches) {
      return false;
    }
    let result = 0;
    if (this.childBranches) {
      this.childBranches.forEach(p => {
        if (!p.checkDisabled()) {
          result++;
        }
      })
    }
    return result == 0;
  }

  public ifDisableBranches() {
    return this.disabledBranches;
  }

  public ifThisBranchDisable(){
    return this.thisBrachDisable;
  }
}
