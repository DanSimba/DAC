//RF04: A ORIGEM DEVE TER SALDO SUFICIENTE
// O DESTINO DEVE EXISTIR 

export interface TransferenceModel {
    cpf_origin: string;
    cpf_destiny: string;
    value: number;
    datetime: string; //DATA E HORA
}
