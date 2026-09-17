import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { ManagerService } from '../../../../application/manager/services/manager-service';

@Component({
  selector: 'app-manager-menu',
  imports: [],
  templateUrl: './manager-menu.html',
  styleUrl: './manager-menu.css',
})
export class ManagerMenu {
  managerService = inject(ManagerService);
  private router = inject(Router);

  logout(): void {
    console.log('gerente saiu!!');
    this.managerService.toggleMenu();
    this.router.navigate(['']);
  }
}
