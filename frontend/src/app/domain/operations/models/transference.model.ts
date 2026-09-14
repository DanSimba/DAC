//RF04: A ORIGEM DEVE TER SALDO SUFICIENTE
// O DESTINO DEVE EXISTIR 

export interface TransferenceModel {
    type: 'transference';
    cpf_origin: string;
    name_origin: string;
    acc_origin: string;
    
    acc_destiny: string;

    value: number;
    datetime: string; //DATA E HORA
}
