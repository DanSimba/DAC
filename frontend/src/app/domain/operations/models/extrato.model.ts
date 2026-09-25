import { OperationModel } from "./operation.model";
import { TransferenceModel } from "./transference.model";

//REFATORAÇÃO: O EXTRATO CONDIZ AO DIA, TDS AS OPERS E TRANFS QUE ACONTECERAM NELE E SOMENTE O SALDO FINAL
export interface ExtratoModel {
    acc_number: string;
    transfs: TransferenceModel[];
    opers: OperationModel[];

    id: number; 
    dateId:number; //O ID SERÁ A DATA NO FORMATO AAAAMMDD
    saldoApos: number; //saldo no fim do dia
}
