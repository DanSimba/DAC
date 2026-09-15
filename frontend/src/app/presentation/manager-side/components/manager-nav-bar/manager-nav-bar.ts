import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { ManagerService } from '../../../../application/manager/services/manager-service';
import { ManagerMenu } from '../manager-menu/manager-menu';

@Component({
  selector: 'app-manager-nav-bar',
  imports: [RouterLink, RouterLinkActive, MatIconModule, ManagerMenu],
  templateUrl: './manager-nav-bar.html',
  styleUrl: './manager-nav-bar.css',
})
export class ManagerNavBar {
  managerService = inject(ManagerService);
}
