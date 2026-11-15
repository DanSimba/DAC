import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, interval, of, switchMap, takeWhile, filter, take, throwError, timeout } from 'rxjs';

export interface JobStatusResponse {
  jobId: string;
  status: 'PENDENTE' | 'CONCLUIDO' | 'FALHA';
  resultType: 'resource' | 'inline' | null;
  dominio: string | null;
  resourceId: string | null;
  resultado?: unknown;
  erro: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class JobPollingService {
  private readonly http = inject(HttpClient);
  private readonly API_URL = 'http://localhost:8080';

  /**
   * Executa uma requisição que pode responder 200/201 (síncrono) ou 202 Accepted (assíncrono via Job).
   */
    executarComPolling<T>(
      requisicao$: Observable<HttpResponse<any>>,
      buscarRecursoFinal: boolean = true
    ): Observable<T> {
    return requisicao$.pipe(
      switchMap((response) => {
        if (response.status !== 202) {
          return of(response.body as T);
        }

        const jobId = response.body?.jobId;
        if (!jobId) {
          return throwError(() => new Error('Resposta 202 recebida sem jobId.'));
        }

        return interval(1500).pipe(
          switchMap(() => this.http.get<JobStatusResponse>(`${this.API_URL}/jobs/${jobId}/status`)),
          takeWhile((job) => job.status === 'PENDENTE', true),
          filter((job) => job.status !== 'PENDENTE'),
          take(1),
          timeout({ first: 60000 }),
          switchMap((jobFinal) => {
            if (jobFinal.status === 'FALHA') {
              return throwError(() => ({ error: { mensagem: jobFinal.erro || 'Falha ao processar operação.' } }));
            }

            if (!buscarRecursoFinal) {
              return of((jobFinal.resultado ?? null) as T);
            }

            if (jobFinal.resultType === 'resource' && jobFinal.dominio && jobFinal.resourceId) {
              return this.http.get<T>(`${this.API_URL}/${jobFinal.dominio}/${jobFinal.resourceId}`);
            }

            if (jobFinal.resultType === 'inline') {
              return this.http.get<T>(`${this.API_URL}/jobs/${jobId}/result`);
            }

            return of(null as T);
          })
        );
      })
    );
  }
}