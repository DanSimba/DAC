import { Routes } from '@angular/router';
import { Home } from './presentation/pages/home/home';
import { ClientSide } from './presentation/sides/client-side/client-side';

export const routes: Routes = [
    {//home deslogado
        path: '',
        component: Home,
        title: 'BANCO DA MONSTER HIGH????'
    },
    { //todas as telas do lado do cliente
        path:'client',
        component:ClientSide,
        title:'BANCO DA MONSTER HIGH????',
        children:[]

    },
];
