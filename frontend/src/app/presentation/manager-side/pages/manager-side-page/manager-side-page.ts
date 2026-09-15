import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ManagerNavBar } from '../../components/manager-nav-bar/manager-nav-bar';

@Component({
  selector: 'app-manager-side-page',
  imports: [RouterOutlet, ManagerNavBar],
  templateUrl: './manager-side-page.html',
  styleUrl: './manager-side-page.css',
})
export class ManagerSidePage {}
