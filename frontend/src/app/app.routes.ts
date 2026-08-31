import { Routes } from '@angular/router';
import { HomePage } from './presentation/shared/pages/home-page/home-page';
import { ClientSidePage } from './presentation/client-side/pages/client-side-page/client-side-page';
import { Autocadastro } from './presentation/shared/pages/autocadastro/autocadastro';
import { ManagerSidePage } from './presentation/manager-side/pages/manager-side-page/manager-side-page';

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
        title:'BANCO DA MONSTER HIGH????',
        children:[
            {
                path:'',
                loadComponent: () => import('../app/presentation/client-side/pages/client-home-page/client-home-page').then(m => m.ClientHomePage)
            },
            {
                path:'depsac',
                loadComponent: () => import('../app/presentation/client-side/pages/dep-sac/dep-sac').then(m => m.DepSac)
            },
        ]
    },
    { //telas do lado gerente
        path:'manager',
        component:ManagerSidePage,
        title:'ah é',
        children:[
            {
                path:'',
                loadComponent: () => import('../app/presentation/manager-side/pages/manager-home-page/manager-home-page').then(m => m.ManagerHomePage)
            },

        ]
    },
    { // Teste do ViaCEP
        path: 'autocadastro',
        component: Autocadastro
     }
];
