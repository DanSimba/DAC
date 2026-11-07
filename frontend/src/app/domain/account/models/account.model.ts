export interface Account {
    client_cpf   : string;
    number   : string;
    balanco  : number;
    managerId  : number;
    createdAt: number;
    lastUpdt: string; //ULTIMO UPDT, VEM JUNTO COM ACCLASTVERSION JA FORMATADO (DATETIME)
}