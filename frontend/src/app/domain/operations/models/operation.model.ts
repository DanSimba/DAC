export interface OperationModel {
    type: 'operation';
    acc_number: string 
    side: 'dep'|'sac';
    value: number;
    datetime: string; //DATA E HORA
}
