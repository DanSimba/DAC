import { Routes } from '@angular/router';
import { HomePage } from './presentation/shared/pages/home-page/home-page';
import { ClientSidePage } from './presentation/client-side/pages/client-side-page/client-side-page';
import { Autocadastro } from './presentation/shared/pages/autocadastro/autocadastro';
import { clientAuthGuard } from './domain/client/guards/client-auth-guard';
import { Login } from './presentation/shared/pages/login/login';

export const routes: Routes = [
    {//home deslogado
        path: '',
        component: HomePage,
        title: 'BANCO DA MONSTER HIGH????',
        children:[
            {
                path:'',
                loadComponent: () => import('../app/presentation/shared/components/home-window/home-window').then(m => m.HomeWindow)
            }
        ]
    },
    { //todas as telas do lado do cliente
        path:'client',
        component:ClientSidePage,
        title:'BANCO DA MONSTER HIGH!!!',
        canActivate: [clientAuthGuard],
        children:[
            {
                path:'',
                loadComponent: () => import('../app/presentation/client-side/pages/client-home-page/client-home-page').then(m => m.ClientHomePage)
            },
            {
                path:'depsac',
                loadComponent: () => import('../app/presentation/client-side/pages/dep-sac/dep-sac').then(m => m.DepSac)
            },
            {
                path:'transferir',
                loadComponent: () => import('../app/presentation/client-side/pages/transference/transference').then(m => m.Transference)
            },
        ]
    },
    { // Teste do ViaCEP
        path: 'autocadastro',
        component: Autocadastro
     },
     { // tela de login
        path: 'login',
        component: Login
     }
];
