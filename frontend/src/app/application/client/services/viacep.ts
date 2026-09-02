import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ViacepResponse } from '../../../domain/address/models/viacep-response.model';

@Injectable({
  providedIn: 'root',
})
export class Viacep {
  private readonly url = "https://viacep.com.br/ws";

  constructor(private http:HttpClient) {}

  getCep(cep: string): Observable<ViacepResponse> {
    return this.http.get<ViacepResponse>(`${this.url}/${cep}/json/`);
  }
}
