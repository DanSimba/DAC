import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  {
    path: 'client',
    renderMode: RenderMode.Client
  },
  {
    path: 'client/**',
    renderMode: RenderMode.Client
  },
  {
    path: 'manager',
    renderMode: RenderMode.Client
  },
  {
    path: 'manager/**',
    renderMode: RenderMode.Client
  },
  {
    path: '**',
    renderMode: RenderMode.Server
  }
];
