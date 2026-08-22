import { Component, signal } from '@angular/core';

@Component({
  selector: 'app-dep-sac',
  imports: [],
  templateUrl: './dep-sac.html',
  styleUrl: './dep-sac.css',
})
export class DepSac {
  side = signal<string>('');

  setSide(s: string){
    this.side.set(s);
  }
}
