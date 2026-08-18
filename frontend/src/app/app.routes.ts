import { Routes } from '@angular/router';
import { HomePage } from './presentation/shared/pages/home-page/home-page';
import { ClientSidePage } from './presentation/client-side/pages/client-side-page/client-side-page';
import { Autocadastro } from './pages/autocadastro/autocadastro';

export const routes: Routes = [
    {//home deslogado
        path: '',
        component: HomePage,
        title: 'BANCO DA MONSTER HIGH????'
    },
    { //todas as telas do lado do cliente
        path:'client',
        component:ClientSidePage,
        title:'BANCO DA MONSTER HIGH????',
        children:[
            {
                path:'',
                loadComponent: () => import('../app/presentation/client-side/pages/client-home-page/client-home-page').then(m => m.ClientHomePage)
            }
        ]
    },
    { // Teste do ViaCEP
        path: 'autocadastro',
        component: Autocadastro
     }
];
