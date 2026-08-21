import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ClientNavBar } from '../../components/client-nav-bar/client-nav-bar';

@Component({
  selector: 'app-client-side-page',
  imports: [RouterOutlet, ClientNavBar],
  templateUrl: './client-side-page.html',
  styleUrl: './client-side-page.css',
})
export class ClientSidePage {}
