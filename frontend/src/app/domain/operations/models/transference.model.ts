//RF04: A ORIGEM DEVE TER SALDO SUFICIENTE
// O DESTINO DEVE EXISTIR 

export interface TransferenceModel {
    type: 'transference';
    cpfOrigin: string;
    nameOrigin: string;

    //cpf_destiny:string;
    //name_destiny: string;
    accOrigin: string;
    accDestiny: string;

    value: number;
    datetime: string; //DATA E HORA
}
