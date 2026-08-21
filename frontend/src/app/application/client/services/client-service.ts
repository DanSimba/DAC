import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ClientService {
  private isMenuOpen = signal<boolean>(false);
  toggleMenu(){
    this.isMenuOpen.set(!this.isMenuOpen());
  }

  getIMO(): boolean{
    return this.isMenuOpen()
  }
}
