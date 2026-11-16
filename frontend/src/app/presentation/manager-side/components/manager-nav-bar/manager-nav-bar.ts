import { Component, inject, OnInit } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { ManagerService } from '../../../../application/manager/services/manager-service';
import { ManagerMenu } from '../manager-menu/manager-menu';
import { AuthHttpService } from '../../../../infraestructure/http/auth.http.service';

@Component({
  selector: 'app-manager-nav-bar',
  imports: [RouterLink, RouterLinkActive, MatIconModule, ManagerMenu],
  templateUrl: './manager-nav-bar.html',
  styleUrl: './manager-nav-bar.css',
})
export class ManagerNavBar implements OnInit {
  managerService = inject(ManagerService);
  private authHttpService = inject(AuthHttpService);

  ngOnInit(): void {
    const usuarioLogado = this.authHttpService.getUsuario();
    if (usuarioLogado) {
      this.managerService.setGerente(usuarioLogado);
    }
  }
}
