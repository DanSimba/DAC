import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavBar } from '../../components/nav-bar/nav-bar';

@Component({
  selector: 'app-home-page',
  imports: [RouterOutlet, NavBar],
  templateUrl: './home-page.html',
  styleUrl: './home-page.css',
})
export class HomePage {}
