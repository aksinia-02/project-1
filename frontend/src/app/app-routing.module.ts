import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HorseCreateInfoEditComponent, HorseCreateInfoEditMode} from './component/horse/horse-create-info-edit/horse-create-edit.component';
import {HorseComponent} from './component/horse/horse.component';
import {TournamentCreateComponent} from "./component/tournament/tournament-create/tournament-create.component";
import {TournamentStandingsComponent} from "./component/tournament/tournament-standings/tournament-standings.component";
import {TournamentComponent} from "./component/tournament/tournament.component";
import {
  TournamentStandingsBranchComponent
} from "./component/tournament/tournament-standings/tournament-standings-branch/tournament-standings-branch.component";

const routes: Routes = [
  {path: '', redirectTo: 'horses', pathMatch: 'full'},
  {path: 'horses', children: [
    {path: '', component: HorseComponent},
    {path: 'create', component: HorseCreateInfoEditComponent, data: {mode: HorseCreateInfoEditMode.create}},
      {path: 'edit/:id', component: HorseCreateInfoEditComponent, data: { mode: HorseCreateInfoEditMode.edit } },
      {path: 'info/:id', component: HorseCreateInfoEditComponent, data: { mode: HorseCreateInfoEditMode.info } },
  ]},
  {path: 'tournaments', children: [
      {path: '', component: TournamentComponent},
    {path: 'create', component: TournamentCreateComponent},
    {path: 'standings/:id', component: TournamentStandingsComponent},
  ]},
  {path: '**', redirectTo: 'horses'},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
