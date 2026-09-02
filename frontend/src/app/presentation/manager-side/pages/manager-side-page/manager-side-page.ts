import { Component, computed, inject } from '@angular/core';
import { RouterOutlet } from "@angular/router";
import { ManagerNavBar } from "../../components/manager-nav-bar/manager-nav-bar";
import { ManagerMenu } from "../../components/manager-menu/manager-menu";
import { ManagerService } from '../../../../application/manager/services/manager-service';

@Component({
  selector: 'app-manager-side-page',
  imports: [RouterOutlet, ManagerNavBar, ManagerMenu],
  templateUrl: './manager-side-page.html',
  styleUrl: './manager-side-page.css',
})
export class ManagerSidePage {

  managerService = inject(ManagerService);
  imo = computed(() => {return this.managerService.getIMO})
  


}
