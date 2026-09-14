import { OperationModel } from "./operation.model";
import { TransferenceModel } from "./transference.model";

//REFATORAÇÃO: O EXTRATO CONDIZ AO DIA, TDS AS OPERS E TRANFS QUE ACONTECERAM NELE E SOMENTE O SALDO FINAL
export interface ExtratoModel {
    tranfs: TransferenceModel[];
    opers: OperationModel[];

    id: number; //O ID SERÁ A DATA NO FORMATO AAAAMMDD
    saldoApos: number; //saldo no fim do dia
}
